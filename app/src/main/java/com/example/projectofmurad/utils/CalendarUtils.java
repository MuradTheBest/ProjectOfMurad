package com.example.projectofmurad.utils;

import android.util.Patterns;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * The type Calendar utils.
 */
public abstract class CalendarUtils {

    /**
     * The constant dateFormatOnline.
     */
    public final static DateTimeFormatter dateFormatOnline = DateTimeFormatter.ofPattern("E, dd.MM.yyyy", Locale.ENGLISH);

    /**
     * The constant dateFormatLocal.
     */
    public final static DateTimeFormatter dateFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");

    /**
     * The constant dateTimeFormatLocal.
     */
    public final static DateTimeFormatter dateTimeFormatLocal = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");

    /**
     * The constant timeFormat.
     */
    public final static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Gets locale.
     *
     * @return the locale
     */
    @NonNull
    public static Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Set locale.
     */
    public static void setLocale(){
        FirebaseUtils.getFirebaseAuth().setLanguageCode(Locale.getDefault().getLanguage());

        if(!Locale.getDefault().getLanguage().equals(new Locale("he").getLanguage())){
            Locale.setDefault(Locale.ENGLISH);
        }
    }

    /**
     * Get narrow days of week string [ ].
     *
     * @return the string [ ]
     */
    @NonNull
    public static String[] getNarrowDaysOfWeek(){
        String[] daysOfWeek = new String[7];

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            daysOfWeek[i] = d.getDisplayName(TextStyle.NARROW, getLocale());
        }

        return daysOfWeek;
    }

    /**
     * Get short days of week string [ ].
     *
     * @return the string [ ]
     */
    @NonNull
    public static String[] getShortDaysOfWeek(){
        String[] daysOfWeek = new String[7];

        for(int i = 0; i < DayOfWeek.values().length; i++) {
            DayOfWeek d = DayOfWeek.of(i+1);
            daysOfWeek[i] = d.getDisplayName(TextStyle.SHORT, getLocale());
        }

        return daysOfWeek;
    }

    /**
     * Get week number int.
     *
     * @param date the date
     *
     * @return the int
     */
    public static int getWeekNumber(@NonNull LocalDate date){
        Month month = date.getMonth();

        int count = 0;
        while(date.getMonth().equals(month)){
            count++;
            date = date.minusWeeks(1);
        }

        return count;
    }

    /**
     * Date time to text local string.
     *
     * @param dateTime the date time
     *
     * @return the string
     */
    public static String DateTimeToTextLocal(@NonNull LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatLocal.withLocale(getLocale()));
    }

    /**
     * Date to text online string.
     *
     * @param date the date
     *
     * @return the string
     */
    public static String DateToTextOnline(@NonNull LocalDate date){
        return date.format(dateFormatOnline);
    }

    /**
     * Date to text local string.
     *
     * @param date the date
     *
     * @return the string
     */
    public static String DateToTextLocal(@NonNull LocalDate date){
        return date.format(dateFormatLocal.withLocale(getLocale()));
    }

    /**
     * Time to text string.
     *
     * @param time the time
     *
     * @return the string
     */
    public static String TimeToText(@NonNull LocalTime time){
        return time.format(timeFormat);
    }

    /**
     * Is email valid boolean.
     *
     * @param email the email
     *
     * @return the boolean
     */
    public static boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Is phone valid boolean.
     *
     * @param phone the phone
     *
     * @return the boolean
     */
    public static boolean isPhoneValid(String phone){
        return Patterns.PHONE.matcher(phone).matches();
    }
}
