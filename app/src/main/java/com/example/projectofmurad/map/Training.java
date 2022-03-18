package com.example.projectofmurad.map;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.calendar.UtilsCalendar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class Training {

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
    private double avgSpeed;
    private HashMap<String, Double> speeds;
    private double maxSpeed;
    private double totalDistance;

    private long end;
    private String endDate;
    private String endTime;
    private String endDateTime;

    private int color;

    private String privateId;

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

        this.endDate = UtilsCalendar.DateToTextOnline(getDate(end));
        this.endTime = UtilsCalendar.TimeToText(getTime(end));
        this.endDateTime = UtilsCalendar.DateTimeToTextOnline(getDateTime(end));
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
                ", speeds=" + speeds.toString() +
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
}
