package com.example.projectofmurad;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Utils {

    //public final static Locale locale = Locale.getDefault();
    public final static Locale locale = Locale.ENGLISH;

    public final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy").withLocale(locale);
    public final static DateTimeFormatter dateFormatForFB = DateTimeFormatter.ofPattern("dd-MM-yyyy").withLocale(locale);
    public final static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public final static String SHARED_PREFERENCES_KEY_USERNAME = "shared_preferences_key_username";
    public final static String SHARED_PREFERENCES_KEY_EMAIL_ADDRESS = "shared_preferences_key_email_address";
    public final static String SHARED_PREFERENCES_KEY_PASSWORD = "shared_preferences_key_password";

    public final static String INTENT_KEY_EVENT_KEY = "intent_key_event_key";
    public final static String INTENT_KEY_EVENT_NAME = "intent_key_event_name";
    public final static String INTENT_KEY_EVENT_DESCRIPTION = "intent_key_event_description";
    public final static String INTENT_KEY_EVENT_PLACE = "intent_key_event_place";
    public final static String INTENT_KEY_EVENT_COLOR = "intent_key_event_color";
    public final static String INTENT_KEY_EVENT_START_DATE = "intent_key_event_start_date";
    public final static String INTENT_KEY_EVENT_START_TIME = "intent_key_event_start_time";
    public final static String INTENT_KEY_EVENT_END_DATE = "intent_key_event_end_date";
    public final static String INTENT_KEY_EVENT_END_TIME = "intent_key_event_end_time";


    public static HashMap<LocalDate, ArrayList<CalendarEvent>> map = new HashMap<>();

    public static DatabaseReference eventsDatabase = FirebaseDatabase.getInstance().getReference("EventsDatabase");


    public static boolean isEmailValid(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String[] getShortDayOfWeek(){
        String[] days = new String[7];

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            days[i] = d.getDisplayName(TextStyle.NARROW, locale);
            Log.d("murad", "position " + i + " is " + days[i]);
        }

        return days;
    }

    public static int getWeekNumber(LocalDate date){
        // Or use a specific locale, or configure your own rules
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        return weekNumber;
    }

    public static String DateToText(LocalDate date){
        return date.format(dateFormat);
    }

    public static LocalDate TextToDate(String date){
        return LocalDate.parse(date, dateFormat);
    }

    public static String DateToTextForFirebase(LocalDate date){
        return date.format(dateFormatForFB);
    }

    public static LocalDate TextToDateForFirebase(String date){
        return LocalDate.parse(date, dateFormatForFB);
    }

    public static String TimeToText(LocalTime time){
        return time.format(timeFormat);
    }

    public static LocalTime TextToTime(String time){
        return LocalTime.parse(time, timeFormat);
    }

    public static String TextToTextForFirebase(String text){
        LocalDate date = TextToDate(text);
        return DateToTextForFirebase(date);
    }

    public static String TextForFirebaseToText(String text){
        LocalDate date = TextToDateForFirebase(text);
        return DateToText(date);
    }

    public static void printHashMap(HashMap<LocalDate, ArrayList<CalendarEvent>> map){
        for (LocalDate date : map.keySet()) {
            ArrayList<CalendarEvent> eventArrayList = map.get(date);
            if(eventArrayList != null){
                for(CalendarEvent e : eventArrayList){
                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");

                    Log.d("murad", "key: " + Utils.DateToText(date) + " value: " + e.getName() + " | " + e.getPlace() + " | " + e.getDescription());
                    Log.d("murad",  Utils.DateToText(e.getStart_date()) + " | " + e.getStart_hour() + ":" + e.getStart_min());
                    Log.d("murad",  Utils.DateToText(e.getEnd_date()) + " | " + e.getEnd_hour() + ":" + e.getEnd_min());

                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");
                }
            }
        }
    }

    public static void addEvent(CalendarEvent event){
        LocalDate start_date = event.getStart_date();
        LocalDate end_date = event.getEnd_date();
        addOrCreateObjectInHashMap(start_date, event);

        if(!start_date.equals(end_date)){
            addOrCreateObjectInHashMap(end_date, event);
        }
    }

    private static void addOrCreateObjectInHashMap(LocalDate date, CalendarEvent event){
        if(Utils.map.containsKey(date)){
            if(Utils.map.get(date) == null){
                ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
                eventArrayList.add(event);
                Utils.map.put(date, eventArrayList);
                Log.d("murad", "eventArrayList on this key is null");
            }
            else {
                Utils.map.get(date).add(event);
                Log.d("murad", "event added to eventArrayList on this key ");
            }
        }
        else {
            ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
            eventArrayList.add(event);
            Utils.map.put(date, eventArrayList);
        }
    }

    public static boolean areEventDetailsValid(Context context, String name, String description, String place){
        String msg = "";

        boolean editTextsFilled = true;
        if(name.isEmpty()) {
            msg += "name";
            editTextsFilled = false;
        }
        else {
            name = name.replaceFirst("\\s+", "");
        }
        if(description.isEmpty()) {
            if(!editTextsFilled) {
                msg += ", ";
            }
            msg += "description";
            editTextsFilled = false;
        }
        else {
            description = description.replaceFirst("\\s+", "");;
        }

        if(place.isEmpty()) {
            /*if(!editTextsFilled){
                msg += ", ";
            }*/
            msg += (editTextsFilled ? "" : ", ");
            msg += "place";
            editTextsFilled = false;
        }
        else {
            place = place.replaceFirst("\\s+", "");;
        }

        if(!editTextsFilled){
            Toast.makeText(context, "Please enter event's " + msg, Toast.LENGTH_LONG).show();
        }

        return editTextsFilled;
    }
}
