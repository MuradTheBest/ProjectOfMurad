package com.example.projectofmurad.home;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.Profile_Screen;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventSlidePageAdapter;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.helpers.ZoomOutPageTransformer;
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
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends androidx.fragment.app.Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar();
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

    public ConstraintLayout constraintLayout;

    private ViewPager2 vp_event;

    private FloatingActionButton fab_add_training;

    private FloatingActionButton fab_add_training2;
    private FloatingActionButton fab_add_private_training2;
    private FloatingActionButton fab_add_group_training2;

    private MainViewModel mainViewModel;

    private ProgressBar progressBar;

    private Animation rotate_open_anim;
    private Animation rotate_close_anim;

    private Animation from_bottom_anim;
    private Animation to_bottom_anim;

    private Animation from_end_anim;
    private Animation to_end_anim;

    private boolean expand;

    private ActionBar actionBar;

    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        mainViewModel.getScrollUp().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                coordinatorLayout.scrollTo(0, coordinatorLayout.getTop());
            }
        });

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (tabLayout.getSelectedTabPosition() == 1){
                    if (isVisible(tabLayout)){
                        fab_add_training.show();
                        fab_add_training2.hide();
                    }
                    else {
                        fab_add_training.hide();
                        fab_add_training2.show();
                    }
                }

//                Log.d(Utils.LOG_TAG, "********************************************************************************************* ");
//                Log.d(Utils.LOG_TAG, "Math.abs(verticalOffset) + Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() = " + (Math.abs(verticalOffset) + Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange()));
//                Log.d(Utils.LOG_TAG, "Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() = " + (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange()));
//                Log.d(Utils.LOG_TAG, "Math.abs(verticalOffset) = " + Math.abs(verticalOffset));
//                Log.d(Utils.LOG_TAG, "appBarLayout.getTotalScrollRange() = " + appBarLayout.getTotalScrollRange());
//                Log.d(Utils.LOG_TAG, "tabLayout.getHeight() = " + tabLayout.getHeight());
//                Log.d(Utils.LOG_TAG, "collapsingToolbarLayout.getScrimVisibleHeightTrigger() = " + collapsingToolbarLayout.getScrimVisibleHeightTrigger());
//                Log.d(Utils.LOG_TAG, "********************************************************************************************* ");

                if (/*(Math.abs(verticalOffset) + Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange()) > collapsingToolbarLayout.getScrimVisibleHeightTrigger()*/
                    /*vp_event.getY() == tabLayout.getY()*/
                        isVisible(tabLayout)) {

                    //Expanded
                    collapsingToolbarLayout.setTitle("");

                }
                else {
//                    Log.d(Utils.LOG_TAG, "show text" + tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText());

//                    toolbar.setTitle(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText());


                    collapsingToolbarLayout.setTitle(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText());
//                    toolbar.setTitle(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText());
                }
            }
        });

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar();


        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

//        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = view.findViewById(R.id.progress_bar);

        vp_event = view.findViewById(R.id.vp_event);

        Query queryLast = FirebaseUtils.getAllEventsDatabase().orderByChild("end").endAt(Calendar.getInstance().getTimeInMillis()).limitToLast(1);
        Log.d(Utils.LOG_TAG, queryLast.getRef().toString());
        Query queryNext = FirebaseUtils.getAllEventsDatabase().orderByChild("end").startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);

        queryLast.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()){
                    mainViewModel.getLastEvent().setValue(null);
                    Log.d(Utils.LOG_TAG, "last event is null");
                    return;
                }
                for (DataSnapshot data : snapshot.getChildren()){
                    mainViewModel.getLastEvent().setValue(data.getValue(CalendarEvent.class));
                    Log.d(Utils.LOG_TAG, "last event is" + mainViewModel.getLastEvent().getValue().toString());
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
                    Log.d(Utils.LOG_TAG, "next event is null");
                    return;
                }
                for (DataSnapshot data : snapshot.getChildren()){
                    mainViewModel.getNextEvent().setValue(data.getValue(CalendarEvent.class));
                    Log.d(Utils.LOG_TAG,"next event is" +  mainViewModel.getNextEvent().getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        mainViewModel.getNextEvent().observe(getViewLifecycleOwner(), event -> mainViewModel.addReady());

        mainViewModel.getLastEvent().observe(getViewLifecycleOwner(), event -> mainViewModel.addReady());

        mainViewModel.getReady().observe(getViewLifecycleOwner(),
                integer -> {
                    Log.d("home", "getReady = " + integer);
                    if (integer == 2){
                        setPagerAdapter();
                    }
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
                System.out.println(expand);
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

    public static boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        boolean isGlobalVisible = view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        return isGlobalVisible && actualPosition.intersect(screen);
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
        vp_event.setPageTransformer(new ZoomOutPageTransformer());

        vp_event.setNestedScrollingEnabled(true);

        vp_event.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, vp_event,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0){
                            tab.setText("Last event");
                        }
                        else if (position == 1){
                            tab.setText("Next event");
                        }
                    }
                }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    Log.d(Utils.LOG_TAG, "on last event tab");

                    fab_add_training.setVisibility(View.GONE);
                    fab_add_training2.setVisibility(View.GONE);

                    actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_person_24);
                    if (menu.findItem(R.id.to_next_event) != null){
                        menu.findItem(R.id.to_next_event).setIcon(R.drawable.ic_baseline_arrow_back_24_180_degrees);
                    }
                }
                else if (tab.getPosition() == 1){

                    fab_add_training.setVisibility(View.VISIBLE);
                    fab_add_training2.setVisibility(View.VISIBLE);

                    if (menu != null && menu.findItem(R.id.to_next_event) != null){
                        menu.findItem(R.id.to_next_event).setIcon(R.drawable.ic_baseline_person_24);
                    }
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
                }

/*

                actionBar.setDisplayShowHomeEnabled(tab.getPosition() == 1);
                actionBar.setHomeButtonEnabled(tab.getPosition() == 1);
                actionBar.setDisplayHomeAsUpEnabled(tab.getPosition() == 1);*/

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_event.setCurrentItem(1, false);
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }

}