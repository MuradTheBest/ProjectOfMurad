package com.example.projectofmurad.calendar;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarEventWithText implements Serializable {

    private String event_id;
    private int timestamp;

    private String name;
    private String description;
    private String place;

    private String start_date;
    private String start_time;

    private String end_date;
    private String end_time;

    private int frequency;

    private String frequencyType;


    public CalendarEventWithText(){

        this.event_id = "";

        this.name = "";
        this.description = "";
        this.place = "";

        this.start_date = "";
        this.start_time = "";

        this.end_date = "";
        this.end_time = "";

        this.timestamp = 0;

        this.frequency = 0;
        this.frequencyType = "";
    }

    public CalendarEventWithText(String name, String description, String place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils_Calendar.DateToTextOnline(startDate);

        this.start_time = Utils_Calendar.TimeToText(startTime);

        this.end_date = Utils_Calendar.DateToTextOnline(endDate);

        this.end_time = Utils_Calendar.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;
    }

    public CalendarEventWithText(String name, String description, String place, String start_date, String start_time, String end_date, String end_time) {

        this.timestamp = Utils_Calendar.TextToTime(start_time).toSecondOfDay();

        this.event_id = start_date + "-" + this.timestamp;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = start_date;

        this.start_time = start_time;

        this.end_date = end_date;

        this.end_time = end_time;

    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
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

    public String getStart_date() {
        return start_date;
        //return LocalDate.of(start_year, start_month, start_day);
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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

    public String toString(){

        return "------------------------------------------------------------------------------------------------------------------------------------ \n" +
                "\n" + this.getName() + " | " + this.getPlace() + " | " + this.getDescription() +
                "\n" + this.getStart_date() + " | " + this.getStart_time() +
                "\n" + this.getEnd_date() + " | " + this.getEnd_time() +
                "\n" + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n";
    }

}
