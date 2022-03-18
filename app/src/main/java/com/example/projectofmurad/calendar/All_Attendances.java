package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class All_Attendances extends AppCompatActivity  implements
        EventsAdapterForFirebase.OnEventListener {

    private ArrayList<CalendarEvent> calendarEventArrayList;

    private RecyclerView rv_events;
    private EventsAdapterForFirebase adapterForFirebase;

    private FirebaseDatabase firebase;
    private DatabaseReference allEventsDatabase;

    private Intent gotten_intent;

    private String event_private_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_attendancies);

        gotten_intent = getIntent();
        String selected_UID = gotten_intent.getStringExtra("selected_UID");
        event_private_id = gotten_intent.getStringExtra("event_private_id");

        allEventsDatabase = FirebaseUtils.allEventsDatabase;

        calendarEventArrayList = new ArrayList<>();

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

        rv_events = findViewById(R.id.rv_events);

        /*if (query == null){
            Toast.makeText(this, "Query is null", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Toast is not null", Toast.LENGTH_SHORT).show();
        }*/

        Toast.makeText(this, "Query is " + (query == null ? "" : "not ") + "null", Toast.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(query, CalendarEvent.class)
                .setLifecycleOwner(this)
                .build();


        adapterForFirebase = new EventsAdapterForFirebase(options, selected_UID, this, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);
        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());
        rv_events.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEventWithTextOnly) {
        String chain_key = calendarEventWithTextOnly.getChainId();
        String private_key = calendarEventWithTextOnly.getPrivateId();

        int timestamp = calendarEventWithTextOnly.getTimestamp();
        String name = calendarEventWithTextOnly.getName();
        String description = calendarEventWithTextOnly. getDescription();
        String place = calendarEventWithTextOnly.getPlace();
        int color = calendarEventWithTextOnly.getColor();
        String start_date = calendarEventWithTextOnly.getStartDate();
        String start_time = calendarEventWithTextOnly.getStartTime();
        String end_date = calendarEventWithTextOnly.getEndDate();
        String end_time = calendarEventWithTextOnly.getEndTime();

        Intent intent = new Intent(this, Edit_Event_Screen.class);

        intent.putExtra("event_chain_key", chain_key);
        intent.putExtra("event_private_key", private_key);

        intent.putExtra("event_name", name);
        intent.putExtra("event_description", description);
        intent.putExtra("event_place", place);
        intent.putExtra("event_color", color);
        intent.putExtra("event_start_date", start_date);
        intent.putExtra("event_start_time", start_time);
        intent.putExtra("event_end_date", end_date);
        intent.putExtra("event_end_time", end_time);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        Intent intent = new Intent(this, Event_Attendance_Screen.class);
        intent.putExtra("event_private_id", event_private_id);

        startActivity(intent);
    }

}