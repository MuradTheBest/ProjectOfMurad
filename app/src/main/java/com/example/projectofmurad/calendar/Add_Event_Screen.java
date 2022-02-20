package com.example.projectofmurad.calendar;

import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;

import com.example.projectofmurad.AlarmReceiver;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        sv_add_event_screen.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        chooseEventFrequencyDialog = new ChooseEventFrequencyDialogCustomWithExposedDropdown(this);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = FirebaseUtils.eventsDatabase;

        selectedColor = Color.GREEN;

        selected_day = gotten_intent.getIntExtra("day", 0);
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
                btn_choose_start_time.setText(Utils_Calendar.TimeToText(startTime));
            }
        });

        btn_choose_start_date = findViewById(R.id.btn_choose_start_date);
        btn_choose_start_date.setText(Utils_Calendar.DateToTextLocal(selectedDate));
        btn_choose_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month,
                                                  int dayOfMonth) {
                                month = month+1;

                                start_year = year;
                                start_month = month;
                                start_day = dayOfMonth;

                                startDate = LocalDate.of(start_year, start_month, start_day);
                                String date_text = Utils_Calendar.DateToTextLocal(startDate);
                                btn_choose_start_date.setText(date_text);

                            }
                        },
                        selected_year, selected_month-1, selected_day);

                startDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                startDatePickerDialog.updateDate(start_year, start_month-1, start_day);
                startDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

                startDatePickerDialog.show();
            }
        });

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
                                        String date_text = Utils_Calendar.DateToTextLocal(startDate);
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

        btn_choose_end_date = findViewById(R.id.btn_choose_end_date);
        btn_choose_end_date.setText(Utils_Calendar.DateToTextLocal(selectedDate));
        btn_choose_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endDatePickerDialog = new DatePickerDialog(Add_Event_Screen.this,
                        android.R.style.ThemeOverlay_Material_Dialog,
                        (view1, year, month, dayOfMonth) -> {
                            month = month+1;

                            end_year = year;
                            end_month = month;
                            end_day = dayOfMonth;

                            endDate = LocalDate.of(end_year, end_month, end_day);
                            String date_text = Utils_Calendar.DateToTextLocal(endDate);
                            btn_choose_end_date.setText(date_text);

                        },
                        selected_year, selected_month-1, selected_day);

                endDatePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                endDatePickerDialog.updateDate(end_year, end_month-1, end_day);
                endDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

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
                        (view, hourOfDay, minute) -> {
                            start_hour = hourOfDay;
                            start_min = minute;

                            LocalTime startTime = LocalTime.of(start_hour, start_min);

                            String time_text = Utils_Calendar.TimeToText(startTime);
                            btn_choose_start_time.setText(time_text);
                        },
                        start_hour, start_min, true);

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
                        (view, hourOfDay, minute) -> {
                            end_hour = hourOfDay;
                            end_min = minute;

                            LocalTime endTime = LocalTime.of(end_hour, end_min);

                            String time_text = Utils_Calendar.TimeToText(endTime);
                            btn_choose_end_time.setText(time_text);
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

                intentToChooseEventFrequencyDialogCustom = new Intent(Add_Event_Screen.this, ChooseEventFrequencyDialogCustomWithExposedDropdown.class);
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

      /*          Fragment prev = getSupportFragmentManager().findFragmentByTag(ChooseEventFrequency_Screen.TAG);
                if (prev != null) {
                    prev.show(ft, ChooseEventFrequency_Screen.TAG);
//                    ((ChooseEventFrequency_Screen) prev).show(getSupportFragmentManager(), ChooseEventFrequency_Screen.TAG);
//                    fragmentManager.beginTransaction().show(prev).commit();
                    Toast.makeText(getApplicationContext(), "Showing created dialog", Toast.LENGTH_SHORT).show();

                    ft.show(prev);

                }
                else{
                    ft.setReorderingAllowed(true)
                            .add(ChooseEventFrequency_Screen.class, bundle, ChooseEventFrequency_Screen.TAG)
                            .commit();
                    Toast.makeText(getApplicationContext(), "Creating dialog", Toast.LENGTH_SHORT).show();

                }
                ft.addToBackStack(null);*/

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

    private void circularRevealActivity() {
        int cx = /*ll_add_event_screen.getRight() - */x;
        int cy = /*ll_add_event_screen.getBottom() -*/ y;

        float finalRadius = Math.max(sv_add_event_screen.getWidth(), sv_add_event_screen.getHeight());

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
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                getSupportActionBar().show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
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

        float finalRadius = Math.max(sv_add_event_screen.getWidth(), sv_add_event_screen.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(sv_add_event_screen, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                getSupportActionBar().hide();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sv_add_event_screen.setVisibility(View.INVISIBLE);

                Intent toCalendar_Screen = new Intent(getApplicationContext(), Calendar_Screen.class);

                int day = selectedDate.getDayOfMonth();
                int month = selectedDate.getMonthValue();
                int year = selectedDate.getYear();

                toCalendar_Screen.putExtra("day", day);
                toCalendar_Screen.putExtra("month", month);
                toCalendar_Screen.putExtra("year", year);

//                startActivity(toCalendar_Screen);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });

        circularReveal.setDuration(800);
        circularReveal.start();
    }

    private void createColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setDefaultColorButton(Color.GREEN);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColorButtonSize(30, 30);
        colorPicker.setColorButtonTickColor(Color.BLACK);
        colorPicker.getDialogViewLayout().setBackgroundResource(R.drawable.round_dialog_background);

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

//            event = new CalendarEvent(selectedColor, name, description, place, timestamp, startDate, startTime, endDate, endTime);
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

                addEventToFirebaseForTextWithPUSH(event, null);
            }
            else if(event.getFrequencyType().endsWith("amount")){
                success = addEventForTimesAdvanced(event);
            }
            else if(event.getFrequencyType().endsWith("end")){
                success = addEventForUntilAdvanced(event);
            }

//            createAlarm();

            //ToDo adjust chain_id for row events in database
 /*           if(event.getStart_date().equals(event.getFrequency_start()) &&
                    event.getEnd_date().equals(event.getFrequency_end())){

                eventsDatabase = FirebaseUtils.eventsDatabase;
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

/*
    public void sendMulticast() throws FirebaseMessagingException {
        // [START send_multicast]
        // Create a list containing up to 500 registration tokens.
        // These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = Arrays.asList(
                "YOUR_REGISTRATION_TOKEN_1",
                // ...
                "YOUR_REGISTRATION_TOKEN_n"
        );

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(title != null ? title : "Firebase notification")
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        Message message = Message.builder().setNotification(Notification.builder()
                .setTitle("Event added")
                .setBody("New Event added1")
//                .setImage()
                .build())
                .setTopic("Adding Event")
//                .putData("score", "850")
//                .putData("time", "2:45")
                .build();

        // See the BatchResponse reference documentation
        // for the contents of response.

        // [END send_multicast]
    }
*/

    public void sendNotification(){
        // Create channel to show notifications.
        String channelId  = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);

        AtomicInteger msgId = new AtomicInteger();

        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW));

        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                                .setMessageId(String.valueOf(msgId.get()))
                                                .setMessageType("ADDING EVENT")
                                                .addData("message_body", "New Event added!")
                                                .addData("message_title", "New Event")
                                                .build()
        );
/*
        *//**
         * If a notification message is tapped, any data accompanying the notification
         * message is available in the intent extras. In this sample the launcher
         * intent is fired when the notification is tapped, so any accompanying data would
         * be handled here. If you want a different intent fired, set the click_action
         * field of the notification message to the desired intent. The launcher intent
         * is used when no click_action is specified.
         * Handle possible data accompanying notification message.
         * [START handle_data_extras]
         *//*
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

// Create a list containing up to 500 registration tokens.
// These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = Arrays.asList(
                "YOUR_REGISTRATION_TOKEN_1",
                // ...
                "YOUR_REGISTRATION_TOKEN_n"
        );


        MulticastMessage message = MulticastMessage.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .addAllTokens(registrationTokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
// See the BatchResponse reference documentation
// for the contents of response.
        System.out.println(response.getSuccessCount() + " messages were sent successfully");


        // Get token
        // [START log_reg_token]
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, "The token is " + token);
                        Toast.makeText(getApplicationContext(), "The token is " + token, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END log_reg_token]*/
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}