package com.example.projectofmurad.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;

import petrov.kristiyan.colorpicker.ColorPicker;

public class Edit_Event_Screen extends MySuperTouchActivity {

/*    private Button btn_choose_start_time;
    private Button btn_choose_start_date;
    private Button btn_choose_end_time;
    private Button btn_choose_end_date;*/

    private String chain_key;
    private String private_key;

    private String name;
    private String description;
    private String place;

    private LocalDate startDate;
    private LocalDate chain_start_date;

   /* private int start_day;
    private int start_month;
    private int start_year;*/

    private LocalTime startTime;

/*    private int start_hour;
    private int start_min;*/

    private LocalDate endDate;
    private LocalDate chain_end_date;

/*    private int end_day;
    private int end_month;
    private int end_year;*/

    private LocalTime endTime;

/*    private int end_hour;
    private int end_min;*/

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

        chain_key = gotten_intent.getStringExtra("event_chain_key");
        private_key = gotten_intent.getStringExtra("event_private_key");

        name = gotten_intent.getStringExtra("event_name");
        description = gotten_intent.getStringExtra("event_description");
        place = gotten_intent.getStringExtra("event_place");
        selectedColor = gotten_intent.getIntExtra("event_color", Color.GREEN);

        startDate = Utils_Calendar.TextToDate(gotten_intent.getStringExtra("event_start_date"));
        endDate = Utils_Calendar.TextToDate(gotten_intent.getStringExtra("event_end_date"));

        chain_start_date = Utils_Calendar.TextToDate(gotten_intent.getStringExtra("event_start_date"));
        chain_end_date = Utils_Calendar.TextToDate(gotten_intent.getStringExtra("event_end_date"));

        startTime = Utils_Calendar.TextToTime(gotten_intent.getStringExtra("event_start_time"));
        endTime = Utils_Calendar.TextToTime(gotten_intent.getStringExtra("event_end_time"));


        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = FirebaseUtils.eventsDatabase;

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


        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);

        et_name.setText(name);
        et_description.setText(description);
        et_place.setText(place);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils_Calendar.DateToTextLocal(startDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new SetDate("start"), start_year, start_month, start_day);
                startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
                //startDatePickerDialog.getDatePicker().setMinDate(LocalDate.now().);

                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils_Calendar.DateToTextLocal(endDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog, new SetDate("end"), end_year, end_month, end_day);
                endDatePickerDialog.updateDate(end_year, end_month-1, end_day);
                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setText(Utils_Calendar.TimeToText(startTime));
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
        btn_choose_end_time.setText(Utils_Calendar.TimeToText(endTime));
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
        btn_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*name = "";
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

                boolean editTextsFilled = Utils_Calendar.areEventDetailsValid(Edit_Event_Screen.this, name, description, place);

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
                    Toast.makeText(Edit_Event_Screen.this, "Event was successfully added " +
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

                    event.addDefaultParams(selectedColor, name, description, place, 0, start_date, start_time, end_date, end_time);

                    event.setEvent_chain_id(chain_key);

                    if(event.getFrequencyType().endsWith("amount")){
                        addEventForTimesAdvanced(event);
                    }
                    else if(event.getFrequencyType().endsWith("end")){
//                        addEventForUntilAdvanced(event);
                    }

                    //addEventToFirebase(event);
                    superDelete(chain_key);
                    addEventToFirebaseForTextWithPUSH(event, chain_key);
                    //addEventToFirebaseForAdvanced(eventAdvanced);

                    startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));

                }

            }
        });


        btn_delete_event = findViewById(R.id.btn_delete_event);
        btn_delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    /*eventsDatabase = FirebaseUtils.eventsDatabase;

                    eventsDatabase = eventsDatabase.child(private_key);

                    Log.d("murad", "-------------------------------------------------------------------");
                    Log.d("murad", " ");

                    Log.d("murad", "RIGHT NOW AT " + eventsDatabase.getKey());
                    Log.d("murad", "RIGHT NOW AT " + eventsDatabase.getRef().getKey());
                    Log.d("murad", "Parent is  " + Objects.requireNonNull(eventsDatabase.getParent()).getKey());

                    Log.d("murad", " ");
                    Log.d("murad", "-------------------------------------------------------------------");

                    eventsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Log.d("murad", "-------------------------------------------------------------------");
                            Log.d("murad", " ");

                            Log.d("murad", "RIGHT NOW AT " + snapshot.getKey());

                            Log.d("murad", " ");
                            Log.d("murad", "-------------------------------------------------------------------");

                            tmp = snapshot.getValue(CalendarEventWithTextOnly2FromSuper.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    startDate = tmp.receiveStart_date();
                    endDate = tmp.receiveEnd_date();

                    chain_start_date = tmp.receiveFrequency_start();
                    chain_end_date = tmp.receiveFrequency_end();

                    String private_key = tmp.getEvent_private_id();
                    String chain_key = tmp.getEvent_chain_id();*/

