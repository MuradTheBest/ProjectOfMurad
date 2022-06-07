package com.example.projectofmurad.calendar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.ColorPickerDialog;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * The type Add or edit event screen.
 */
public class AddOrEditEventScreen extends AppCompatActivity {

    private TextInputLayout et_name;
    private TextInputLayout et_place;
    private TextInputLayout et_description;

    private SwitchMaterial switch_all_day;
    private SwitchMaterial switch_alarm;

    private MaterialButton btn_choose_start_time;
    private MaterialButton btn_choose_start_date;

    private RelativeLayout rl_event_setup;
    private ScrollView sv_add_event_screen;

    private MaterialButton btn_choose_end_time;
    private MaterialButton btn_choose_end_date;

    private CalendarEvent event = new CalendarEvent();

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private boolean editMode = false;

    private LoadingDialog loadingDialog;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    /**
     * The X.
     */
    int x;
    /**
     * The Y.
     */
    int y;

    private int selectedColor;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_do_not_move, R.anim.anim_do_not_move);
        setContentView(R.layout.activity_add_event_screen_linear_layout);

        loadingDialog = new LoadingDialog(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().hide();

        Intent gotten_intent = getIntent();

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
                                sv_add_event_screen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

                        });
            }

        }

        et_name = findViewById(R.id.et_name);
        et_name.setEndIconOnClickListener(v -> createColorPickerDialog());

        et_description = findViewById(R.id.et_description);
        et_place = findViewById(R.id.et_place);
        switch_alarm = findViewById(R.id.switch_alarm);

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_start_time = findViewById(R.id.btn_choose_start_time);
        btn_choose_end_time = findViewById(R.id.btn_choose_end_time);

        rl_event_setup = findViewById(R.id.rl_event_setup);

        switch_all_day = findViewById(R.id.switch_all_day);
        switch_all_day.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        event.setAllDay(isChecked);
                        btn_choose_start_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                        btn_choose_end_time.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                        animate(rl_event_setup);
                    }
                });

        int start_day = gotten_intent.getIntExtra("day", LocalDate.now().getDayOfMonth());
        int start_month = gotten_intent.getIntExtra("month", LocalDate.now().getMonthValue());
        int start_year = gotten_intent.getIntExtra("year", LocalDate.now().getYear());

        startDateTime = LocalDateTime.of(start_year, start_month, start_day, 8, 0);
        endDateTime = LocalDateTime.of(start_year, start_month, start_day, 9, 0);

        selectedColor = getColor(R.color.colorAccent);

        if (gotten_intent.hasExtra(CalendarEvent.KEY_EVENT)){
            editMode = true;

            Log.d("murad", "event is not null => Edit_Event_Screen");

            event = (CalendarEvent) gotten_intent.getSerializableExtra(CalendarEvent.KEY_EVENT);

            boolean allDay = event.isAllDay();
            switch_all_day.setChecked(allDay);

            startDateTime = event.receiveStartDateTime();
            endDateTime = event.receiveEndDateTime();

            Utils.setText(et_name, event.getName());
            Utils.setText(et_description, event.getDescription());
            Utils.setText(et_place, event.getPlace());

            selectedColor = event.getColor();
            et_name.setEndIconTintList(ColorStateList.valueOf(event.getColor()));

            switch_alarm.setChecked(MyAlarmManager.checkIfAlarmSet(this, event.getPrivateId()));
        }

        btn_choose_start_date.setText(CalendarUtils.DateToTextLocal(startDateTime.toLocalDate()));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDatePickerDialog = new DatePickerDialog(AddOrEditEventScreen.this,
                        /*AlertDialog.THEME_HOLO_LIGHT,*/
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month + 1;

                                startDateTime = startDateTime.withYear(year);
                                startDateTime = startDateTime.withMonth(month);
                                startDateTime = startDateTime.withDayOfMonth(dayOfMonth);

                                String date_text = CalendarUtils.DateToTextLocal(startDateTime.toLocalDate());
                                btn_choose_start_date.setText(date_text);

                                if (startDateTime.isAfter(endDateTime)) {

                                    endDateTime = startDateTime.plusDays(1);

                                    btn_choose_end_date.setText(CalendarUtils.DateToTextLocal(endDateTime.toLocalDate()));
                                    btn_choose_end_time.setText(CalendarUtils.TimeToText(endDateTime.toLocalTime()));
                                }

                                event.updateStartDateTime(startDateTime);
                                event.updateEndDateTime(endDateTime);
                            }
                        },
                        startDateTime.getYear(), startDateTime.getMonthValue() - 1, startDateTime.getDayOfMonth());

                startDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                startDatePickerDialog.updateDate(startDateTime.getYear(),
                        startDateTime.getMonthValue() - 1,
                        startDateTime.getDayOfMonth());
                Calendar calendar = Calendar.getInstance();
                startDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                Utils.createCustomDialog(startDatePickerDialog);

                startDatePickerDialog.show();
            }
        });

        btn_choose_end_date.setText(CalendarUtils.DateToTextLocal(endDateTime.toLocalDate()));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endDatePickerDialog = new DatePickerDialog(AddOrEditEventScreen.this,
                        /*android.R.style.ThemeOverlay_Material_Dialog,*/
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view1, int year, int month, int dayOfMonth) {
                                month = month + 1;

                                endDateTime = endDateTime.withYear(year);
                                endDateTime = endDateTime.withMonth(month);
                                endDateTime = endDateTime.withDayOfMonth(dayOfMonth);

                                String date_text = CalendarUtils.DateToTextLocal(endDateTime.toLocalDate());
                                btn_choose_end_date.setText(date_text);

                                if (endDateTime.isBefore(startDateTime)) {

                                    startDateTime = endDateTime.minusHours(1);

                                    btn_choose_start_date.setText(CalendarUtils.DateToTextLocal(startDateTime.toLocalDate()));
                                    btn_choose_start_time.setText(CalendarUtils.TimeToText(startDateTime.toLocalTime()));
                                }

                                event.updateStartDateTime(startDateTime);
                                event.updateEndDateTime(endDateTime);
                            }
                        },
                        endDateTime.getYear(), endDateTime.getMonthValue() - 1, endDateTime.getDayOfMonth());

                endDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                endDatePickerDialog.updateDate(endDateTime.getYear(),
                        endDateTime.getMonthValue() - 1,
                        endDateTime.getDayOfMonth());
                Calendar calendar = Calendar.getInstance();
                endDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                Utils.createCustomDialog(endDatePickerDialog);

                endDatePickerDialog.show();
            }
        });

        btn_choose_start_time.setText(CalendarUtils.TimeToText(startDateTime.toLocalTime()));
        btn_choose_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePickerDialog = new TimePickerDialog(AddOrEditEventScreen.this,
                        /*AlertDialog.THEME_HOLO_LIGHT,*/
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                startDateTime = startDateTime.withHour(hourOfDay);
                                startDateTime = startDateTime.withMinute(minute);

                                String time_text = CalendarUtils.TimeToText(startDateTime.toLocalTime());

                                Log.d("murad", "hour: " + hourOfDay);
                                Log.d("murad", "minute: " + minute);
                                Log.d("murad", "timeData: " + time_text);

                                btn_choose_start_time.setText(time_text);

                                if (startDateTime.isAfter(endDateTime)) {

                                    endDateTime = startDateTime.plusHours(1);

                                    btn_choose_end_date.setText(CalendarUtils.DateToTextLocal(endDateTime.toLocalDate()));

                                    btn_choose_end_time.setText(CalendarUtils.TimeToText(endDateTime.toLocalTime()));
                                }

                                event.updateStartDateTime(startDateTime);
                                event.updateEndDateTime(endDateTime);
                            }
                        },
                        startDateTime.getHour(), startDateTime.getMinute(), true);

