package com.example.projectofmurad.tracking;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.projectofmurad.Utils;
import com.example.projectofmurad.calendar.UtilsCalendar;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@Entity(tableName = "trainings")
public class Training implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    private String privateId;

    private String eventPrivateId;
    private String userID;

    private int day;
    private int month;
    private int year;

    private long range;

    private String name;
    private String description;
    private String place;

    //ToDo change dates and times to long

    private long start;
    private String startDate;
    private String startTime;
    private String startDateTime;

    private long time;
    private long totalTime;

    private String duration;
    private String totalDuration;

    private double avgSpeed;
    @Ignore
    private HashMap<String, Double> speeds;
    private double maxSpeed;

    private String avgPace;
/*    @Ignore
    private HashMap<String, Double> paces;*/
    private String maxPace;

    private double totalDistance;

    private long end;
    private String endDate;
    private String endTime;
    private String endDateTime;

    private int color;

    @Ignore
    public final static String KEY_TRAINING = "key_training";

    @Ignore
    public final static String KEY_TRAINING_uid = "training_uid";
    @Ignore
    public final static String KEY_TRAINING_privateId = "training_privateId";
    @Ignore
    public final static String KEY_TRAINING_eventPrivateId = "training_eventPrivateId";
    @Ignore
    public final static String KEY_TRAINING_userID = "training_userID";
    @Ignore
    public final static String KEY_TRAINING_day = "training_day";
    @Ignore
    public final static String KEY_TRAINING_month = "training_month";
    @Ignore
    public final static String KEY_TRAINING_year = "training_year";
    @Ignore
    public final static String KEY_TRAINING_range = "training_range";
    @Ignore
    public final static String KEY_TRAINING_name = "training_name";
    @Ignore
    public final static String KEY_TRAINING_description = "training_description";
    @Ignore
    public final static String KEY_TRAINING_place = "training_place";
    @Ignore
    public final static String KEY_TRAINING_start = "training_start";
    @Ignore
    public final static String KEY_TRAINING_startDate = "training_startDate";
    @Ignore
    public final static String KEY_TRAINING_startTime = "training_startTime";
    @Ignore
    public final static String KEY_TRAINING_startDateTime = "training_startDateTime";
    @Ignore
    public final static String KEY_TRAINING_time = "training_time";
    @Ignore
    public final static String KEY_TRAINING_totalTime = "training_totalTime";
    @Ignore
    public final static String KEY_TRAINING_avgSpeed = "training_avgSpeed";
    @Ignore
    public final static String KEY_TRAINING_speeds = "training_speeds";
    @Ignore
    public final static String KEY_TRAINING_maxSpeed = "training_maxSpeed";
    @Ignore
    public final static String KEY_TRAINING_totalDistance = "training_totalDistance";
    @Ignore
    public final static String KEY_TRAINING_end = "training_end";
    @Ignore
    public final static String KEY_TRAINING_endDate = "training_endDate";
    @Ignore
    public final static String KEY_TRAINING_endTime = "training_endTime";
    @Ignore
    public final static String KEY_TRAINING_endDateTime = "training_endDateTime";
    @Ignore
    public final static String KEY_TRAINING_color = "training_color";


    public Training(){}

    public Training(String privateId, String name, long start, long end) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.privateId = privateId;

        this.startDate = UtilsCalendar.DateToTextOnline(getDate(start));
        this.startTime = UtilsCalendar.TimeToText(getTime(start));
        this.startDateTime = UtilsCalendar.DateTimeToTextOnline(getDateTime(start));

        this.endDate = UtilsCalendar.DateToTextOnline(getDate(end));
        this.endTime = UtilsCalendar.TimeToText(getTime(end));
        this.endDateTime = UtilsCalendar.DateTimeToTextOnline(getDateTime(end));
    }

    public Training(String privateId, long start, long end, long time, long totalTime, double avgSpeed, double maxSpeed, HashMap<String, Double> speeds, double totalDistance) {
        this.start = start;
        this.end = end;
        this.privateId = privateId;

        this.time = time;
        this.totalTime = totalTime;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.totalDistance = totalDistance;

        this.speeds = speeds;

        this.startDate = UtilsCalendar.DateToTextOnline(getDate(start));
        this.startTime = UtilsCalendar.TimeToText(getTime(start));
        this.startDateTime = UtilsCalendar.DateTimeToTextOnline(getDateTime(start));

        this.day = getDate(start).getDayOfMonth();
        this.month = getDate(start).getMonthValue();
        this.year = getDate(start).getYear();

        this.endDate = UtilsCalendar.DateToTextOnline(getDate(end));
        this.endTime = UtilsCalendar.TimeToText(getTime(end));
        this.endDateTime = UtilsCalendar.DateTimeToTextOnline(getDateTime(end));
    }

    public Training(String privateId, LocalDateTime start, LocalDateTime end, long time, long totalTime, double avgSpeed, double maxSpeed, HashMap<String, Double> speeds, double totalDistance) {
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


        this.startDate = UtilsCalendar.DateToTextOnline(start.toLocalDate());
        this.startTime = UtilsCalendar.TimeToText(start.toLocalTime());
        this.startDateTime = UtilsCalendar.DateTimeToTextOnline(start);

        this.day = start.getDayOfMonth();
        this.month = start.getMonthValue();
        this.year = start.getYear();

        this.endDate = UtilsCalendar.DateToTextOnline(end.toLocalDate());
        this.endTime = UtilsCalendar.TimeToText(end.toLocalTime());
        this.endDateTime = UtilsCalendar.DateTimeToTextOnline(end);
    }

    @NonNull
    public static String getDurationFromTime(long seconds){

        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);

        // Format the time into hours, minute and time.
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
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
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
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", time=" + time +
                ", totalTime=" + totalTime +
                ", avgSpeed=" + avgSpeed +
                ", speeds=" + (speeds == null ? "" : speeds.toString()) +
                ", maxSpeed=" + maxSpeed +
                ", totalDistance=" + totalDistance +
                ", end=" + end +
                ", endDate='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
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

    public HashMap<String, Double> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(HashMap<String, Double> speeds) {
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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
}
