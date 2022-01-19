package com.example.projectofmurad.calendar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Utils_Calendar {

    public final static Locale locale = Locale.getDefault();

    /*public final static Locale locale = Locale.ENGLISH;*/

    public static String[] narrowDaysOfWeek = getNarrowDaysOfWeek();
    public static String[] shortDaysOfWeek = getShortDaysOfWeek();

    public final static DateTimeFormatter dateFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", Locale.ENGLISH);
    public final static DateTimeFormatter dateFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy").withLocale(locale);

    public final static DateTimeFormatter dateFormatForFBOnline= DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public final static DateTimeFormatter dateFormatForFBOffline = DateTimeFormatter.ofPattern("dd-MM-yyyy").withLocale(locale);

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

    public static String[] getNarrowDaysOfWeek(){
        ArrayList<String> days = new ArrayList<>(7);

        //days.add(DayOfWeek.SUNDAY.getDisplayName(TextStyle.NARROW, locale));

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            days.add(d.getDisplayName(TextStyle.NARROW, locale)) ;
            Log.d("murad", "position " + i + " is " + days.get(i));
        }

        String[] tmp = new String[7];
        int i=0;
        for(String d : days) {
            tmp[i] = d;
            i++;
        }


        return tmp;
    }

    public static String[] getShortDaysOfWeek(){
        ArrayList<String> days = new ArrayList<>(7);

        //days.add(DayOfWeek.SUNDAY.getDisplayName(TextStyle.NARROW, locale));

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            days.add(d.getDisplayName(TextStyle.SHORT, locale)) ;
            Log.d("murad", "position " + i + " is " + days.get(i));
        }

        String[] tmp = new String[7];
        int i=0;
        for(String d : days) {
            tmp[i] = d;
            i++;
        }


        return tmp;
    }

    /*public static int getWeekNumber(LocalDate date){
        // Or use a specific locale, or configure your own rules
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.get(weekFields.weekOfMonth());
    }*/

    public static int getWeekNumber(LocalDate date){
        Month month = date.getMonth();

        int count = 0;
        while(date.getMonth().equals(month)){
            count++;
            date = date.minusWeeks(1);
        }

        return count;
    }

    /*public static int getWeekNumber(LocalDate date){
        // Or use a specific locale, or configure your own rules
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int count = date.get(weekFields.weekOfMonth());

        int dayOfWeek = DayOfWeek.from(date).getValue();
        Log.d("murad", "dayOfWeek = " + dayOfWeek);

        Month month = date.getMonth();

        Log.d("murad", "weekNumber = " + count);

        int firstDayOfWeek = date.withDayOfMonth(1).getDayOfWeek().getValue();
        Log.d("murad", "firstDayOfWeek = " + firstDayOfWeek);

        if(dayOfWeek < firstDayOfWeek *//*|| (dayOfWeek == 7 && firstDayOfWeek !=7 )*//*){
            count--;
        }



        return count;
    }*/

    public static LocalDate getNextOccurrence(LocalDate date, int weekNumber){
        int dayOfWeek = date.getDayOfWeek().getValue();

        LocalDate tmp = date;

//        Log.d("frequency_dayOfWeek_and_month", "getNextOccurrence: " + Utils_Calendar.DateToTextOnline(tmp));

        for(int i = 1; i < weekNumber; i++) {
            tmp = tmp.plusWeeks(1);
        }

        return tmp;
    }

    public static LocalDate getFirstDayWithDayOfWeek(LocalDate date, int dayOfWeek){
        date = date.withDayOfMonth(1);
        while(date.getDayOfWeek().getValue() != dayOfWeek+1){
            date = date.plusDays(1);
        }

        return date;
    }

    public static LocalDate getNextOccurrenceForLast(LocalDate date, int dayOfWeek){
        int month_length = date.lengthOfMonth();
        LocalDate tmp = date.withDayOfMonth(month_length);

        while(tmp.getDayOfWeek().getValue() != dayOfWeek+1){
            tmp = tmp.minusDays(1);
        }

        return tmp;
    }

    public static String DateToTextOnline(LocalDate date){
        return date.format(dateFormatOnline);
    }

    public static String DateToTextLocal(LocalDate date){
        return date.format(dateFormatLocal);
    }

    public static String OnlineTextToLocal(String date){
        LocalDate h = LocalDate.parse(date, dateFormatOnline);
        return h.format(dateFormatLocal);
    }

    public static LocalDate TextToDate(String date){
        return LocalDate.parse(date, dateFormatOnline);
    }

    public static String DateToTextForFirebase(LocalDate date){
        return date.format(dateFormatForFBOnline);
    }

    /*public static LocalDate TextToDateForFirebase(String date){
        return LocalDate.parse(date, dateFormat);
    }*/

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

    /*public static String TextForFirebaseToText(String text){
        LocalDate date = TextToDateForFirebase(text);
        return DateToTextOnline(date);
    }*/

    public static void printHashMap(HashMap<LocalDate, ArrayList<CalendarEvent>> map){
        for (LocalDate date : map.keySet()) {
            ArrayList<CalendarEvent> eventArrayList = map.get(date);
            if(eventArrayList != null){
                for(CalendarEvent e : eventArrayList){
                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");

                    Log.d("murad", "key: " + Utils_Calendar.DateToTextOnline(date) + " value: " + e.getName() + " | " + e.getPlace() + " | " + e.getDescription());
                    Log.d("murad",  Utils_Calendar.DateToTextOnline(e.getStart_date()) + " | " + e.getStart_hour() + ":" + e.getStart_min());
                    Log.d("murad",  Utils_Calendar.DateToTextOnline(e.getEnd_date()) + " | " + e.getEnd_hour() + ":" + e.getEnd_min());

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
        if(Utils_Calendar.map.containsKey(date)){
            if(Utils_Calendar.map.get(date) == null){
                ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
                eventArrayList.add(event);
                Utils_Calendar.map.put(date, eventArrayList);
                Log.d("murad", "eventArrayList on this key is null");
            }
            else {
                Utils_Calendar.map.get(date).add(event);
                Log.d("murad", "event added to eventArrayList on this key ");
            }
        }
        else {
            ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
            eventArrayList.add(event);
            Utils_Calendar.map.put(date, eventArrayList);
        }
    }

    public static boolean areEventDetailsValid(Context context, String name, String description, String place){
        String msg = "";

        boolean editTextsFilled = true;
        if(name.isEmpty()) {
            msg += "name";
            editTextsFilled = false;
        }
/*        else {
            name = name.replaceFirst("\\s+", "");
        }*/

        if(description.isEmpty()) {
            if(!editTextsFilled) {
                msg += ", ";
            }
            msg += "description";
            editTextsFilled = false;
        }
/*        else {
            description = description.replaceFirst("\\s+", "");;
        }*/

        if(place.isEmpty()) {
            /*if(!editTextsFilled){
                msg += ", ";
            }*/
            msg += (editTextsFilled ? "" : ", ");
            msg += "place";
            editTextsFilled = false;
        }
/*        else {
            place = place.replaceFirst("\\s+", "");;
        }*/

        if(!editTextsFilled){
            Toast.makeText(context, "Please enter event's " + msg, Toast.LENGTH_LONG).show();
        }

        return editTextsFilled;
    }
}
