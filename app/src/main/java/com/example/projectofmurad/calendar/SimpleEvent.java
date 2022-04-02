package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.tracking.Training;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class SimpleEvent implements Serializable {

    protected String chainId;

    protected long range;


    protected String name;
    protected String description;
    protected String place;

    //ToDo change dates and times to long

    protected int color;
    protected boolean allDay;

    protected String frequencyType;

    protected int frequency;
    protected int amount;

    protected int day;
    protected int dayOfWeekPosition;
    protected List<Boolean> array_frequencyDayOfWeek;
    protected int weekNumber;
    protected int month;

    protected List<Training> trainings;

    protected String startTime;

    protected long frequencyStart;
    protected String frequency_start;

    protected String endTime;

    protected long frequencyEnd;
    protected String frequency_end;

    public SimpleEvent(){

        this.frequencyType = MySuperTouchActivity.DAY_BY_END;
        this.frequency = 1;

        this.startTime = "08:00";
        this.endTime = "09:00";

/*
        this.event_id = "";

        this.name = "";
        this.description = "";
        this.place = "";

        this.startDate = "";
        this.startTime = "";

        this.endDate = "";
        this.endTime = "";

        this.timestamp = 0;

        this.frequency = 0;
        this.frequencyDay = "";
        this.frequencyDayOfWeek = new String[]{};
        this.frequencyMonth = "";
        this.frequencyYear = "";*/
    }

    public void addDefaultParams(int color, String name, String description, String place, int timestamp, @NonNull LocalDate startDate, LocalTime startTime, @NonNull LocalDate endDate, LocalTime endTime) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.startTime = UtilsCalendar.TimeToText(startTime);

        this.endTime = UtilsCalendar.TimeToText(endTime);


        this.frequencyStart = getMillis(startDate.atTime(startTime));
        this.frequencyEnd = getMillis(endDate.atTime(endTime));

        this.range = frequencyEnd - frequencyStart;

        this.color = color;

        /*Calendar start = Calendar.getInstance();
        start.set(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(),
                startTime.getHour(), startTime.getMinute());

        this.timeData = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth(),
                endTime.getHour(), endTime.getMinute());

        this.range = end.getTimeInMillis() - start.getTimeInMillis();*/
/*
        Period period = Period.between(startDate, endDate);
        long d = endDate.toEpochDay();
        startDate.getChronology().dateEpochDay(d);


        startDate.getChronology().dateEpochDay(d);

        LocalDateTime newDateTime =
                LocalDateTime.from(Instant.ofEpochMilli(end.getTimeInMillis()));

        newDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        newDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        ZonedDateTime zonedDateTime = ZonedDateTime.now();*/

    }

    public void addDefaultParams(int color, String name, String description, String place, int timestamp, @NonNull LocalDateTime startDateTime, @NonNull LocalDateTime endDateTime) {

        this.name = name;
        this.description = description;
        this.place = place;


        this.startTime = UtilsCalendar.TimeToText(startDateTime.toLocalTime());

        this.endTime = UtilsCalendar.TimeToText(endDateTime.toLocalTime());



        this.frequencyStart = getMillis(startDateTime);
        this.frequencyEnd = getMillis(endDateTime);

        this.range = frequencyEnd - frequencyStart;

        this.color = color;

    }

    public static long getMillis(@NonNull LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getMillis(@NonNull LocalDate localDate, LocalTime localTime){
        return localDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime getDateTime(long millis){
        Log.d("murad", "getDateTime " + new Date(millis));
        Log.d("murad", "instant " + Instant.ofEpochMilli(millis).toString());

        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate getDate(long millis){
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime getTime(long millis){
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
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

    public String getStartTime() {
        return startTime;
    }

    public LocalTime receiveStart_time(){
        return UtilsCalendar.TextToTime(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void updateStart_time(LocalTime start_time) {
        this.startTime = UtilsCalendar.TimeToText(start_time);
    }

    public String getEndTime() {
        return endTime;
    }

    public LocalTime receiveEnd_time(){
        return UtilsCalendar.TextToTime(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void updateEnd_time(LocalTime end_time) {
        this.endTime = UtilsCalendar.TimeToText(end_time);
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
        return UtilsCalendar.TextToDateForFirebase(frequency_start);
    }

    public void setFrequency_start(String frequency_start) {
        this.frequency_start = frequency_start;
    }

    public void updateFrequency_start(LocalDate frequency_start) {
        this.frequency_start = UtilsCalendar.DateToTextOnline(frequency_start);
        Log.d("murad", "Frequency_start date updated " + UtilsCalendar.DateToTextOnline(frequency_start));
    }

    public String getFrequency_end() {
        return frequency_end;
    }

    public LocalDate receiveFrequency_end(){
        Log.d("murad", "Frequency end date received " + frequency_end);
        return UtilsCalendar.TextToDateForFirebase(frequency_end);
    }

    public void setFrequency_end(String frequency_end) {
        this.frequency_end = frequency_end;
    }

    public void updateFrequency_end(LocalDate frequency_end) {
        this.frequency_end = UtilsCalendar.DateToTextOnline(frequency_end);
        Log.d("murad", "Absolute end date updated " + UtilsCalendar.DateToTextOnline(frequency_end));
    }

    public void clearFrequencyData(){
        this.frequencyType = MySuperTouchActivity.DAY_BY_END;
        this.frequency = 1;

        this.day = 0;
        this.dayOfWeekPosition = 0;
        this.array_frequencyDayOfWeek = null;
        this.weekNumber = 0;
        this.month = 0;
        this.frequency_start = "";
        this.frequency_end = "";
    }

    public long getRange() {
        return range;
    }

    public void setRange(long range) {
        this.range = range;
    }

    public String toJson(){
        return new Gson().toJson(this);
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }
}
