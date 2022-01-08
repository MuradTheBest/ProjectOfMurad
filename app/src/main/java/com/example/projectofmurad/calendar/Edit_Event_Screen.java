package com.example.projectofmurad.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Edit_Event_Screen extends AppCompatActivity {
    private EditText et_name;
    private EditText et_place;
    private EditText et_description;

    private Button btn_choose_start_time;
    private Button btn_choose_start_date;
    private Button btn_choose_end_time;
    private Button btn_choose_end_date;

    private Button btn_add_event;

    private String key;

    private String name;
    private String description;
    private String place;

    private LocalDate startDate;
    private LocalDate old_start_date;

    private int start_day;
    private int start_month;
    private int start_year;

    private LocalTime startTime;

    private int start_hour;
    private int start_min;

    private LocalDate endDate;
    private LocalDate old_end_date;

    private int end_day;
    private int end_month;
    private int end_year;

    private LocalTime endTime;

    private int end_hour;
    private int end_min;

    private LocalDate selectedDate;

    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;
    private DatabaseReference eventsDatabaseForText;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;


    Intent gotten_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        gotten_intent = getIntent();

        key = gotten_intent.getStringExtra("event_key");

        name = gotten_intent.getStringExtra("event_name");
        description = gotten_intent.getStringExtra("event_description");
        place = gotten_intent.getStringExtra("event_place");

        startDate = Utils.TextToDate(gotten_intent.getStringExtra("event_start_date"));
        endDate = Utils.TextToDate(gotten_intent.getStringExtra("event_end_date"));

        old_start_date = Utils.TextToDate(gotten_intent.getStringExtra("event_start_date"));
        old_end_date = Utils.TextToDate(gotten_intent.getStringExtra("event_end_date"));

        startTime = Utils.TextToTime(gotten_intent.getStringExtra("event_start_time"));
        endTime = Utils.TextToTime(gotten_intent.getStringExtra("event_end_time"));


        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = firebase.getReference("EventsDatabase");

        start_day = startDate.getDayOfMonth();
        start_month = startDate.getMonthValue();
        start_year = startDate.getYear();

        end_day = endDate.getDayOfMonth();
        end_month = endDate.getMonthValue();
        end_year = endDate.getYear();

        start_hour = startTime.getHour();
        start_min = startTime.getMinute();

        end_hour = endTime.getHour();
        end_min = endTime.getMinute();

        //localDate = getIntent().getParcelableExtra("day_date");

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);

        et_name.setText(name);
        et_description.setText(description);
        et_place.setText(place);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils.DateToText(startDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new SetDate("start"), start_year, start_month, start_day);
                startDatePickerDialog.updateDate(start_year, start_month, start_day);
