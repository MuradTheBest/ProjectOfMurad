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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The type Calendar event.
 */
public class CalendarEvent implements Serializable {

    /**
     * The constant KEY_EVENT.
     */
    public static final String KEY_EVENT = "event";
    /**
     * The constant KEY_EVENT_CHAIN_ID.
     */
    public static final String KEY_EVENT_CHAIN_ID = "chainId";
    /**
     * The constant KEY_EVENT_START.
     */
    public static final String KEY_EVENT_START = "start";
    /**
     * The constant KEY_EVENT_END.
     */
    public static final String KEY_EVENT_END = "end";
    /**
     * The constant KEY_EVENT_PRIVATE_ID.
     */
    public static final String KEY_EVENT_PRIVATE_ID = "privateId";

    private String chainId;
    private String privateId;
    private String groupKey;

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
    private List<Boolean> daysOfWeek;
    private int weekNumber;
    private int month;
    private boolean isLast;
    private long chainStart;
    private long chainEnd;
    private FrequencyType frequencyType;

    /**
     * Instantiates a new Calendar event.
     */
    public CalendarEvent() {
        this.frequency = 1;
        this.frequencyType = FrequencyType.DAY_BY_END;
    }

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
     * Gets frequency type.
     *
     * @return the frequency type
     */
    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    /**
     * Sets frequency type.
     *
     * @param frequencyType the frequency type
     */
    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
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
     * Gets chain id.
     *
     * @return the chain id
     */
    public String getChainId() {
        return chainId;
    }

    /**
     * Sets chain id.
     *
     * @param chainId the chain id
     */
    public void setChainId(String chainId) {
        this.chainId = chainId;
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
     * Gets frequency.
     *
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets chain start.
     *
     * @return the chain start
     */
    public long getChainStart() {
        return chainStart;
    }

    /**
     * Set chain start.
     *
     * @param chainStart the chain start
     */
    public void setChainStart(long chainStart){
        this.chainStart = chainStart;
    }

    /**
     * Gets chain start date.
     *
     * @return the chain start date
     */
    public String getChainStartDate() {
        return CalendarUtils.DateToTextLocal(receiveChainStartDate());
    }

    /**
     * Receive chain start date local date.
     *
     * @return the local date
     */
    public LocalDate receiveChainStartDate() {
        return getDate(chainStart);
    }

    /**
     * Update chain start date.
     *
     * @param startDate the start date
     */
    public void updateChainStartDate(@NonNull LocalDate startDate) {
        this.chainStart = getMillis(startDate.atTime(receiveStartTime()));
    }

    /**
     * Gets chain end.
     *
     * @return the chain end
     */
    public long getChainEnd() {
        return chainEnd;
    }

    /**
     * Set chain end.
     *
     * @param chainEnd the chain end
     */
    public void setChainEnd(long chainEnd){
        this.chainEnd = chainEnd;
    }

    /**
     * Gets chain end date.
     *
     * @return the chain end date
     */
    public String getChainEndDate() {
        return CalendarUtils.DateToTextLocal(receiveChainEndDate());
    }

    /**
     * Receive chain end date local date.
     *
     * @return the local date
     */
    public LocalDate receiveChainEndDate(){
        return getDate(chainEnd);
    }

    /**
     * Update chain end date.
     *
     * @param endDate the end date
     */
    public void updateChainEndDate(@NonNull LocalDate endDate) {
        this.chainEnd = getMillis(endDate.atTime(receiveEndTime()));
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Gets day of week position.
     *
     * @return the day of week position
     */
    public int getDayOfWeekPosition() {
        return dayOfWeekPosition;
    }

    /**
     * Sets day of week position.
     *
     * @param dayOfWeekPosition the day of week position
     */
    public void setDayOfWeekPosition(int dayOfWeekPosition) {
        this.dayOfWeekPosition = dayOfWeekPosition;
    }

    /**
     * Gets days of week.
     *
     * @return the days of week
     */
    public List<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * Sets days of week.
     *
     * @param daysOfWeek the days of week
     */
    public void setDaysOfWeek(List<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    /**
     * Gets week number.
     *
     * @return the week number
     */
    public int getWeekNumber() {
        return weekNumber;
    }

    /**
     * Sets week number.
     *
     * @param weekNumber the week number
     */
    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    /**
     * Gets month.
     *
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Sets month.
     *
     * @param month the month
     */
    public void setMonth(int month) {
        this.month = month;
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

    /**
     * Is single boolean.
     *
     * @return the boolean
     */
    public boolean isSingle(){
        return privateId.equals(chainId);
    }

    /**
     * Is last boolean.
     *
     * @return the boolean
     */
    public boolean isLast() {
        return isLast;
    }

    /**
     * Sets last.
     *
     * @param last the last
     */
    public void setLast(boolean last) {
        isLast = last;
    }

    /**
     * Clear frequency data.
     */
    public void clearFrequencyData(){
        this.frequencyType = FrequencyType.DAY_BY_END;
        this.frequency = 1;

        this.day = 0;
        this.dayOfWeekPosition = 0;
        this.daysOfWeek = new ArrayList<>();
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
                ", \n daysOfWeek = " + daysOfWeek +
                ", \n weekNumber = " + weekNumber +
                ", \n month = " + month +
                ", \n isLast = " + isLast +
                ", \n chainStart = '" + chainStart + '\'' +
                ", \n chainEnd = '" + chainEnd + '\'' +
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
        newEvent.setDaysOfWeek(getDaysOfWeek());
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

    /**
     * Is row event boolean.
     *
     * @return the boolean
     */
    public boolean isRowEvent(){
        return frequencyType.equals(FrequencyType.DAY_BY_END) && frequency == 1;
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

    /**
     * The enum Frequency type.
     */
    public enum FrequencyType {
        /**
         * Day by amount frequency type.
         */
        DAY_BY_AMOUNT,
        /**
         * Day of week by amount frequency type.
         */
        DAY_OF_WEEK_BY_AMOUNT,
        /**
         * Day and month by amount frequency type.
         */
        DAY_AND_MONTH_BY_AMOUNT,
        /**
         * Day of week and month by amount frequency type.
         */
        DAY_OF_WEEK_AND_MONTH_BY_AMOUNT,
        /**
         * Day and year by amount frequency type.
         */
        DAY_AND_YEAR_BY_AMOUNT,
        /**
         * Day of week and year by amount frequency type.
         */
        DAY_OF_WEEK_AND_YEAR_BY_AMOUNT,
        /**
         * Day by end frequency type.
         */
        DAY_BY_END,
        /**
         * Day of week by end frequency type.
         */
        DAY_OF_WEEK_BY_END,
        /**
         * Day and month by end frequency type.
         */
        DAY_AND_MONTH_BY_END,
        /**
         * Day of week and month by end frequency type.
         */
        DAY_OF_WEEK_AND_MONTH_BY_END,
        /**
         * Day and year by end frequency type.
         */
        DAY_AND_YEAR_BY_END,
        /**
         * Day of week and year by end frequency type.
         */
        DAY_OF_WEEK_AND_YEAR_BY_END;

        @NonNull
        @Override
        public String toString() {
            return name().toUpperCase(Locale.ROOT).replaceAll("_", " ");
        }
    }
}
