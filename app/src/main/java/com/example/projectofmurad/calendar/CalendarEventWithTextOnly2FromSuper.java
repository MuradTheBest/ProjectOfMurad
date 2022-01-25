package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.MySuperTouchActivity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CalendarEventWithTextOnly2FromSuper extends CalendarEventSuperClass implements Serializable {

    private String frequencyType;
    private String event_private_id;

    private int frequency;
    private int amount;

    private int day;
    private int dayOfWeekPosition;
    private List<Boolean> array_frequencyDayOfWeek;
    private int weekNumber;
    private int month;

    private boolean isLast;

    private String frequency_start;
    private String frequency_end;


    public CalendarEventWithTextOnly2FromSuper(){
        super();

        this.frequencyType = MySuperTouchActivity.DAY_BY_END;
        this.frequency = 1;

/*
        this.event_id = "";

        this.name = "";
        this.description = "";
        this.place = "";

        this.start_date = "";
        this.start_time = "";

        this.end_date = "";
        this.end_time = "";

        this.timestamp = 0;

        this.frequency = 0;
        this.frequencyDay = "";
        this.frequencyDayOfWeek = new String[]{};
        this.frequencyMonth = "";
        this.frequencyYear = "";*/
    }

    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description,
                                               String place, int timestamp,
                                               LocalDate startDate, LocalTime startTime,
                                               LocalDate endDate, LocalTime endTime) {

        super(color, name, description, place, timestamp, startDate, startTime, endDate, endTime);

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }

    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }
    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               List<Boolean> selected_array_frequencyDayOfWeek,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

        this.array_frequencyDayOfWeek = selected_array_frequencyDayOfWeek;

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }

    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               int selected_day,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

        this.day = selected_day;

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }

    public CalendarEventWithTextOnly2FromSuper(int flag, int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               int selected_weekNumber, int selected_dayOfWeekPosition,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

        this.dayOfWeekPosition = selected_dayOfWeekPosition;
        this.weekNumber = selected_weekNumber;

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }


    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               int selected_day, int selected_month,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

        this.day = selected_day;
        this.month = selected_month;
/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }


    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place,
                                               LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                               int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, int selected_amount) {

        super(color, name, description, place, startDate, startTime, endDate, endTime);


        this.frequency = selected_frequency;
        this.amount = selected_amount;

        this.dayOfWeekPosition = selected_dayOfWeekPosition;
        this.weekNumber = selected_weekNumber;
        this.month = selected_month;

/*        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;*/
    }


    public CalendarEventWithTextOnly2FromSuper(int color, String name, String description, String place, String start_date, String start_time, String end_date, String end_time) {

        super(color, name, description, place, start_date, start_time, end_date, end_time);

/*        this.timestamp = Utils_Calendar.TextToTime(start_time).toSecondOfDay();

        this.event_id = start_date + "-" + this.timestamp;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = start_date;

        this.start_time = start_time;

        this.end_date = end_date;

        this.end_time = end_time;

        this.color = color;*/
    }

/*    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStart_date() {
        return start_date;
        //return LocalDate.of(start_year, start_month, start_day);
    }

    public LocalDate receiveStart_date(){
        return Utils_Calendar.TextToDate(start_date);
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void updateStart_date(LocalDate start_date) {
        this.start_date = Utils_Calendar.DateToTextOnline(start_date);
    }

    public String getStart_time() {
        return start_time;
    }

    public LocalTime receiveStart_time(){
        return Utils_Calendar.TextToTime(start_time);
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void updateStart_time(LocalTime start_time) {
        this.start_time = Utils_Calendar.TimeToText(start_time);
    }

    public String getEnd_date() {
        return end_date;
    }

    public LocalDate receiveEnd_date(){
        return Utils_Calendar.TextToDate(end_date);
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void updateEnd_date(LocalDate end_date) {
        this.end_date = Utils_Calendar.DateToTextOnline(end_date);
    }

    public String getEnd_time() {
        return end_time;
    }

    public LocalTime receiveEnd_time(){
        return Utils_Calendar.TextToTime(end_time);
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void updateEnd_time(LocalTime end_time) {
        this.end_time = Utils_Calendar.TimeToText(end_time);
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void deleteTimeStamp(){
        this.timestamp = 0;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }*/

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayOfWeekPosition() {
        return dayOfWeekPosition;
    }

    public void setDayOfWeekPosition(int dayOfWeekPosition) {
        this.dayOfWeekPosition = dayOfWeekPosition;
    }

    public List<Boolean> getArray_frequencyDayOfWeek() {
        return array_frequencyDayOfWeek;
    }

    public void setArray_frequencyDayOfWeek(List<Boolean> array_frequencyDayOfWeek) {
        this.array_frequencyDayOfWeek = array_frequencyDayOfWeek;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getFrequency_start() {
        return frequency_start;
    }

    public LocalDate receiveFrequency_start(){
        Log.d("murad", "Frequency start date received " + frequency_start);
        return Utils_Calendar.TextToDate(frequency_start);
    }

    public void setFrequency_start(String frequency_start) {
        this.frequency_start = frequency_start;
    }

    public void updateFrequency_start(LocalDate frequency_start) {
        this.frequency_start = Utils_Calendar.DateToTextOnline(frequency_start);
        Log.d("murad", "Frequency_start date updated " + Utils_Calendar.DateToTextOnline(frequency_start));
    }

    public String getFrequency_end() {
        return frequency_end;
    }

    public LocalDate receiveFrequency_end(){
        Log.d("murad", "Frequency end date received " + frequency_end);
        return Utils_Calendar.TextToDate(frequency_end);
    }

    public void setFrequency_end(String frequency_end) {
        this.frequency_end = frequency_end;
    }

    public void updateFrequency_end(LocalDate frequency_end) {
        this.frequency_end = Utils_Calendar.DateToTextOnline(frequency_end);
        Log.d("murad", "Absolute end date updated " + Utils_Calendar.DateToTextOnline(frequency_end));
    }

    public String getEvent_private_id() {
        return event_private_id;
    }

    public void setEvent_private_id(String event_private_id) {
        this.event_private_id = event_private_id;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public void clearFrequencyData(){
        this.day = 0;
        this.dayOfWeekPosition = 0;
        this.array_frequencyDayOfWeek = null;
        this.weekNumber = 0;
        this.month = 0;
        this.frequency_start = "";
        this.frequency_end = "";
    }

    @NonNull
    public String toString(){

        return /*"------------------------------------------------------------------------------------------------------------------------------------ \n" +*/
                "\n" + this.getName() + " | " + this.getPlace() + " | " + this.getDescription() +
                "\n" + this.getStart_date() + " | " + this.getStart_time() +
                "\n" + this.getEnd_date() + " | " + this.getEnd_time() +
                "\n"/* + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n"*/;
    }

}
