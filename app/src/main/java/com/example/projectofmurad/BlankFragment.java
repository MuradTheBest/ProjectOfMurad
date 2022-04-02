package com.example.projectofmurad;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventSlidePageAdapter;
import com.example.projectofmurad.calendar.ZoomOutPageTransformer;
import com.example.projectofmurad.tracking.TrackingViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar();
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
            case R.id.go_to_profile:
                startActivity(new Intent(requireContext(), Profile_Screen.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_for_viewpager2, container, false);
    }

    //Todo dashboard screen

    //ToDo show last happened event and show closest one to happen

    private AppBarLayout appBarLayout;

    public TabLayout tabLayout;
    public TabLayoutMediator tabLayoutMediator;
    public MaterialToolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    public CardView cv_event;
    public TextView tv_there_are_no_upcoming_events;

    public ConstraintLayout constraintLayout;

    private MutableLiveData<Boolean> isNext_event_ready = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLast_event_ready = new MutableLiveData<>();

    private EventSlidePageAdapter pagerAdapter;
    private ViewPager2 vp_event;

    private FloatingActionButton fab_add_training;
//    private FloatingActionButton fab_add_private_training;
//    private FloatingActionButton fab_add_group_training;

    private FloatingActionButton fab_add_training2;
    private FloatingActionButton fab_add_private_training2;
    private FloatingActionButton fab_add_group_training2;

    private MainViewModel mainViewModel;

    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    private Animation rotate_open_anim;
    private Animation rotate_close_anim;

    private Animation from_bottom_anim;
    private Animation to_bottom_anim;

    private Animation from_end_anim;
    private Animation to_end_anim;

    private boolean expand;

    private BottomNavigationView bottomNavigationView;

    private boolean onLastEventTab = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (!onLastEventTab){
                    if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                        //  Collapsed
                        fab_add_training.hide();
                        fab_add_training2.show();
                    }
                    else {
                        //Expanded
                        fab_add_training.show();
                        fab_add_training2.hide();
                    }
                }

            }
        });

        toolbar = view.findViewById(R.id.toolbar);
//        toolbar.setVisibility(View.GONE);

        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);

        collapsingToolbarLayout.setContentScrimColor(requireContext().getColor(R.color.colorAccent));

//        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        progressBar = view.findViewById(R.id.progress_bar);

        vp_event = view.findViewById(R.id.vp_event);

/*        Query queryLast = FirebaseUtils.allEventsDatabase.orderByChild("start")
                .startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);*/

        isNext_event_ready.setValue(false);
        isLast_event_ready.setValue(false);

        Query queryLast = FirebaseUtils.allEventsDatabase.orderByChild("end").endAt(Calendar.getInstance().getTimeInMillis()).limitToLast(1);
        Query queryNext = FirebaseUtils.allEventsDatabase.orderByChild("end").startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);


        queryLast.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists()){
                        mainViewModel.getLastEvent().setValue(data.getValue(CalendarEvent.class));

                        Log.d(Utils.LOG_TAG, "last event is" + mainViewModel.getLastEvent().getValue().toString());

                        isLast_event_ready.setValue(true);

//                        tv_there_are_no_upcoming_events.setVisibility(View.GONE);

                    }
                    else {
                        cv_event.setVisibility(View.GONE);
                        tv_there_are_no_upcoming_events.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        queryNext.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists()){
                        mainViewModel.getNextEvent().setValue(data.getValue(CalendarEvent.class));
                        Log.d(Utils.LOG_TAG,"next event is" +  mainViewModel.getNextEvent().getValue().toString());
                        isNext_event_ready.setValue(true);

//                        tv_there_are_no_upcoming_events.setVisibility(View.GONE);

                    }
                    else {
                        cv_event.setVisibility(View.GONE);
                        tv_there_are_no_upcoming_events.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mainViewModel.getNextEvent().observe(getViewLifecycleOwner(), event -> isNext_event_ready.setValue(true));

        mainViewModel.getLastEvent().observe(getViewLifecycleOwner(), event -> isLast_event_ready.setValue(true));

        isNext_event_ready.observe(getViewLifecycleOwner(), aBoolean -> {
            if (isLast_event_ready.getValue()){
                setPagerAdapter();
            }
        });

        isLast_event_ready.observe(getViewLifecycleOwner(), aBoolean -> {
            if (isNext_event_ready.getValue()){
                setPagerAdapter();
            }
        });

        tabLayout = view.findViewById(R.id.tabLayout);

        fab_add_training = view.findViewById(R.id.fab_add_training);
        fab_add_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
                ((MainActivity) requireActivity()).moveToTrackingFragment();
                TrackingViewModel.trainingType.setValue(TrackingViewModel.GROUP_TRAINING);

                CalendarEvent event = new ViewModelProvider(BlankFragment.this).get(MainViewModel.class).getNextEvent().getValue();
                TrackingViewModel.eventPrivateId.setValue(event.getPrivateId());
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
//                bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
                ((MainActivity) requireActivity()).moveToTrackingFragment();
                TrackingViewModel.trainingType.setValue(TrackingViewModel.PRIVATE_TRAINING);
            }
        });

        fab_add_group_training2 = view.findViewById(R.id.fab_add_group_training2);
        fab_add_group_training2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
                ((MainActivity) requireActivity()).moveToTrackingFragment();
                TrackingViewModel.trainingType.setValue(TrackingViewModel.GROUP_TRAINING);
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

    private void setUpEverything(){

    }

    private void setPagerAdapter() {
//        progressDialog.dismiss();
        progressBar.setVisibility(View.GONE);

        pagerAdapter = new EventSlidePageAdapter(this, mainViewModel.getNextEvent().getValue(), mainViewModel.getLastEvent().getValue());

        vp_event.setAdapter(pagerAdapter);
        vp_event.setPageTransformer(new ZoomOutPageTransformer());

        vp_event.setCurrentItem(1, false);
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
                if (tab.getText().toString().equals("Last event")){
                    onLastEventTab = true;
                    Log.d(Utils.LOG_TAG, "on last event tab");

                    fab_add_training.hide();
                    fab_add_training2.hide();
                }
                else if (tab.getText().toString().equals("Next event")){
                    onLastEventTab = false;
                    fab_add_training.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}