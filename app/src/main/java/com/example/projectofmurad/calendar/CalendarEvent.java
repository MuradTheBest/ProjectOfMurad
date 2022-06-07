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

/**
 * The type Calendar event.
 */
public class CalendarEvent implements Serializable {

    /**
     * The constant KEY_EVENT.
     */
    public static final String KEY_EVENT = "event";
    /**
     * The constant KEY_EVENT_START.
     */
    public static final String KEY_EVENT_START = "start";
    /**
     * The constant KEY_EVENT_PRIVATE_ID.
     */
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

    /**
     * Instantiates a new Calendar event.
     */
    public CalendarEvent() {}

    /**
     * Instantiates a new Calendar event.
     *
     * @param name          the name
     * @param description   the description
     * @param place         the place
     * @param startDateTime the start date time
     * @param endDateTime   the end date time
     * @param color         the color
     * @param allDay        the all day
     */
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

    /**
     * From json calendar event.
     *
     * @param eventJson the event json
     *
     * @return the calendar event
     */
    public static CalendarEvent fromJson(String eventJson){
        return new Gson().fromJson(eventJson, CalendarEvent.class);
    }

    /**
     * Add default params.
     *
     * @param color         the color
     * @param name          the name
     * @param description   the description
     * @param place         the place
     * @param startDateTime the start date time
     * @param endDateTime   the end date time
     */
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

    /**
     * Gets millis.
     *
     * @param localDateTime the local date time
     *
     * @return the millis
     */
    public static long getMillis(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Gets start.
     *
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * Gets end.
     *
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    /**
     * Sets end.
     *
     * @param end the end
     */
    public void setEnd(long end) {
        this.end = end;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public String getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return CalendarUtils.DateToTextLocal(receiveStartDate());
    }

    /**
     * Receive start date local date.
     *
     * @return the local date
     */
    public LocalDate receiveStartDate(){
        return getDate(start);
    }

    /**
     * Get date local date.
     *
     * @param millis the millis
     *
     * @return the local date
     */
    public static LocalDate getDate(long millis){
        return getDateTime(millis).toLocalDate();
    }

    /**
     * Gets date time.
     *
     * @param millis the millis
     *
     * @return the date time
     */
    public static LocalDateTime getDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Update start date.
     *
     * @param start_date the start date
     */
    public void updateStartDate(@NonNull LocalDate start_date) {
        updateStartDateTime(start_date.atTime(receiveStartTime()));
    }

    /**
     * Update start date time.
     *
     * @param localDateTime the local date time
     */
    public void updateStartDateTime(LocalDateTime localDateTime) {
        this.start = getMillis(localDateTime);
    }

    /**
     * Receive start time local time.
     *
     * @return the local time
     */
    public LocalTime receiveStartTime(){
        return getTime(start);
    }

    /**
     * Get time local time.
     *
     * @param millis the millis
     *
     * @return the local time
     */
    public static LocalTime getTime(long millis){
        return getDateTime(millis).toLocalTime();
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        return CalendarUtils.TimeToText(receiveStartTime());
    }

    /**
     * Update start time.
     *
     * @param start_time the start time
     */
    public void updateStartTime(@NonNull LocalTime start_time) {
        updateStartDateTime(receiveStartDate().atTime(start_time));
    }

    /**
     * Gets start date time.
     *
     * @return the start date time
     */
    public String getStartDateTime() {
        return CalendarUtils.DateTimeToTextLocal(receiveStartDateTime());
    }

    /**
     * Receive start date time local date time.
     *
     * @return the local date time
     */
    public LocalDateTime receiveStartDateTime() {
        return getDateTime(start);
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return CalendarUtils.DateToTextLocal(receiveEndDate());
    }

    /**
     * Receive end date local date.
     *
     * @return the local date
     */
    public LocalDate receiveEndDate(){
        return getDate(end);
    }

    /**
     * Update end date.
     *
     * @param endDate the end date
     */
    public void updateEndDate(@NonNull LocalDate endDate) {
        updateEndDateTime(endDate.atTime(receiveEndTime()));
    }

    /**
     * Update end date time.
     *
     * @param localDateTime the local date time
     */
    public void updateEndDateTime(LocalDateTime localDateTime) {
        this.end = getMillis(localDateTime);
    }

    /**
     * Receive end time local time.
     *
     * @return the local time
     */
    public LocalTime receiveEndTime() {
        return getTime(end);
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public String getEndTime() {
        return CalendarUtils.TimeToText(receiveEndTime());
    }

    /**
     * Update end time.
     *
     * @param endTime the end time
     */
    public void updateEndTime(@NonNull LocalTime endTime) {
        updateEndDateTime(receiveEndDate().atTime(endTime));
    }

    /**
     * Gets end date time.
     *
     * @return the end date time
     */
    public String getEndDateTime() {
        return CalendarUtils.DateTimeToTextLocal(receiveEndDateTime());
    }

    /**
     * Receive end date time local date time.
     *
     * @return the local date time
     */
    public LocalDateTime receiveEndDateTime() {
        return getDateTime(end);
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Gets private id.
     *
     * @return the private id
     */
    public String getPrivateId() {
        return privateId;
    }

    /**
     * Sets private id.
     *
     * @param privateId the private id
     */
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

    /**
     * Gets range.
     *
     * @return the range
     */
    public long getRange() {
        return range;
    }

    /**
     * Sets range.
     *
     * @param range the range
     */
    public void setRange(long range) {
        this.range = range;
    }

    /**
     * To json string.
     *
     * @return the string
     */
    public String toJson(){
        return new Gson().toJson(this);
    }

    /**
     * Is all day boolean.
     *
     * @return the boolean
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Sets all day.
     *
     * @param allDay the all day
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    /**
     * Copy calendar event.
     *
     * @return the calendar event
     */
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

    /**
     * Gets group key.
     *
     * @return the group key
     */
    public String getGroupKey() {
        return groupKey;
    }

    /**
     * Sets group key.
     *
     * @param groupKey the group key
     */
    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }
}
