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
import androidx.core.util.Pair;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import petrov.kristiyan.colorpicker.ColorPicker;

public class Add_Event_Screen extends AppCompatActivity implements ChooseEventFrequencyDialogCustom.GetFrequencyListener {
    private EditText et_name;
    private EditText et_place;
    private EditText et_description;

    int selectedColor = Color.GREEN;

    private Button btn_choose_start_time;
    private Button btn_choose_start_date;
    private Button btn_choose_end_time;
    private Button btn_choose_end_date;

    private Button btn_add_event;
    private Button btn_color;
    private Button btn_repeat;

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

    private Intent gotten_intent;
    private Intent intentToChooseEventFrequencyDialogCustom;

    private int frequency;

    private boolean frequencyDay;
    private boolean[] frequencyDayOfWeek;
    private boolean frequencyMonth;
    private boolean frequencyYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = firebase.getReference("EventsDatabase");

        gotten_intent = getIntent();
        selected_day = gotten_intent.getIntExtra("day", 0);
        selected_dayOfWeek = gotten_intent.getStringExtra("dayOfWeek");
        selected_month = gotten_intent.getIntExtra("month", 0);
        selected_year = gotten_intent.getIntExtra("year", 0);

        selectedDate = LocalDate.of(selected_year, selected_month, selected_day);

        start_day = end_day = selected_day;
        start_month = end_month = selected_month;
        start_year = end_year = selected_year;

        start_hour = LocalTime.now().getHour();
        start_min = LocalTime.now().getMinute();
        end_hour = LocalTime.now().getHour();
        end_min = LocalTime.now().getMinute();

