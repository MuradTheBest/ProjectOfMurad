package com.example.projectofmurad;

import android.util.Log;

import com.example.projectofmurad.calendar.CalendarEvent;

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



    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getDefaultDate(LocalDate date){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
        return date.format(simpleDateFormat);
    }

    public static String getDefaultTime(LocalTime time){
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(simpleDateFormat);
    }

    public static void printHashMap(HashMap<LocalDate, ArrayList<CalendarEvent>> map){
        for (LocalDate date : map.keySet()) {
            ArrayList<CalendarEvent> eventArrayList = map.get(date);
            if(eventArrayList != null){
                for(CalendarEvent e : eventArrayList){
                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");

                    Log.d("murad", "key: " + Utils.getDefaultDate(date) + " value: " + e.getName() + " | " + e.getPlace() + " | " + e.getDescription());
                    Log.d("murad",  Utils.getDefaultDate(e.getStart_date()) + " | " + e.getStart_hour() + ":" + e.getStart_min());
                    Log.d("murad",  Utils.getDefaultDate(e.getEnd_date()) + " | " + e.getEnd_hour() + ":" + e.getEnd_min());

                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");
                }
            }
        }
    }


}
