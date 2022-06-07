package com.example.projectofmurad.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventSlidePageAdapter;
import com.example.projectofmurad.tracking.TrackingService;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    /**
     * Instantiates a new Home fragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (tabLayout.getSelectedTabPosition() == 1){
                    tabLayout.selectTab(tabLayout.getTabAt(0), true);
                }
                break;
            case R.id.to_next_event:
                if (tabLayout.getSelectedTabPosition() == 0){
                    tabLayout.selectTab(tabLayout.getTabAt(1), true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.to_next_event).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * The Tab layout.
     */
    public TabLayout tabLayout;
    /**
     * The Toolbar.
     */
    public MaterialToolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ViewPager2 vp_event;

    private FloatingActionButton fab_add_training;

    private MainViewModel mainViewModel;

    private ProgressBar progressBar;
    private AppCompatImageView iv_group_picture;

    private ActionBar actionBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                boolean isContentHide = collapsingToolbarLayout.getScrimVisibleHeightTrigger()
                        + Math.abs(verticalOffset) > collapsingToolbarLayout.getHeight();

                if (tabLayout.getSelectedTabPosition() == 1){
                    if (!isContentHide && mainViewModel.getNextEvent().getValue() != null){
                        fab_add_training.show();
                    }
                    else {
                        fab_add_training.hide();
                    }
                }

                if (!isContentHide) {
                    collapsingToolbarLayout.setTitle(null);
                }
                else {
                    TabLayout.Tab tab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());

                    if (tab != null){
                        collapsingToolbarLayout.setTitle(tab.getText());
                    }
                }
            }
        });

        toolbar = view.findViewById(R.id.toolbar);
        iv_group_picture = view.findViewById(R.id.iv_group_picture);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseUtils.getCurrentGroupPicture().observe(getViewLifecycleOwner(),
                picture -> Glide.with(requireContext())
                        .load(picture)
                        .error(R.drawable.sample_group_picture)
                        .placeholder(R.drawable.sample_group_picture)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target,
                                                        boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {

                                int height = resource.getIntrinsicHeight();
                                int margin = height - Utils.dpToPx(20, requireContext());

                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabLayout.getLayoutParams();
                                params.topMargin = margin;

                                tabLayout.setLayoutParams(params);

                                return false;
                            }
                        })
                        .centerInside().into(iv_group_picture));

        progressBar = view.findViewById(R.id.progress_bar);

        vp_event = view.findViewById(R.id.vp_event);

        Query queryLast = FirebaseUtils.getAllEventsDatabase().orderByChild("end")
                .endAt(Calendar.getInstance().getTimeInMillis()).limitToLast(1);

        Query queryNext = FirebaseUtils.getAllEventsDatabase().orderByChild("end")
                .startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);

        queryLast.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()){
                    mainViewModel.getLastEvent().setValue(null);
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()){
                    mainViewModel.getLastEvent().setValue(data.getValue(CalendarEvent.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        queryNext.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()){
                    mainViewModel.getNextEvent().setValue(null);
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()){
                    mainViewModel.getNextEvent().setValue(data.getValue(CalendarEvent.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        mainViewModel.getNextEvent().observe(getViewLifecycleOwner(), event -> mainViewModel.addReady());

        mainViewModel.getLastEvent().observe(getViewLifecycleOwner(), event -> mainViewModel.addReady());

        mainViewModel.getReady().observe(getViewLifecycleOwner(),
                integer -> {
                    if (integer == 2) setPagerAdapter();
                });

        tabLayout = view.findViewById(R.id.tabLayout);

        fab_add_training = view.findViewById(R.id.fab_add_training);
        fab_add_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarEvent event = mainViewModel.getNextEvent().getValue();
                TrackingService.eventPrivateId.setValue(event.getPrivateId());
                ((MainActivity) requireActivity()).moveToTrackingFragment();
            }
        });

    }

    private void setPagerAdapter() {
        mainViewModel.resetReady();

        progressBar.setVisibility(View.GONE);

        EventSlidePageAdapter pagerAdapter = new EventSlidePageAdapter(this,
                mainViewModel.getNextEvent().getValue(), mainViewModel.getLastEvent().getValue());

        vp_event.setAdapter(pagerAdapter);
        vp_event.setNestedScrollingEnabled(true);
        vp_event.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, vp_event,
                (tab, position) -> tab.setText(position == 0 ? R.string.last_event : R.string.next_event)).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fab_add_training.setVisibility(tab.getPosition() == 0 ? View.GONE : View.VISIBLE);

                actionBar.setDisplayHomeAsUpEnabled(tab.getPosition() == 1);

                if (toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.to_next_event) != null){
                    toolbar.getMenu().findItem(R.id.to_next_event).setVisible(tab.getPosition() == 0);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayout.selectTab(tabLayout.getTabAt(1), true);
        vp_event.setCurrentItem(1, false);
    }

}