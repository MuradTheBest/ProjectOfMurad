package com.example.projectofmurad.calendar;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

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


    private LocalDate chain_start_date;

   /* private int start_day;
    private int start_month;
    private int start_year;*/


/*    private int start_hour;
    private int start_min;*/

    private LocalDate chain_end_date;

/*    private int end_day;
    private int end_month;
    private int end_year;*/


/*    private int end_hour;
    private int end_min;*/


    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;
    private DatabaseReference eventsDatabaseForText;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private Intent gotten_intent;
    private Intent intentToChooseEventFrequencyDialogCustom;

    private final String[] days = UtilsCalendar.getNarrowDaysOfWeek();

    private int day;
    private int dayOfWeekPosition;
    private List<Boolean> array_frequencyDayOfWeek;
    private int weekNumber;
    private int month;

    private boolean initial = true;

    int x;
    int y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_do_not_move, R.anim.anim_do_not_move);
        setContentView(R.layout.activity_add_event_screen_linear_layout);

        gotten_intent = getIntent();

        x = gotten_intent.getIntExtra("cx", 0);
        y = gotten_intent.getIntExtra("cy", 0);

        sv_add_event_screen = findViewById(R.id.sv_add_event_screen);

        if (savedInstanceState == null) {
            sv_add_event_screen.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = sv_add_event_screen.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                circularRevealActivity();
                                sv_add_event_screen.getViewTreeObserver().removeOnGlobalLayoutListener(
                                        this);
                            }

                        });
            }

        }

        et_name = findViewById(R.id.et_name);
        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);
        switch_alarm = findViewById(R.id.switch_alarm);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);

        ib_color = findViewById(R.id.ib_color);
        ib_color.setOnClickListener(v -> createColorPickerDialog());

        rl_event_setup = findViewById(R.id.rl_event_setup);

        switch_all_day = findViewById(R.id.switch_all_day);
        switch_all_day.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //                    event.setTimestamp(isChecked ? 0 : event.receiveStart_time().toSecondOfDay());
                        timestamp = isChecked ? 0 : ((start_hour * 60 + start_min) * 60);

                        event.setAllDay(isChecked);

                        btn_choose_start_time.setVisibility(
                                isChecked ? View.GONE : View.VISIBLE);
                        btn_choose_end_time.setVisibility(
                                isChecked ? View.GONE : View.VISIBLE);
                        //                    animate(ll_add_event_screen);
                        Edit_Event_Screen.this.animate(
                                rl_event_setup);
                    }
                }
        );

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        eventsDatabase = FirebaseUtils.eventsDatabase;

        selectedColor = getColor(R.color.colorAccent);

        start_hour = 8;
        start_min = 0;

        startTime = LocalTime.of(start_hour, start_min);

        end_hour = 9;
        end_min = 0;

        endTime = LocalTime.of(end_hour, end_min);

        start_day = end_day = gotten_intent.getIntExtra("day", LocalDate.now().getDayOfMonth());
        start_month = end_month = gotten_intent.getIntExtra("month", LocalDate.now().getMonthValue());
        start_year = end_year = gotten_intent.getIntExtra("year", LocalDate.now().getYear());

        startDate = endDate = LocalDate.of(start_year, start_month, start_day);

        startDateTime = LocalDateTime.of(startDate, startTime);
        endDateTime = LocalDateTime.of(endDate, endTime);

        Log.d("murad", "startDateTime is " + UtilsCalendar.DateTimeToTextOnline(startDateTime));
        Log.d("murad", "endDateTime is " + UtilsCalendar.DateTimeToTextOnline(endDateTime));

        Log.d("murad", "Receiving selectedDate " + start_day + " " + start_month + " " + start_year);

        if (gotten_intent.hasExtra(UtilsCalendar.KEY_EVENT)){
            editMode = true;
            Log.d("murad", "event is not null => Edit_Event_Screen");

            event = (CalendarEvent) gotten_intent.getSerializableExtra("event");

            chain_key = event.getChainId();
            private_key = event.getPrivateId();

            name = event.getName();
            description = event.getDescription();
            place = event.getPlace();

            selectedColor = event.getColor();
            boolean allDay = event.isAllDay();

            startDate = event.receiveStartDateTime().toLocalDate();
            endDate = event.receiveEndDateTime().toLocalDate();

            chain_start_date = event.receiveFrequency_start();
            chain_end_date = event.receiveFrequency_end();

            startTime = event.receiveStart_time();
            endTime = event.receiveEnd_time();

            startDateTime = event.receiveStartDateTime();
            endDateTime = event.receiveEndDateTime();

            chain_start_date = event.receiveFrequency_start();
            chain_end_date = event.receiveFrequency_end();

            et_name.setText(name);
            et_description.setText(description);
            et_place.setText(place);

            ib_color.getDrawable().setTint(selectedColor);

            switch_all_day.setChecked(allDay);

            switch_alarm.setChecked(AlarmManagerForToday.checkIfAlarmSet(this, private_key));

        }

        eventsDatabase = FirebaseUtils.eventsDatabase;

        start_day = startDateTime.getDayOfMonth();
        start_month = startDateTime.getMonthValue();
        start_year = startDateTime.getYear();

        end_day = endDateTime.getDayOfMonth();
        end_month = endDateTime.getMonthValue();
        end_year = endDateTime.getYear();

        start_hour = startDateTime.getHour();
        start_min = startDateTime.getMinute();

        end_hour = endDateTime.getHour();
        end_min = endDateTime.getMinute();



