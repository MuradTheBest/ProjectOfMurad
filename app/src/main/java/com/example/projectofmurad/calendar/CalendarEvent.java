package com.example.projectofmurad.calendar;

import java.time.LocalDate;

public class CalendarEvent {
    private static int event_id = 0;

    private LocalDate event_date;
    private String name;
    private String description;
    private String place;

    private int start_hour;
    private int start_min;

    private int end_hour;
    private int end_min;

    private int start_day;
    private int start_month;
    private int start_year;

    private int end_day;
    private int end_month;
    private int end_year;

    private LocalDate start_date;
    private LocalDate end_date;

    public CalendarEvent(LocalDate event_date, String name, String description, String place, String start_hour, String start_min, String end_hour, String end_min) {
        event_id++;
        this.event_date = event_date;
        this.name = name;
        this.description = description;
        this.place = place;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        CalendarEvent.event_id = event_id;
    }

    public LocalDate getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDate event_date) {
        this.event_date = event_date;
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
}
