package com.example.projectofmurad.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AddEvent_Screen extends AppCompatActivity implements View.OnClickListener{
    EditText et_name;
    EditText et_place;
    EditText et_description;

    TextView textView5;

    Button btn_choose_start_time;
    Button btn_choose_start_date;
    Button btn_choose_end_time;
    Button btn_choose_end_date;

    Button btn_add_event;

    int start_hour;
    int start_min;

    int end_hour;
    int end_min;

    int start_day;
    int start_month;
    int start_year;

    int end_day;
    int end_month;
    int end_year;

    int today_day = 0;
    String today_dayOfWeek;
    int today_month = 0;
    int today_year = 0;

    DatePickerDialog datePickerDialog;
    LocalDate localDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        Intent gotten_intent = getIntent();
        today_day = gotten_intent.getIntExtra("day", 0);
        today_dayOfWeek = gotten_intent.getStringExtra("dayOfWeek");
        today_month = gotten_intent.getIntExtra("month",0);
        today_year = gotten_intent.getIntExtra("year", 0);

        Log.d("murad","Receiving selectedDate " + today_day + " " + today_month + " " + today_year);

        //localDate = getIntent().getParcelableExtra("day_date");

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(setDefaultDates());
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(AddEvent_Screen.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new SetDate("start"), today_year, today_month, today_day);
                datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                    }
                });
                datePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(setDefaultDates());
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(AddEvent_Screen.this, android.R.style.Theme_Material_Light_Dialog, new SetDate("end"), today_year, today_month, today_day);
                datePickerDialog.show();
            }
        });

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                TimePickerDialog StartTimePickerDialog = new TimePickerDialog(AddEvent_Screen.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        new SetTime("start"), 0, 0, true);

                StartTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                StartTimePickerDialog.updateTime(start_hour, start_min);
                StartTimePickerDialog.show();
            }
        });

        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                TimePickerDialog StartTimePickerDialog = new TimePickerDialog(AddEvent_Screen.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        new SetTime("end"), 0, 0, true);

                StartTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                StartTimePickerDialog.updateTime(end_hour, end_min);
                StartTimePickerDialog.show();
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
            }
            else {
                Toast.makeText(this, "Please enter event's " + msg, Toast.LENGTH_LONG).show();
            }

        });


    }

    @Override
    public void onClick(View view) {
        if(view == btn_choose_start_date){

        }
        if(view == btn_choose_end_date){

        }
        if(view == btn_choose_start_time){

        }
        if(view == btn_choose_end_time){

        }
    }

    public class SetTime implements TimePickerDialog.OnTimeSetListener{
        private String start_or_end;
        private int hour;
        private int min;

        public SetTime(String start_or_end){
            this.start_or_end = start_or_end;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfDay) {
            //Initialize hour and minute
            hour = hourOfDay;
            min = minuteOfDay;

            String time = "";
            if(hour < 10){
                time += "0";
            }
            time += hour + ":";
            if(min < 10){
                time += "0";
            }
            time += min;

            switch(start_or_end) {
                case "start":
                    btn_choose_start_time.setText(time);
                    start_hour = hour;
                    start_min = min;
                    break;
                case "end":
                    btn_choose_end_time.setText(time);
                    end_hour = hour;
                    end_min = min;
                    break;
            }
        }
    }

    //sets date
    public class SetDate implements DatePickerDialog.OnDateSetListener {
        private String start_or_end;

        public SetDate(String start_or_end){
            this.start_or_end = start_or_end;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
            LocalDate date = LocalDate.of(year, month, day);
            Date da = new Date(year, month, day);

            String d = date.format(simpleDateFormat);

            switch(start_or_end) {
                case "start":
                    btn_choose_start_date.setText(d);

                    start_day = day;
                    start_month = month;
                    start_year = year;

                    break;
                case "end":
                    btn_choose_end_date.setText(d);

                    end_day = day;
                    end_month = month;
                    end_year = year;

                    break;
            }
        }
    }

    public String setDefaultDates(){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
        LocalDate date = LocalDate.of(today_year, today_month, today_day);
        return date.format(simpleDateFormat);
    }


}