package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class AllUserAttendancesScreen extends AppCompatActivity implements EventsAdapterForFirebase.OnEventClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_attendancies);

        String selected_UID = getIntent().getStringExtra(UserData.KEY_UID);

        Query allEventsDatabase = FirebaseUtils.getAllEventsDatabase().orderByChild(CalendarEvent.KEY_EVENT_START);

        RecyclerView rv_events = findViewById(R.id.rv_events);

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(allEventsDatabase, CalendarEvent.class)
                .setLifecycleOwner(this)
                .build();

        EventsAdapterForFirebase adapterForFirebase = new EventsAdapterForFirebase(options, selected_UID, this,this);

        rv_events.setAdapter(adapterForFirebase);
        rv_events.setLayoutManager(new LinearLayoutManagerWrapper(this));
    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEvent) {
        Intent intent = new Intent(this, AddOrEditEventScreen.class);
        intent.putExtra(CalendarEvent.KEY_EVENT, calendarEvent);
        startActivity(intent);
    }

}