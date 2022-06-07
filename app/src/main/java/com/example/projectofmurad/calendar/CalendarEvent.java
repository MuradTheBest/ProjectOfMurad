package com.example.projectofmurad.calendar;

import androidx.annotation.NonNull;

import com.example.projectofmurad.utils.CalendarUtils;
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

public class CalendarEvent implements Serializable {

    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_CHAIN_ID = "chainId";
    public static final String KEY_EVENT_START = "start";
    public static final String KEY_EVENT_PRIVATE_ID = "privateId";

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
    private long chainStart;
    private long chainEnd;
    private FrequencyType frequencyType;

    public CalendarEvent() {
        this.frequency = 1;
        this.frequencyType = FrequencyType.DAY_BY_END;
    }

    public CalendarEvent(String name, String description,
                         String place, LocalDateTime startDateTime, LocalDateTime endDateTime, int color, boolean allDay) {

        this.frequency = 1;
        this.frequencyType = FrequencyType.DAY_BY_END;

        this.name = name;
        this.description = description;
        this.place = place;
        this.start = getMillis(startDateTime);
        this.end = getMillis(endDateTime);
        this.color = color;
        this.allDay = allDay;
    }

    public static CalendarEvent fromJson(String eventJson){
        return new Gson().fromJson(eventJson, CalendarEvent.class);
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(
            FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getChainStart() {
        return chainStart;
    }

    public void setChainStart(long chainStart){
        this.chainStart = chainStart;
    }

    public String getChainStartDate() {
        return CalendarUtils.DateToTextLocal(receiveChainStartDate());
    }

    public LocalDate receiveChainStartDate() {
        return getDate(chainStart);
    }

    public void updateChainStartDate(@NonNull LocalDate startDate) {
        this.chainStart = getMillis(startDate.atTime(receiveStartTime()));
    }

    public long getChainEnd() {
        return chainEnd;
    }

    public void setChainEnd(long chainEnd){
        this.chainEnd = chainEnd;
    }

    public String getChainEndDate() {
        return CalendarUtils.DateToTextLocal(receiveChainEndDate());
    }

    public LocalDate receiveChainEndDate(){
        return getDate(chainEnd);
    }

    public void updateChainEndDate(@NonNull LocalDate endDate) {
        this.chainEnd = getMillis(endDate.atTime(receiveEndTime()));
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
        this.chainStart = start;
        this.chainEnd = end;
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
                ", \n chainStart = '" + chainStart + '\'' +
                ", \n chainEnd = '" + chainEnd + '\'' +
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
        newEvent.updateChainStartDate(receiveChainStartDate());
        newEvent.updateChainEndDate(receiveEndDate());
        newEvent.setChainEnd(getChainEnd());
        newEvent.setPrivateId(getPrivateId());
        newEvent.setLast(isLast());
        newEvent.setRange(getRange());
        newEvent.setAllDay(isAllDay());

        return newEvent;
    }

    public boolean isRowEvent(){
        return frequencyType.equals(FrequencyType.DAY_BY_END) && frequency == 1;
    }

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
}
