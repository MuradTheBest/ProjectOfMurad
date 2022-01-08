package com.example.projectofmurad.calendar;

import com.example.projectofmurad.Utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalendarEvent implements Serializable {
    private static int event_id = 0;

    private String name;
    private String description;
    private String place;

    private LocalDate start_date;

    private int start_day;
    private int start_month;
    private int start_year;

    private LocalTime start_time;

    private int start_hour;
    private int start_min;

    private LocalDate end_date;

    private int end_day;
    private int end_month;
    private int end_year;

    private LocalTime end_time;

    private int end_hour;
    private int end_min;

    public CalendarEvent(){
        event_id++;
    }

    public CalendarEvent(String name, String description, String place) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;
    }

    public CalendarEvent(String name, String description, String place, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = startDateTime.toLocalDate();

        this.start_day = startDateTime.getDayOfMonth();
        this.start_month = startDateTime.getMonthValue();
        this.start_year = startDateTime.getYear();

        this.start_time = startDateTime.toLocalTime();

        this.start_hour = startDateTime.getHour();
        this.start_min = startDateTime.getMinute();

        this.end_date = endDateTime.toLocalDate();

        this.end_day = endDateTime.getDayOfMonth();
        this.end_month = endDateTime.getMonthValue();
        this.end_year =  endDateTime.getYear();

        this.end_time = endDateTime.toLocalTime();

        this.end_hour = endDateTime.getHour();
        this.end_min = endDateTime.getMinute();
    }

    public CalendarEvent(String name, String description, String place, LocalDate start_date, LocalTime start_time, LocalDate end_date, LocalTime end_time) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = start_date;

        this.start_day = start_date.getDayOfMonth();
        this.start_month = start_date.getMonthValue();
        this.start_year = start_date.getYear();

        this.start_time = start_time;

        this.start_hour = start_time.getHour();
        this.start_min = start_time.getMinute();

        this.end_date = end_date;

        this.end_day = end_date.getDayOfMonth();
        this.end_month = end_date.getMonthValue();
        this.end_year =  end_date.getYear();

        this.end_time = end_time;

        this.end_hour = end_time.getHour();
        this.end_min = end_time.getMinute();
    }

    public CalendarEvent(String name, String description, String place, int start_day, int start_month, int start_year, int start_hour, int start_min, int end_day, int end_month, int end_year, int end_hour, int end_min) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = LocalDate.of(start_year, start_month, start_day);

        this.start_day = start_day;
        this.start_month = start_month;
        this.start_year = start_year;

        this.start_time = LocalTime.of(start_hour, start_min);

        this.start_hour = start_hour;
        this.start_min = start_min;

        this.end_date = LocalDate.of(end_year, end_month, end_day);

        this.end_day = end_day;
        this.end_month = end_month;
        this.end_year = end_year;

        this.end_time = LocalTime.of(end_hour, end_min);

        this.end_hour = end_hour;
        this.end_min = end_min;
    }

    public CalendarEvent(String name, String description, String place, LocalDate date, int start_hour, int start_min, int end_hour, int end_min) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = this.end_date = date;

        this.start_day = this.end_day = date.getDayOfMonth();
        this.start_month = this.end_month = date.getMonthValue();
        this.start_year = this.end_year = date.getYear();

        this.start_time = LocalTime.of(start_hour, start_min);

        this.start_hour = start_hour;
        this.start_min = start_min;

        this.end_time = LocalTime.of(end_hour, end_min);

        this.end_hour = end_hour;
        this.end_min = end_min;
    }

    public CalendarEvent(String name, String description, String place, int day, int month, int year, int start_hour, int start_min, int end_hour, int end_min) {
        event_id++;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = this.end_date = LocalDate.of(year, month, day);

        this.start_day = this.end_day = day;
        this.start_month = this.end_month = month;
        this.start_year = this.end_year = year;

        this.start_time = LocalTime.of(start_hour, start_min);

        this.start_hour = start_hour;
        this.start_min = start_min;

        this.end_time = LocalTime.of(end_hour, end_min);

        this.end_hour = end_hour;
        this.end_min = end_min;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        CalendarEvent.event_id = event_id;
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

    public LocalDate getStart_date() {
        return start_date;
        //return LocalDate.of(start_year, start_month, start_day);
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;

        this.start_day = start_date.getDayOfMonth();
        this.start_month = start_date.getMonth().getValue();
        this.start_year = start_date.getYear();
    }

    public int getStart_day() {
        return start_day;
    }

    public void setStart_day(int start_day) {
        this.start_day = start_day;
    }

    public int getStart_month() {
        return start_month;
    }

    public void setStart_month(int start_month) {
        this.start_month = start_month;
    }

    public int getStart_year() {
        return start_year;
    }

    public void setStart_year(int start_year) {
        this.start_year = start_year;
    }

    public LocalTime getStart_time() {
        return start_time;
        //return LocalTime.of(start_hour, start_min);
    }

    public void setStart_time(LocalTime start_time) {
        this.start_time = start_time;

        this.start_hour = start_time.getHour();
        this.start_min = start_time.getMinute();
    }

    public int getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public int getStart_min() {
        return start_min;
    }

    public void setStart_min(int start_min) {
        this.start_min = start_min;
    }

    public LocalDate getEnd_date() {
        return end_date;
        //return LocalDate.of(end_year, end_month, end_day);
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;

        this.end_day = end_date.getDayOfMonth();
        this.end_month = end_date.getMonth().getValue();
        this.end_year = end_date.getYear();
    }

    public int getEnd_day() {
        return end_day;
    }

    public void setEnd_day(int end_day) {
        this.end_day = end_day;
    }

    public int getEnd_month() {
        return end_month;
    }

    public void setEnd_month(int end_month) {
        this.end_month = end_month;
    }

    public int getEnd_year() {
        return end_year;
    }

    public void setEnd_year(int end_year) {
        this.end_year = end_year;
    }

    public LocalTime getEnd_time() {
        return end_time;
        //return LocalTime.of(end_hour, end_min);
    }

    public void setEnd_time(LocalTime end_time) {
        this.end_time = end_time;

        this.end_hour = end_time.getHour();
        this.end_min = end_time.getMinute();
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public int getEnd_min() {
        return end_min;
    }

    public void setEnd_min(int end_min) {
        this.end_min = end_min;
    }

    public String toString(){
        String str = "------------------------------------------------------------------------------------------------------------------------------------ \n" +
                "\n" + this.getName() + " | " + this.getPlace() + " | " + this.getDescription() +
                "\n" + Utils.DateToText(this.getStart_date()) + " | " + Utils.TimeToText(this.start_time) +
                "\n" + Utils.DateToText(this.getEnd_date()) + " | " + Utils.TimeToText(this.end_time) +
                "\n" + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n";

        return str;
    }
}