//                    deleteSingleEvent(private_key, startDate, endDate);
//                    deleteAllEventsInChain(chain_key, chain_start_date, chain_end_date);

                    superDelete(chain_key);


//                    superDelete(chain_key);
//                    startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Ups... Deleting failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    CalendarEventWithTextOnly2FromSuper tmp = new CalendarEventWithTextOnly2FromSuper();

/*    //sets time
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

            *//*if(hour < 10){
                time += "0";
            }
            time += hour + ":";
            if(min < 10){
                time += "0";
            }
            time += min;*//*

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
            String date_text = Utils_Calendar.DateToTextOnline(date);

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
    }*/

    private void createColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setDefaultColorButton(Color.GREEN);
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

    /*
    public void deleteEvent(CalendarEventWithTextOnly event){
        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = FirebaseUtils.eventsDatabase;

        LocalDate tmp = start_date;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(old_start_date));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = FirebaseUtils.eventsDatabase;
            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(key).removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));
    }
*/

    public void deleteEvent(LocalDate start, LocalDate end){

        eventsDatabaseForText = FirebaseUtils.eventsDatabase;

        LocalDate tmp = start;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(start));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = FirebaseUtils.eventsDatabase;
            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(chain_key).getParent().removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end.plusDays(1)));
    }

    public void deleteSingleEvent(String private_key, LocalDate start, LocalDate end){

        eventsDatabaseForText = FirebaseUtils.eventsDatabase;

        LocalDate tmp = start;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(start));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = FirebaseUtils.eventsDatabase;
            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(private_key).removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end.plusDays(1)));
    }

    public void deleteAllEventsInChain(String chain_key, LocalDate chain_start, LocalDate chain_end){

        eventsDatabaseForText = FirebaseUtils.eventsDatabase;

        LocalDate tmp = chain_start;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(chain_start));
        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = FirebaseUtils.eventsDatabase;
            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabaseForText.child(chain_key).getParent().removeValue();

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(chain_end.plusDays(1)));
    }

    boolean finished = true;

    public void superDelete(String key) {
        eventsDatabaseForText = FirebaseUtils.eventsDatabase;

        eventsDatabaseForText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()) {
                    /*if(data.hasChild(key)) {
                        data.child(key).getRef().removeValue();
                    }*/

                    for (DataSnapshot d : data.getChildren()){


                        Log.d("murad", "-----------------------------------------------------------------------");
                        Log.d("murad", " ");

                        Log.d("murad", "RIGHT NOW AT " + data.getKey() + " / " + d.getKey());


/*                        if(d.getValue(CalendarEventWithTextOnly2FromSuper.class).getEvent_chain_id().equals(key)){
                            d.getRef().removeValue();
                        }*/

                        if(d.child("event_chain_id").getValue().toString().equals(key)){
                            finished = false;

                            CalendarEventWithTextOnly2FromSuper c = d.getValue(CalendarEventWithTextOnly2FromSuper.class);
                            if(c == null){
                                Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "SUCCEEDED " + c.toString(), Toast.LENGTH_SHORT).show();
                            }

                            /*d.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error,
                                                       @NonNull DatabaseReference ref) {
                                    finished = true;
                                }
                            });*/

                            d.getRef().setValue(null);

//                            d.getRef().child(d.getKey()).removeValue();
                            Log.d("murad", " ");

                            Log.d("murad", "EVENT " + "'" + d.child("name").getValue().toString() + "'" + " IS DELETED");
                        }

                        Log.d("murad", " ");
                        Log.d("murad", "-----------------------------------------------------------------------");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });



        Log.d("murad", "DELETING EVENT HAS BEEN SUCCESSFULLY FINISHED");
        Toast.makeText(getApplicationContext(), "DELETING EVENT HAS BEEN SUCCESSFULLY FINISHED", Toast.LENGTH_SHORT).show();


        startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));
        startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));
//        Query query = eventsDatabase.equalTo(key);

    }
}