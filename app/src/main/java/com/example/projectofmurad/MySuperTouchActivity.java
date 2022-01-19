package com.example.projectofmurad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.projectofmurad.calendar.CalendarEventWithTextOnly2FromSuper;
import com.example.projectofmurad.calendar.Calendar_Screen;
import com.example.projectofmurad.calendar.ChooseEventFrequencyDialogCustomWithExposedDropdown;
import com.example.projectofmurad.calendar.Utils_Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.List;

public class MySuperTouchActivity extends AppCompatActivity implements
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnNeverFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayAndMonthFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekAndMonthFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayAndYearFrequencyListener,
        ChooseEventFrequencyDialogCustomWithExposedDropdown.OnDayOfWeekAndYearFrequencyListener,
        View.OnFocusChangeListener{

    protected EditText et_name;
    protected EditText et_place;
    protected EditText et_description;

    protected Button btn_choose_start_time;
    protected Button btn_choose_start_date;
    protected Button btn_choose_end_time;
    protected Button btn_choose_end_date;

    protected Button btn_add_event;
    protected Button btn_delete_event;

    protected Button btn_color;
    protected Button btn_repeat;

    protected CalendarEventWithTextOnly2FromSuper event = new CalendarEventWithTextOnly2FromSuper();

    protected int selectedColor;

    protected int start_hour;
    protected int start_min;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_super_touch);
    }

    public MySuperTouchActivity() {
        super();


    }

    public void addEventToFirebaseForTextWithPUSH(CalendarEventWithTextOnly2FromSuper event) {
        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveEnd_date();

        Log.d("murad", "start_date of event: " + event.getStart_date());
        Log.d("murad", "end_date of event: " + event.getEnd_date());

        eventsDatabase = firebase.getReference("EventsDatabase");

        LocalDate tmp = start_date;

        String key = event.getEvent_id();

        Log.d("murad", key);

        do {
            eventsDatabase = firebase.getReference("EventsDatabase");
            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

            eventsDatabase = eventsDatabase.child(key);
            eventsDatabase.setValue(event);

            event.setTimestamp(0);

            tmp = tmp.plusDays(1);
        }
        while(!tmp.isEqual(end_date.plusDays(1)));

    }

    public void addEventForTimes(CalendarEventWithTextOnly2FromSuper event){
//        LocalDate end_date = event.receiveEnd_date();

        eventsDatabase = firebase.getReference("EventsDatabase");
        String key = event.getEvent_id();

        int frequency = event.getFrequency();
        int amount = event.getAmount();

        LocalDate tmp = event.receiveStart_date();

        LocalDate startDate = event.receiveStart_date();
        LocalDate endDate = event.receiveEnd_date();

        LocalDate absolute_end_date = tmp;

        switch(event.getFrequencyType()) {
            case DAY_BY_AMOUNT:
                for(int i = 0; i < amount; i++) {
                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                            eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

    public void addEventForUntil(CalendarEventWithTextOnly2FromSuper event){

        eventsDatabase = firebase.getReference("EventsDatabase");
        String key = event.getEvent_id();

        int frequency = event.getFrequency();
        LocalDate end = event.receiveFrequency_end();

        LocalDate tmp = event.receiveStart_date();


        LocalDate absolute_end_date = end;
        Log.d("murad", "Chosen end is " + Utils_Calendar.DateToTextOnline(absolute_end_date));


        switch(event.getFrequencyType()) {
            case DAY_BY_END:
                do {
                    eventsDatabase = firebase.getReference("EventsDatabase");
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
                            eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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
                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

    public void addEventForTimesAdvanced(CalendarEventWithTextOnly2FromSuper event){

        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");
        Log.d("murad", "\nADDING BY AMOUNT STARTED");

        eventsDatabase = firebase.getReference("EventsDatabase");
        String key = event.getEvent_id();

        int frequency = event.getFrequency();
        Log.d("murad", "FREQUENCY IS " + frequency);

        int amount = event.getAmount();
        Log.d("murad", "AMOUNT IS " + amount);

        LocalDate tmp = event.receiveStart_date();

        LocalDate startDate = event.receiveStart_date();
        LocalDate endDate = event.receiveEnd_date();

        event.updateFrequency_start(startDate);

        Period range = startDate.until(endDate);

        LocalDate absolute_end_date = tmp;

        switch(event.getFrequencyType()) {
            case DAY_BY_AMOUNT:
                for(int i = 0; i < amount; i++) {

                    startDate = tmp;
                    endDate = tmp.plus(range);

                    event.updateStart_date(startDate);
                    event.updateEnd_date(endDate);

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + (i+1));
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    addEventToFirebaseForTextWithPUSH(event);

                    tmp = tmp.plusDays(frequency);

                    Log.d("murad", "___________________________________________________________");

                }
                break;

            case DAY_OF_WEEK_BY_AMOUNT:

                List<Boolean> event_array_frequencyDayOfWeek = event.getArray_frequencyDayOfWeek();

                for(int i = 0; i < amount; i++) {

                    Log.d("murad", "___________________________________________________________");
                    Log.d("murad", "CIRCLE NUMBER " + (i+1));
                    Log.d("murad", "Frequency Start of event: " + event.getFrequency_start());

                    for(int j = 0; j < event_array_frequencyDayOfWeek.size(); j++) {
                        if(event_array_frequencyDayOfWeek.get(j)) {

                            eventsDatabase = firebase.getReference("EventsDatabase");
                            eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(tmp));

                            eventsDatabase = eventsDatabase.child(key);
                            eventsDatabase.setValue(event);

                            startDate = tmp;
                            endDate = tmp.plus(range);

                            event.updateStart_date(startDate);
                            event.updateEnd_date(endDate);

                            addEventToFirebaseForTextWithPUSH(event);

                        }

                        tmp = tmp.plusDays(1);
                    }

                    Log.d("murad", "___________________________________________________________");


                    tmp = tmp.plusWeeks(frequency);
                }
                break;

            case DAY_AND_MONTH_BY_AMOUNT:

                for(int i = 0; i < amount; i++) {
                    if(tmp.lengthOfMonth() >= event.getDay()) {

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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

        event.updateFrequency_end(endDate);
        Log.d("murad", "Frequency End of event: " + event.getFrequency_end());

        Log.d("murad", "ADDING BY AMOUNT FINISHED");
        Log.d("murad", "\n------------------------------------------------------------------------------------------");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "******************************************************************************************");
        Log.d("murad", "//////////////////////////////////////////////////////////////////////////////////////////");
        Log.d("murad", "------------------------------------------------------------------------------------------");
    }

    public void addEventForUntilAdvanced(CalendarEventWithTextOnly2FromSuper event){

        eventsDatabase = firebase.getReference("EventsDatabase");
        String key = event.getEvent_id();

        int frequency = event.getFrequency();
        LocalDate end = event.receiveFrequency_end();

        LocalDate tmp = event.receiveStart_date();


        LocalDate absolute_end_date = end;
        Log.d("murad", "Chosen end is " + Utils_Calendar.DateToTextOnline(absolute_end_date));


        switch(event.getFrequencyType()) {
            case DAY_BY_END:
                do {
                    eventsDatabase = firebase.getReference("EventsDatabase");
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
                            eventsDatabase = firebase.getReference("EventsDatabase");
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

                        eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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
                    eventsDatabase = firebase.getReference("EventsDatabase");
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

                    eventsDatabase = firebase.getReference("EventsDatabase");
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
                    btn_choose_start_date.setText(date_text);

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
            if(!b){
                text = text.replaceFirst("\\s+", "");
                ((EditText) view).setText(text);
            }
        }

    }

    @Override
    public void setOnDayFrequency(int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_BY_AMOUNT);

        event.setFrequency(selected_frequency);
        event.setAmount(selected_amount);

        String msg = "Every " + selected_frequency + " days " + selected_amount + " times";

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayFrequency(int selected_frequency, LocalDate selected_end) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        String msg = "Every " + selected_frequency + " days until " + Utils_Calendar.DateToTextLocal(selected_end);

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_AND_MONTH_BY_AMOUNT);

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

        btn_repeat.setText(msg);
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

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, int selected_amount) {
        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDay(selected_day);

        String msg = "Every month on " + selected_day + " day with frequency of " + selected_frequency + " " + selected_amount + " times";

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, LocalDate selected_end) {
        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_AND_MONTH_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDay(selected_day);

        String msg = "Every month on " + selected_day + " day with frequency of " + selected_frequency + " until " + Utils_Calendar.DateToTextLocal(selected_end);

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_AND_YEAR_BY_AMOUNT);

        event.setFrequency(selected_frequency);
        event.setAmount(selected_amount);

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);

        String msg = "Every month on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " " + selected_amount + " times";

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, LocalDate selected_end) {

        row_event = false;
        event.clearFrequencyData();


        event.setFrequencyType(DAY_OF_WEEK_AND_MONTH_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);

        String msg = "Every month on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " until " + Utils_Calendar.DateToTextLocal(selected_end);

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, int selected_amount) {

        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDay(selected_day);
        event.setMonth(selected_month);

        String msg = "Every year on " + selected_day + " of " +
                Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " " + selected_amount + " times";

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, LocalDate selected_end) {

        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_AND_YEAR_BY_END);

        event.setFrequency(selected_frequency);
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());
        event.setDay(selected_day);

        event.setMonth(selected_month);

        String msg = "Every year on " + selected_day + " of " +
                Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " until " + Utils_Calendar.DateToTextLocal(selected_end);

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, int selected_amount) {
        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);

        event.setFrequency(selected_frequency);;
        event.setAmount(selected_amount);

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);;
        event.setMonth(selected_month);

        String msg = "Every " + Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " " + selected_amount + " times";

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, LocalDate selected_end) {
        row_event = false;
        event.clearFrequencyData();

        event.setFrequencyType(DAY_OF_WEEK_AND_YEAR_BY_END);

        event.setFrequency(selected_frequency);;
        event.updateFrequency_end(selected_end);

        Log.d("murad", event.getFrequency_end());

        event.setDayOfWeekPosition( selected_dayOfWeekPosition);
        event.setWeekNumber(selected_weekNumber);;
        event.setMonth(selected_month);

        String msg = "Every " + Month.of(selected_month).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, Utils_Calendar.locale) +
                " with frequency of " + selected_frequency + " until " + Utils_Calendar.DateToTextLocal(selected_end);

        btn_repeat.setText(msg);
    }

    @Override
    public void setOnNeverFrequency() {

        event.clearFrequencyData();

        event.setFrequencyType(DAY_BY_END);
        event.setFrequency(1);

        row_event = true;
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