/*        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(UtilsCalendar.DateToTextLocal(startDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new SetDate("start"), start_year, start_month, start_day);
                startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
                //startDatePickerDialog.getDatePicker().setMinDate(LocalDate.now().);

                startDatePickerDialog.show();
            }
        });*/

        event_time_picker = findViewById(R.id.event_time_picker);
        event_time_picker.setIs24HourView(true);
        event_time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                start_hour = hourOfDay;
                start_min = minute;
//                startTime = LocalTime.of(hourOfDay, minute);

                startDateTime = startDateTime.withHour(hourOfDay);
                startDateTime = startDateTime.withMinute(minute);

                btn_choose_start_time.setText(UtilsCalendar.TimeToText(startDateTime.toLocalTime()));
            }
        });

        btn_choose_start_date.setText(UtilsCalendar.DateToTextLocal(startDateTime.toLocalDate()));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month,
                                                  int dayOfMonth) {
                                month = month+1;

                                start_year = year;
                                start_month = month;
                                start_day = dayOfMonth;

                                /*startDate = LocalDate.of(start_year, start_month, start_day);*/

                                startDateTime = startDateTime.withYear(year);
                                startDateTime = startDateTime.withMonth(month);
                                startDateTime = startDateTime.withDayOfMonth(dayOfMonth);

                                String date_text = UtilsCalendar.DateToTextLocal(startDateTime.toLocalDate());
                                btn_choose_start_date.setText(date_text);

                                if (startDateTime.toLocalDate().isAfter(endDateTime.toLocalDate())) {
//                                    endDate = startDate;

                                    LocalDate startDate = startDateTime.toLocalDate();
                                    LocalTime endTime = endDateTime.toLocalTime();

                                    endDateTime = LocalDateTime.of(startDate, endTime);
                                    btn_choose_end_date.setText(UtilsCalendar.DateToTextLocal(endDateTime.toLocalDate()));

                                }
                            }
                        },
                        start_year, start_month - 1, start_day);

                startDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                startDatePickerDialog.updateDate(start_year, start_month - 1, start_day);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                startDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                startDatePickerDialog.show();
            }
        });

        btn_choose_start_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startDatePickerDialog = new DatePickerDialog(getApplicationContext(),
                            AlertDialog.THEME_HOLO_LIGHT,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month,
                                                      int dayOfMonth) {
                                    month = month + 1;

                                    start_year = year;
                                    start_month = month;
                                    start_day = dayOfMonth;

                                    startDate = LocalDate.of(start_year, start_month, start_day);
                                    String date_text = UtilsCalendar.DateToTextLocal(startDate);
                                    btn_choose_start_date.setText(date_text);
                                }
                            },
                            start_year, start_month-1, start_day);

                    startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
                    startDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

                    startDatePickerDialog.show();
                }
                else{
                    startDatePickerDialog.dismiss();
                }
            }
        });

        btn_choose_end_date.setText(UtilsCalendar.DateToTextLocal(endDateTime.toLocalDate()));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endDatePickerDialog = new DatePickerDialog(Edit_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year, int month,
                                                  int dayOfMonth) {
                                month = month + 1;

                                end_year = year;
                                end_month = month;
                                end_day = dayOfMonth;

//                                endDate = LocalDate.of(end_year, end_month, end_day);

                                endDateTime = endDateTime.withYear(end_year);
                                endDateTime = endDateTime.withMonth(end_month);
                                endDateTime = endDateTime.withDayOfMonth(end_day);

                                String date_text = UtilsCalendar.DateToTextLocal(endDateTime.toLocalDate());
                                btn_choose_end_date.setText(date_text);

                                if (endDate.isBefore(startDate)) {
                                    startDate = endDate;

                                    LocalDate endDate = endDateTime.toLocalDate();
                                    LocalTime startTime = startDateTime.toLocalTime();

                                    startDateTime = LocalDateTime.of(endDate, startTime);

                                    btn_choose_start_date.setText(
                                            UtilsCalendar.DateToTextLocal(startDateTime.toLocalDate()));

                                }
                            }
                        },
                        end_year, end_month - 1, end_day);

                endDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                endDatePickerDialog.updateDate(end_year, end_month - 1, end_day);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                endDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time.setText(UtilsCalendar.TimeToText(startDateTime.toLocalTime()));
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                startTimePickerDialog = new TimePickerDialog(Edit_Event_Screen.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                start_hour = hourOfDay;
                                start_min = minute;

//                                LocalTime startTime = LocalTime.of(start_hour, start_min);

                                startDateTime = startDateTime.withHour(start_hour);
                                startDateTime = startDateTime.withMinute(start_min);

                                String time_text = UtilsCalendar.TimeToText(startDateTime.toLocalTime());

                                Log.d("murad", "hour: " + hourOfDay);
                                Log.d("murad", "minute: " + minute);
                                Log.d("murad", "time: " + time_text);

                                btn_choose_start_time.setText(time_text);

                                if (startDateTime.isAfter(endDateTime)) {
//                                    endTime = startTime.plusHours(1);

                                    endDateTime = endDateTime.toLocalDate().atTime(LocalTime.from(startDateTime.plusHours(1)));

                                    btn_choose_end_time.setText(UtilsCalendar.TimeToText(endDateTime.toLocalTime()));
                                }
                            }
                        },
                        start_hour, start_min, true);

                startTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                startTimePickerDialog.updateTime(start_hour, start_min);
                startTimePickerDialog.show();
            }
        });

        btn_choose_end_time.setText(UtilsCalendar.TimeToText(endDateTime.toLocalTime()));
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                endTimePickerDialog = new TimePickerDialog(Edit_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                end_hour = hourOfDay;
                                end_min = minute;

//                            LocalTime endTime = LocalTime.of(end_hour, end_min);

                                endDateTime = endDateTime.withHour(end_hour);
                                endDateTime = endDateTime.withMinute(end_min);

                                String time_text = UtilsCalendar.TimeToText(endDateTime.toLocalTime());
                                btn_choose_end_time.setText(time_text);

                                if (endDateTime.isBefore(startDateTime)) {
//                                    endTime = startTime.plusHours(1);

                                    startDateTime = startDateTime.toLocalDate().atTime(LocalTime.from(endDateTime.minusHours(1)));

                                    btn_choose_start_time.setText(UtilsCalendar.TimeToText(startDateTime.toLocalTime()));
                                }
                            }
                        },
                        end_hour, end_min, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
            }
        });

        chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustomWithExposedDropdown(this);

        btn_delete_event = findViewById(R.id.btn_delete_event);
        btn_delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    absoluteDelete(chain_key, new OnDeleteFinishedCallback() {
                        @Override
                        public void onDeleteFinished() {
                            deletingProgressDialog.dismiss();
                            Log.d("murad", "DELETING EVENT HAS BEEN SUCCESSFULLY FINISHED");
                            Toast.makeText(getApplicationContext(), "DELETING EVENT HAS BEEN SUCCESSFULLY FINISHED", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Edit_Event_Screen.this, Calendar_Screen.class));
                        }
                    });
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Ups... Deleting failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        btn_repeat = findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate startDate = LocalDate.of(start_year, start_month, start_day);
                String start_date = UtilsCalendar.DateToTextLocal(startDate);

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

                intentToChooseEventFrequencyDialogCustom = new Intent(getApplicationContext(),
                        ChooseEventFrequencyDialogCustomWithExposedDropdown.class);
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
                        .add(ChooseEventFrequency_Screen.class, bundle, ChooseEventFrequency_Screen.LOG_TAG)
                        .commit();*/


                /**
                 * DialogFragment.show() will take care of adding the fragment
                 * in a transaction.  We also want to remove any currently showing
                 * dialog, so make our own transaction and take care of that here.
                 */

      /*          Fragment prev = getSupportFragmentManager().findFragmentByTag(ChooseEventFrequency_Screen.LOG_TAG);
                if (prev != null) {
                    prev.show(ft, ChooseEventFrequency_Screen.LOG_TAG);
//                    ((ChooseEventFrequency_Screen) prev).show(getSupportFragmentManager(), ChooseEventFrequency_Screen.LOG_TAG);
//                    fragmentManager.beginTransaction().show(prev).commit();
                    Toast.makeText(getApplicationContext(), "Showing created dialog", Toast.LENGTH_SHORT).show();

                    ft.show(prev);

                }
                else{
                    ft.setReorderingAllowed(true)
                            .add(ChooseEventFrequency_Screen.class, bundle, ChooseEventFrequency_Screen.LOG_TAG)
                            .commit();
                    Toast.makeText(getApplicationContext(), "Creating dialog", Toast.LENGTH_SHORT).show();

                }
                ft.addToBackStack(null);*/

                // Create and show the dialog.
