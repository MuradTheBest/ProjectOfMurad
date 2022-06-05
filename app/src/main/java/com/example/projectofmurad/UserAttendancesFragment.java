package com.example.projectofmurad;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.projectofmurad.calendar.EventsAdapterForFirebase;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
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
public class UserAttendancesFragment extends DialogFragment implements EventsAdapterForFirebase.OnEventClickListener{

    public static final String TAG = "UserAttendancesFragment";
    public static final String ARG_IS_SHOWS_DIALOG = "isShowsDialog";

    private String UID;
    private String username;
    private boolean isShowsDialog;

    public UserAttendancesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param UID uid of selected user.
     * @param username username of selected user
     * @param isShowsDialog if to show as a dialog or fragment
     *
     * @return A new instance of fragment AlarmsFragment.
     */
    @NonNull
    public static UserAttendancesFragment newInstance(String UID, String username, boolean isShowsDialog) {
        UserAttendancesFragment fragment = new UserAttendancesFragment();
        Bundle args = new Bundle();
        args.putString(UserData.KEY_UID, UID);
        args.putString(UserData.KEY_USERNAME, username);
        args.putBoolean(ARG_IS_SHOWS_DIALOG, isShowsDialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UID = getArguments().getString(UserData.KEY_UID);
            username = getArguments().getString(UserData.KEY_USERNAME);
            isShowsDialog = getArguments().getBoolean(ARG_IS_SHOWS_DIALOG);
        }

        allEventsAmount = new MutableLiveData<>(0L);
        attendingEventsAmount = new MutableLiveData<>(0L);

        setShowsDialog(isShowsDialog);
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

    ProgressBar progressBar;
    TextView tv_relative_attendance;
    TextView tv_there_are_no_events;
    TextView tv_there_are_no_attending_events;
    RecyclerView rv_events;
    SwitchMaterial switch_only_attend;
    EventsAdapterForFirebase eventsAdapter;

    MutableLiveData<Long> allEventsAmount;
    MutableLiveData<Long> attendingEventsAmount;

    DatabaseReference allEvents;
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

        eventsAdapter = new EventsAdapterForFirebase(options, FirebaseUtils.getCurrentUID(), requireContext(), this);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(requireContext());
        layoutManager.setOnLayoutCompleteListener(() -> new Handler().postDelayed(this::stopRVEventsShimmer, 500));

        rv_events.setLayoutManager(layoutManager);
    }

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
    public void onEventClick(int position, @NonNull CalendarEvent event) {
        FragmentManager fm = getParentFragmentManager();

        if (fm.findFragmentByTag(EventInfoDialogFragment.TAG) == null){
            EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(event, true);
            event_info_dialogFragment.show(fm, EventInfoDialogFragment.TAG);
        }
    }

    public void startRVEventsShimmer() {
        rv_events.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        tv_there_are_no_events.setVisibility(View.GONE);
        tv_there_are_no_attending_events.setVisibility(View.GONE);
    }

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

    public void getUserRelativeAttendance(){
        long count = attendingEventsAmount.getValue();
        long all = allEventsAmount.getValue();
        double attendance = (double) count/all;

        if (FirebaseUtils.isCurrentUID(UID)){
            tv_relative_attendance.setText("Your relative attendance: " + (int) (attendance*100) + "%");
        }
        else {
            tv_relative_attendance.setText(username + "'s relative attendance: " + (int) (attendance*100) + "%");
        }

        tv_relative_attendance.setTextColor(ColorUtils.blendARGB(Color.RED, Color.GREEN, (float) attendance));
    }
}