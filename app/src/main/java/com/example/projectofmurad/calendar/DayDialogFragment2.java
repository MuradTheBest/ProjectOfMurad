package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayDialogFragment2 extends Dialog implements AdapterForFirebase2.OnEventListener{
    LocalDate passingDate;
    Context context;

    ArrayList<CalendarEventWithTextOnly> calendarEventArrayList;

    private RecyclerView rv_events;
    private AdapterForFirebase2 adapterForFirebase;

    FirebaseDatabase firebase;
    DatabaseReference databaseReference;

    public DayDialogFragment2(@NonNull Context context, LocalDate passingDate) {
        super(context);
        this.passingDate = passingDate;
        this.context = context;
    }

    /*public DayDialogFragment(@NonNull Context context, LocalDate passingDate, int themeResId) {
        super(context, themeResId);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.day_dialog_with_recyclerview);
        this.setCancelable(true);

        String day = String.valueOf(passingDate.getDayOfMonth());
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy"));

        TextView tv_day = this.findViewById(R.id.tv_day);
        tv_day.setText(day);

        TextView tv_full_date = this.findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        rv_events = this.findViewById(R.id.rv_events);
        rv_events.setVisibility(View.INVISIBLE);

        TextView tv_no_events = this.findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference("EventsDatabase");

        calendarEventArrayList = new ArrayList<>();

        databaseReference = databaseReference.child(Utils.DateToTextForFirebase(passingDate));
        Query query = databaseReference.orderByChild("timestamp");

        FirebaseRecyclerOptions<CalendarEventWithTextOnly> options
                = new FirebaseRecyclerOptions.Builder<CalendarEventWithTextOnly>()
                .setQuery(query, CalendarEventWithTextOnly.class)
                .setLifecycleOwner((LifecycleOwner) this.getOwnerActivity())
                .build();


        adapterForFirebase = new AdapterForFirebase2(options, passingDate, DayDialogFragment2.this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);
        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());
        rv_events.setLayoutManager(new LinearLayoutManager(context));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    rv_events.setVisibility(View.VISIBLE);
                }
                else {
//                  rv_events.setVisibility(View.INVISIBLE);
                    tv_no_events.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FloatingActionButton fab_add_event = this.findViewById(R.id.fab_add_event);
        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createAddEventDialog(dayText, selectedDate);
                Intent toAddEvent_Screen = new Intent(context, Add_Event_Screen.class);

                DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
                Log.d("murad","passingDate " + passingDate.format(simpleDateFormat));

                int day = passingDate.getDayOfMonth();
                int month = passingDate.getMonth().getValue();
                int year = passingDate.getYear();
                toAddEvent_Screen.putExtra("day", day);
                toAddEvent_Screen.putExtra("month", month);
                toAddEvent_Screen.putExtra("year", year);


                context.startActivity(toAddEvent_Screen);
                dismiss();
            }
        });
    }


    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    protected void onStart() {
        super.onStart();
        adapterForFirebase.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    protected void onStop() {
        super.onStop();
        adapterForFirebase.stopListening();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        adapterForFirebase.stopListening();
    }

    @Override
    public void onEventClick(int position, CalendarEventWithTextOnly calendarEventWithTextOnly) {
        this.dismiss();
        String key = calendarEventWithTextOnly.getEvent_id();

        int timestamp = calendarEventWithTextOnly.getTimestamp();
        String name = calendarEventWithTextOnly.getName();
        String description = calendarEventWithTextOnly. getDescription();
        String place = calendarEventWithTextOnly.getPlace();
        String start_date = calendarEventWithTextOnly.getStart_date();
        String start_time = calendarEventWithTextOnly.getStart_time();
        String end_date = calendarEventWithTextOnly.getEnd_date();
        String end_time = calendarEventWithTextOnly.getEnd_time();

        Intent intent = new Intent(context, Edit_Event_Screen.class);

        intent.putExtra("event_key", key);
        intent.putExtra("event_name", name);
        intent.putExtra("event_description", description);
        intent.putExtra("event_place", place);
        intent.putExtra("event_start_date", start_date);
        intent.putExtra("event_start_time", start_time);
        intent.putExtra("event_end_date", end_date);
        intent.putExtra("event_end_time", end_time);

        getContext().startActivity(intent);
    }
}
