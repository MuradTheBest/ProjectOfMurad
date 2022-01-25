package com.example.projectofmurad.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.core.util.Pair;

import com.example.projectofmurad.MySuperTouchActivity;
import com.example.projectofmurad.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class Add_Event_Screen extends MySuperTouchActivity implements
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnSwitchDialog{



    /*private Button btn_choose_start_time;
    private Button btn_choose_start_date;
    private Button btn_choose_end_time;
    private Button btn_choose_end_date;*/



/*    private int start_hour;
    private int start_min;

    private int end_hour;
    private int end_min;

    private int start_day;
    private int start_month;
    private int start_year;

    private int end_day;
    private int end_month;
    private int end_year;*/

    private int selected_day = 0;
    private String selected_dayOfWeek;
    private int selected_month = 0;
    private int selected_year = 0;

    private LocalDate selectedDate;

    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private Intent gotten_intent;
    private Intent intentToChooseEventFrequencyDialogCustom;

    private final String[] days = Utils_Calendar.getNarrowDaysOfWeek();

    private int day;
    private int dayOfWeekPosition;
    private List<Boolean> array_frequencyDayOfWeek;
    private int weekNumber;
    private int month;

    private boolean initial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustomWithExposedDropdown(Add_Event_Screen.this);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = firebase.getReference("EventsDatabase");

        selectedColor = Color.GREEN;

        gotten_intent = getIntent();
        selected_day = gotten_intent.getIntExtra("day", 0);
        selected_dayOfWeek = gotten_intent.getStringExtra("dayOfWeek");
        selected_month = gotten_intent.getIntExtra("month", 0);
        selected_year = gotten_intent.getIntExtra("year", 0);

        selectedDate = LocalDate.of(selected_year, selected_month, selected_day);

        start_day = end_day = selected_day;
        start_month = end_month = selected_month;
        start_year = end_year = selected_year;

/*        start_hour = LocalTime.now().getHour();
        start_min = LocalTime.now().getMinute();
        end_hour = LocalTime.now().getHour();
        end_min = LocalTime.now().getMinute();*/

        start_hour = 8;
        start_min = 0;
        end_hour = 9;
        end_min = 0;

        Log.d("murad", "Receiving selectedDate " + selected_day + " " + selected_month + " " + selected_year);

        et_name = findViewById(R.id.et_name);
        et_name.setOnFocusChangeListener(this);

        et_description = findViewById(R.id.et_description);
        et_description.setOnFocusChangeListener(this);

        et_place = findViewById(R.id.et_place);
        et_place.setOnFocusChangeListener(this);

        ib_color = findViewById(R.id.ib_color);
        ib_color.setOnClickListener(v -> createColorPickerDialog());

        switch_all_day = findViewById(R.id.switch_all_day);
        switch_all_day.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    event.setTimestamp(isChecked ? 0 : event.receiveStart_time().toSecondOfDay());
                    timestamp = isChecked ? 0 : ((start_hour*60 + start_min)*60);
/*                    int i = event.receiveStart_time().toSecondOfDay();
                    if(isChecked){
                        event.setTimestamp(0);
                    }
                    else{
                        event.setTimestamp(i);
                    }*/
                    btn_choose_start_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    btn_choose_end_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                }
        );

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils_Calendar.DateToTextLocal(selectedDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this, AlertDialog.THEME_HOLO_LIGHT, new SetDate("start"), selected_year, selected_month, selected_day);
                startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
//                startDatePickerDialog.getDatePicker().setMinDate(LocalDate.now().);

                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils_Calendar.DateToTextLocal(selectedDate));
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
                        AlertDialog.THEME_HOLO_LIGHT,
                        new SetTime("start"), start_hour, start_min, true);

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
                        new SetTime("end"), end_hour, end_min, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
            }
        });

        btn_color = findViewById(R.id.btn_color);
        btn_color.setOnClickListener(view -> createColorPickerDialog());

        btn_repeat = findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate startDate = LocalDate.of(start_year, start_month, start_day);
                String start_date = Utils_Calendar.DateToTextLocal(startDate);

                //ChooseEventFrequencyDialog chooseEventFrequencyDialog = new ChooseEventFrequencyDialog(Add_Event_Screen.this);
                //AlertDialogToChooseFrequency chooseEventFrequencyDialog = new AlertDialogToChooseFrequency(Add_Event_Screen.this, startDate);
