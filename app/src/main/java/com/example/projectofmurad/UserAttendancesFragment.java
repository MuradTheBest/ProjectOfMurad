package com.example.projectofmurad;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventInfoDialogFragment;
import com.example.projectofmurad.calendar.EventsAdapter;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class UserAttendancesFragment extends DialogFragment implements EventsAdapter.OnEventClickListener{

    /**
     * The constant TAG.
     */
    public static final String TAG = "UserAttendancesFragment";

    private String UID;
    private String username;

    /**
     * Instantiates a new User attendances fragment.
     */
    public UserAttendancesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param UID      uid of selected user.
     * @param username username of selected user
     *
     * @return A new instance of fragment UserAttendancesFragment.
     */
    @NonNull
    public static UserAttendancesFragment newInstance(String UID, String username) {
        UserAttendancesFragment fragment = new UserAttendancesFragment();
        Bundle args = new Bundle();
        args.putString(UserData.KEY_UID, UID);
        args.putString(UserData.KEY_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UID = getArguments().getString(UserData.KEY_UID);
            username = getArguments().getString(UserData.KEY_USERNAME);
            setShowsDialog(true);
        }
        else {
            UID = FirebaseUtils.getCurrentUID();
            setShowsDialog(false);
        }

        allEventsAmount = new MutableLiveData<>(0L);
        attendingEventsAmount = new MutableLiveData<>(0L);

        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme);
        setCancelable(true);

        allEvents = FirebaseUtils.getAllEventsDatabase();
        attendingEvents = FirebaseUtils.getAttendanceDatabase().orderByChild(UID + "/attend").equalTo(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_attendances, container, false);
    }

    /**
     * The Progress bar.
     */
    ProgressBar progressBar;
    /**
     * The Tv relative attendance.
     */
    TextView tv_relative_attendance;
    /**
     * The Tv there are no events.
     */
    TextView tv_there_are_no_events;
    /**
     * The Tv there are no attending events.
     */
    TextView tv_there_are_no_attending_events;
    /**
     * The Rv events.
     */
    RecyclerView rv_events;
    /**
     * The Switch only attend.
     */
    SwitchMaterial switch_only_attend;
    /**
     * The Events adapter.
     */
    EventsAdapter eventsAdapter;

    /**
     * The All events amount.
     */
    MutableLiveData<Long> allEventsAmount;
    /**
     * The Attending events amount.
     */
    MutableLiveData<Long> attendingEventsAmount;

    /**
     * The All events.
     */
    DatabaseReference allEvents;
    /**
     * The Attending events.
     */
    Query attendingEvents;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        tv_relative_attendance = view.findViewById(R.id.tv_relative_attendance);
        tv_there_are_no_events = view.findViewById(R.id.tv_there_are_no_events);
        tv_there_are_no_attending_events = view.findViewById(R.id.tv_there_are_no_attending_events);
        switch_only_attend = view.findViewById(R.id.switch_only_attend);
        rv_events = view.findViewById(R.id.rv_events);

        initializeRVEvents();

        switch_only_attend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setUpAttendEventsRecyclerView();
            }
            else {
                setUpAllEventsRecyclerView();
            }
        });

        setUpAllEventsRecyclerView();

        allEventsAmount.observe(this, count -> getUserRelativeAttendance());
        attendingEventsAmount.observe(this, count -> getUserRelativeAttendance());

        allEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allEventsAmount.setValue(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attendingEvents.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attendingEventsAmount.setValue(snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initializeRVEvents() {
        Query events = allEvents.orderByChild(CalendarEvent.KEY_EVENT_START);

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setLifecycleOwner(this)
                .setQuery(events, CalendarEvent.class)
                .build();

        eventsAdapter = new EventsAdapter(options, UID, requireContext(), this);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(requireContext());
        layoutManager.setOnLayoutCompleteListener(() -> new Handler().postDelayed(this::stopRVEventsShimmer, 500));

        rv_events.setLayoutManager(layoutManager);
    }

    /**
     * Sets up all events recycler view.
     */
    public void setUpAllEventsRecyclerView() {
        startRVEventsShimmer();

        Query events = allEvents.orderByChild(CalendarEvent.KEY_EVENT_START);

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setLifecycleOwner(this)
                .setQuery(events, CalendarEvent.class)
                .build();

        eventsAdapter.updateOptions(options);
        rv_events.setAdapter(eventsAdapter);
    }

    /**
     * Sets up attend events recycler view.
     */
    public void setUpAttendEventsRecyclerView() {
        startRVEventsShimmer();

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setLifecycleOwner(this)
                .setIndexedQuery(attendingEvents, allEvents, CalendarEvent.class)
                .build();

        eventsAdapter.updateOptions(options);
        rv_events.setAdapter(eventsAdapter);
    }

    @Override
    public void onEventClick(@NonNull CalendarEvent event) {
        FragmentManager fm = getParentFragmentManager();

        if (fm.findFragmentByTag(EventInfoDialogFragment.TAG) == null){
            EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(event, true);
            event_info_dialogFragment.show(fm, EventInfoDialogFragment.TAG);
        }
    }

    /**
     * Start rv events shimmer.
     */
    public void startRVEventsShimmer() {
        rv_events.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        tv_there_are_no_events.setVisibility(View.GONE);
        tv_there_are_no_attending_events.setVisibility(View.GONE);
    }

    /**
     * Stop rv events shimmer.
     */
    public void stopRVEventsShimmer() {
        progressBar.setVisibility(View.GONE);
        rv_events.setVisibility(View.VISIBLE);

        if (eventsAdapter.getItemCount() < 1) {
            if (switch_only_attend.isChecked()) {
                tv_there_are_no_attending_events.setVisibility(View.VISIBLE);
            }
            else {
                tv_there_are_no_events.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Get user relative attendance.
     */
    public void getUserRelativeAttendance(){
        long count = attendingEventsAmount.getValue();
        long all = allEventsAmount.getValue();
        double attendance = (double) count/all;

        Log.d(Utils.LOG_TAG, "count = " + count);
        Log.d(Utils.LOG_TAG, "all = " + all);

        if (FirebaseUtils.isCurrentUID(UID)){
            tv_relative_attendance.setText("Your relative attendance: " + (int) (attendance*100) + "%");
        }
        else {
            tv_relative_attendance.setText(username + "'s relative attendance: " + (int) (attendance*100) + "%");
        }

        tv_relative_attendance.setTextColor(ColorUtils.blendARGB(Color.RED, Color.GREEN, (float) attendance));
    }
}