//                startTimePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Utils.createCustomDialog(startTimePickerDialog);

                startTimePickerDialog.updateTime(startDateTime.getHour(), startDateTime.getMinute());
                startTimePickerDialog.show();
            }
        });

        btn_choose_end_time.setText(CalendarUtils.TimeToText(endDateTime.toLocalTime()));
        btn_choose_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimePickerDialog = new TimePickerDialog(AddOrEditEventScreen.this,
                        /*android.R.style.ThemeOverlay_Material_Dialog,*/
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endDateTime = endDateTime.withHour(hourOfDay);
                                endDateTime = endDateTime.withMinute(minute);

                                String time_text = CalendarUtils.TimeToText(endDateTime.toLocalTime());
                                btn_choose_end_time.setText(time_text);

                                if (endDateTime.isBefore(startDateTime)) {

                                    startDateTime = endDateTime.minusHours(1);

                                    btn_choose_start_date.setText(CalendarUtils.DateToTextLocal(startDateTime.toLocalDate()));
                                    btn_choose_start_time.setText(CalendarUtils.TimeToText(startDateTime.toLocalTime()));
                                }

                                event.updateStartDateTime(startDateTime);
                                event.updateEndDateTime(endDateTime);
                            }
                        },
                        endDateTime.getHour(), endDateTime.getMinute(), true);

                Utils.createCustomDialog(endTimePickerDialog);

                endTimePickerDialog.updateTime(endDateTime.getHour(), endDateTime.getMinute());
                endTimePickerDialog.show();
            }
        });

    }

    // method to inflate the options menu when
    // the user opens the menu for the first timeData
    @Override
    public boolean onCreateOptionsMenu(Menu menu ) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.action_save:
                onAddEventClick();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
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

        circularReveal.start();
    }

    /**
     * Animate.
     *
     * @param viewGroup the view group
     */
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

    /**
     * On add event click.
     */
    public void onAddEventClick() {
        String name = Utils.getText(et_name);
        String description = Utils.getText(et_description);
        String place = Utils.getText(et_place);

        if(!checkFields(name, description, place)) {
            return;
        }

        if (editMode) {
            absoluteDeleteSingleEvent(event.getPrivateId());
        }
        else {
            uploadEvent();
        }
    }

    /**
     * Upload event.
     */
    public void uploadEvent() {
        String name = Utils.getText(et_name);
        String description = Utils.getText(et_description);
        String place = Utils.getText(et_place);

        event.addDefaultParams(selectedColor, name, description, place, startDateTime, endDateTime);

        boolean success = true;

        Log.d(Utils.EVENT_TAG, "uploading event " + event);

        Log.d(Utils.EVENT_TAG, event.toString());
        addEventToFirebase(event);

        FCMSend.sendNotificationsToAllUsersWithTopic(this, event, editMode ? Utils.EDIT_EVENT_NOTIFICATION_CODE : Utils.ADD_EVENT_NOTIFICATION_CODE);
        if (switch_alarm.isChecked()){
            MyAlarmManager.addAlarm(this, event, 0);
        }

        Intent toCalendar_Screen = new Intent(getApplicationContext(), MainActivity.class);
        toCalendar_Screen.setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT);

        int day = event.receiveStartDate().getDayOfMonth();
        int month = event.receiveStartDate().getMonth().getValue();
        int year = event.receiveStartDate().getYear();

        toCalendar_Screen.putExtra("day", day);
        toCalendar_Screen.putExtra("month", month);
        toCalendar_Screen.putExtra("year", year);

        startActivity(toCalendar_Screen);
    }

    /**
     * Add event to firebase.
     *
     * @param event the event
     */
    public void addEventToFirebase(@NonNull CalendarEvent event) {

        LocalDate start_date = event.receiveStartDate();
        LocalDate end_date = event.receiveEndDate();

        Log.d("murad", "start_date of event: " + event.getStartDate());
        Log.d("murad", "end_date of event: " + event.getEndDate());

        DatabaseReference eventsDatabase = FirebaseUtils.getEventsDatabase();

        String private_key = "Event" + eventsDatabase.push().getKey();

        event.setPrivateId(private_key);

        Log.d("murad", "event in addEventToFirebaseForTextWithPUSH" + event);

        FirebaseUtils.getAllEventsDatabase().child(event.getPrivateId()).setValue(event);

        do {
            eventsDatabase = FirebaseUtils.getEventsForDateRef(start_date);
            eventsDatabase.child(event.getPrivateId()).setValue(event.getStart());

            start_date = start_date.plusDays(1);
        }
        while(!start_date.isAfter(end_date));


        if (switch_alarm.isChecked()){
            FirebaseUtils.isMadrich().observe(this,
                    isMadrich -> FirebaseUtils.getCurrentUserTrackingRef(event.getPrivateId()).child("attend").setValue(isMadrich));
        }
    }

    private boolean checkFields(@NonNull String name, String description, String place) {
        boolean editTextsFilled = true;

        if (name.isEmpty()) {
            et_name.setError(getString(R.string.invalid_email));
            editTextsFilled = false;
        }
        if(description.isEmpty()){
            et_description.setError(getString(R.string.invalid_password));
            editTextsFilled = false;
        }
        if(place.isEmpty()){
            et_place.setError(getString(R.string.invalid_password));
            editTextsFilled = false;
        }

        return editTextsFilled;
    }

    /**
     * Create color picker dialog.
     */
    protected void createColorPickerDialog() {
        ColorPickerDialog colorPicker = new ColorPickerDialog(this);

        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                selectedColor = color;
                et_name.setEndIconTintList(ColorStateList.valueOf(selectedColor));
            }

            @Override
            public void onCancel() {}
        });

        colorPicker.addListenerButton(getString(R.string.generate),
                (v, position, color) -> {
                    selectedColor = Utils.generateRandomColor();
                    et_name.setEndIconTintList(ColorStateList.valueOf(selectedColor));
                    colorPicker.dismissDialog();
                });

        colorPicker.show();
    }

    /**
     * Absolute delete single event.
     *
     * @param private_key the private key
     */
    public void absoluteDeleteSingleEvent(String private_key) {
        loadingDialog.setMessage("Editing event");
        loadingDialog.show();

        FirebaseUtils.deleteAll(FirebaseUtils.getEventsDatabase(), private_key, this::uploadEvent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().show();
    }

}