package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayDialogFragmentWithRecyclerView extends Dialog {
    LocalDate passingDate;
    Context context;

    ArrayList<CalendarEvent> calendarEventArrayList;

    private RecyclerView rv_events;
    private AdapterForFirebase adapterForFirebase;

    private FirebaseDatabase firebase;
    private DatabaseReference databaseReference;

    public DayDialogFragmentWithRecyclerView(@NonNull Context context, LocalDate passingDate) {
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

        TextView tv_no_events = this.findViewById(R.id.tv_no_events);
        //rv_events = d.findViewById(R.id.rv_events);


       /* if(Utils_Calendar.map.containsKey(passingDate)){
            tv_no_events.setVisibility(View.INVISIBLE);

            calendarEventArrayList = Utils_Calendar.map.get(passingDate);
            Log.d("murad", "empty calendarEventArrayList " + calendarEventArrayList.isEmpty());
            *//*calendarEventAdapter = new CalendarEventAdapter(calendarEventArrayList, getApplicationContext());
            rv_events.setAdapter(calendarEventAdapter);*//*

            adapter = new DayAdapter(calendarEventArrayList, context, passingDate);
            rv_events.setAdapter(adapter);
            rv_events.setVisibility(View.VISIBLE);

            Log.d("murad", "containsKey");
        }
        else{
            //rv_events.setVisibility(View.INVISIBLE);
            lv_events.setVisibility(View.INVISIBLE);
            tv_no_events.setVisibility(View.VISIBLE);
            Log.d("murad", "not containsKey");
        }*/

        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference("EventsDatabase");

        /*if(databaseReference.child(Utils_Calendar.DateToTextForFirebase(passingDate)).get() != null){

        }*/

        calendarEventArrayList = new ArrayList<>();

        rv_events = this.findViewById(R.id.rv_events);

        databaseReference = databaseReference.child(Utils_Calendar.DateToTextForFirebase(passingDate));

        if(databaseReference != null){

        }
          tv_no_events.setVisibility(View.GONE);

        FirebaseRecyclerOptions<CalendarEvent> options
                = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(databaseReference, CalendarEvent.class)
                .build();

        adapterForFirebase = new AdapterForFirebase(options, passingDate);
        adapterForFirebase.startListening();
        rv_events.setAdapter(adapterForFirebase);
        rv_events.setLayoutManager(new LinearLayoutManager(this.getContext()));




/*
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    if(data.getChildrenCount() == 7){
                        Log.d("firebase","snapshot's children = " + snapshot.getChildrenCount());
                        Log.d("firebase","data's children = " + data.getChildrenCount());

                        String name = data.child("Name").getValue().toString();
                        String description = data.child("Place").getValue().toString();
                        String place = data.child("Description").getValue().toString();

                        LocalDate start_date = Utils_Calendar.TextToDateForFirebase(data.child("Starting Date").getValue().toString());
                        LocalTime start_time = Utils_Calendar.TextToTime(data.child("Starting Time").getValue().toString());

                        LocalDate end_date = Utils_Calendar.TextToDateForFirebase(data.child("Ending Date").getValue().toString());
                        LocalTime end_time = Utils_Calendar.TextToTime(data.child("Ending Time").getValue().toString());

                        CalendarEvent tmp = new CalendarEvent(name, description, place, start_date, start_time, end_date, end_time);
                        Log.d("murad", tmp.toString());

                        calendarEventArrayList.add(tmp);
                    }
                }

                Log.d("murad", "isEmpty = " + calendarEventArrayList.isEmpty());
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("murad", "failed");
            }
        });
*/

        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                    if(data.getChildrenCount() == 7){
                        Log.d("firebase","snapshot's children = " + snapshot.getChildrenCount());
                        Log.d("firebase","data's children = " + data.getChildrenCount());

                        String name = data.child("Name").getValue().toString();
                        String description = data.child("Place").getValue().toString();
                        String place = data.child("Description").getValue().toString();

                        LocalDate start_date = Utils_Calendar.TextToDateForFirebase(data.child("Starting Date").getValue().toString());
                        LocalTime start_time = Utils_Calendar.TextToTime(data.child("Starting Time").getValue().toString());

                        LocalDate end_date = Utils_Calendar.TextToDateForFirebase(data.child("Ending Date").getValue().toString());
                        LocalTime end_time = Utils_Calendar.TextToTime(data.child("Ending Time").getValue().toString());

                        CalendarEvent tmp = new CalendarEvent(name, description, place, start_date, start_time, end_date, end_time);
                        Log.d("murad", tmp.toString());

                        calendarEventArrayList.add(tmp);
                        adapter.notifyDataSetChanged();
                    }
                }

                Log.d("murad", "isEmpty = " + calendarEventArrayList.isEmpty());
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("murad", "failed");
            }
        });*/

        /*if(calendarEventArrayList.isEmpty()){
            rv_events.setVisibility(View.INVISIBLE);
            tv_no_events.setVisibility(View.VISIBLE);
            Log.d("murad", "not containsKey");
        }
        else {

            tv_no_events.setVisibility(View.INVISIBLE);
            Log.d("murad", "empty calendarEventArrayList " + calendarEventArrayList.isEmpty());
            *//*calendarEventAdapter = new CalendarEventAdapter(calendarEventArrayList, getApplicationContext());
            rv_events.setAdapter(calendarEventAdapter);*//*


            rv_events.setVisibility(View.VISIBLE);

            Log.d("murad", "containsKey");
        }*/

        /*if(Utils_Calendar.map.isEmpty()){
            Log.d("murad", "isEmpty");
        }*/

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

    //returns ArrayList of CalendarEvents for this date
   /* private ArrayList<CalendarEvent> ifSelectedDateHasEvents(LocalDate passingDate) {

        return events;
    }*/


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
}
