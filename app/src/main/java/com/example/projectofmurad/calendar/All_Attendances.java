package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.MyActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class All_Attendances extends MyActivity implements EventsAdapterForFirebase.OnEventClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_attendancies);

        Intent gotten_intent = getIntent();

        String selected_UID = gotten_intent.getStringExtra("selected_UID");

        Query query = FirebaseUtils.getAllEventsDatabase().orderByChild("start");

        RecyclerView rv_events = findViewById(R.id.rv_events);

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(query, CalendarEvent.class)
                .setLifecycleOwner(this)
                .build();

        EventsAdapterForFirebase adapterForFirebase = new EventsAdapterForFirebase(options,
                selected_UID, this, this);

        rv_events.setAdapter(adapterForFirebase);
        rv_events.setLayoutManager(new LinearLayoutManagerWrapper(this));
    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEvent) {
        Intent intent = new Intent(this, Edit_Event_Screen.class);
        intent.putExtra(UtilsCalendar.KEY_EVENT, calendarEvent);

        startActivity(intent);
    }

}