/*                if (initial){
                    Toast.makeText(getApplicationContext(), "Initial open", Toast.LENGTH_SHORT).show();
                    chooseEventFrequencyDialog.setStartDateForRepeatInitial(startDate);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Open", Toast.LENGTH_SHORT).show();
                    chooseEventFrequencyDialog.setStartDateForRepeat(startDate);
                }
                initial = false;*/

                chooseEventFrequencyDialog.setStartDateForRepeatInitial(startDate);


                intentToChooseEventFrequencyDialogCustom = new Intent(Add_Event_Screen.this, ChooseEventFrequencyDialogCustom.class);
                intentToChooseEventFrequencyDialogCustom.putExtra("end_year", start_year);
                intentToChooseEventFrequencyDialogCustom.putExtra("end_month", start_month);
                intentToChooseEventFrequencyDialogCustom.putExtra("end_day", start_day);

                chooseEventFrequencyDialog.show();

                Bundle bundle = new Bundle();
                bundle.putInt("year", start_year);
                bundle.putInt("month", start_month);
                bundle.putInt("day", start_day);

/*
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(ChooseEventFrequency_Screen.class, bundle, ChooseEventFrequency_Screen.TAG)
                        .commit();*/


                /**
                 * DialogFragment.show() will take care of adding the fragment
                 * in a transaction.  We also want to remove any currently showing
                 * dialog, so make our own transaction and take care of that here.
                 */

/*                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag(ChooseEventFrequency_Screen.TAG);
                if (prev != null) {
//                    prev.show(ft, ChooseEventFrequency_Screen.TAG);
                    ((ChooseEventFrequency_Screen) prev).show(fragmentManager, ChooseEventFrequency_Screen.TAG);
//                    fragmentManager.beginTransaction().show(prev).commit();
                    Toast.makeText(getApplicationContext(), "Showing created dialog", Toast.LENGTH_SHORT).show();

                }
                else{
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .add(ChooseEventFrequency_Screen.class, bundle, ChooseEventFrequency_Screen.TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    Toast.makeText(getApplicationContext(), "Creating dialog", Toast.LENGTH_SHORT).show();

                }*/
//                ft.addToBackStack(null);

                // Create and show the dialog.
/*                ChooseEventFrequency_Screen newFragment = ChooseEventFrequency_Screen.newInstance(start_year, start_month, start_day);
                newFragment.show(ft, ChooseEventFrequency_Screen.TAG);*/
            }
        });

        btn_add_event = findViewById(R.id.btn_add_event);

        btn_delete_event = findViewById(R.id.btn_delete_event);
        btn_delete_event.setVisibility(View.GONE);

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
        colorPicker.setDefaultColorButton(Color.GREEN);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColorButtonSize(30, 30);
        colorPicker.setColorButtonTickColor(Color.BLACK);

/*        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
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
        });*/

        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                selectedColor = color;
                btn_color.setBackgroundColor(color);
