package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.projectofmurad.AlarmManagerForToday;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MySuperTouchActivity extends AppCompatActivity implements
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnNeverFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnSendMessageListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayAndMonthFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekAndMonthFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayAndYearFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekAndYearFrequencyListener,
        View.OnFocusChangeListener {

    protected EditText et_name;
    protected EditText et_place;
    protected EditText et_description;

    protected AppCompatImageButton ib_color;
    protected SwitchMaterial switch_all_day;
    protected SwitchMaterial switch_alarm;

    protected Button btn_choose_start_time;
    protected Chip btn_choose_start_date;

    protected TimePicker event_time_picker;

    protected RelativeLayout rl_event_setup;
    protected ScrollView sv_add_event_screen;

    protected Button btn_choose_end_time;
    protected Button btn_choose_end_date;

    protected Button btn_add_event;
    protected Button btn_delete_event;

    protected Button btn_color;
    protected TextView btn_repeat;

    protected CalendarEvent event = new CalendarEvent();

    protected int selectedColor;

    protected int start_hour;
    protected int start_min;

    protected int timestamp;

    protected int end_hour;
    protected int end_min;

    protected LocalDate startDate;

    protected int start_day;
    protected int start_month;
    protected int start_year;

    protected LocalDate endDate;

    protected int end_day;
    protected int end_month;
    protected int end_year;

    protected FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    protected DatabaseReference eventsDatabase;

    public final static String DAY_BY_AMOUNT = "frequency_day_by_amount";
    public final static String DAY_OF_WEEK_BY_AMOUNT = "frequencyDayOfWeek_by_amount";
    public final static String DAY_AND_MONTH_BY_AMOUNT = "frequency_day_and_month_by_amount";
    public final static String DAY_OF_WEEK_AND_MONTH_BY_AMOUNT = "frequency_dayOfWeek_and_month_by_amount";
    public final static String DAY_AND_YEAR_BY_AMOUNT = "frequency_day_and_year_by_amount";
    public final static String DAY_OF_WEEK_AND_YEAR_BY_AMOUNT = "frequency_dayOfWeek_and_year_by_amount";

    protected boolean row_event = true;

    public final static String DAY_BY_END = "frequency_day_by_end";
    public final static String DAY_OF_WEEK_BY_END = "frequencyDayOfWeek_by_end";
    public final static String DAY_AND_MONTH_BY_END = "frequency_day_and_month_by_end";
    public final static String DAY_OF_WEEK_AND_MONTH_BY_END = "frequency_dayOfWeek_and_month_by_end";
    public final static String DAY_AND_YEAR_BY_END = "frequency_day_and_year_by_end";
    public final static String DAY_OF_WEEK_AND_YEAR_BY_END = "frequency_dayOfWeek_and_year_by_end";

    protected String frequencyType;

    protected int frequency;

    protected int amount;
    protected LocalDate end;

    protected boolean isFrequency_by_amount = true;

    protected boolean frequency_never = false;

    protected boolean frequency_day = false;
    protected boolean frequency_dayOfWeek = false;
    protected boolean frequency_day_and_month = false;
    protected boolean frequency_dayOfWeek_and_month = false;
    protected boolean frequency_day_and_year = false;
    protected boolean frequency_dayOfWeek_and_year = false;

    protected ChooseEventFrequencyDialogCustomWithExposedDropdown chooseEventFrequencyDialog;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().hide();

//        getSupportActionBar().hide();
//        setContentView(R.layout.activity_my_super_touch);
    }


    public MySuperTouchActivity() {
        super();
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.action_save:
                break;
            case android.R.id.home:
                Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show();
//                this.finish();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddEventClick(MenuItem item) {

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

            eventsDatabase = FirebaseUtils.eventsDatabase;
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

//            startAlarm();
            if(success){
                if (switch_alarm.isChecked()){
                    AlarmManagerForToday.addAlarm(this, event, 0);
                }
                if (Utils.isMadrich){
                    FirebaseUtils.attendanceDatabase.child(event.getEvent_private_id())
                            .child(FirebaseUtils.getCurrentUID()).setValue(true);
                }

            }

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

//                sendNotification();
            }
            else {
                createBottomSheetDialog();
            }

        }

    }

    public void createBottomSheetDialog(){
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Widget_Design_BottomSheet_Modal);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_event_frequency);
        bottomSheetDialog.setTitle("Repeat");
        bottomSheetDialog.setCancelable(true);

        TextView tv_event_error = bottomSheetDialog.findViewById(R.id.tv_event_error);

        Button btn_error = bottomSheetDialog.findViewById(R.id.btn_error);
        btn_error.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setDismissWithAnimation(true);
        bottomSheetDialog.show();
    }

    public void addEventToFirebaseForTextWithPUSH(@NonNull CalendarEvent event,
                                                  String chain_key) {

        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveEnd_date();

        Log.d("murad", "start_date of event: " + event.getStart_date());
        Log.d("murad", "end_date of event: " + event.getEnd_date());

        eventsDatabase = FirebaseUtils.eventsDatabase;

        LocalDate tmp = start_date;

        String private_key = eventsDatabase.push().getKey();
//        event.setEvent_private_id(private_key);

        event.setEvent_private_id(private_key);
        event.setEvent_chain_id(chain_key == null ? private_key : chain_key);



        Log.d("murad", "PRIVATE ID IS " + event.getEvent_private_id());
        Log.d("murad", "CHAIN ID IS " + event.getEvent_chain_id());

        FirebaseUtils.allEventsDatabase.child(private_key).setValue(event);

        do {
            eventsDatabase = FirebaseUtils.eventsDatabase;
            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabase = eventsDatabase.child(private_key);
            eventsDatabase.setValue(event);

            event.setTimestamp(0);

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

    public void addEventForTimes(@NonNull CalendarEvent event){
//        LocalDate end_date = event.receiveEnd_date();

        eventsDatabase = FirebaseUtils.eventsDatabase;
        String key = event.getEvent_chain_id();

        int frequency = event.getFrequency();
        int amount = event.getAmount();

        LocalDate tmp = event.receiveStart_date();

        LocalDate startDate = event.receiveStart_date();
        LocalDate endDate = event.receiveEnd_date();

        LocalDate absolute_end_date = tmp;

        switch(event.getFrequencyType()) {
            case DAY_BY_AMOUNT:
                for(int i = 0; i < amount; i++) {
                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    if(frequency == 1){
                        event.setTimestamp(0);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusDays(frequency);
                }
                break;
            case DAY_OF_WEEK_BY_AMOUNT:
                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

                for(int i = 0; i < amount; i++) {
                    for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j)) {

                            eventsDatabase = FirebaseUtils.eventsDatabase;
                            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                            eventsDatabase = eventsDatabase.child(key);
                            eventsDatabase.setValue(event);

//                          event.setTimestamp(0);

                        }

                        tmp = tmp.plusDays(1);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusWeeks(frequency);
                }
                break;
            case DAY_AND_MONTH_BY_AMOUNT:
                for(int i = 0; i < amount; i++) {
                    if(tmp.lengthOfMonth() >= event.getDay()) {

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

//                      event.setTimestamp(0);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);
                }
                break;
            case DAY_OF_WEEK_AND_MONTH_BY_AMOUNT:
                int weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                for(int i = 0; i < amount; i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);


                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);

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

                /*if(weekNumber > 4) {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

                        tmp = tmp.plusMonths(frequency);
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());

                    }
                }
                else {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

//                      event.setTimestamp(0);

                        tmp = tmp.plusMonths(frequency);
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
                }*/
                break;
            case DAY_AND_YEAR_BY_AMOUNT:

                int selected_day = event.getDay();
                int selected_month = event.getMonth();

                if(event.receiveStart_date().getMonth() == Month.FEBRUARY && selected_day == 29){
                    event.setFrequency(4);

                    Toast.makeText(getApplicationContext(), "This event will repeat every 4 years", Toast.LENGTH_SHORT).show();
                }

                for(int i = 0; i < amount; i++) {

                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
                }
                break;
            case DAY_OF_WEEK_AND_YEAR_BY_AMOUNT:
                weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                for(int i = 0; i < amount; i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);


                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
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

        event.updateFrequency_end(absolute_end_date);
        Log.d("murad", event.getFrequency_end());
    }

    public void addEventForUntil(@NonNull CalendarEvent event){

        eventsDatabase = FirebaseUtils.eventsDatabase;
        String key = event.getEvent_chain_id();

        int frequency = event.getFrequency();
        LocalDate end = event.receiveFrequency_end();

        LocalDate tmp = event.receiveStart_date();


        LocalDate absolute_end_date = end;
        Log.d("murad", "Chosen end is " + Utils_Calendar.DateToTextOnline(absolute_end_date));


        switch(event.getFrequencyType()) {
            case DAY_BY_END:
                do {
                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    if(frequency == 1){
                        event.setTimestamp(0);
                    }
                    absolute_end_date = tmp;

                    tmp = tmp.plusDays(frequency);
                }
                while(!tmp.isAfter(end));

                break;
            case DAY_OF_WEEK_BY_END:
                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

                do {
                    for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j)) {
                            eventsDatabase = FirebaseUtils.eventsDatabase;
                            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                            eventsDatabase = eventsDatabase.child(key);
                            eventsDatabase.setValue(event);

                        }
                        tmp = tmp.plusDays(1);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusWeeks(frequency);

                }
                while(!tmp.isAfter(end));

                break;
            case DAY_AND_MONTH_BY_END:
                do {
                    if(tmp.lengthOfMonth() >= event.getDay()) {

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

//                      event.setTimestamp(0);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);

                }
                while(!tmp.isAfter(end));

                break;
            case DAY_OF_WEEK_AND_MONTH_BY_END:
                int weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                do {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);
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
                while(!tmp.isAfter(end));

                break;
            case DAY_AND_YEAR_BY_END:

                int selected_day = event.getDay();
                int selected_month = event.getMonth();

                if(event.receiveStart_date().getMonth() == Month.FEBRUARY && selected_day == 29){
                    event.setFrequency(4);

                    Toast.makeText(getApplicationContext(), "This event will repeat every 4 years", Toast.LENGTH_SHORT).show();
                }

                do {
                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
                }
                while(!tmp.isAfter(end));

                break;
            case DAY_OF_WEEK_AND_YEAR_BY_END:
                weekNumber = event.getWeekNumber();
                Log.d("frequency_dayOfWeek_and_month", "weekNumber = " + weekNumber);

                do {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    eventsDatabase = FirebaseUtils.eventsDatabase;
                    eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                    eventsDatabase = eventsDatabase.child(key);
                    eventsDatabase.setValue(event);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
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
                while(!tmp.isAfter(end));

                break;

        }

        Log.d("murad", "Absolute end date after switch is " + Utils_Calendar.DateToTextOnline(absolute_end_date));

        event.updateFrequency_end(absolute_end_date);
    }

    public boolean addEventForTimesAdvanced(@NonNull CalendarEvent event){

        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", " ");
        Log.d("murad", "ADDING BY AMOUNT STARTED");

        eventsDatabase = FirebaseUtils.eventsDatabase;

        String chain_key = event.getEvent_chain_id();

        int frequency = event.getFrequency();
        Log.d("murad", "FREQUENCY IS " + (frequency));

        int amount = event.getAmount();
        Log.d("murad", "AMOUNT IS " + amount);

        boolean isLast = event.isLast();
        Log.d("murad", "IS LAST = " + isLast);

        LocalDate tmp = event.receiveStart_date();

        LocalDate startDate = event.receiveStart_date();
        LocalDate endDate = event.receiveEnd_date();

        event.updateFrequency_start(startDate);

        Period range = startDate.until(endDate);

        int day;
        int weekNumber;
        int dayOfWeekPosition;
        DayOfWeek dayOfWeek;

        LocalDate absolute_end_date = tmp;

        switch(event.getFrequencyType()) {
            case DAY_BY_AMOUNT:

                if(range.getDays() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                for(int i = 0; i < amount; i++) {

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + (i+1));
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    tmp = tmp.plusDays(frequency);

                    Log.d("murad", "___________________________________________________________");

                }

                break;

            case DAY_OF_WEEK_BY_AMOUNT:

                if(range.getDays() >= 7 * frequency){
                    //                    createBottomSheetDialog();
                    return false;
                }

                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

/*
                while (!event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {
                    // get TemporalAdjuster with
                    // the next in month adjuster
*//*                        TemporalAdjuster temporalAdjuster
                                = TemporalAdjusters.nextOrSame(
                                DayOfWeek.WEDNESDAY);

                        WeekFields.of(startDate).weekOfMonth();*//*

                    Log.d("murad", "Going over days to find first occurrence of selected Day Of Week " + Utils_Calendar.DateToTextForFirebase(tmp));
                    tmp = tmp.plusDays(1);

                }

                Log.d("murad", "");
                Log.d("murad", "FOUND!!!");
                Log.d("murad", "");

                startDate = tmp;
                endDate = tmp.plus(range);

                event.updateStart_date(startDate);
                event.updateEnd_date(endDate);

                event.updateFrequency_start(tmp);

                        *//*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*//*

                private_key = eventsDatabase.push().getKey();
                event.setPrivate_id(private_key);

                addEventToFirebaseForTextWithPUSH(event);
                Log.d("murad", "___________________________________________________________");*/

                boolean first = true;

                for(int i = 0; i < amount; i++) {

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + (i+1));
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    /*for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j) && tmp.getDayOfWeek().getValue() == j+1) {

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStart_date(startDate);
                            event.updateEnd_date(endDate);

                            *//*eventsDatabase = FirebaseUtils.eventsDatabase;
                            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*//*

                            String private_key = eventsDatabase.push().getKey();
                            event.setPrivate_id(private_key);

                            addEventToFirebaseForTextWithPUSH(event);

                        }

                        tmp = tmp.plusDays(1);
                    }*/


                    do{

                        Log.d("murad", "___________________________________________________________");

                        if(event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {
                            // get TemporalAdjuster with
                            // the next in month adjuster
/*                        TemporalAdjuster temporalAdjuster
                                = TemporalAdjusters.nextOrSame(
                                DayOfWeek.WEDNESDAY);

                                WeekFields.of(startDate).weekOfMonth();*/

                            Log.d("murad", "");
                            Log.d("murad", "FOUND!!! " + Utils_Calendar.DateToTextLocal(tmp));
                            Log.d("murad", "");

                            if(first){
                                event.updateFrequency_start(tmp);
                                first = false;
                            }

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStart_date(startDate);
                            event.updateEnd_date(endDate);

                        /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                            addEventToFirebaseForTextWithPUSH(event, chain_key);
                            Log.d("murad", "___________________________________________________________");

                        }

                            Log.d("murad", "Going over days to find first occurrence of selected Day Of Week " + Utils_Calendar.DateToTextForFirebase(tmp));
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

                        event.updateStart_date(startDate);
                        event.updateEnd_date(endDate);

                        */
/*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*//*


                        private_key = eventsDatabase.push().getKey();
                        event.setPrivate_id(private_key);

                        addEventToFirebaseForTextWithPUSH(event);
                        Log.d("murad", "___________________________________________________________");
                    }
*/
                    tmp = tmp.plusWeeks(frequency);
                    tmp = tmp.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                }

                break;

            case DAY_AND_MONTH_BY_AMOUNT:

                day = event.getDay();

                if(/*endDate.getDayOfMonth() >= day && */ range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {

                    if(tmp.lengthOfMonth() >= day) {
                        if(isLast){

                            tmp = tmp.with(TemporalAdjusters.lastDayOfMonth());
                            Log.d("murad", "Last date of current month is " + Utils_Calendar.DateToTextForFirebase(tmp));



                            /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                        }
                        else {

                            tmp = tmp.withDayOfMonth(day);
                            Log.d("murad", "The " + day + " day of current month is " + Utils_Calendar.DateToTextForFirebase(tmp));

                            /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                        }

                        startDate = tmp;
                        endDate = tmp.plus(range);

                        event.updateStart_date(startDate);
                        event.updateEnd_date(endDate);

                        addEventToFirebaseForTextWithPUSH(event, chain_key);
                    }

                    absolute_end_date = tmp;

//                    tmp = tmp.with(TemporalAdjusters.firstDayOfNextMonth());
//                    Log.d("murad", "The first day of next month is " + Utils_Calendar.DateToTextForFirebase(tmp));

                    tmp = tmp.plusMonths(frequency);
                    Log.d("murad", "The next month is " + Utils_Calendar.DateToTextForFirebase(tmp));
                }

                break;

            case DAY_OF_WEEK_AND_MONTH_BY_AMOUNT:

                if(range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                weekNumber = event.getWeekNumber();
                Log.d("murad", "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d("murad", "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d("murad", "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {
                    Log.d("murad", "--------------------------------------------------------------");
                    Log.d("murad", Utils_Calendar.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                        /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);

                    Log.d("murad", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));
/*                    if(weekNumber > 4) {

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

                    }*/

                }

                /*if(weekNumber > 4) {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

                        tmp = tmp.plusMonths(frequency);
                        Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                        tmp = Utils_Calendar.getNextOccurrenceForLast(tmp, event.getDayOfWeekPosition());

                    }
                }
                else {
                    for(int i = 0; i < amount; i++) {
                        Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                        Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                        eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                        eventsDatabase = eventsDatabase.child(key);
                        eventsDatabase.setValue(event);

//                      event.setTimestamp(0);

                        tmp = tmp.plusMonths(frequency);
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
                }*/
                break;

            case DAY_AND_YEAR_BY_AMOUNT:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                day = event.getDay();
                int selected_month = event.getMonth();

                if(event.receiveStart_date().getMonth() == Month.FEBRUARY && day == 29){
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

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                        /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
                }

                break;

            case DAY_OF_WEEK_AND_YEAR_BY_AMOUNT:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                weekNumber = event.getWeekNumber();
                Log.d("murad", "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d("murad", "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d("murad", "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                for(int i = 0; i < amount; i++) {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                        /*eventsDatabase = FirebaseUtils.eventsDatabase;
                        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));*/

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));
/*                    if(weekNumber > 4) {

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

                    }*/

                }

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + event.getFrequencyType());
        }

        event.updateFrequency_end(endDate);

        Log.d("murad", "Frequency End of event: " + event.getFrequency_end());

        Log.d("murad", "ADDING BY AMOUNT FINISHED");
        Log.d("murad", " ");
        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");

        return true;
    }

    public boolean addEventForUntilAdvanced(@NonNull CalendarEvent event){

        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", " ");
        Log.d("murad", "ADDING BY END STARTED");

        eventsDatabase = FirebaseUtils.eventsDatabase;

        String chain_key = event.getEvent_chain_id();

        int frequency = event.getFrequency();
        Log.d("murad", "FREQUENCY IS " + (frequency));

        LocalDate end = event.receiveFrequency_end();
        Log.d("murad", "END IS " + event.getFrequency_end());

        boolean isLast = event.isLast();
        Log.d("murad", "IS LAST = " + isLast);

        LocalDate tmp = event.receiveStart_date();

        LocalDate startDate = event.receiveStart_date();
        LocalDate endDate = event.receiveEnd_date();

        event.updateFrequency_start(startDate);

        Period range = startDate.until(endDate);

        int day;
        int weekNumber;
        int dayOfWeekPosition;
        DayOfWeek dayOfWeek;

        LocalDate absolute_end_date = end;
        Log.d("murad", "Chosen end is " + Utils_Calendar.DateToTextOnline(absolute_end_date));


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

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + (i));
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusDays(frequency);

                    Log.d("murad", "___________________________________________________________");
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

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + i);
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    do{

                        Log.d("murad", "___________________________________________________________");

                        if(event_array_frequencyDayOfWeek.get(tmp.getDayOfWeek().getValue()-1)) {

                            Log.d("murad", "");
                            Log.d("murad", "FOUND!!! " + Utils_Calendar.DateToTextLocal(tmp));
                            Log.d("murad", "");

                            if(first){
                                event.updateFrequency_start(tmp);
                                first = false;
                            }

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStart_date(startDate);
                            event.updateEnd_date(endDate);

                            addEventToFirebaseForTextWithPUSH(event, chain_key);
                            Log.d("murad", "___________________________________________________________");

                        }

                        Log.d("murad", "Going over days to find first occurrence of selected Day Of Week " + Utils_Calendar.DateToTextForFirebase(tmp));
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
                            Log.d("murad", "Last date of current month is " + Utils_Calendar.DateToTextForFirebase(tmp));

                        }
                        else {

                            tmp = tmp.withDayOfMonth(day);
                            Log.d("murad", "The " + day + " day of current month is " + Utils_Calendar.DateToTextForFirebase(tmp));

                        }

                        startDate = tmp;
                        endDate = tmp.plus(range);

                        event.updateStart_date(startDate);
                        event.updateEnd_date(endDate);

                        addEventToFirebaseForTextWithPUSH(event, chain_key);
                    }

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);
                    Log.d("murad", "The next month is " + Utils_Calendar.DateToTextForFirebase(tmp));

                }
                while(!tmp.isAfter(end));

                break;

            case DAY_OF_WEEK_AND_MONTH_BY_END:

                if(range.getMonths() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                weekNumber = event.getWeekNumber();
                Log.d("murad", "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d("murad", "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d("murad", "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                do {
                    Log.d("murad", "--------------------------------------------------------------");
                    Log.d("murad", Utils_Calendar.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusMonths(frequency);

                    Log.d("murad", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                }
                while(!tmp.isAfter(end));

                break;

            case DAY_AND_YEAR_BY_END:

                if(range.getYears() >= frequency){
//                    createBottomSheetDialog();
                    return false;
                }

                day = event.getDay();

                if(event.receiveStart_date().getMonth() == Month.FEBRUARY && day == 29){
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

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

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
                Log.d("murad", "weekNumber = " + weekNumber);

                dayOfWeekPosition = event.getDayOfWeekPosition();
                Log.d("murad", "dayOfWeekPosition = " + dayOfWeekPosition);

                dayOfWeek = DayOfWeek.of(dayOfWeekPosition+1);
                Log.d("murad", "DayOfWeek is " + dayOfWeek);

                tmp = tmp.withDayOfMonth(1);

                do {
                    Log.d("frequency_dayOfWeek_and_month", "--------------------------------------------------------------");
                    Log.d("frequency_dayOfWeek_and_month", Utils_Calendar.DateToTextOnline(tmp));

                    if(isLast){
                        tmp = tmp.with(TemporalAdjusters.lastInMonth(dayOfWeek));
                    }
                    else {
                        tmp = tmp.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, dayOfWeek));
                    }

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    addEventToFirebaseForTextWithPUSH(event, chain_key);

                    absolute_end_date = tmp;

                    tmp = tmp.plusYears(frequency);
                    Log.d("frequency_dayOfWeek_and_month", "the next month is " + Utils_Calendar.DateToTextOnline(tmp));

                }
                while(!tmp.isAfter(end));

                break;

        }

        Log.d("murad", "Absolute end date after switch is " + Utils_Calendar.DateToTextOnline(endDate));

        event.updateFrequency_end(endDate);

        Log.d("murad", "Frequency End of event: " + event.getFrequency_end());

        Log.d("murad", "ADDING BY AMOUNT FINISHED");
        Log.d("murad", " ");
        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");

        return true;
    }

    @Override
    public void setOnMessage(String msg) {
        btn_repeat.setText(msg);
        et_name.setText(msg);
    }

    //sets time
    protected class SetTime implements TimePickerDialog.OnTimeSetListener {
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
    protected class SetDate implements DatePickerDialog.OnDateSetListener {
        private String date_start_or_end;

        public SetDate(String date_start_or_end) {
            this.date_start_or_end = date_start_or_end;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;

            String date_text;

            switch(date_start_or_end) {
                case "start":
                    startDate = LocalDate.of(year, month, day);
                    date_text = Utils_Calendar.DateToTextLocal(startDate);

                    start_day = day;
                    start_month = month;
                    start_year = year;

                    break;
                case "end":
                    endDate = LocalDate.of(year, month, day);
                    date_text = Utils_Calendar.DateToTextLocal(endDate);
                    btn_choose_end_date.setText(date_text);

//                    setOnNeverFrequency();
                    end_day = day;
                    end_month = month;
                    end_year = year;

                    break;
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(view instanceof EditText){
            String text = ((EditText) view).getText().toString();
            if(!b  && !text.isEmpty() && text.charAt(0) == ' '){
                text = text.replaceFirst("\\s+", "");
                ((EditText) view).setText(text);
            }
        }

    }

    public boolean isRow_event(CalendarEvent event) {
        return true;
    }

    @Override
    public void setOnDayFrequency(int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_BY_AMOUNT);

        event.setFrequency(selected_frequency);
        event.setAmount(selected_amount);

        String msg = "Every " + selected_frequency + " days, " + selected_amount + " times";

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayFrequency(int selected_frequency, LocalDate selected_end) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        String msg = "Every " + selected_frequency + " days until " + Utils_Calendar.DateToTextLocal(selected_end);

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_OF_WEEK_BY_AMOUNT);

        event.setFrequency(selected_frequency);
        event.setAmount(selected_amount);

        event.setArray_frequencyDayOfWeek(selected_array_frequencyDayOfWeek);

        String days_of_week = "";
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week += DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) + " ,";
            }
        }
        String msg = "Every " + selected_frequency + " weeks on " + days_of_week + " " + selected_amount + " times";

        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
    }

    @Override
    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek,
                                        int selected_frequency, LocalDate selected_end) {

        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setArray_frequencyDayOfWeek(selected_array_frequencyDayOfWeek);

/*        String days_of_week = "";
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week += DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) + " ,";
            }
        }*/

        StringBuilder days_of_week = new StringBuilder();
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week.append(DayOfWeek.of(i + 1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale)).append(" ,");
            }
        }

        String msg = "Every " + selected_frequency + " weeks on " + days_of_week + " until " + Utils_Calendar.DateToTextLocal(selected_end);

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, int selected_amount, boolean isLast) {
        event.setLast(isLast);

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_AND_MONTH_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDay(selected_day);

        String msg = "Every " + selected_frequency + " months on " + selected_day + ", " + selected_amount + " times";

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, LocalDate selected_end, boolean isLast) {
        event.setLast(isLast);

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_AND_MONTH_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDay(selected_day);

        String msg = "Every " + selected_frequency + " months on " + selected_day + " until " + Utils_Calendar.DateToTextLocal(selected_end);

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, int selected_amount, boolean isLast) {
        event.setLast(isLast);


        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);

        event.setFrequency(selected_frequency);
        event.setAmount(selected_amount);

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);

        String msg = "Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                ", " + selected_amount + " times";

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, LocalDate selected_end, boolean isLast) {
        event.setLast(isLast);


        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_OF_WEEK_AND_MONTH_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);

        String msg = "Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " until " + Utils_Calendar.DateToTextLocal(selected_end);

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, int selected_amount, boolean isLast) {
        event.setLast(isLast);


        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_AND_YEAR_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDay(selected_day);
        event.setMonth(selected_month);

        String msg = "Every " + selected_frequency + " years on " + selected_day + " of " +
                Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                ", " + selected_amount + " times";

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, LocalDate selected_end, boolean isLast) {
        event.setLast(isLast);


        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_AND_YEAR_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());
        event.setDay(selected_day);

        event.setMonth(selected_month);

        String msg = String.format(Utils_Calendar.locale,
                "Every %d years on %d of %s until %s",
                selected_frequency,
                selected_day,
                Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale),
                Utils_Calendar.DateToTextLocal(selected_end));

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));

        String text = getString(R.string.day_and_year_text,
                selected_frequency,
                selected_day,
                Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale),
                Utils_Calendar.DateToTextLocal(selected_end));

        btn_repeat.setText(text);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);

    }

    @Override
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, int selected_amount, boolean isLast) {
        event.setLast(isLast);

        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDayOfWeekPosition(selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);;
        event.setMonth(selected_month);

        String msg = "Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                ", " + selected_amount + " times";

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, LocalDate selected_end, boolean isLast) {
        event.setLast(isLast);

        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_AND_YEAR_BY_END);

        event.setFrequency(selected_frequency);;
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);;
        event.setMonth(selected_month);

        String msg = "Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " until " + Utils_Calendar.DateToTextLocal(selected_end);

//        btn_repeat.setText(msg);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.colorAccent));
//        et_name.setText(msg);
    }

    @Override
    public void setOnNeverFrequency() {

        event.clearFrequencyData();

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        row_event = true;

        btn_repeat.setHint(R.string.does_not_repeat);
        btn_repeat.getCompoundDrawables()[0].setTint(getColor(R.color.black));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getting Root View that gets focus
        View rootView =((ViewGroup)findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(MySuperTouchActivity.this);
                }
            }
        });
    }

    public static void hideKeyboard(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( context.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), Calendar_Screen.class));
    }



    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        return super.dispatchTouchEvent(ev);
    }*/

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity act) {
        if(act!=null)
            ((InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((act.getWindow().getDecorView().getApplicationWindowToken()), 0);
    }*/
}