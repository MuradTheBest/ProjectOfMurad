package com.example.projectofmurad;

import android.util.Log;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    final static String SHARED_PREFERENCES_KEY_USERNAME = "shared_preferences_key_username";
    final static String SHARED_PREFERENCES_KEY_EMAIL_ADDRESS = "shared_preferences_key_email_address";
    final static String SHARED_PREFERENCES_KEY_PASSWORD = "shared_preferences_key_password";

    public static HashMap<LocalDate, ArrayList<CalendarEvent>> map = new HashMap<>();

    public static DatabaseReference eventsDatabase = FirebaseDatabase.getInstance().getReference("EventsDatabase");


    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String DateToText(LocalDate date){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
        return date.format(simpleDateFormat);
    }

    public static LocalDate TextToDate(String date){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
        return LocalDate.parse(date, simpleDateFormat);
    }

    public static String DateToTextForFirebase(LocalDate date){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(simpleDateFormat);
    }

    public static LocalDate TextToDateForFirebase(String date){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, simpleDateFormat);
    }

    public static String TimeToText(LocalTime time){
        DateTimeFormatter simpleTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(simpleTimeFormat);
    }

    public static LocalTime TextToTime(String time){
        DateTimeFormatter simpleTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time, simpleTimeFormat);
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


}
