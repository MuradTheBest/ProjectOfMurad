package com.example.projectofmurad;

import android.os.Bundle;
import android.util.Log;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.Calendar_Month_Fragment;

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


    public static Calendar_Month_Fragment createCalendar_Month_Fragment(LocalDate selectedDate){
        Calendar_Month_Fragment fragment = new Calendar_Month_Fragment();
        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, selectedDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, selectedDate.getYear());

        fragment.setArguments(bundle);
        return fragment;
    }

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

    public static void addEvent(CalendarEvent event){
        LocalDate start_date = event.getStart_date();
        LocalDate end_date = event.getEnd_date();
        addOrCreateObjectInHashMap(start_date, event);
        addOrCreateObjectInHashMap(end_date, event);

        /*if(!start_date.equals(end_date.minusDays(1))){
            LocalDate interDate = start_date.plusDays(1);
            while(interDate != end_date){
                addOrCreateObjectInHashMap(interDate, event);
                interDate = interDate.plusDays(1);
            }
        }*/
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

    public static ArrayList<Calendar_Month_Fragment> move(ArrayList<Calendar_Month_Fragment> fragmentArrayList, int i){
        ArrayList<Calendar_Month_Fragment> tmp = fragmentArrayList;

        if(i == -1){
            tmp.set(4, fragmentArrayList.get(3));
            tmp.set(3, fragmentArrayList.get(2));
            tmp.set(2, fragmentArrayList.get(1));
            tmp.set(1, fragmentArrayList.get(0));
        }
        else if(i == 1){
            tmp.set(0, fragmentArrayList.get(1));
            tmp.set(1, fragmentArrayList.get(2));
            tmp.set(2, fragmentArrayList.get(3));
            tmp.set(3, fragmentArrayList.get(4));
        }
        return tmp;
    }


}
