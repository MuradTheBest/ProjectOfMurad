package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class All_Attendances extends AppCompatActivity implements EventsAdapterForFirebase.OnEventClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_attendancies);

        Intent gotten_intent = getIntent();

        String selected_UID = gotten_intent.getStringExtra("selected_UID");

        Query query = FirebaseUtils.allEventsDatabase.orderByChild("start");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Log.d("snapshot", "===============================================================");
                    Log.d("snapshot", data.getKey());
                    Log.d("snapshot", "=====================================================================");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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