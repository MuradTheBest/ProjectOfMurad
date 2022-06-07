package com.example.projectofmurad.utils;

import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public abstract class CalendarUtils {

    public final static DateTimeFormatter dateFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", Locale.ENGLISH);
    public final static DateTimeFormatter dateFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");

    public final static DateTimeFormatter dateTimeFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm", Locale.ENGLISH);
    public final static DateTimeFormatter dateTimeFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");

    public final static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    @NonNull
    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static void setLocale(){
        FirebaseUtils.getFirebaseAuth().setLanguageCode(Locale.getDefault().getLanguage());

        if(!Locale.getDefault().getLanguage().equals(new Locale("he").getLanguage())){
            Locale.setDefault(Locale.ENGLISH);
        }
    }

    @NonNull
    public static String[] getNarrowDaysOfWeek(){
        String[] daysOfWeek = new String[7];

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            daysOfWeek[i] = d.getDisplayName(TextStyle.NARROW, getLocale());
        }

        return daysOfWeek;
    }

    @NonNull
    public static String[] getShortDaysOfWeek(){
        String[] daysOfWeek = new String[7];

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            daysOfWeek[i] = d.getDisplayName(TextStyle.SHORT, getLocale());
        }

        return daysOfWeek;
    }

    public static int getWeekNumber(@NonNull LocalDate date){
        Month month = date.getMonth();

        int count = 0;
        while(date.getMonth().equals(month)){
            count++;
            date = date.minusWeeks(1);
        }

        return count;
    }

    public static String DateTimeToTextOnline(@NonNull LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatOnline);
    }

    public static String DateTimeToTextLocal(@NonNull LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatLocal.withLocale(getLocale()));
    }

    public static String DateToTextOnline(@NonNull LocalDate date){
        return date.format(dateFormatOnline);
    }

    public static String DateToTextLocal(@NonNull LocalDate date){
        return date.format(dateFormatLocal.withLocale(getLocale()));
    }

    public static LocalDate TextToDateForFirebase(String date){
        return LocalDate.parse(date, dateFormatOnline);
    }

    @NonNull
    public static String DateToTextForFirebase(@NonNull LocalDate date){
        return date.toString();
    }

    public static String TimeToText(@NonNull LocalTime time){
        return time.format(timeFormat);
    }

    public static boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone){
        return Patterns.PHONE.matcher(phone).matches();
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
