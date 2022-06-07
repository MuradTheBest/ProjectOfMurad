package com.example.projectofmurad.calendar;

import androidx.annotation.NonNull;

import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class CalendarEvent implements Serializable {

    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_START = "start";
    public static final String KEY_EVENT_PRIVATE_ID = "privateId";

    private String privateId;
    private long range;

    private String groupKey;

    private String name;
    private String description;
    private String place;
    private long start;
    private long end;
    private int color;
    private boolean allDay;

    public CalendarEvent() {}

    public CalendarEvent(String name, String description,
                         String place, LocalDateTime startDateTime, LocalDateTime endDateTime, int color, boolean allDay) {

        this.name = name;
        this.description = description;
        this.place = place;
        this.start = getMillis(startDateTime);
        this.end = getMillis(endDateTime);
        this.color = color;
        this.allDay = allDay;
        this.groupKey = FirebaseUtils.CURRENT_GROUP_KEY;
    }

    public static CalendarEvent fromJson(String eventJson){
        return new Gson().fromJson(eventJson, CalendarEvent.class);
    }

    public void addDefaultParams(int color, String name, String description, String place,
                                 @NonNull LocalDateTime startDateTime, @NonNull LocalDateTime endDateTime) {
        this.name = name;
        this.description = description;
        this.place = place;

        this.start = getMillis(startDateTime);
        this.end = getMillis(endDateTime);

        this.range = end - start;
        this.color = color;
    }

    public static long getMillis(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
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

    public String getStartDate() {
        return CalendarUtils.DateToTextLocal(receiveStartDate());
    }

    public LocalDate receiveStartDate(){
        return getDate(start);
    }

    public static LocalDate getDate(long millis){
        return getDateTime(millis).toLocalDate();
    }

    public static LocalDateTime getDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void updateStartDate(@NonNull LocalDate start_date) {
        updateStartDateTime(start_date.atTime(receiveStartTime()));
    }

    public void updateStartDateTime(LocalDateTime localDateTime) {
        this.start = getMillis(localDateTime);
    }

    public LocalTime receiveStartTime(){
        return getTime(start);
    }

    public static LocalTime getTime(long millis){
        return getDateTime(millis).toLocalTime();
    }

    public String getStartTime() {
        return CalendarUtils.TimeToText(receiveStartTime());
    }

    public void updateStartTime(@NonNull LocalTime start_time) {
        updateStartDateTime(receiveStartDate().atTime(start_time));
    }

    public String getStartDateTime() {
        return CalendarUtils.DateTimeToTextLocal(receiveStartDateTime());
    }

    public LocalDateTime receiveStartDateTime() {
        return getDateTime(start);
    }

    public String getEndDate() {
        return CalendarUtils.DateToTextLocal(receiveEndDate());
    }

    public LocalDate receiveEndDate(){
        return getDate(end);
    }

    public void updateEndDate(@NonNull LocalDate endDate) {
        updateEndDateTime(endDate.atTime(receiveEndTime()));
    }

    public void updateEndDateTime(LocalDateTime localDateTime) {
        this.end = getMillis(localDateTime);
    }

    public LocalTime receiveEndTime() {
        return getTime(end);
    }

    public String getEndTime() {
        return CalendarUtils.TimeToText(receiveEndTime());
    }

    public void updateEndTime(@NonNull LocalTime endTime) {
        updateEndDateTime(receiveEndDate().atTime(endTime));
    }

    public String getEndDateTime() {
        return CalendarUtils.DateTimeToTextLocal(receiveEndDateTime());
    }

    public LocalDateTime receiveEndDateTime() {
        return getDateTime(end);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    @NonNull
    @Override
    public String toString() {
        return "CalendarEvent{" +
                ", \n range = " + range +
                ", \n name = '" + name + '\'' +
                ", \n description = '" + description + '\'' +
                ", \n place = '" + place + '\'' +
                ", \n start = " + start +
                ", \n start = " + new Date(start) +
                ", \n end = " + end +
                ", \n end = " + new Date(end) +
                ", \n color = " + color +
                ", \n privateId = '" + privateId + '\'' +
                "\n}";
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

    public CalendarEvent copy() {

        CalendarEvent newEvent = new CalendarEvent();
        newEvent.setStart(getStart());
        newEvent.setEnd(getEnd());
        newEvent.setName(getName());
        newEvent.setDescription(getDescription());
        newEvent.setPlace(getPlace());
        newEvent.setColor(getColor());
        newEvent.setPrivateId(getPrivateId());
        newEvent.setRange(getRange());
        newEvent.setAllDay(isAllDay());

        return newEvent;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }
}
