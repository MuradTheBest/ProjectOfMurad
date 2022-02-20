package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CalendarEvent implements Serializable {

    private String event_chain_id;
    private int timestamp;

    private String name;
    private String description;
    private String place;

    private String start_date;
    private String start_time;

    private String end_date;
    private String end_time;

    private int color;

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

    private boolean alarm;

    //ToDo save in local database
    private boolean alarmAlreadySet;

    public CalendarEvent(){

        this.frequencyType = MySuperTouchActivity.DAY_BY_END;
        this.frequency = 1;

        this.start_time = "08:00";
        this.end_time = "09:00";

        this.alarmAlreadySet = false;

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

    public void addDefaultParams(int color, String name, String description, String place, int timestamp,LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.timestamp = timestamp;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_chain_id = this.start_date + "-" + this.timestamp;

        this.color = color;

        this.alarmAlreadySet = false;


    }

    public void addDefaultParams(int color, String name, String description, String place, int timestamp,LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, boolean setAlarm) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.timestamp = timestamp;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_chain_id = this.start_date + "-" + this.timestamp;

        this.color = color;

        this.alarm = setAlarm;
    }

    public String getEvent_chain_id() {
        return event_chain_id;
    }

    public void setEvent_chain_id(String event_chain_id) {
        this.event_chain_id = event_chain_id;
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
        return Utils_Calendar.TextToDateForFirebase(start_date);
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
        return Utils_Calendar.TextToDateForFirebase(end_date);
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
    }

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
        return Utils_Calendar.TextToDateForFirebase(frequency_start);
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
        return Utils_Calendar.TextToDateForFirebase(frequency_end);
    }

    public void setFrequency_end(String frequency_end) {
        this.frequency_end = frequency_end;
    }

    public void updateFrequency_end(LocalDate frequency_end) {
        this.frequency_end = Utils_Calendar.DateToTextOnline(frequency_end);
        Log.d("murad", "Absolute end date updated " + Utils_Calendar.DateToTextOnline(frequency_end));
    }

    public LocalDateTime receiveStart_dateTime(){
        return LocalDateTime.of(receiveStart_date(), receiveStart_time());
    }

    public LocalDateTime receiveEnd_dateTime(){
        return LocalDateTime.of(receiveEnd_date(), receiveEnd_time());
    }

    public String getStart_dateTime(){
        return Utils_Calendar.DateTimeToTextOnline(LocalDateTime.of(receiveStart_date(), receiveStart_time()));
    }

    public String getEnd_dateTime(){
        return Utils_Calendar.DateTimeToTextOnline(LocalDateTime.of(receiveEnd_date(), receiveEnd_time()));
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

/*    @NonNull
    public String toString(){

        return *//*"------------------------------------------------------------------------------------------------------------------------------------ \n" +*//*
                "\n" + this.getName() + " | " + this.getPlace() + " | " + this.getDescription() +
                "\n" + this.getStart_date() + " | " + this.getStart_time() +
                "\n" + this.getEnd_date() + " | " + this.getEnd_time() +
                "\n"*//* + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n"*//*;
    }*/

    @NonNull
    @Override
    public String toString() {
        return "CalendarEvent{ " +
                " \n event_chain_id = '" + event_chain_id + '\'' +
                ", \n timestamp = " + timestamp +
                ", \n name = '" + name + '\'' +
                ", \n description = '" + description + '\'' +
                ", \n place = '" + place + '\'' +
                ", \n start_date = '" + start_date + '\'' +
                ", \n start_time = '" + start_time + '\'' +
                ", \n end_date = '" + end_date + '\'' +
                ", \n end_time = '" + end_time + '\'' +
                ", \n color = " + color +
                ", \n frequencyType = '" + frequencyType + '\'' +
                ", \n event_private_id = '" + event_private_id + '\'' +
                ", \n frequency = " + frequency +
                ", \n amount = " + amount +
                ", \n day = " + day +
                ", \n dayOfWeekPosition = " + dayOfWeekPosition +
                ", \n array_frequencyDayOfWeek = " + array_frequencyDayOfWeek +
                ", \n weekNumber = " + weekNumber +
                ", \n month = " + month +
                ", \n isLast = " + isLast +
                ", \n frequency_start = '" + frequency_start + '\'' +
                ", \n frequency_end = '" + frequency_end + '\'' +
                ", \n alarm = " + alarm +
                ", \n alarmAlreadySet = " + alarmAlreadySet +
                '}';
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

}