        Log.d("murad", "Receiving selectedDate " + selected_day + " " + selected_month + " " + selected_year);

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils.DateToText(selectedDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new SetDate("start"), selected_year, selected_month, selected_day);
                startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
//                startDatePickerDialog.getDatePicker().setMinDate(LocalDate.now().);
                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils.DateToText(selectedDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this, android.R.style.ThemeOverlay_Material_Dialog, new SetDate("end"), selected_year, selected_month, selected_day);
                endDatePickerDialog.updateDate(end_year, end_month-1, end_day);
                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                startTimePickerDialog = new TimePickerDialog(Add_Event_Screen.this,
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
                endTimePickerDialog = new TimePickerDialog(Add_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        new SetTime("end"), 0, 0, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
            }
        });

        btn_color = findViewById(R.id.btn_color);
        btn_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createColorPickerDialog();
            }
        });

        btn_repeat = findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseEventFrequencyDialogCustom chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustom(Add_Event_Screen.this);
                intentToChooseEventFrequencyDialogCustom = new Intent(Add_Event_Screen.this, ChooseEventFrequencyDialogCustom.class);


                chooseEventFrequencyDialog.show();
            }
        });

        btn_add_event = findViewById(R.id.btn_add_event);
        btn_add_event.setOnClickListener(view -> {
            /*String name = "";
            String description = "";
            String place = "";

            String msg = "";

            boolean editTextsFilled = true;
            if(et_name.getText().toString().isEmpty()) {
                msg += "name";
                editTextsFilled = false;
            }
            else {
                name = et_name.getText().toString().replaceFirst("\\s+", "");
            }
            if(et_description.getText().toString().isEmpty()) {
                if(!editTextsFilled) {
                    msg += ", ";
                }
                msg += "description";
                editTextsFilled = false;
            }
            else {
                description = et_description.getText().toString();
            }

            if(et_place.getText().toString().isEmpty()) {
            *//*if(!editTextsFilled){
                msg += ", ";
            }*//*
                msg += (editTextsFilled ? "" : ", ");
                msg += "place";
                editTextsFilled = false;
            }
            else {
                place = et_place.getText().toString();
            }*/

            String name = et_name.getText().toString();
            String description = et_description.getText().toString();
            String place = et_place.getText().toString();

            boolean editTextsFilled = Utils.areEventDetailsValid(this, name, description, place);

            if(editTextsFilled) {
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

                CalendarEventWithTextOnly eventWithTextOnly = new CalendarEventWithTextOnly(name, description, place, selectedColor, start_date, start_time, end_date, end_time);

                //addEventToFirebase(event);
                //addEventToFirebaseForText(eventWithTextOnly);
                //addEventToFirebaseForText(eventWithTextOnly);
                addEventToFirebaseForTextWithPUSH(eventWithTextOnly);
                //addEventToFirebaseForAdvanced(eventAdvanced);

                startActivity(new Intent(Add_Event_Screen.this, Calendar_Screen.class));

            }
            /*else {
                Toast.makeText(this, "Please enter event's " + msg, Toast.LENGTH_LONG).show();
            }*/

        });

        // now create instance of the material date picker
        // builder make sure to add the "dateRangePicker"
        // which is material date range picker which is the
        // second type of the date picker in material design
        // date picker we need to pass the pair of Long
        // Long, because the start date and end date is
        // store as "Long" type value

        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

        // now define the properties of the
        // materialDateBuilder
        materialDateBuilder.setTitleText("SELECT A DATE");

        MaterialDatePicker materialDatePicker = materialDateBuilder.build();
    }

    private void createColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setDefaultColorButton(Color.BLUE);
        colorPicker.setRoundColorButton(true);

        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                if(color == 0){
                    return;
                }
                Log.d("murad", "color " + color);
                selectedColor = color;
                btn_color.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
            }
        });
        colorPicker.show();
    }

    @Override
    public void getFrequency(int selectedFrequency, boolean selectedFrequencyDay, boolean[] selectedFrequencyDayOfWeek, boolean selectedFrequencyMonth, boolean selectedFrequencyYear) {
        frequency = selectedFrequency;
        frequencyDay = selectedFrequencyDay;
        frequencyDayOfWeek = selectedFrequencyDayOfWeek;
        frequencyMonth = selectedFrequencyMonth;
        frequencyYear = selectedFrequencyYear;

        Log.d("murad", "frequency is " + frequency);
        Log.d("murad", "frequencyDay is " + frequencyDay);
        Log.d("murad", "frequencyDayOfWeek is " + Arrays.toString(frequencyDayOfWeek));
        Log.d("murad", "frequencyMonth is " + frequencyMonth);
        Log.d("murad", "frequencyYear is " + frequencyYear);
    }

    //sets time
    public class SetTime implements TimePickerDialog.OnTimeSetListener {
        private String time_start_or_end;

        public SetTime(String time_start_or_end) {
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

        public SetDate(String date_start_or_end) {
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

    public void addEventToFirebase(CalendarEvent event) {
        LocalDate start_date = event.getStart_date();
        LocalDate end_date = event.getEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //ToDo check if current user is madrich
        }

/*        eventsDatabase.child(""+Utils.getDefaultDateForFirebase(start_date)).child(""+event.getEvent_id()).push();
        eventsDatabase = eventsDatabase.child(""+Utils.getDefaultDateForFirebase(start_date)).child(""+event.getEvent_id());
        //eventsDatabase.setValue(event.getClass());

        eventsDatabase.child("Name").setValue(event.getName());
        eventsDatabase.child("Place").setValue(event.getPlace());
        eventsDatabase.child("Description").setValue(event.getDescription());

        eventsDatabase.child("Starting Date").setValue(Utils.getDefaultDateForFirebase(event.getStart_date()));
        eventsDatabase.child("Starting Time").setValue(Utils.getDefaultTime(event.getStart_time()));

        eventsDatabase.child("Ending Date").setValue(Utils.getDefaultDateForFirebase(event.getEnd_date()));
        eventsDatabase.child("Ending Time").setValue(Utils.getDefaultTime(event.getEnd_time()));*/

        LocalDate tmp = start_date;

        do {
            eventsDatabase = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = firebase.getReference("EventsDatabase");

            //eventsDatabase.child(""+Utils.DateToTextForFirebase(tmp)).push();
            //eventsDatabase = eventsDatabase.child(""+Utils.DateToTextForFirebase(tmp)).child("");

            eventsDatabase.child("" + Utils.DateToTextForFirebase(tmp)).child("" + event.getEvent_id()).push();
            eventsDatabase = eventsDatabase.child("" + Utils.DateToTextForFirebase(tmp)).child("" + event.getEvent_id());

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

            eventsDatabase.child("Starting Date").setValue(Utils.DateToTextForFirebase(event.getStart_date()));
            eventsDatabase.child("Starting Time").setValue(Utils.TimeToText(event.getStart_time()));

            eventsDatabase.child("Absolute Starting Time").setValue(String.valueOf(event.getStart_time().toSecondOfDay()));

            eventsDatabase.child("Ending Date").setValue(Utils.DateToTextForFirebase(event.getEnd_date()));
            eventsDatabase.child("Ending Time").setValue(Utils.TimeToText(event.getEnd_time()));

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

    public void addEventToFirebaseForText(CalendarEventWithTextOnly event) {
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
        int timestamp = startTime.toSecondOfDay();

        LocalTime endTime = Utils.TextToTime(event.getEnd_time());


        LocalDate tmp = Utils.TextToDate(start_date);

        eventsDatabaseForText = eventsDatabaseForText.child(Utils.TextToTextForFirebase(start_date));
        Log.d("murad", eventsDatabaseForText.getKey());

        /*eventsDatabaseForText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    id = (int) snapshot.getChildrenCount();
                    Log.d("murad","------------------------");
                    Log.d("murad","snapshot.getChildrenCount" + snapshot.getChildrenCount());
                }
                else{
                }
                //snapshot.getRef().child("event_id").setValue(Utils.TextToTextForFirebase(start_date) + "_" + id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        do {

            //eventsDatabase.child(""+Utils.DateToTextForFirebase(tmp)).push();
            //eventsDatabase = eventsDatabase.child(""+Utils.DateToTextForFirebase(tmp)).child("");

//            eventsDatabaseForText.child(""+Utils.DateToTextForFirebase(tmp)).push();
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            eventsDatabaseForText = eventsDatabaseForText.push();

            String key = eventsDatabaseForText.getKey();
            event.setEvent_id(key);

            eventsDatabaseForText.setValue(event);
            event.setTimestamp(0);

            tmp = tmp.plusDays(1);

        }
        while(!tmp.isEqual(Utils.TextToDate(end_date).plusDays(1)));

    }

    public void addEventToFirebaseForTextWithPUSH(CalendarEventWithTextOnly event) {
        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");

        LocalDate tmp = start_date;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(start_date));
        eventsDatabaseForText = eventsDatabaseForText.push();

        String key = eventsDatabaseForText.getKey();
        event.setEvent_id(key);

        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils.DateToTextForFirebase(tmp));

            eventsDatabaseForText = eventsDatabaseForText.child(key);
            eventsDatabaseForText.setValue(event);

            event.setTimestamp(0);

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

}