//                ib_color.setBackgroundColor(color);
//                ib_color.getBackground().setTint(color);
//                ib_color.setBackgroundColor(color);
                ib_color.getDrawable().setTint(color);
            }

            @Override
            public void onCancel() {

            }
        });

        colorPicker.show();

    }

    public void animate(ViewGroup viewGroup){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

        Slide slide = new Slide(Gravity.TOP);
        TransitionManager.beginDelayedTransition(viewGroup, slide);

//        TransitionManager.beginDelayedTransition(viewGroup, trans);


    }


    public void onAddEventClick(View view) {

        String name = et_name.getText().toString();
        String description = et_description.getText().toString();
        String place = et_place.getText().toString();

        boolean editTextsFilled = Utils_Calendar.areEventDetailsValid(this, name, description, place);

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
            Toast.makeText(getApplicationContext(), "Event was successfully added " +
                            "\n NAME: " + name +
                            "\n DESCRIPTION: " + description +
                            "\n PLACE: " + place +
                            "\n STARTS AT " + start_hour + " : " + start_min + " on " + start_day + "." + start_month + "." + start_year +
                            "\n ENDS AT " + end_hour + " : " + end_min + " on " + end_day + "." + end_month + "." + end_year,
                    Toast.LENGTH_SHORT).show();

            startDate = LocalDate.of(start_year, start_month, start_day);
            endDate = LocalDate.of(end_year, end_month, end_day);

            LocalTime startTime = LocalTime.of(start_hour, start_min);
            LocalTime endTime = LocalTime.of(end_hour, end_min);

//            event = new CalendarEventWithTextOnly2FromSuper(selectedColor, name, description, place, timestamp, startDate, startTime, endDate, endTime);
/*            event.setColor(selectedColor);
            event.setName(name);
            event.setDescription(description);
            event.setPlace(place);

            event.updateStart_date(startDate);


            event.updateStart_time(startTime);

            event.updateEnd_date(endDate);

            event.updateEnd_time(endTime);*/

            event.addDefaultParams(selectedColor, name, description, place, timestamp, startDate, startTime, endDate, endTime);

            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(startDate));
            eventsDatabase = eventsDatabase.push();

            if (event.getTimestamp() == 0){
                event.setStart_time("");
                event.setEnd_time("");
            }

            String chain_key = eventsDatabase.getKey();
            event.setEvent_chain_id(chain_key);

            boolean success = true;

            if(row_event) {
                event.updateFrequency_start(startDate);
                event.updateFrequency_end(endDate);

                addEventToFirebaseForTextWithPUSH(event, chain_key);
            }
            else if(event.getFrequencyType().endsWith("amount")){
                success = addEventForTimesAdvanced(event);
            }
            else if(event.getFrequencyType().endsWith("end")){
                success = addEventForUntilAdvanced(event);
            }

            //ToDo adjust chain_id for row events in database
 /*           if(event.getStart_date().equals(event.getFrequency_start()) &&
                    event.getEnd_date().equals(event.getFrequency_end())){

                eventsDatabase = firebase.getReference("EventsDatabase");
                eventsDatabase = eventsDatabase.child(event.getStart_date());

                eventsDatabase.child(chain_key).setValue(event.getEvent_private_id());
            }*/

/*            Intent intent_toCalendar = new Intent(getApplicationContext(), Calendar_Screen.class);
            intent_toCalendar.putExtra("selected_day", start_);
            intent_toCalendar.putExtra("selected_month", month);
            intent_toCalendar.putExtra("selected_year", year);
            startActivity();*/

//            startActivity(new Intent(getApplicationContext(), Calendar_Screen.class));

            Intent toCalendar_Screen = new Intent(getApplicationContext(), Calendar_Screen.class);

            int day = event.receiveFrequency_start().getDayOfMonth();
            int month = event.receiveFrequency_start().getMonth().getValue();
            int year = event.receiveFrequency_start().getYear();

            toCalendar_Screen.putExtra("day", day);
            toCalendar_Screen.putExtra("month", month);
            toCalendar_Screen.putExtra("year", year);

            if (success) {
                startActivity(toCalendar_Screen);
            }
            else {
                createBottomSheetDialog();
            }

        }

    }

    @Override
    public void switchDialog(ChooseEventFrequencyDialogCustomWithExposedDropdown copy) {
        if (copy == null){
            chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustomWithExposedDropdown(Add_Event_Screen.this);
//            chooseEventFrequencyDialog.setStartDateForRepeatInitial(startDate);
        }
        else {
            chooseEventFrequencyDialog = copy;
        }

    }


    /*@Override
    public void onFocusChange(View view, boolean b) {
        if(view instanceof EditText){
            String text = ((EditText) view).getText().toString();
            if(!b){
                text = text.replaceFirst("\\s+", "");
                ((EditText) view).setText(text);
            }
        }

    }*/

