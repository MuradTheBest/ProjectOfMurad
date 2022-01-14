package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils_Calendar;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayDialogFragment extends Dialog {
    LocalDate passingDate;
    Context context;

    private ListView lv_events;
    private FirebaseListAdapter adapter;
    ArrayList<CalendarEventWithTextOnly> calendarEventArrayList;

    private FirebaseDatabase firebase;
    private DatabaseReference databaseReference;

    public DayDialogFragment(@NonNull Context context, LocalDate passingDate) {
        super(context);
        this.passingDate = passingDate;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.day_dialog);
        this.setCancelable(true);

        String day = String.valueOf(passingDate.getDayOfMonth());
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy"));

        TextView tv_day = this.findViewById(R.id.tv_day);
        tv_day.setText(day);

        TextView tv_full_date = this.findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        TextView tv_no_events = this.findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        lv_events = this.findViewById(R.id.lv_events);


        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference("EventsDatabase");

        calendarEventArrayList = new ArrayList<>();



        databaseReference = databaseReference.child(Utils_Calendar.DateToTextForFirebase(passingDate));

        Query query = databaseReference.orderByChild("timestamp");


        FirebaseListOptions<CalendarEventWithTextOnly> options = new FirebaseListOptions.Builder<CalendarEventWithTextOnly>()
                .setLayout(R.layout.event_info)
                .setQuery(query, CalendarEventWithTextOnly.class)
                .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                TextView tv_event_name = v.findViewById(R.id.tv_event_name);
                TextView tv_event_place = v.findViewById(R.id.tv_event_place);
                TextView tv_event_description = v.findViewById(R.id.tv_event_description);
                TextView tv_event_start_time = v.findViewById(R.id.tv_event_start_time);
                TextView tv_hyphen = v.findViewById(R.id.tv_hyphen);
                TextView tv_event_end_time = v.findViewById(R.id.tv_event_end_time);

                Log.d("murad", "RECYCLING STARTED");

                CalendarEventWithTextOnly event = (CalendarEventWithTextOnly) model;

                tv_event_name.setText(event.getName());
                Log.d("murad","name: " + event.getName());

                tv_event_place.setText(event.getPlace());
                Log.d("murad","place: " +  event.getPlace());

                tv_event_description.setText(event.getDescription());
                Log.d("murad", "description " + event.getDescription());

                if(event.getStart_date().equals(event.getEnd_date())){
                    tv_event_start_time.setText(event.getStart_time());
                    Log.d("murad","Starting date: " +  event.getStart_time());

                    tv_event_end_time.setText(event.getEnd_time());
                    Log.d("murad","Ending date: " + event.getEnd_time());

                }
                else if(event.getStart_date().equals(Utils_Calendar.DateToText(passingDate))){
                    tv_event_start_time.setText(event.getStart_time());
                    Log.d("murad","Starting date: " + event.getStart_time());

                }
                else if(event.getEnd_date().equals(Utils_Calendar.DateToText(passingDate))){
                    tv_event_end_time.setText(event.getEnd_time());
                    Log.d("murad","Ending date: " + event.getEnd_time());

                }
                else{
                    tv_hyphen.setText("All day");
                }

                //this.calendarEventArrayList.add(event);
            }
        };

        lv_events.setAdapter(adapter);
        lv_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CalendarEventWithTextOnly event = options.getSnapshots().get(i);
                String key = event.getEvent_id();

                int timestamp = event.getTimestamp();
                String name = event.getName();
                String description = event. getDescription();
                String place = event.getPlace();
                String start_date = event.getStart_date();
                String start_time = event.getEnd_time();
                String end_date = event.getEnd_date();
                String end_time = event.getEnd_time();

                Intent intent = new Intent(getContext(), Edit_Event_Screen.class);

                intent.putExtra("event_key", key);
                intent.putExtra("event_name", name);
                intent.putExtra("event_description", description);
                intent.putExtra("event_place", place);
                intent.putExtra("event_start_date", start_date);
                intent.putExtra("event_start_time", start_time);
                intent.putExtra("event_end_date", end_date);
                intent.putExtra("event_end_time", end_time);
            }
        });


        /*if(calendarEventArrayList.isEmpty()){
            lv_events.setVisibility(View.INVISIBLE);
            tv_no_events.setVisibility(View.VISIBLE);
            Log.d("murad", "not containsKey");
        }
        else {

            tv_no_events.setVisibility(View.INVISIBLE);
            Log.d("murad", "empty calendarEventArrayList " + calendarEventArrayList.isEmpty());
            *//*calendarEventAdapter = new CalendarEventAdapter(calendarEventArrayList, getApplicationContext());
            rv_events.setAdapter(calendarEventAdapter);*//*


            lv_events.setVisibility(View.VISIBLE);

            Log.d("murad", "containsKey");
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
}
