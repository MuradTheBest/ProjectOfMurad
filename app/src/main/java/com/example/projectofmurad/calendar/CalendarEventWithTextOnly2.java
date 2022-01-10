package com.example.projectofmurad.calendar;

import android.graphics.Color;

import com.example.projectofmurad.Utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarEventWithTextOnly2 implements Serializable {

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

    private String frequencyDay;
    private String[] frequencyDayOfWeek;
    private String frequencyMonth;
    private String frequencyYear;

    private int color;


    public CalendarEventWithTextOnly2(){
/*
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
        this.frequencyDay = "";
        this.frequencyDayOfWeek = new String[]{};
        this.frequencyMonth = "";
        this.frequencyYear = "";*/
    }

    public CalendarEventWithTextOnly2(String name, String description, String place, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils.DateToText(startDate);

        this.start_time = Utils.TimeToText(startTime);

        this.end_date = Utils.DateToText(endDate);

        this.end_time = Utils.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = Color.GREEN;
    }

    public CalendarEventWithTextOnly2(String name, String description, String place, int color,
                                      LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                      String frequencyDay, String[] frequencyDayOfWeek, String frequencyMonth,
                                      String frequencyYear, int frequency) {

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = Utils.DateToText(startDate);

        this.start_time = Utils.TimeToText(startTime);

        this.end_date = Utils.DateToText(endDate);

        this.end_time = Utils.TimeToText(endTime);

        this.timestamp = startTime.toSecondOfDay();

        this.event_id = this.start_date + "-" + this.timestamp;

        this.color = color;

        this.frequency = frequency;

        this.frequencyDay = frequencyDay;
        this.frequencyDayOfWeek = frequencyDayOfWeek;
        this.frequencyMonth = frequencyMonth;
        this.frequencyYear = frequencyYear;
    }

    public CalendarEventWithTextOnly2(String name, String description, String place, String start_date, String start_time, String end_date, String end_time) {

        this.timestamp = Utils.TextToTime(start_time).toSecondOfDay();

        this.event_id = start_date + "-" + this.timestamp;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = start_date;

        this.start_time = start_time;

        this.end_date = end_date;

        this.end_time = end_time;

        this.color = Color.GREEN;
    }

    public CalendarEventWithTextOnly2(String name, String description, String place, int color,
                                      String start_date, String start_time, String end_date, String end_time,
                                      String frequencyDay, String[] frequencyDayOfWeek, String frequencyMonth,
                                      String frequencyYear, int frequency) {

        this.timestamp = Utils.TextToTime(start_time).toSecondOfDay();

        this.event_id = start_date + "-" + this.timestamp;

        this.name = name;
        this.description = description;
        this.place = place;

        this.start_date = start_date;

        this.start_time = start_time;

        this.end_date = end_date;

        this.end_time = end_time;

        this.color = color;

        this.frequency = frequency;

        this.frequencyDay = frequencyDay;
        this.frequencyDayOfWeek = frequencyDayOfWeek;
        this.frequencyMonth = frequencyMonth;
        this.frequencyYear = frequencyYear;
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

    public LocalDate receiveStart_date(){
        return Utils.TextToDate(start_date);
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void updateStart_date(LocalDate start_date) {
        this.start_date = Utils.DateToText(start_date);
    }

    public String getStart_time() {
        return start_time;
    }

    public LocalTime receiveStart_time(){
        return Utils.TextToTime(start_time);
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void updateStart_time(LocalTime start_time) {
        this.start_time = Utils.TimeToText(start_time);
    }

    public String getEnd_date() {
        return end_date;
    }

    public LocalDate receiveEnd_date(){
        return Utils.TextToDate(end_date);
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void updateEnd_date(LocalDate end_date) {
        this.end_date = Utils.DateToText(end_date);
    }

    public String getEnd_time() {
        return end_time;
    }

    public LocalTime receiveEnd_time(){
        return Utils.TextToTime(end_time);
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void updateEnd_time(LocalTime end_time) {
        this.end_time = Utils.TimeToText(end_time);
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

    public String toString(){
        String str = "------------------------------------------------------------------------------------------------------------------------------------ \n" +
                "\n" + this.getName() + " | " + this.getPlace() + " | " + this.getDescription() +
                "\n" + this.getStart_date() + " | " + this.getStart_time() +
                "\n" + this.getEnd_date() + " | " + this.getEnd_time() +
                "\n" + "\n ------------------------------------------------------------------------------------------------------------------------------------ \n";

        return str;
    }

}