/*                ChooseEventFrequency_Screen newFragment = ChooseEventFrequency_Screen.newInstance(start_year, start_month, start_day);
                newFragment.show(ft, ChooseEventFrequency_Screen.LOG_TAG);*/
            }
        });


    }

    private void circularRevealActivity() {
        int cx = /*ll_add_event_screen.getRight() - */x;
        int cy = /*ll_add_event_screen.getBottom() -*/ y;

        float finalRadius = Math.max(sv_add_event_screen.getWidth(),
                sv_add_event_screen.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                sv_add_event_screen,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(1300);
        sv_add_event_screen.setVisibility(View.VISIBLE);
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getSupportActionBar().show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        circularReveal.start();

    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        int cx =/* ll_add_event_screen.getWidth() - */x;
        int cy =/* ll_add_event_screen.getBottom() -*/ y;

        float finalRadius = Math.max(sv_add_event_screen.getWidth(),
                sv_add_event_screen.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(sv_add_event_screen, cx,
                cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                getSupportActionBar().hide();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sv_add_event_screen.setVisibility(View.INVISIBLE);

                Intent toCalendar_Screen = new Intent(getApplicationContext(),
                        Calendar_Screen.class);

                int day = startDate.getDayOfMonth();
                int month = startDate.getMonthValue();
                int year = startDate.getYear();

                toCalendar_Screen.putExtra("day", day);
                toCalendar_Screen.putExtra("month", month);
                toCalendar_Screen.putExtra("year", year);

//                startActivity(toCalendar_Screen);
                Edit_Event_Screen.super.onBackPressed();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        circularReveal.setDuration(800);
        circularReveal.start();
    }

    public void animate(ViewGroup viewGroup) {
        AutoTransition trans = new AutoTransition();
//        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

        Slide slide = new Slide(Gravity.TOP);
        TransitionManager.beginDelayedTransition(viewGroup, trans);
//        TransitionManager.beginDelayedTransition(viewGroup, trans);

    }

    ProgressDialog deletingProgressDialog;

    @Override
    public void onAddEventClick(MenuItem item) {
        if (editMode){
            absoluteDelete(chain_key, () -> Edit_Event_Screen.super.onAddEventClick(item));
        }
        else {
            super.onAddEventClick(item);
        }
    }

    public void absoluteDelete(String key,  OnDeleteFinishedCallback onDeleteFinishedCallback){
        DatabaseReference allEventsDatabase = FirebaseUtils.allEventsDatabase;

        CircularProgressIndicator circularProgressIndicator = new CircularProgressIndicator(this);
        circularProgressIndicator.setIndicatorDirection(CircularProgressIndicator.INDICATOR_DIRECTION_CLOCKWISE);
        circularProgressIndicator.show();

        deletingProgressDialog = new ProgressDialog(this);

        deletingProgressDialog.setMessage("Editing event");
        deletingProgressDialog.setIndeterminate(true);
        deletingProgressDialog.show();

        Query query = allEventsDatabase.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    data.getRef().removeValue();
                }
//                onDeleteFinishedCallback.onDeleteFinished();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference eventsDatabaseReference = FirebaseUtils.eventsDatabase;

        eventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot date : snapshot.getChildren()){

                    Log.d("murad", "========================================================");
                    Log.d("murad", "Date key is " + date.getKey());
                    Log.d("murad", "events on this date: " + date.getChildrenCount());

                    for (DataSnapshot event : date.getChildren()){

                        Log.d("murad", "Event key is " + date.getKey());
                        Log.d("murad", event.getValue(CalendarEvent.class).toString());

                        if (event.child(UtilsCalendar.KEY_EVENT_CHAIN_ID).getValue(String.class).equals(key)){
                            Log.d("murad", "Event found");
                            event.getRef().removeValue();
                        }
                    }


                    Log.d("murad", "========================================================");
                }
                onDeleteFinishedCallback.onDeleteFinished();
                circularProgressIndicator.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnDeleteFinishedCallback {
        void onDeleteFinished();
    }
}