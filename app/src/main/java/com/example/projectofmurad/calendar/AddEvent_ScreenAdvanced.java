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
import com.example.projectofmurad.Utils_Calendar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddEvent_ScreenAdvanced extends AppCompatActivity {
    private EditText et_name;
    private EditText et_place;
    private EditText et_description;

    private Button btn_choose_start_time;
    private Button btn_choose_start_date;
    private Button btn_choose_end_time;
    private Button btn_choose_end_date;

    private Button btn_add_event;

    private int start_hour;
    private int start_min;

    private int end_hour;
    private int end_min;

    private int start_day;
    private int start_month;
    private int start_year;

    private int end_day;
    private int end_month;
    private int end_year;

    private int selected_day = 0;
    private String selected_dayOfWeek;
    private int selected_month = 0;
    private int selected_year = 0;

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

    private static int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = firebase.getReference("EventsDatabase");

        Intent gotten_intent = getIntent();
        selected_day = gotten_intent.getIntExtra("day", 0);
        selected_dayOfWeek = gotten_intent.getStringExtra("dayOfWeek");
        selected_month = gotten_intent.getIntExtra("month",0);
        selected_year = gotten_intent.getIntExtra("year", 0);

        selectedDate = LocalDate.of(selected_year, selected_month, selected_day);

        start_day = end_day = selected_day;
        start_month = end_month = selected_month;
        start_year = end_year = selected_year;

        start_hour = LocalTime.now().getHour();
        start_min = LocalTime.now().getMinute();
        end_hour = LocalTime.now().getHour();
        end_min = LocalTime.now().getMinute();

        Log.d("murad","Receiving selectedDate " + selected_day + " " + selected_month + " " + selected_year);

        //localDate = getIntent().getParcelableExtra("day_date");

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils_Calendar.DateToText(selectedDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(AddEvent_ScreenAdvanced.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new SetDate("start"), selected_year, selected_month, selected_day);
                startDatePickerDialog.updateDate(start_year, start_month, start_day);
                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils_Calendar.DateToText(selectedDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePickerDialog = new DatePickerDialog(AddEvent_ScreenAdvanced.this, android.R.style.ThemeOverlay_Material_Dialog, new SetDate("end"), selected_year, selected_month, selected_day);
                endDatePickerDialog.updateDate(end_year, end_month, end_day);
                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                startTimePickerDialog = new TimePickerDialog(AddEvent_ScreenAdvanced.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        new SetTime("start"), 0, 0, true);

                startTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                startTimePickerDialog.updateTime(start_hour, start_min);
                startTimePickerDialog.show();
            }
        });

        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                endTimePickerDialog = new TimePickerDialog(AddEvent_ScreenAdvanced.this,
                        android.R.style.ThemeOverlay_Material_ActionBar,
                        new SetTime("end"), 0, 0, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
            }
        });

        btn_add_event = findViewById(R.id.btn_add_event);
        btn_add_event.setOnClickListener(view -> {
            String name = "";
            String description = "";
            String place = "";

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

                CalendarEvent event;

                if(start_date.equals(end_date)){
                    event = new CalendarEvent(name, description, place, start_date, start_hour, start_min, end_hour, end_min);
                }
                else {
                    event = new CalendarEvent(name, description, place, startDateTime, endDateTime);
                }

                CalendarEventWithTextOnly eventWithTextOnly = new CalendarEventWithTextOnly(name, description, place, Utils_Calendar.DateToText(start_date), Utils_Calendar.TimeToText(start_time), Utils_Calendar.DateToText(end_date), Utils_Calendar.TimeToText(end_time));

                Log.d("murad", event.toString());

                Utils_Calendar.addEvent(event);
                //addEventToFirebase(event);
                //addEventToFirebaseForText(eventWithTextOnly);
                addEventToFirebaseForTextWithAddingChild(eventWithTextOnly);
                //addEventToFirebaseForAdvanced(eventAdvanced);

                startActivity(new Intent(AddEvent_ScreenAdvanced.this, Calendar_Screen.class));

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

            String time_text = Utils_Calendar.TimeToText(time);

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
            String date_text = Utils_Calendar.DateToText(date);

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

    public void addEventToFirebase(CalendarEvent event){
        LocalDate start_date = event.getStart_date();
        LocalDate end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }

/*        eventsDatabase.child(""+Utils_Calendar.getDefaultDateForFirebase(start_date)).child(""+event.getEvent_id()).push();
        eventsDatabase = eventsDatabase.child(""+Utils_Calendar.getDefaultDateForFirebase(start_date)).child(""+event.getEvent_id());
        //eventsDatabase.setValue(event.getClass());

        eventsDatabase.child("Name").setValue(event.getName());
        eventsDatabase.child("Place").setValue(event.getPlace());
        eventsDatabase.child("Description").setValue(event.getDescription());

        eventsDatabase.child("Starting Date").setValue(Utils_Calendar.getDefaultDateForFirebase(event.getStart_date()));
        eventsDatabase.child("Starting Time").setValue(Utils_Calendar.getDefaultTime(event.getStart_time()));

        eventsDatabase.child("Ending Date").setValue(Utils_Calendar.getDefaultDateForFirebase(event.getEnd_date()));
        eventsDatabase.child("Ending Time").setValue(Utils_Calendar.getDefaultTime(event.getEnd_time()));*/

        LocalDate tmp = start_date;

        do {
            eventsDatabase = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = firebase.getReference("EventsDatabase");

            //eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).push();
            //eventsDatabase = eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).child("");

            eventsDatabase.child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).child(""+event.getEvent_id()).push();
            eventsDatabase = eventsDatabase.child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).child(""+event.getEvent_id());

            eventsDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //eventsDatabase.setValue(event.getClass());

            eventsDatabase.child("Name").setValue(event.getName());
            eventsDatabase.child("Place").setValue(event.getPlace());
            eventsDatabase.child("Description").setValue(event.getDescription());

            eventsDatabase.child("Starting Date").setValue(Utils_Calendar.DateToTextForFirebase(event.getStart_date()));
            eventsDatabase.child("Starting Time").setValue(Utils_Calendar.TimeToText(event.getStart_time()));

            eventsDatabase.child("Absolute Starting Time").setValue(String.valueOf(event.getStart_time().toSecondOfDay()));

            eventsDatabase.child("Ending Date").setValue(Utils_Calendar.DateToTextForFirebase(event.getEnd_date()));
            eventsDatabase.child("Ending Time").setValue(Utils_Calendar.TimeToText(event.getEnd_time()));

            //fix this
            Query query = firebase.getReference("EventsDatabase").child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).orderByChild("Absolute Starting Time").startAt(0);