/*    //sets time
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

        public SetDate(String date_start_or_end) {
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

/*
    public CalendarEventWithTextOnly2FromSuper createEventAccordingToFrequencyType(CalendarEventWithTextOnly2FromSuper event) {
        Log.d("frequency_dayOfWeek_and_month", "frequencyType = " + frequencyType);
        switch(frequencyType) {
            case 1:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(1);
                event.setFrequency(frequency);
                event.setAmount(amount);
                break;
            case 10:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(1);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                break;
            case 2:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(2);
                event.setFrequency(frequency);
                event.setAmount(amount);
                event.setArray_frequencyDayOfWeek(array_frequencyDayOfWeek);
                break;
            case 20:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(2);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                event.setArray_frequencyDayOfWeek(array_frequencyDayOfWeek);
                break;
            case 3:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(3);
                event.setFrequency(frequency);
                event.setAmount(amount);
                event.setDay(day);
                break;
            case 30:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(3);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                event.setDay(day);
                break;
            case 4:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(4);
                event.setFrequency(frequency);
                event.setAmount(amount);
                event.setDayOfWeekPosition(dayOfWeekPosition);
                event.setWeekNumber(weekNumber);
                break;
            case 40:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(4);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                event.setDayOfWeekPosition(dayOfWeekPosition);
                event.setWeekNumber(weekNumber);
                break;
            case 5:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(5);
                event.setFrequency(frequency);
                event.setAmount(amount);
                event.setDay(day);
                event.setMonth(month);
                break;
            case 50:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(5);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                event.setDay(day);
                event.setMonth(month);
                break;
            case 6:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(6);
                event.setFrequency(frequency);
                event.setAmount(amount);
                event.setDayOfWeekPosition(dayOfWeekPosition);
                event.setWeekNumber(weekNumber);
                event.setMonth(month);
                break;
            case 60:
                Add_Event_Screen.this.event.clearFrequencyData();

                event.setFrequencyType(6);
                event.setFrequency(frequency);
                event.updateFrequency_end(end);
                event.setDayOfWeekPosition(dayOfWeekPosition);
                event.setWeekNumber(weekNumber);
                event.setMonth(month);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + frequencyType);
        }

        return event;
    }
*/

