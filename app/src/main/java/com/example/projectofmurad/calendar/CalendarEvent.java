package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.helpers.CalendarUtils;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarEvent implements Serializable, Cloneable {

    private String chainId;
    private String privateId;

    private long range;

    private String name;
    private String description;
    private String place;

    private long start;
    private long end;

    private int color;
    private boolean allDay;

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

    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_CHAIN_ID = "chainId";
    public static final String KEY_EVENT_TIMESTAMP = "timestamp";
    public static final String KEY_EVENT_RANGE = "range";
    public static final String KEY_EVENT_NAME = "name";
    public static final String KEY_EVENT_DESCRIPTION = "description";
    public static final String KEY_EVENT_PLACE = "place";
    public static final String KEY_EVENT_START = "start";
    public static final String KEY_EVENT_END = "end";
    public static final String KEY_EVENT_COLOR = "color";
    public static final String KEY_EVENT_ALL_DAY = "allDay";
    public static final String KEY_EVENT_FREQUENCY_TYPE = "frequencyType";
    public static final String KEY_EVENT_PRIVATE_ID = "privateId";
    public static final String KEY_EVENT_FREQUENCY = "frequency";
    public static final String KEY_EVENT_AMOUNT = "amount";
    public static final String KEY_EVENT_DAY = "day";
    public static final String KEY_EVENT_DAY_OF_WEEK_POSITION = "dayOfWeekPosition";
    public static final String KEY_EVENT_ARRAY_FREQUENCY_DAY_OF_WEEK = "array_frequencyDayOfWeek";
    public static final String KEY_EVENT_WEEK_NUMBER = "weekNumber";
    public static final String KEY_EVENT_MONTH = "month";
    public static final String KEY_EVENT_FREQUENCY_START = "frequency_start";
    public static final String KEY_EVENT_FREQUENCY_END = "frequency_end";

    public enum FrequencyType {
        DAY_BY_AMOUNT,
        DAY_OF_WEEK_BY_AMOUNT,
        DAY_AND_MONTH_BY_AMOUNT,
        DAY_OF_WEEK_AND_MONTH_BY_AMOUNT,
        DAY_AND_YEAR_BY_AMOUNT,
        DAY_OF_WEEK_AND_YEAR_BY_AMOUNT,
        DAY_BY_END,
        DAY_OF_WEEK_BY_END,
        DAY_AND_MONTH_BY_END,
        DAY_OF_WEEK_AND_MONTH_BY_END,
        DAY_AND_YEAR_BY_END,
        DAY_OF_WEEK_AND_YEAR_BY_END;

        @NonNull
        @Override
        public String toString() {
            return name().toUpperCase(Locale.ROOT);
        }
    }

    private FrequencyType frequencyType;

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(
            FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public CalendarEvent() {
        this.frequency = 1;
        this.frequencyType = FrequencyType.DAY_BY_END;
    }

    public void addDefaultParams(int color, String name, String description, String place,
                                 @NonNull LocalDate startDate, @NonNull LocalTime startTime,
                                 @NonNull LocalDate endDate, LocalTime endTime) {
        this.name = name;
        this.description = description;
        this.place = place;

        this.start = getMillis(startDate.atTime(startTime));
        this.end = getMillis(endDate.atTime(endTime));

        this.range = end - start;
        this.color = color;
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

    public static long getMillis(@NonNull LocalDate localDate, LocalTime localTime) {
        return localDate.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime getDateTime(long millis) {
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

    public String getStartDate() {
        return CalendarUtils.DateToTextLocal(receiveStartDate());
    }

    public LocalDate receiveStartDate(){
        return getDate(start);
    }

    public void updateStartDate(@NonNull LocalDate start_date) {
        updateStartDateTime(start_date.atTime(receiveStartTime()));
    }

    public String getStartTime() {
        return CalendarUtils.TimeToText(receiveStartTime());
    }

    public LocalTime receiveStartTime(){
        return getTime(start);
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

    public void updateStartDateTime(LocalDateTime localDateTime) {
        this.start = getMillis(localDateTime);
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

    public String getEndTime() {
        return CalendarUtils.TimeToText(receiveEndTime());
    }

    public LocalTime receiveEndTime() {
        return getTime(end);
    }

    public void updateEndTime(@NonNull LocalTime endTime) {
        updateEndDateTime(receiveEndDate().atTime(endTime));
    }

    public String getEndDateTime() {
        return CalendarUtils.DateTimeToTextLocal(receiveEndDateTime());
    }

    public void updateEndDateTime(LocalDateTime localDateTime) {
        this.end = getMillis(localDateTime);
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
        return CalendarUtils.TextToDateForFirebase(frequency_start);
    }

    public void setFrequency_start(String frequency_start) {
        this.frequency_start = frequency_start;
    }

    public void updateFrequency_start(LocalDate frequency_start) {
        this.frequency_start = CalendarUtils.DateToTextOnline(frequency_start);
        Log.d("murad", "Frequency_start date updated " + CalendarUtils.DateToTextOnline(frequency_start));
    }

    public String getFrequency_end() {
        return frequency_end;
    }

    public LocalDate receiveFrequency_end(){
        Log.d("murad", "Frequency end date received " + frequency_end);
        return CalendarUtils.TextToDateForFirebase(frequency_end);
    }

    public void setFrequency_end(String frequency_end) {
        this.frequency_end = frequency_end;
    }

    public void updateFrequency_end(LocalDate frequency_end) {
        this.frequency_end = CalendarUtils.DateToTextOnline(frequency_end);
        Log.d("murad", "Absolute end date updated " + CalendarUtils.DateToTextOnline(frequency_end));
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    public boolean isSingle(){
        return privateId.equals(chainId);
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public void clearFrequencyData(){
        this.frequencyType = FrequencyType.DAY_BY_END;
        this.frequency = 1;

        this.day = 0;
        this.dayOfWeekPosition = 0;
        this.array_frequencyDayOfWeek = null;
        this.weekNumber = 0;
        this.month = 0;
        this.frequency_start = "";
        this.frequency_end = "";
    }

    @NonNull
    @Override
    public String toString() {
        return "CalendarEvent{" +
                "\n chainId = '" + chainId + '\'' +
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
                "\n}";
    }


    public String print(){
        return "{" +
                ", \n name = '" + name + '\'' +
                ", \n description = '" + description + '\'' +
                ", \n place = '" + place + '\'' +
                ", \n start = " + start +
                ", \n start = " + new Date(start) +
                ", \n end = " + end +
                ", \n end = " + new Date(end) +
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

    public static CalendarEvent fromJson(String eventJson){
        return new Gson().fromJson(eventJson, CalendarEvent.class);
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
        newEvent.setChainId(getChainId());
        newEvent.setName(getName());
        newEvent.setDescription(getDescription());
        newEvent.setPlace(getPlace());
        newEvent.setColor(getColor());
        newEvent.setFrequencyType(getFrequencyType());
        newEvent.setFrequency(getFrequency());
        newEvent.setAmount(getAmount());
        newEvent.setDay(getDay());
        newEvent.setDayOfWeekPosition(getDayOfWeekPosition());
        newEvent.setArray_frequencyDayOfWeek(getArray_frequencyDayOfWeek());
        newEvent.setWeekNumber(getWeekNumber());
        newEvent.setMonth(getMonth());
        newEvent.setFrequency_start(getFrequency_start());
        newEvent.setFrequency_end(getFrequency_end());
        newEvent.setPrivateId(getPrivateId());
        newEvent.setLast(isLast());
        newEvent.setRange(getRange());
        newEvent.setAllDay(isAllDay());

        return newEvent;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isRowEvent(){
        return frequencyType.equals(FrequencyType.DAY_BY_END) && frequency == 1;
    }
}