//            Query q = firebase.getReference("EventsDatabase").child(""+Utils_Calendar.DateToTextForFirebase(tmp)).orderByChild("Starting Time");
//            Query q2 = firebase.getReference("EventsDatabase").child(""+Utils_Calendar.DateToTextForFirebase(tmp)).orderByKey();
            query.addChildEventListener(new ChildEventListener() {
                // TODO: implement the ChildEventListener methods as documented above
                // [START_EXCLUDE]
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
                // [END_EXCLUDE]
            });
            // [END rtdb_order_by_nested]


            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

    public void addEventToFirebaseForText(CalendarEventWithTextOnly event){
        String start_date = event.getStart_date();
        String end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }


        LocalDate tmp = Utils_Calendar.TextToDate(start_date);

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            count++;
            //eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).push();
            //eventsDatabase = eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).child("");

            eventsDatabaseForText.child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).child(""+count).push();
            eventsDatabaseForText = eventsDatabaseForText.child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).child(""+count);

            eventsDatabaseForText.setValue(event);

            tmp = tmp.plusDays(1);

            Log.d("murad", "count == " + count);
        }
        while(!tmp.isEqual(Utils_Calendar.TextToDate(end_date).plusDays(1)));



    }

    public void addEventToFirebaseForTextWithAddingChild(CalendarEventWithTextOnly event){
        String start_date = event.getStart_date();
        String end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }


        LocalDate tmp = Utils_Calendar.TextToDate(start_date);

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            count++;
            //eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).push();
            //eventsDatabase = eventsDatabase.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).child("");

//            eventsDatabaseForText.child(""+Utils_Calendar.DateToTextForFirebase(tmp)).push();
            eventsDatabaseForText = eventsDatabaseForText.child(""+ Utils_Calendar.DateToTextForFirebase(tmp)).push();

            eventsDatabaseForText.getParent().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(previousChildName != null){
                        //String prev_id = eventsDatabaseForText.getParent().child(previousChildName).child("event_id");
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            eventsDatabaseForText.setValue(event);

            tmp = tmp.plusDays(1);

            Log.d("murad", "count == " + count);
        }
        while(!tmp.isEqual(Utils_Calendar.TextToDate(end_date).plusDays(1)));



    }

}