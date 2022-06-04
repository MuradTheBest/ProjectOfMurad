package com.example.projectofmurad.training;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.helpers.utils.CalendarUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.tracking.Location;
import com.example.projectofmurad.tracking.SpeedAndLocation;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Training implements Serializable {

    private String privateId;
    private String eventPrivateId;
    private String UID;
    private String groupKey;

    private int day;
    private int month;
    private int year;

    private long range;

    private String name;
    private String description;
    private String place;

    //ToDo change dates and times to long

    private long start;

    private long time;
    private long totalTime;

    private String duration;
    private String totalDuration;

    private double avgSpeed;
    private double maxSpeed;

    private HashMap<String, SpeedAndLocation> speeds;

    private String avgPace;
    private String maxPace;

    private double totalDistance;

    private long end;

    private int color;

    private List<Location> locations;

    private String picture;

    public final static String KEY_TRAINING = "training";

    public final static String KEY_TRAINING_PRIVATE_ID = "privateId";
    public final static String KEY_TRAINING_EVENT_PRIVATE_ID = "eventPrivateId";
    public final static String KEY_TRAINING_UID = "uid";
    public final static String KEY_TRAINING_DAY = "day";
    public final static String KEY_TRAINING_MONTH = "month";
    public final static String KEY_TRAINING_YEAR = "year";
    public final static String KEY_TRAINING_RANGE = "range";
    public final static String KEY_TRAINING_NAME = "name";
    public final static String KEY_TRAINING_description = "description";
    public final static String KEY_TRAINING_PLACE = "place";
    public final static String KEY_TRAINING_START = "start";
    public final static String KEY_TRAINING_START_DATE = "startDate";
    public final static String KEY_TRAINING_START_TIME = "startTime";
    public final static String KEY_TRAINING_START_DATE_TIME = "startDateTime";
    public final static String KEY_TRAINING_TIME = "time";
    public final static String KEY_TRAINING_TOTAL_TIME = "totalTime";
    public final static String KEY_TRAINING_AVG_SPEED = "avgSpeed";
    public final static String KEY_TRAINING_SPEEDS = "speeds";
    public final static String KEY_TRAINING_MAX_SPEED = "maxSpeed";
    public final static String KEY_TRAINING_TOTAL_DISTANCE = "totalDistance";
    public final static String KEY_TRAINING_END = "end";
    public final static String KEY_TRAINING_END_DATE = "endDate";
    public final static String KEY_TRAINING_END_TIME = "endTime";
    public final static String KEY_TRAINING_END_DATE_TIME = "endDateTime";
    public final static String KEY_TRAINING_COLOR = "color";

    public Training(){}

    public Training(String privateId, LocalDateTime start, LocalDateTime end, long time, long totalTime, double avgSpeed, double maxSpeed, HashMap<String, SpeedAndLocation> speeds, List<Location> locations, double totalDistance) {
        this.start = getMillis(start);
        this.end = getMillis(end);

        this.privateId = privateId;

        this.time = time;
        this.totalTime = totalTime;

        this.avgSpeed = avgSpeed;
        this.speeds = speeds;
        this.maxSpeed = maxSpeed;

        this.avgPace = Utils.convertSpeedToPace(avgSpeed);
        this.maxPace = Utils.convertSpeedToPace(maxSpeed);

        this.totalDistance = totalDistance;

        this.duration = getDurationFromTime(time);
        this.totalDuration = getDurationFromTime(totalTime);

        this.locations = locations;

        this.day = start.getDayOfMonth();
        this.month = start.getMonthValue();
        this.year = start.getYear();
    }

    @NonNull
    public static String getDurationFromTime(long seconds){

        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
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

    /*public static LocalTime getTime(long millis){
        return LocalTime.from(Instant.ofEpochMilli(millis));
    }*/

    public long getRange() {
        return range;
    }

    public void setRange(long range) {
        this.range = range;
    }

    public String getName() {
        return name == null ? "" : name;
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

    public long getTime() {
        return time;
    }

    public String getTimeText() {
        return getDurationFromTime(time);
    }

    public void setTime(long time) {
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Training{" +
                "range=" + range +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", start=" + start +
                ", timeData=" + time +
                ", totalTimeData=" + totalTime +
                ", avgSpeedData=" + avgSpeed +
                ", speedsData=" + (speeds == null ? "" : speeds.toString()) +
                ", maxSpeedData=" + maxSpeed +
                ", totalDistanceData=" + totalDistance +
                ", end=" + end +
                ", color=" + color +
                ", privateId='" + privateId + '\'' +
                '}';
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public HashMap<String, SpeedAndLocation> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(HashMap<String, SpeedAndLocation> speeds) {
        this.speeds = speeds;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEventPrivateId() {
        return eventPrivateId;
    }

    public void setEventPrivateId(String eventPrivateId) {
        this.eventPrivateId = eventPrivateId;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(String avgPace) {
        this.avgPace = avgPace;
    }

    public String getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(String maxPace) {
        this.maxPace = maxPace;
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
        return CalendarUtils.DateTimeToTextOnline(receiveStartDateTime());
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

    public String getDate() {
        return getStartDate().equals(getEndDate()) ? getStartDate() : getStartDate() + " -\n" + getEndDate();
    }

    public String getDateTime() {
        return getStartDate().equals(getEndDate()) ?
                (getStartDate() + "\n" + getStartTime() + " - " + getEndTime()) :
                (getStartDate() + " -\n" + getEndDate() + "\n" + getStartTime() + " - " + getEndTime());
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<Location> getLocations() {
        return (ArrayList<Location>) locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }
}