/*    public void addEventToFirebaseForTextWithPUSH(CalendarEventWithTextOnly2FromSuper event) {
        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");

        LocalDate tmp = start_date;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(start_date));
        eventsDatabaseForText = eventsDatabaseForText.push();

        String key = eventsDatabaseForText.getKey();
        event.setEvent_id(key);

        Log.d("murad", eventsDatabaseForText.getKey());

        do {
            eventsDatabaseForText = firebase.getReference("EventsDatabase");
            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabaseForText = eventsDatabaseForText.child(key);
            eventsDatabaseForText.setValue(event);

            event.setTimestamp(0);

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

    public void addEventForTimes(CalendarEventWithTextOnly2FromSuper event){
        LocalDate start_date = event.receiveStart_date();
//        LocalDate end_date = event.receiveEnd_date();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            //ToDo check if current user is madrich
        }

        eventsDatabaseForText = firebase.getReference("EventsDatabase");

        LocalDate tmp = start_date;

        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(start_date));
        eventsDatabaseForText = eventsDatabaseForText.push();

        String key = eventsDatabaseForText.getKey();
        event.setEvent_id(key);

        switch(event.getFrequencyType()) {
            case 1:
                for(int i = 0; i < event.getAmount(); i++) {
                    eventsDatabaseForText = firebase.getReference("EventsDatabase");
                    eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabaseForText = eventsDatabaseForText.child(key);
                    eventsDatabaseForText.setValue(event);

//                event.setTimestamp(0);

                    tmp = tmp.plusDays(event.getFrequency());
                }
                break;
            case 2:
                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

                for(int i = 0; i < event.getAmount(); i++) {
                    for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j)) {
                            eventsDatabaseForText = firebase.getReference("EventsDatabase");
                            eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                            eventsDatabaseForText = eventsDatabaseForText.child(key);
                            eventsDatabaseForText.setValue(event);

//                          event.setTimestamp(0);

                        }

                        tmp = tmp.plusDays(1);
                    }

                    tmp = tmp.plusWeeks(event.getFrequency());
                }
                break;
            case 3:
                for(int i = 0; i < event.getAmount(); i++) {
                    if(tmp.lengthOfMonth() >= event.getDay()) {

                        eventsDatabaseForText = firebase.getReference("EventsDatabase");
                        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabaseForText = eventsDatabaseForText.child(key);
                        eventsDatabaseForText.setValue(event);

//                      event.setTimestamp(0);
                    }
                    tmp = tmp.plusMonths(event.getFrequency());
                }
                break;
            case 4:
                int weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                for(int i = 0; i < event.getAmount(); i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabaseForText = firebase.getReference("EventsDatabase");
                    eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabaseForText = eventsDatabaseForText.child(key);
                    eventsDatabaseForText.setValue(event);

                    tmp = tmp.plusMonths(event.getFrequency());
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                    if(weekNumber > 4) {

                        tmp = Utils_Calendar.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());
                    }
                    else {

                        tmp = Utils_Calendar.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale)
                                + " is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                                " is on " + Utils_Calendar.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }

                }

                *//*if(weekNumber > 4) {
                    for(int i = 0; i < event.getAmount(); i++) {
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabaseForText = firebase.getReference("EventsDatabase");
                        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabaseForText = eventsDatabaseForText.child(key);
                        eventsDatabaseForText.setValue(event);

                        tmp = tmp.plusMonths(event.getFrequency());
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());

                    }
                }
                else {
                    for(int i = 0; i < event.getAmount(); i++) {
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabaseForText = firebase.getReference("EventsDatabase");
                        eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabaseForText = eventsDatabaseForText.child(key);
                        eventsDatabaseForText.setValue(event);

//                      event.setTimestamp(0);

                        tmp = tmp.plusMonths(event.getFrequency());
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale)
                                + " is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                                " is on " + Utils_Calendar.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }
                }*//*
                break;
            case 5:
                int selected_frequency = event.getFrequency();
                int selected_amount = event.getAmount();

                int selected_day = event.getDay();
                int selected_month = event.getMonth();

                if(event.receiveStart_date().getMonth() == Month.FEBRUARY && selected_day == 29){
                    event.setFrequency(4);

                    Toast.makeText(getApplicationContext(), "This event will repeat every 4 years", Toast.LENGTH_SHORT).show();
                }

                for(int i = 0; i < event.getAmount(); i++) {

                    eventsDatabaseForText = firebase.getReference("EventsDatabase");
                    eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabaseForText = eventsDatabaseForText.child(key);
                    eventsDatabaseForText.setValue(event);

                    tmp = tmp.plusYears(event.getFrequency());
                }
                break;
            case 6:
                weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                for(int i = 0; i < event.getAmount(); i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabaseForText = firebase.getReference("EventsDatabase");
                    eventsDatabaseForText = eventsDatabaseForText.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabaseForText = eventsDatabaseForText.child(key);
                    eventsDatabaseForText.setValue(event);

                    tmp = tmp.plusYears(event.getFrequency());
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                    if(weekNumber > 4) {

                        tmp = Utils_Calendar.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());
                    }
                    else {

                        tmp = Utils_Calendar.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale)
                                + " is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                                " is on " + Utils_Calendar.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }

                }

                break;

        }

    }*/


}