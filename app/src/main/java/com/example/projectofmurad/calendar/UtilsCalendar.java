package com.example.projectofmurad.calendar;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.FirebaseUtils;
import com.google.firebase.database.DatabaseReference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class UtilsCalendar {

    public static Locale locale = Locale.getDefault();
    public static Locale getLocale(){
      return locale;
    }


    public /*final*/ static DateTimeFormatter dateFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", Locale.ENGLISH);
    public /*final*/ static DateTimeFormatter dateTimeFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm", Locale.ENGLISH);
    public /*final*/ static DateTimeFormatter dateFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", locale).withLocale(locale);
    public /*final*/ static DateTimeFormatter dateTimeFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm", locale).withLocale(locale);

    public /*final*/ static DateTimeFormatter dateFormatForFBOnline = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    public /*final*/ static DateTimeFormatter dateFormatForFBOffline = DateTimeFormatter.ofPattern("dd-MM-yyyy", locale).withLocale(locale);

    public /*final*/ static SimpleDateFormat simpleDateFormatOnline = new SimpleDateFormat("E, dd.MM.yyyy", Locale.ENGLISH);
    public /*final*/ static SimpleDateFormat simpleDateTimeFormatOnline = new SimpleDateFormat("E, dd.MM.yyyy, HH:mm", Locale.ENGLISH);
    public /*final*/ static SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat("E, dd.MM.yyyy", locale);

    public /*final*/ static SimpleDateFormat simpleDateFormatForFBOnline = new SimpleDateFormat("dd-MM-yyyy", locale);
    public /*final*/ static SimpleDateFormat simpleDateFormatForFBOffline = new SimpleDateFormat("dd-MM-yyyy", locale);

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


    public final static String INTENT_KEY_SELECTED_YEAR = "intent_key_selected_year";
    public final static String INTENT_KEY_SELECTED_MONTH = "intent_key_selected_month";
    public final static String INTENT_KEY_SELECTED_DAY = "intent_key_selected_day";

    public final static String INTENT_KEY_PASSING_YEAR = "intent_key_passing_year";
    public final static String INTENT_KEY_PASSING_MONTH = "intent_key_passing_month";
    public final static String INTENT_KEY_PASSING_DAY = "intent_key_passing_day";

    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_CHAIN_ID = "chainId";
    public static final String KEY_EVENT_TIMESTAMP = "timestamp";
    public static final String KEY_EVENT_range = "range";
    public static final String KEY_EVENT_position = "position";
    public static final String KEY_EVENT_name = "name";
    public static final String KEY_EVENT_description = "description";
    public static final String KEY_EVENT_place = "place";
    public static final String KEY_EVENT_start = "start";
    public static final String KEY_EVENT_startDate = "startDate";
    public static final String KEY_EVENT_startTime = "startTime";
    public static final String KEY_EVENT_startDateTime = "startDateTime";
    public static final String KEY_EVENT_end = "end";
    public static final String KEY_EVENT_endDate = "endDate";
    public static final String KEY_EVENT_endTime = "endTime";
    public static final String KEY_EVENT_endDateTime = "endDateTime";
    public static final String KEY_EVENT_color = "color";
    public static final String KEY_EVENT_allDay = "allDay";
    public static final String KEY_EVENT_frequencyType = "frequencyType";
    public static final String KEY_EVENT_PRIVATE_ID = "privateId";
    public static final String KEY_EVENT_frequency = "frequency";
    public static final String KEY_EVENT_amount = "amount";
    public static final String KEY_EVENT_day = "day";
    public static final String KEY_EVENT_dayOfWeekPosition = "dayOfWeekPosition";
    public static final String KEY_EVENT_array_frequencyDayOfWeek = "array_frequencyDayOfWeek";
    public static final String KEY_EVENT_weekNumber = "weekNumber";
    public static final String KEY_EVENT_month = "month";
    public static final String KEY_EVENT_isLast = "isLast";
    public static final String KEY_EVENT_frequency_start = "frequency_start";
    public static final String KEY_EVENT_frequency_end = "frequency_end";

    @NonNull
    public static DatabaseReference getEventByPrivate_Id(String private_id){
        return FirebaseUtils.eventsDatabase.child(private_id).getRef();
    }

    public static HashMap<LocalDate, ArrayList<CalendarEvent>> map = new HashMap<>();

    public static void setLocale(){
        FirebaseUtils.getFirebaseAuth().setLanguageCode(Locale.getDefault().getLanguage());

        if(Locale.getDefault().getLanguage().equals(new Locale("he").getLanguage())){
            Log.d("murad", "LOCALE IS HEBREW");
            locale = Locale.getDefault();
            Log.d("murad", "Locale " + locale.getDisplayName());
        }
        else{
            Log.d("murad", "LOCALE IS NOT HEBREW");
            Locale.setDefault(Locale.ENGLISH);
            Log.d("murad", "Locale " + locale.getDisplayName());

            locale = Locale.ENGLISH;


            dateFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", Locale.ENGLISH).withLocale(locale);
            dateFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", locale).withLocale(locale);
            dateFormatForFBOnline = DateTimeFormatter.ofPattern("dd-MM-yyyy", locale).withLocale(locale);
            dateFormatForFBOffline = DateTimeFormatter.ofPattern("dd-MM-yyyy", locale).withLocale(locale);
        }

    }

    @NonNull
    public static String[] getNarrowDaysOfWeek(){
        ArrayList<String> days = new ArrayList<>(7);

        //days.add(DayOfWeek.SUNDAY.getDisplayName(TextStyle.NARROW, locale));

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            days.add(d.getDisplayName(TextStyle.NARROW, locale));
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

    @NonNull
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

    public static int getWeekNumber(@NonNull LocalDate date){
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

    public static LocalDate getNextOccurrence(@NonNull LocalDate date, int weekNumber){
        int dayOfWeek = date.getDayOfWeek().getValue();

        LocalDate tmp = date;

//        Log.d("frequency_dayOfWeek_and_month", "getNextOccurrence: " + UtilsCalendar.DateToTextOnline(tmp));

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

    public static LocalDate getNextOccurrenceForLast(@NonNull LocalDate date, int dayOfWeek){
        int month_length = date.lengthOfMonth();
        LocalDate tmp = date.withDayOfMonth(month_length);

        while(tmp.getDayOfWeek().getValue() != dayOfWeek+1){
            tmp = tmp.minusDays(1);
        }

/*        TemporalField h = WeekFields.SUNDAY_START.dayOfWeek();
        DayOfWeek.from();
        tmp.get(h);*/

        return tmp;
    }

    public static String DateTimeToTextOnline(@NonNull LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatOnline);
    }

    public static String DateToTextOnline(@NonNull LocalDate date){
        return date.format(dateFormatOnline);
    }

    public static String DateTimeToTextLocal(@NonNull LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatLocal);
    }

    public static String DateToTextLocal(@NonNull LocalDate date){
        return date.format(dateFormatLocal);
    }

    public static String OnlineTextToLocal(String date){
        LocalDate h = LocalDate.parse(date, dateFormatOnline);
        return h.format(dateFormatLocal);
    }

    public static LocalDate TextToDateForFirebase(String date){
        return LocalDate.parse(date, dateFormatOnline);
    }

    public static String DateToTextForFirebase(@NonNull LocalDate date){
        return date.format(dateFormatForFBOnline);
    }

    /*public static LocalDate TextToDateForFirebase(String date){
        return LocalDate.parse(date, dateFormat);
    }*/

    public static String TimeToText(@NonNull LocalTime time){
        return time.format(timeFormat);
    }

    public static LocalTime TextToTime(String time){
        return LocalTime.parse(time, timeFormat);
    }

    public static String TextToTextForFirebase(String text){
        LocalDate date = TextToDateForFirebase(text);
        return DateToTextForFirebase(date);
    }

    /*public static String TextForFirebaseToText(String text){
        LocalDate date = TextToDateForFirebase(text);
        return DateToTextOnline(date);
    }*/

    public static void printHashMap(@NonNull HashMap<LocalDate, ArrayList<CalendarEvent>> map){
        for (LocalDate date : map.keySet()) {
            ArrayList<CalendarEvent> eventArrayList = map.get(date);
            if(eventArrayList != null){
                for(CalendarEvent e : eventArrayList){
                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");

                    Log.d("murad", "key: " + UtilsCalendar.DateToTextOnline(date) + " value: " + e.getName() + " | " + e.getPlace() + " | " + e.getDescription());
                    Log.d("murad",  e.getStartDate() + " | " + e.getStartTime());
                    Log.d("murad",  e.getEndDate() + " | " + e.getEndTime());

                    Log.d("murad",  "------------------------------------------------------------------------------------------------------------------------------------");
                }
            }
        }
    }

    public static void addEvent(@NonNull CalendarEvent event){
        LocalDate start_date = event.receiveStart_date();
        LocalDate end_date = event.receiveStart_date();
        addOrCreateObjectInHashMap(start_date, event);

        if(!start_date.equals(end_date)){
            addOrCreateObjectInHashMap(end_date, event);
        }
    }

    private static void addOrCreateObjectInHashMap(LocalDate date, CalendarEvent event){
        if(UtilsCalendar.map.containsKey(date)){
            if(UtilsCalendar.map.get(date) == null){
                ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
                eventArrayList.add(event);
                UtilsCalendar.map.put(date, eventArrayList);
                Log.d("murad", "eventArrayList on this key is null");
            }
            else {
                UtilsCalendar.map.get(date).add(event);
                Log.d("murad", "event added to eventArrayList on this key ");
            }
        }
        else {
            ArrayList<CalendarEvent> eventArrayList = new ArrayList<>();
            eventArrayList.add(event);
            UtilsCalendar.map.put(date, eventArrayList);
        }
    }

    public static boolean areEventDetailsValid(Context context, @NonNull String name, String description, String place){
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

    public static boolean areObjectDetailsValid(Context context, String object, @NonNull HashMap<String, String> list){
        StringBuilder msg = new StringBuilder();

        boolean editTextsFilled = true;

        for (String key : list.keySet()){
            String input = list.get(key);

            if (input != null && input.isEmpty()) {
    /*                if(!editTextsFilled) {
                        msg += ", ";
                    }*/
                msg.append(editTextsFilled ? "" : ", ");

                msg.append(key);
                editTextsFilled = false;
            }
        }

        if(!editTextsFilled){
            Toast.makeText(context, "Please enter " + object + "'s " + msg, Toast.LENGTH_LONG).show();
        }

        return editTextsFilled;
    }

    public static boolean isEmailValid(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone){
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isBooleanValid(@NonNull String bool){
        return bool.equals("true") || bool.equals("false");
    }

    public static int lighten(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = lightenColor(red, fraction);
        green = lightenColor(green, fraction);
        blue = lightenColor(blue, fraction);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private static int darkenColor(int color, double fraction) {
        return (int)Math.max(color - (color * fraction), 0);
    }

    private static int lightenColor(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }

    public static void animate(ViewGroup viewGroup){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

//        TransitionManager.beginDelayedTransition(viewGroup, trans);
        TransitionManager.beginDelayedTransition(viewGroup, changeBounds);


    }
}
