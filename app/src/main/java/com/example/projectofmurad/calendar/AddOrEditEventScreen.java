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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.ColorPickerDialog;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

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

    private TextView btn_repeat;

    private CalendarEvent event = new CalendarEvent();

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private EventFrequencyViewModel eventFrequencyViewModel;

    private boolean editMode = false;

    private LoadingDialog loadingDialog;

    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    int x;
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

        eventFrequencyViewModel = new ViewModelProvider(this).get(EventFrequencyViewModel.class);
        startObserving();

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

        btn_repeat = findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = EventFrequencyDialogFragment.TAG + startDateTime.toLocalDate().toString();

                if (getSupportFragmentManager().findFragmentByTag(TAG) == null){
                    EventFrequencyDialogFragment chooseEventFrequency_dialogFragment
                            = EventFrequencyDialogFragment.newInstance(startDateTime.toLocalDate());

                    chooseEventFrequency_dialogFragment.show(getSupportFragmentManager(), TAG);
                }
            }
        });
    }

    // method to inflate the options menu when
    // the user opens the menu for the first timeData
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
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

    public void createSaveDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText("Save");

        TextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                Log.d(Utils.EVENT_TAG, "starting editing single event with privateId " + event.getChainId());
                absoluteDeleteSingleEvent(event.getPrivateId());
            }
        });

        TextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                Log.d(Utils.EVENT_TAG, "starting editing all events with chainId " + event.getChainId());

                absoluteDeleteAllEventsInChain(event.getChainId());
            }
        });

        bottomSheetDialog.show();
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

    public void onAddEventClick() {
        String name = Utils.getText(et_name);
        String description = Utils.getText(et_description);
        String place = Utils.getText(et_place);

        if(!checkFields(name, description, place)) {
            return;
        }

        if (editMode) {
            if (event.isSingle()) {
                absoluteDeleteSingleEvent(event.getChainId());
            }
            else {
                createSaveDialog();
            }
        }
        else {
            uploadEvent();
        }
    }

    public void uploadEvent() {
        String name = Utils.getText(et_name);
        String description = Utils.getText(et_description);
        String place = Utils.getText(et_place);

        event.addDefaultParams(selectedColor, name, description, place, startDateTime, endDateTime);

        DatabaseReference eventsDatabase = FirebaseUtils.getEventsForDateRef(startDateTime.toLocalDate());

        String chain_key = "Event" + eventsDatabase.push().getKey();
        event.setChainId(chain_key);

        boolean success = true;

        Log.d(Utils.EVENT_TAG, "uploading event " + event);

        if(event.isRowEvent()) {
            event.updateChainStartDate(startDateTime.toLocalDate());
            event.updateChainEndDate(endDateTime.toLocalDate());
            Log.d(Utils.EVENT_TAG, event.toString());
            addEventToFirebase(event, event.getChainId());
        }
        else if(event.getFrequencyType().name().endsWith("AMOUNT")){
            success = addEventForTimesAdvanced(event);
        }
        else if(event.getFrequencyType().name().endsWith("END")){
            success = addEventForUntilAdvanced(event);
        }

        if(success){
            FCMSend.sendNotificationsToAllUsersWithTopic(this, event, editMode ? Utils.EDIT_EVENT_NOTIFICATION_CODE : Utils.ADD_EVENT_NOTIFICATION_CODE);
            if (switch_alarm.isChecked()){
                MyAlarmManager.addAlarm(this, event, 0);
            }
        }

        Intent toCalendar_Screen = new Intent(getApplicationContext(), MainActivity.class);
        toCalendar_Screen.setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT);

        int day = event.receiveChainStartDate().getDayOfMonth();
        int month = event.receiveChainStartDate().getMonth().getValue();
        int year = event.receiveChainStartDate().getYear();

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

    public void addEventToFirebase(@NonNull CalendarEvent event, String chain_key) {

        LocalDate start_date = event.receiveStartDate();
        LocalDate end_date = event.receiveEndDate();

        Log.d("murad", "start_date of event: " + event.getStartDate());
        Log.d("murad", "end_date of event: " + event.getEndDate());

        DatabaseReference eventsDatabase = FirebaseUtils.getEventsDatabase();

        String private_key = "Event" + eventsDatabase.push().getKey();

        event.setPrivateId(private_key);
        event.setChainId(chain_key);

        Log.d("murad", "PRIVATE ID IS " + event.getPrivateId());
        Log.d("murad", "CHAIN ID IS " + event.getChainId());

        Log.d("murad", "event in addEventToFirebaseForTextWithPUSH" + event);

        FirebaseUtils.getAllEventsDatabase().child(event.getPrivateId()).setValue(event);

        do {
            eventsDatabase = FirebaseUtils.getEventsForDateRef(start_date);

            eventsDatabase.child(event.getPrivateId()).child(CalendarEvent.KEY_EVENT_CHAIN_ID).setValue(event.getChainId());
            eventsDatabase.child(event.getPrivateId()).child(CalendarEvent.KEY_EVENT_START).setValue(event.getStart());

            start_date = start_date.plusDays(1);
        }
        while(!start_date.isAfter(end_date));


        if (switch_alarm.isChecked()){
            FirebaseUtils.isMadrich().observe(this,
                    isMadrich -> FirebaseUtils.getCurrentUserTrackingRef(event.getPrivateId()).child("attend").setValue(isMadrich));
        }
    }

    public boolean addEventForTimesAdvanced(@NonNull CalendarEvent event){

        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "******************************************************************************************");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, " ");
        Log.d(Utils.EVENT_TAG, "ADDING BY AMOUNT STARTED");

        String chain_key = event.getChainId();

        CalendarEvent.FrequencyType frequencyType = event.getFrequencyType();
        Log.d(Utils.EVENT_TAG, "FREQUENCYTYPE IS " + frequencyType);

        int frequency = event.getFrequency();
        Log.d(Utils.EVENT_TAG, "FREQUENCY IS " + (frequency));

        int amount = event.getAmount();
        Log.d(Utils.EVENT_TAG, "AMOUNT IS " + amount);

        boolean isLast = event.isLast();
        Log.d(Utils.EVENT_TAG, "IS LAST = " + isLast);

        LocalDate tmp = event.receiveStartDate();

        LocalDate startDate = event.receiveStartDate();
        LocalDate endDate = event.receiveEndDate();

        event.updateChainStartDate(startDate);

        Period range = startDate.until(endDate);

        int day = event.getDay();
        int weekNumber = event.getWeekNumber();
        int dayOfWeekPosition = event.getDayOfWeekPosition();
        DayOfWeek dayOfWeek = DayOfWeek.of(dayOfWeekPosition + 1);
        List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

        LocalDate absolute_end_date = tmp;

        switch(frequencyType) {
            case DAY_BY_AMOUNT:

                if(range.getDays() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                for(int i = 0; i < amount; i++) {

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    Log.d(Utils.EVENT_TAG, "CIRCLE NUMBER " + (i+1));

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusDays(frequency);

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");

                }

                break;

            case DAY_OF_WEEK_BY_AMOUNT:

                if(range.getDays() >= 7 * frequency){
                    //                    createBottomSheetDialog();
                    return false;
                }



/*
                while (!event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {
                    // get TemporalAdjuster with
                    // the next in month adjuster
*//*                        TemporalAdjuster temporalAdjuster
                                = TemporalAdjusters.nextOrSame(
                                DayOfWeek.WEDNESDAY);

                        WeekFields.of(startDate).weekOfMonth();*//*

                    Log.d(Utils.EVENT_TAG, "Going over days to find first occurrence of selected Day Of Week " + CalendarUtils.DateToTextForFirebase(tmp));
                    tmp = tmp.plusDays(1);

                }

                Log.d(Utils.EVENT_TAG, "");
                Log.d(Utils.EVENT_TAG, "FOUND!!!");
                Log.d(Utils.EVENT_TAG, "");

                startDate = tmp;
                endDate = tmp.plus(range);

                event.updateStartDate(startDate);
                event.updateEndDate(endDate);

                event.updateFrequency_start(tmp);

                        *//*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*//*

                private_key = eventsDatabase.push().getKey();
                event.setPrivate_id(private_key);

                addEventToFirebaseForTextWithPUSH(event);
                Log.d(Utils.EVENT_TAG, "___________________________________________________________");*/

                boolean first = true;

                for(int i = 0; i < amount; i++) {

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    Log.d(Utils.EVENT_TAG, "CIRCLE NUMBER " + (i+1));

                    /*for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j) && tmp.getDayOfWeek().getValue() == j+1) {

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStartDate(startDate);
                            event.updateEndDate(endDate);

                            *//*eventsDatabase = FirebaseUtils.getEventsDatabase();
                            eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*//*

                            String private_key = eventsDatabase.push().getKey();
                            event.setPrivate_id(private_key);

                            addEventToFirebaseForTextWithPUSH(event);

                        }

                        tmp = tmp.plusDays(1);
                    }*/


                    do{

                        Log.d(Utils.EVENT_TAG, "___________________________________________________________");

                        if(event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {
                            // get TemporalAdjuster with
                            // the next in month adjuster
/*                        TemporalAdjuster temporalAdjuster
                                = TemporalAdjusters.nextOrSame(
                                DayOfWeek.WEDNESDAY);

                                WeekFields.of(startDate).weekOfMonth();*/

                            Log.d(Utils.EVENT_TAG, "");
                            Log.d(Utils.EVENT_TAG, "FOUND!!! " + CalendarUtils.DateToTextLocal(tmp));
                            Log.d(Utils.EVENT_TAG, "");

                            if(first){
                                event.updateChainStartDate(tmp);
                                first = false;
                            }

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStartDate(startDate);
                            event.updateEndDate(endDate);

                        /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                            addEventToFirebase(event, chain_key);
                            Log.d(Utils.EVENT_TAG, "___________________________________________________________");

                        }

                        Log.d(Utils.EVENT_TAG, "Going over days to find first occurrence of selected Day Of Week " + CalendarUtils.DateToTextForFirebase(tmp));
                        tmp = tmp.plusDays(1);


                    }
                    while (tmp.plusDays(1).getDayOfWeek().getValue() != DayOfWeek.MONDAY.getValue());
/*
                    while (tmp.plusDays(1).getDayOfWeek().getValue() != DayOfWeek.MONDAY.getValue()){

                        while (!event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {
                            // get TemporalAdjuster with
                            // the next in month adjuster
*/
/*                        TemporalAdjuster temporalAdjuster
                                = TemporalAdjusters.nextOrSame(
                                DayOfWeek.WEDNESDAY);

                        WeekFields.of(startDate).weekOfMonth();*//*


                            tmp = tmp.plusDays(1);

                        }

                        startDate = tmp;
                        endDate = tmp.plus(range);

                        event.updateStartDate(startDate);
                        event.updateEndDate(endDate);

                        */
/*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*//*


                        private_key = eventsDatabase.push().getKey();
                        event.setPrivate_id(private_key);

                        addEventToFirebaseForTextWithPUSH(event);
                        Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    }
*/
                    tmp = tmp.plusWeeks(frequency);
                    tmp = tmp.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                }

                break;

            case DAY_AND_MONTH_BY_AMOUNT:

                if(/*endDate.getDayOfMonth() >= day && */ range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {

                    if(tmp.lengthOfMonth() >= day) {
                        if(isLast){

                            tmp = tmp.with(TemporalAdjusters.lastDayOfMonth());
                            Log.d(Utils.EVENT_TAG, "Last date of current month is " + CalendarUtils.DateToTextForFirebase(tmp));



                            /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                        }
                        else {

                            tmp = tmp.withDayOfMonth(day);
                            Log.d(Utils.EVENT_TAG, "The " + day + " day of current month is " + CalendarUtils.DateToTextForFirebase(tmp));

                            /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                        }

                        startDate = tmp;
                        endDate = tmp.plus(range);

                        event.updateStartDate(startDate);
                        event.updateEndDate(endDate);

                        addEventToFirebase(event, chain_key);
                    }

                    //                    tmp = tmp.with(TemporalAdjusters.firstDayOfNextMonth());
//                    Log.d(Utils.EVENT_TAG, "The first day of next month is " + CalendarUtils.DateToTextForFirebase(tmp));

                    tmp = tmp.plusMonths(frequency);
                    Log.d(Utils.EVENT_TAG, "The next month is " + CalendarUtils.DateToTextForFirebase(tmp));
                }

                break;

            case DAY_OF_WEEK_AND_MONTH_BY_AMOUNT:

                if(range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {
                    Log.d(Utils.EVENT_TAG, "--------------------------------------------------------------");
                    Log.d(Utils.EVENT_TAG, CalendarUtils.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                        /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusMonths(frequency);

                    Log.d(Utils.EVENT_TAG, "the next month is " + CalendarUtils.DateToTextOnline(tmp));
/*                    if(weekNumber > 4) {

                        tmp = CalendarUtils.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());
                    }
                    else {

                        tmp = CalendarUtils.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()
                                + " is " + CalendarUtils.DateToTextOnline(tmp));

                        tmp = CalendarUtils.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale() +
                                " is on " + CalendarUtils.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }*/

                }

                /*if(weekNumber > 4) {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", CalendarUtils.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

                        tmp = tmp.plusMonths(frequency);
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + CalendarUtils.DateToTextOnline(tmp));

                        tmp = CalendarUtils.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());

                    }
                }
                else {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                        Log.d("frequency_dayOfWeek_and_month", CalendarUtils.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

//                      event.setTimestamp(0);

                        tmp = tmp.plusMonths(frequency);
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + CalendarUtils.DateToTextOnline(tmp));

                        tmp = CalendarUtils.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()
                                + " is " + CalendarUtils.DateToTextOnline(tmp));

                        tmp = CalendarUtils.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale() +
                                " is on " + CalendarUtils.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }
                }*/
                break;

            case DAY_AND_YEAR_BY_AMOUNT:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                if(event.receiveStartDate().getMonth() == Month.FEBRUARY && day == 29){
                    event.setFrequency(4);
                    frequency = 4;

                    Toast.makeText(getApplicationContext(), "This event will repeat every 4 years", Toast.LENGTH_SHORT).show();
                }

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {

                    if (isLast){
                        tmp = tmp.with(TemporalAdjusters.lastDayOfMonth());
                    }
                    else {
                        tmp = tmp.withDayOfMonth(day);
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                        /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusYears(frequency);
                }

                break;

            case DAY_OF_WEEK_AND_YEAR_BY_AMOUNT:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", CalendarUtils.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                        /*eventsDatabase = FirebaseUtils.getEventsDatabase();
                        eventsDatabase = eventsDatabase.child(CalendarUtils.DateToTextForFirebase(tmp));*/

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusYears(frequency);
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + CalendarUtils.DateToTextOnline(tmp));
/*                    if(weekNumber > 4) {

                        tmp = CalendarUtils.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());
                    }
                    else {

                        tmp = CalendarUtils.getFirstDayWithDayOfWeek(tmp, event.getDayOfWeekPosition());
                        Log.d("frequency_dayOfWeek_and_month", "the first day of this month on " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()
                                + " is " + CalendarUtils.DateToTextOnline(tmp));

                        tmp = CalendarUtils.getNextOccurrence(tmp, event.getWeekNumber());
                        Log.d("frequency_dayOfWeek_and_month", "the " + weekNumber + " occurrence of " +
                                DayOfWeek.of(event.getDayOfWeekPosition() + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale() +
                                " is on " + CalendarUtils.DateToTextOnline(tmp));
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");

                    }*/

                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + event.getFrequencyType());
        }

        event.updateChainEndDate(endDate);

//        FirebaseUtils.getAllEventsDatabase().child(event.getChainId()).setValue(event);

        Log.d(Utils.EVENT_TAG, "ADDING BY AMOUNT FINISHED");
        Log.d(Utils.EVENT_TAG, " ");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "******************************************************************************************");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");

        return true;
    }

    public boolean addEventForUntilAdvanced(@NonNull CalendarEvent event){

        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "******************************************************************************************");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, " ");
        Log.d(Utils.EVENT_TAG, "ADDING BY END STARTED");

        String chain_key = event.getChainId();

        int frequency = event.getFrequency();
        Log.d(Utils.EVENT_TAG, "FREQUENCY IS " + (frequency));

        LocalDate end = event.receiveChainEndDate();

        boolean isLast = event.isLast();
        Log.d(Utils.EVENT_TAG, "IS LAST = " + isLast);

        LocalDate tmp = event.receiveStartDate();

        LocalDate startDate = event.receiveStartDate();
        LocalDate endDate = event.receiveEndDate();

        event.updateChainStartDate(startDate);

        Period range = startDate.until(endDate);

        int day;
        int weekNumber;
        int dayOfWeekPosition;
        DayOfWeek dayOfWeek;

        LocalDate absolute_end_date = end;
        Log.d(Utils.EVENT_TAG, "Chosen end is " + CalendarUtils.DateToTextOnline(absolute_end_date));


        switch(event.getFrequencyType()) {
            case DAY_BY_END:

                if(range.getDays() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                int i = 1;

                do {

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    Log.d(Utils.EVENT_TAG, "CIRCLE NUMBER " + (i));

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusDays(frequency);

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    i++;
                }
                while(!tmp.isAfter(end));

                break;

            case DAY_OF_WEEK_BY_END:

                if(range.getDays() >= 7 * frequency){
                    //                    createBottomSheetDialog();
                    return false;
                }

                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

                boolean first = true;

                i = 1;

                do {

                    Log.d(Utils.EVENT_TAG, "___________________________________________________________");
                    Log.d(Utils.EVENT_TAG, "CIRCLE NUMBER " + i);

                    do{

                        Log.d(Utils.EVENT_TAG, "___________________________________________________________");

                        if(event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {

                            Log.d(Utils.EVENT_TAG, "");
                            Log.d(Utils.EVENT_TAG, "FOUND!!! " + CalendarUtils.DateToTextLocal(tmp));
                            Log.d(Utils.EVENT_TAG, "");

                            if(first){
                                event.updateChainStartDate(tmp);
                                first = false;
                            }

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStartDate(startDate);
                            event.updateEndDate(endDate);

                            addEventToFirebase(event, chain_key);
                            Log.d(Utils.EVENT_TAG, "___________________________________________________________");

                        }

                        Log.d(Utils.EVENT_TAG, "Going over days to find first occurrence of selected Day Of Week " + CalendarUtils.DateToTextForFirebase(tmp));
                        tmp = tmp.plusDays(1);

                    }
                    while (tmp.plusDays(1).getDayOfWeek().getValue() != DayOfWeek.MONDAY.getValue());

                    tmp = tmp.plusWeeks(frequency);
                    tmp = tmp.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

                    i++;
                }
                while(!tmp.isAfter(end));

                break;

            case DAY_AND_MONTH_BY_END:

                day = event.getDay();

                if(/*endDate.getDayOfMonth() >= day && */ range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                tmp = tmp.withDayOfMonth(1);

                do {
                    if(tmp.lengthOfMonth() >= day) {

                        if(isLast){

                            tmp = tmp.with(TemporalAdjusters.lastDayOfMonth());
                            Log.d(Utils.EVENT_TAG, "Last date of current month is " + CalendarUtils.DateToTextForFirebase(tmp));

                        }
                        else {

                            tmp = tmp.withDayOfMonth(day);
                            Log.d(Utils.EVENT_TAG, "The " + day + " day of current month is " + CalendarUtils.DateToTextForFirebase(tmp));

                        }

                        startDate = tmp;
                        endDate = tmp.plus(range);

                        event.updateStartDate(startDate);
                        event.updateEndDate(endDate);

                        addEventToFirebase(event, chain_key);
                    }

                    tmp = tmp.plusMonths(frequency);
                    Log.d(Utils.EVENT_TAG, "The next month is " + CalendarUtils.DateToTextForFirebase(tmp));

                }
                while(!tmp.isAfter(end));

                break;

            case DAY_OF_WEEK_AND_MONTH_BY_END:

                if(range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                weekNumber = event.getWeekNumber();
                Log.d(Utils.EVENT_TAG, "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d(Utils.EVENT_TAG, "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d(Utils.EVENT_TAG, "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                do {
                    Log.d(Utils.EVENT_TAG, "--------------------------------------------------------------");
                    Log.d(Utils.EVENT_TAG, CalendarUtils.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusMonths(frequency);

                    Log.d(Utils.EVENT_TAG, "the next month is " + CalendarUtils.DateToTextOnline(tmp));

                }
                while(!tmp.isAfter(end));

                break;

            case DAY_AND_YEAR_BY_END:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                day = event.getDay();

                if(event.receiveStartDate().getMonth() == Month.FEBRUARY && day == 29){
                    event.setFrequency(4);

                    Toast.makeText(getApplicationContext(), "This event will repeat every 4 years", Toast.LENGTH_SHORT).show();
                }

                do {

                    if (isLast){
                        tmp = tmp.with(TemporalAdjusters.lastDayOfMonth());
                    }
                    else {
                        tmp = tmp.withDayOfMonth(day);
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusYears(frequency);
                }
                while(!tmp.isAfter(end));

                break;

            case DAY_OF_WEEK_AND_YEAR_BY_END:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                weekNumber = event.getWeekNumber();
                Log.d(Utils.EVENT_TAG, "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d(Utils.EVENT_TAG, "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d(Utils.EVENT_TAG, "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                do {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", CalendarUtils.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStartDate(startDate);
                    event.updateEndDate(endDate);

                    addEventToFirebase(event, chain_key);

                    tmp = tmp.plusYears(frequency);
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + CalendarUtils.DateToTextOnline(tmp));

                }
                while(!tmp.isAfter(end));

                break;

        }

        Log.d(Utils.EVENT_TAG, "Absolute end date after switch is " + CalendarUtils.DateToTextOnline(endDate));

        event.updateChainEndDate(endDate);


//        FirebaseUtils.getAllEventsDatabase().child(event.getChainId()).setValue(event);

        Log.d(Utils.EVENT_TAG, "ADDING BY AMOUNT FINISHED");
        Log.d(Utils.EVENT_TAG, " ");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "******************************************************************************************");
        Log.d(Utils.EVENT_TAG, "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d(Utils.EVENT_TAG, "------------------------------------------------------------------------------------------");

        return true;
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

    public void createBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_event_frequency);
        bottomSheetDialog.setTitle("Repeat");
        bottomSheetDialog.setCancelable(true);

        Button btn_error = bottomSheetDialog.findViewById(R.id.btn_error);
        btn_error.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setDismissWithAnimation(true);
        bottomSheetDialog.show();
    }

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

    public void absoluteDeleteSingleEvent(String private_key) {
        loadingDialog.setMessage("Editing event");
        loadingDialog.show();

        FirebaseUtils.deleteAll(FirebaseUtils.getEventsDatabase(), private_key, this::uploadEvent);
    }

    public void absoluteDeleteAllEventsInChain(String chain_key) {
        loadingDialog.setMessage("Editing event");
        loadingDialog.show();

        FirebaseUtils.getEventsDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot date : snapshot.getChildren()){
                    for (DataSnapshot event : date.getChildren()){
                        String eventChainId = event.child(CalendarEvent.KEY_EVENT_CHAIN_ID).getValue(String.class);
                        if (Objects.equals(eventChainId, chain_key)){
                            event.getRef().removeValue();
                        }
                    }
                }

                uploadEvent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startObserving();
        getSupportActionBar().show();
    }

    public void startObserving() {
        eventFrequencyViewModel.frequencyType.observe(this, frequencyType -> {
            Log.d(Utils.EVENT_TAG, "event's frequency type changed");
            Log.d(Utils.EVENT_TAG, "event's frequency is " + frequencyType);
            event.setFrequencyType(frequencyType);
        });
        eventFrequencyViewModel.frequency.observe(this, frequency -> event.setFrequency(frequency));
        eventFrequencyViewModel.amount.observe(this, amount -> {
            event.setAmount(amount);
            Log.d(Utils.EVENT_TAG, "amount changed");
            Log.d(Utils.EVENT_TAG, "amount is " + amount);
        });
        eventFrequencyViewModel.msg.observe(this, msg -> {
            Log.d(Utils.EVENT_TAG, "message changed");
            Log.d(Utils.EVENT_TAG, msg);
            btn_repeat.setText(msg);
        });
        eventFrequencyViewModel.end.observe(this, end -> event.updateChainEndDate(end));
        eventFrequencyViewModel.last.observe(this, last -> event.setLast(last));
        eventFrequencyViewModel.day.observe(this, day -> event.setDay(day));
        eventFrequencyViewModel.dayOfWeekPosition.observe(this, dayOfWeekPosition -> event.setDayOfWeekPosition(dayOfWeekPosition));
        eventFrequencyViewModel.array_frequencyDayOfWeek.observe(this, array_frequencyDayOfWeek -> event.setArray_frequencyDayOfWeek(array_frequencyDayOfWeek));
        eventFrequencyViewModel.weekNumber.observe(this, weekNumber -> event.setWeekNumber(weekNumber));
        eventFrequencyViewModel.month.observe(this, month -> event.setMonth(month));
    }

}