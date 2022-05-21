package com.example.projectofmurad.home;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.Profile_Screen;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventSlidePageAdapter;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.tracking.TrackingService;
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
        inflater.inflate(R.menu.blank_fragment_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private Menu menu;

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (tabLayout.getSelectedTabPosition() == 1){
                    tabLayout.selectTab(tabLayout.getTabAt(0), true);
                }
                else {
                    startActivity(new Intent(requireContext(), Profile_Screen.class));
                }
                break;
            case R.id.to_next_event:
                if (tabLayout.getSelectedTabPosition() == 0){
                    tabLayout.selectTab(tabLayout.getTabAt(1), true);
                }
                else {
                    startActivity(new Intent(requireContext(), Profile_Screen.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank_for_viewpager2, container, false);
    }

    public TabLayout tabLayout;
    public MaterialToolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ViewPager2 vp_event;

    private FloatingActionButton fab_add_training;

    private FloatingActionButton fab_add_training2;
    private FloatingActionButton fab_add_private_training2;
    private FloatingActionButton fab_add_group_training2;

    private MainViewModel mainViewModel;

    private ProgressBar progressBar;
    private ImageView iv_group_picture;

    private Animation rotate_open_anim;
    private Animation rotate_close_anim;

    private Animation from_bottom_anim;
    private Animation to_bottom_anim;

    private Animation from_end_anim;
    private Animation to_end_anim;

    private boolean expand;

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
                        fab_add_training2.hide();
                    }
                    else {
                        fab_add_training.hide();
                        fab_add_training2.show();
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
        ((AppCompatActivity) requireActivity()).getSupportActionBar();

        iv_group_picture = view.findViewById(R.id.iv_group_picture);

        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FirebaseUtils.getCurrentGroupPicture().observe(getViewLifecycleOwner(),
                picture -> Glide.with(requireContext())
                        .load(picture)
                        .error(R.drawable.sample_group_picture)
                        .placeholder(R.drawable.sample_group_picture)
                        .centerInside().into(iv_group_picture));

        progressBar = view.findViewById(R.id.progress_bar);

        vp_event = view.findViewById(R.id.vp_event);

        Query queryLast = FirebaseUtils.getAllEventsDatabase().orderByChild("end").endAt(Calendar.getInstance().getTimeInMillis()).limitToLast(1);

        Query queryNext = FirebaseUtils.getAllEventsDatabase().orderByChild("end").startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);

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
                TrackingService.trainingType.setValue(TrackingService.GROUP_TRAINING);

                CalendarEvent event = mainViewModel.getNextEvent().getValue();
                TrackingService.eventPrivateId.setValue(event.getPrivateId());
                ((MainActivity) requireActivity()).moveToTrackingFragment();
            }
        });

        fab_add_training2 = view.findViewById(R.id.fab_add_training2);
        fab_add_training2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand = !expand;
                setVisibility(expand);
                setAnimation(expand);
                setClickable(expand);
            }
        });

        fab_add_training2.addOnHideAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand){
                    expand = false;
                    setVisibility(false);
                    setAnimation(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        fab_add_training2.hide();

        fab_add_private_training2 = view.findViewById(R.id.fab_add_private_training2);
        fab_add_private_training2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackingService.trainingType.setValue(TrackingService.PRIVATE_TRAINING);
                ((MainActivity) requireActivity()).moveToTrackingFragment();
            }
        });

        fab_add_group_training2 = view.findViewById(R.id.fab_add_group_training2);
        fab_add_group_training2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackingService.trainingType.setValue(TrackingService.GROUP_TRAINING);
                ((MainActivity) requireActivity()).moveToTrackingFragment();
            }
        });

        /*MainViewModel.toSwipeFragments.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(requireContext(), "toSwipeFragments is " + aBoolean, Toast.LENGTH_SHORT).show();
                Log.d("murad", "toSwipeFragments is " + aBoolean);
                vp_event.setUserInputEnabled(aBoolean);

            }
        });*/

        initializeAnimations();
    }

    private void initializeAnimations(){
        rotate_open_anim = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim);
        rotate_close_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim);

        from_bottom_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim);
        to_bottom_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim);

        from_end_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.from_end_anim);
        to_end_anim = AnimationUtils.loadAnimation(requireContext(), R.anim.to_end_anim);
    }

    private void setVisibility(boolean expand){
        fab_add_private_training2.setVisibility(expand ? View.VISIBLE : View.INVISIBLE);
        fab_add_group_training2.setVisibility(expand ? View.VISIBLE : View.INVISIBLE);
    }

    private void setAnimation(boolean expand){
        fab_add_training.startAnimation(expand ? rotate_open_anim : rotate_close_anim);
        fab_add_private_training2.startAnimation(expand ? from_bottom_anim : to_bottom_anim);
        fab_add_group_training2.startAnimation(expand ? from_end_anim : to_end_anim);
    }

    private void setClickable(boolean clickable){
        fab_add_private_training2.setClickable(clickable);
        fab_add_group_training2.setClickable(clickable);
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
                mainViewModel.setSelectedTab(tab.getPosition());

                fab_add_training.setVisibility(tab.getPosition() == 0 ? View.GONE : View.VISIBLE);
                fab_add_training2.setVisibility(tab.getPosition() == 0 ? View.GONE : View.VISIBLE);

                actionBar.setHomeAsUpIndicator(tab.getPosition() == 0
                        ? R.drawable.ic_baseline_person_24
                        : R.drawable.ic_baseline_arrow_back_24);

                if (menu != null && menu.findItem(R.id.to_next_event) != null){
                    menu.findItem(R.id.to_next_event).setIcon(tab.getPosition() == 0
                            ? R.drawable.ic_baseline_arrow_back_24_180_degrees
                            : R.drawable.ic_baseline_person_24);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayout.selectTab(tabLayout.getTabAt(mainViewModel.getSelectedTab().getValue()), true);
        vp_event.setCurrentItem(mainViewModel.getSelectedTab().getValue(), false);
    }

}