//                startDatePickerDialog.getDatePicker().setMinDate(LocalDate.now().);
                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils.DateToText(endDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this, android.R.style.ThemeOverlay_Material_Dialog, new SetDate("end"), end_year, end_month, end_day);
                endDatePickerDialog.updateDate(end_year, end_month, end_day);
                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setText(Utils.TimeToText(startTime));
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                startTimePickerDialog = new TimePickerDialog(Edit_Event_Screen.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        new SetTime("start"), 0, 0, true);

                startTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                startTimePickerDialog.updateTime(start_hour, start_min);
                startTimePickerDialog.show();
            }
        });

        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);
        btn_choose_end_time.setText(Utils.TimeToText(endTime));
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                endTimePickerDialog = new TimePickerDialog(Edit_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        new SetTime("end"), 0, 0, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
            }
        });

        btn_add_event = findViewById(R.id.btn_add_event);
        btn_add_event.setOnClickListener(view -> {
            name = "";
            description = "";
            place = "";

            String msg = "";

            boolean editTextsFilled = true;

            if(et_name.getText().toString().isEmpty()){
                msg += "name";
                editTextsFilled = false;
            }
            else {
                name = et_name.getText().toString();
            }
            if(et_description.getText().toString().isEmpty()){
                if(!editTextsFilled){
                    msg += ", ";
                }
                msg += "description";
                editTextsFilled = false;
            }
            else {
                description = et_description.getText().toString();
            }
            if(et_place.getText().toString().isEmpty()){
            /*if(!editTextsFilled){
                msg += ", ";
            }*/
                msg += (editTextsFilled ? "" : ", ");
                msg += "place";
                editTextsFilled = false;
            }
            else {
                place = et_place.getText().toString();
            }

            //boolean error = true;
            if(editTextsFilled){
                /*if(start_day <= end_day && start_month <= end_month && start_year <= end_year ){

                    if(start_hour == end_hour && start_min <= end_min || start_hour < end_hour){
                        Toast.makeText(this, "Event was successfully added " +
                                        "\n NAME: " + name +
                                        "\n DESCRIPTION: " + description +
                                        "\n PLACE: " + place +
                                        "\n STARTS AT " + start_hour + " : " + start_min + " on " + start_day + "." + start_month + "." + start_year +
                                        "\n ENDS AT " + end_hour + " : " + end_min + " on " + end_day + "." + end_month + "." + end_year,
                                Toast.LENGTH_SHORT).show();
                        error = false;
                    }
                    //CalendarEvent calendarEvent = new CalendarEvent(selectedDate, name, place, description, start_hour, start_min, end_hour, end_min);
                }
                //TODO check all the possibilities and write if/else conditions

                if(error)
                    Toast.makeText(this, "Ups... Something is wrong", Toast.LENGTH_LONG).show();*/
                Toast.makeText(this, "Event was successfully added " +
                                "\n NAME: " + name +
                                "\n DESCRIPTION: " + description +
                                "\n PLACE: " + place +
                                "\n STARTS AT " + start_hour + " : " + start_min + " on " + start_day + "." + start_month + "." + start_year +
                                "\n ENDS AT " + end_hour + " : " + end_min + " on " + end_day + "." + end_month + "." + end_year,
                        Toast.LENGTH_SHORT).show();

                LocalDate start_date = LocalDate.of(start_year, start_month, start_day);
                LocalDate end_date = LocalDate.of(end_year, end_month, end_day);

                LocalTime start_time = LocalTime.of(start_hour, start_min);
                LocalTime end_time = LocalTime.of(end_hour, end_min);

                LocalDateTime startDateTime = LocalDateTime.of(start_date, start_time);
                LocalDateTime endDateTime = LocalDateTime.of(end_date, end_time);


                CalendarEventWithTextOnly eventWithTextOnly = new CalendarEventWithTextOnly(name, description, place, Utils.DateToText(start_date), Utils.TimeToText(start_time), Utils.DateToText(end_date), Utils.TimeToText(end_time));

                //addEventToFirebase(event);
                //addEventToFirebaseForText(eventWithTextOnly);
                editEvent(eventWithTextOnly);
                //addEventToFirebaseForAdvanced(eventAdvanced);

                startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));

            }
            else {
                Toast.makeText(this, "Please enter event's " + msg, Toast.LENGTH_LONG).show();
            }

        });


    }

    //sets time
    public class SetTime implements TimePickerDialog.OnTimeSetListener{
        private String time_start_or_end;

        public SetTime(String time_start_or_end){
            this.time_start_or_end = time_start_or_end;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfDay) {
            //Initialize hour and minute

            LocalTime time = LocalTime.of(hourOfDay, minuteOfDay);
            int u = time.toSecondOfDay();
            LocalTime f = LocalTime.ofSecondOfDay(u);

            String time_text = Utils.TimeToText(time);

            /*if(hour < 10){
                time += "0";
            }
            time += hour + ":";
            if(min < 10){
                time += "0";
            }
            time += min;*/

            switch(time_start_or_end) {
                case "start":
                    btn_choose_start_time.setText(time_text);

                    start_hour = hourOfDay;
                    start_min = minuteOfDay;

                    break;
                case "end":
                    btn_choose_end_time.setText(time_text);

                    end_hour = hourOfDay;
                    end_min = minuteOfDay;

                    break;
            }
        }
    }

    //sets date
    public class SetDate implements DatePickerDialog.OnDateSetListener {
        private String date_start_or_end;

        public SetDate(String date_start_or_end){
            this.date_start_or_end = date_start_or_end;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            LocalDate date = LocalDate.of(year, month, day);
            String date_text = Utils.DateToText(date);

            switch(date_start_or_end) {
                case "start":
                    btn_choose_start_date.setText(date_text);

                    start_day = day;
                    start_month = month;
                    start_year = year;

                    break;
                case "end":
                    btn_choose_end_date.setText(date_text);

                    end_day = day;
                    end_month = month;
                    end_year = year;

                    break;
            }
        }
    }

    public void editEventToFirebaseForTextWithPUSH(CalendarEventWithTextOnly event) {
        String start_date = event.getStart_date();
        String end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");


        LocalDate startDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(start_date));
        LocalDate endDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(end_date));

        LocalTime startTime = Utils.TextToTime(event.getStart_time());

        LocalTime endTime = Utils.TextToTime(event.getEnd_time());

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);



        eventsDatabaseForText = eventsDatabaseForText.child(Utils.TextToTextForFirebase(start_date));
        eventsDatabaseForText = eventsDatabaseForText.push();
        String key = eventsDatabaseForText.getKey();

        Log.d("murad", eventsDatabaseForText.getKey());

        Map<String, CalendarEventWithTextOnly> map = new HashMap<>();

        LocalDate tmp = Utils.TextToDate(start_date);
        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            event.setEvent_id(key);

            map.put(Utils.DateToTextForFirebase(tmp), event);

            eventsDatabaseForText = eventsDatabaseForText.child(key);
            eventsDatabaseForText.setValue(event);
            event.setTimestamp(0);

            tmp = tmp.plusDays(1);

        }
        while(!tmp.isEqual(Utils.TextToDate(end_date).plusDays(1)));

    }

    public void editEvent(CalendarEventWithTextOnly event){
        String start_date = event.getStart_date();
        String end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");

        LocalDate startDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(start_date));
        LocalDate endDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(end_date));

        LocalTime startTime = Utils.TextToTime(event.getStart_time());
        int timestamp = startTime.toSecondOfDay();

        LocalTime endTime = Utils.TextToTime(event.getEnd_time());

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        LocalDate tmp = old_start_date;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(old_start_date));
        Log.d("murad", eventsDatabaseForText.getKey());


        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(key).removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(old_end_date.plusDays(1)));

        tmp = Utils.TextToDate(start_date);

        eventsDatabaseForText = eventsDatabaseForText.child(Utils.TextToTextForFirebase(start_date));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(key).setValue(event);

            event.setTimestamp(0);

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(Utils.TextToDate(end_date).plusDays(1)));
    }

    public void deleteEvent(CalendarEventWithTextOnly event){
        String start_date = event.getStart_date();
        String end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");

        LocalDate startDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(start_date));
        LocalDate endDate = Utils.TextToDateForFirebase(Utils.TextToTextForFirebase(end_date));

        LocalTime startTime = Utils.TextToTime(event.getStart_time());

        LocalTime endTime = Utils.TextToTime(event.getEnd_time());

        LocalDate tmp = Utils.TextToDate(start_date);

        eventsDatabaseForText = eventsDatabaseForText.child(Utils.TextToTextForFirebase(start_date));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(key).removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(Utils.TextToDate(end_date).plusDays(1)));
    }
}