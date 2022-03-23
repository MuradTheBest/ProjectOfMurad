package com.example.projectofmurad.calendar;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class Add_Event_Screen extends MySuperTouchActivity {

    private DatabaseReference eventsDatabase;

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

    protected PendingIntent pendingIntent;

    protected Intent alarm;

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
                viewTreeObserver.addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                circularRevealActivity();
                                sv_add_event_screen.getViewTreeObserver().removeOnGlobalLayoutListener(
                                        this);
                            }

                        });
            }

        }

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustomWithExposedDropdown(this);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = FirebaseUtils.eventsDatabase;

        selectedColor = getColor(R.color.colorAccent);

        /*        start_hour = LocalTime.now().getHour();
        start_min = LocalTime.now().getMinute();
        end_hour = LocalTime.now().getHour();
        end_min = LocalTime.now().getMinute();*/

        start_hour = 8;
        start_min = 0;

        startTime = LocalTime.of(start_hour, start_min);

        end_hour = 9;
        end_min = 0;

        endTime = LocalTime.of(end_hour, end_min);

        start_day = end_day = gotten_intent.getIntExtra("day", 0);
        start_month = end_month = gotten_intent.getIntExtra("month", 0);
        start_year = end_year = gotten_intent.getIntExtra("year", 0);

        startDate = endDate = LocalDate.of(start_year, start_month, start_day);

        startDateTime = LocalDateTime.of(startDate, startTime);
        endDateTime = LocalDateTime.of(endDate, endTime);

        Log.d("murad", "Receiving selectedDate " + start_day + " " + start_month + " " + start_year);

        sv_add_event_screen = findViewById(R.id.sv_add_event_screen);

        et_name = findViewById(R.id.et_name);
        et_name.setOnFocusChangeListener(this);

        et_description = findViewById(R.id.et_description);
        et_description.setOnFocusChangeListener(this);

        et_place = findViewById(R.id.et_place);
        et_place.setOnFocusChangeListener(this);

        ib_color = findViewById(R.id.ib_color);
        ib_color.setOnClickListener(v -> createColorPickerDialog());

        rl_event_setup = findViewById(R.id.rl_event_setup);

        switch_all_day = findViewById(R.id.switch_all_day);
        switch_all_day.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    event.setTimestamp(isChecked ? 0 : event.receiveStart_time().toSecondOfDay());
                    timestamp = isChecked ? 0 : ((start_hour * 60 + start_min) * 60);
/*                    int i = event.receiveStart_time().toSecondOfDay();
                    if(isChecked){
                        event.setTimestamp(0);
                    }
                    else{
                        event.setTimestamp(i);
                    }*/
                    btn_choose_start_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    btn_choose_end_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
//                    animate(ll_add_event_screen);
                    animate(rl_event_setup);
                }
        );

        switch_alarm = findViewById(R.id.switch_alarm);

        event_time_picker = findViewById(R.id.event_time_picker);
        event_time_picker.setIs24HourView(true);
        event_time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                start_hour = hourOfDay;
                start_min = minute;
                LocalTime startTime = LocalTime.of(hourOfDay, minute);
                btn_choose_start_time.setText(UtilsCalendar.TimeToText(startTime));
            }
        });

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(UtilsCalendar.DateToTextLocal(startDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start_year = startDateTime.getYear();
                start_month = startDateTime.getMonthValue();
                start_day = startDateTime.getDayOfMonth();

                startDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this,
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

                                startDateTime.withYear(start_year);
                                startDateTime.withMonth(start_month);
                                startDateTime.withDayOfMonth(start_day);

                                String date_text = UtilsCalendar.DateToTextLocal(startDate);
                                btn_choose_start_date.setText(date_text);

                                if (startDate.isAfter(endDate)) {
                                    endDate = startDate;
                                    btn_choose_end_date.setText(UtilsCalendar.DateToTextLocal(endDate));

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

/*
        btn_choose_start_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){


                            startDatePickerDialog = new DatePickerDialog(getApplicationContext(),
                                    AlertDialog.THEME_HOLO_LIGHT,
                                    (view, year, month, dayOfMonth) -> {
                                        month = month+1;

                                        start_year = year;
                                        start_month = month;
                                        start_day = dayOfMonth;

                                        startDate = LocalDate.of(start_year, start_month, start_day);
                                        String date_text = UtilsCalendar.DateToTextLocal(startDate);
                                        btn_choose_start_date.setText(date_text);
                                    },
                                    selected_year, selected_month-1, selected_day);

                            startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
                            startDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

                            startDatePickerDialog.show();
                        }
                        else{
                            startDatePickerDialog.dismiss();
                        }
                    }
                });
*/

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(UtilsCalendar.DateToTextLocal(endDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                end_year = endDate.getYear();
                end_month = endDate.getMonthValue();
                end_day = endDate.getDayOfMonth();

                endDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year, int month,
                                                  int dayOfMonth) {
                                month = month + 1;

                                end_year = year;
                                end_month = month;
                                end_day = dayOfMonth;

                                endDate = LocalDate.of(end_year, end_month, end_day);
                                String date_text = UtilsCalendar.DateToTextLocal(endDate);
                                btn_choose_end_date.setText(date_text);


                                if (endDate.isBefore(startDate)) {
                                    startDate = endDate;
                                    btn_choose_start_date.setText(
                                            UtilsCalendar.DateToTextLocal(startDate));

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

        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize time picker dialog
                startTimePickerDialog = new TimePickerDialog(Add_Event_Screen.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                start_hour = hourOfDay;
                                start_min = minute;

                                startTime = LocalTime.of(start_hour, start_min);

                                String time_text = UtilsCalendar.TimeToText(startTime);
                                btn_choose_start_time.setText(time_text);

                                if (startTime.isAfter(endTime) && !startDate.isAfter(endDate)) {
                                    endTime = startTime.plusHours(1);
                                    btn_choose_end_time.setText(UtilsCalendar.TimeToText(endTime));
                                }
                            }
                        },
                        start_hour, start_min, true);

                startTimePickerDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

                startTimePickerDialog.updateTime(start_hour, start_min);
                startTimePickerDialog.show();
            }
        });

        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                endTimePickerDialog = new TimePickerDialog(Add_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        (view, hourOfDay, minute) -> {
                            end_hour = hourOfDay;
                            end_min = minute;

                            endTime = LocalTime.of(end_hour, end_min);

                            String time_text = UtilsCalendar.TimeToText(endTime);
                            btn_choose_end_time.setText(time_text);

                            if (endTime.isBefore(startTime) && !endDate.isBefore(startDate)) {
                                startTime = endTime.minusHours(1);
                                btn_choose_start_time.setText(UtilsCalendar.TimeToText(startTime));
                            }
                        },
                        end_hour, end_min, true);

                endTimePickerDialog.updateTime(end_hour, end_min);
                endTimePickerDialog.show();
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

                intentToChooseEventFrequencyDialogCustom = new Intent(Add_Event_Screen.this,
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
                ;
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


}