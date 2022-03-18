package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.map.Training;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CalendarEvent implements Serializable {

    private String chainId;
    private int timestamp;

    private long range;

    private int position;
    private String name;
    private String description;
    private String place;

    //ToDo change dates and times to long

    private long start;
    private String startDate;
    private String startTime;
    private String startDateTime;

    private long end;
    private String endDate;
    private String endTime;
    private String endDateTime;

    private int color;
    private boolean allDay;

    private String frequencyType;
    private String privateId;

    private int frequency;
    private int amount;

    private int day;
    private int dayOfWeekPosition;
    private List<Boolean> array_frequencyDayOfWeek;
    private int weekNumber;
    private int month;

    private boolean isLast;
    private List<Training> trainings;

    private String frequency_start;
    private String frequency_end;


    public static final String EVENT_CHAIN_ID = "chainId";
    public static final String EVENT_TIMESTAMP = "timestamp";
    public static final String EVENT_range = "range";
    public static final String EVENT_position = "position";
    public static final String EVENT_name = "name";
    public static final String EVENT_description = "description";
    public static final String EVENT_place = "place";
    public static final String EVENT_start = "start";
    public static final String EVENT_startDate = "startDate";
    public static final String EVENT_startTime = "startTime";
    public static final String EVENT_startDateTime = "startDateTime";
    public static final String EVENT_end = "end";
    public static final String EVENT_endDate = "endDate";
    public static final String EVENT_endTime = "endTime";
    public static final String EVENT_endDateTime = "endDateTime";
    public static final String EVENT_color = "color";
    public static final String EVENT_allDay = "allDay";
    public static final String EVENT_frequencyType = "frequencyType";
    public static final String EVENT_PRIVATE_ID = "privateId";
    public static final String EVENT_frequency = "frequency";
    public static final String EVENT_amount = "amount";
    public static final String EVENT_day = "day";
    public static final String EVENT_dayOfWeekPosition = "dayOfWeekPosition";
    public static final String EVENT_array_frequencyDayOfWeek = "array_frequencyDayOfWeek";
    public static final String EVENT_weekNumber = "weekNumber";
    public static final String EVENT_month = "month";
    public static final String EVENT_isLast = "isLast";
    public static final String EVENT_frequency_start = "frequency_start";
    public static final String EVENT_frequency_end = "frequency_end";


    public CalendarEvent(){

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

    public void addDefaultParams(int color, String name, String description, String place, int timestamp, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.timestamp = timestamp;

        this.startDate = UtilsCalendar.DateToTextOnline(startDate);

        this.startTime = UtilsCalendar.TimeToText(startTime);

        this.endDate = UtilsCalendar.DateToTextOnline(endDate);

        this.endTime = UtilsCalendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.start = getMillis(startDate.atTime(startTime));
        this.end = getMillis(endDate.atTime(endTime));

        this.range = end - start;

        this.color = color;

        /*Calendar start = Calendar.getInstance();
        start.set(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth(),
                startTime.getHour(), startTime.getMinute());

        this.time = start.getTimeInMillis();

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

        this.timestamp = timestamp;

        this.startDate = UtilsCalendar.DateToTextOnline(startDateTime.toLocalDate());
        this.startTime = UtilsCalendar.TimeToText(startDateTime.toLocalTime());
        this.startDateTime = UtilsCalendar.DateTimeToTextOnline(startDateTime);

        this.endDate = UtilsCalendar.DateToTextOnline(endDateTime.toLocalDate());
        this.endTime = UtilsCalendar.TimeToText(endDateTime.toLocalTime());
        this.endDateTime = UtilsCalendar.DateTimeToTextOnline(endDateTime);

        this.timestamp = startDateTime.getSecond();

        this.start = getMillis(startDateTime);
        this.end = getMillis(endDateTime);

        this.range = end - start;

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

    public long getStart() {
        return start;
    }

    public String getStartDateTime() {
        return UtilsCalendar.DateTimeToTextLocal((getDateTime(start)));
    }

    public LocalDateTime receiveStart() {
        return getDateTime(start);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void updateStart(LocalDateTime localDateTime) {
        this.start = getMillis(localDateTime);
    }

    public long getEnd() {
        return end;
    }

    public String getEndDateTime() {
        return UtilsCalendar.DateTimeToTextLocal((getDateTime(end)));
    }

    public LocalDateTime receiveEnd() {
        return getDateTime(end);
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void updateEnd(LocalDateTime localDateTime) {
        this.end = getMillis(localDateTime);
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
        return startDate;
        //return LocalDate.of(start_year, start_month, start_day);
    }

    public LocalDate receiveStart_date(){
        return UtilsCalendar.TextToDateForFirebase(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void updateStart_date(LocalDate start_date) {
        this.startDate = UtilsCalendar.DateToTextOnline(start_date);
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

    public String getEndDate() {
        return endDate;
    }

    public LocalDate receiveEnd_date(){
        return UtilsCalendar.TextToDateForFirebase(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void updateEnd_date(LocalDate end_date) {
        this.endDate = UtilsCalendar.DateToTextOnline(end_date);
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

    public LocalDateTime receiveStartDateTime(){
        return LocalDateTime.of(receiveStart_date(), receiveStart_time());
    }

    public LocalDateTime receiveEndDateTime(){
        return LocalDateTime.of(receiveEnd_date(), receiveEnd_time());
    }

    public String getStart_dateTime(){
        return UtilsCalendar.DateTimeToTextOnline(LocalDateTime.of(receiveStart_date(), receiveStart_time()));
    }

    public String getEnd_dateTime(){
        return UtilsCalendar.DateTimeToTextOnline(LocalDateTime.of(receiveEnd_date(), receiveEnd_time()));
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
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
                "\n" + this.getStartDate() + " | " + this.getStartTime() +
                "\n" + this.getEndDate() + " | " + this.getEndTime() +
                "\n"*//* + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n"*//*;
    }*/

    @NonNull
    @Override
    public String toString() {
        return "CalendarEvent{" +
                "\n chainId='" + chainId + '\'' +
                ", \n timestamp=" + timestamp +
                ", \n range=" + range +
                ", \n position=" + position +
                ", \n name='" + name + '\'' +
                ", \n description='" + description + '\'' +
                ", \n place='" + place + '\'' +
                ", \n start=" + new Date(start) +
                ", \n startDate='" + startDate + '\'' +
                ", \n startTime='" + startTime + '\'' +
                ", \n startDateTime='" + startDateTime + '\'' +
                ", \n end=" + new Date(end) +
                ", \n endDate='" + endDate + '\'' +
                ", \n endTime='" + endTime + '\'' +
                ", \n endDateTime='" + endDateTime + '\'' +
                ", \n color=" + color +
                ", \n frequencyType='" + frequencyType + '\'' +
                ", \n privateId='" + privateId + '\'' +
                ", \n frequency=" + frequency +
                ", \n amount=" + amount +
                ", \n day=" + day +
                ", \n dayOfWeekPosition=" + dayOfWeekPosition +
                ", \n array_frequencyDayOfWeek=" + array_frequencyDayOfWeek +
                ", \n weekNumber=" + weekNumber +
                ", \n month=" + month +
                ", \n isLast=" + isLast +
                ", \n frequency_start='" + frequency_start + '\'' +
                ", \n frequency_end='" + frequency_end + '\'' +
                '}';
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
