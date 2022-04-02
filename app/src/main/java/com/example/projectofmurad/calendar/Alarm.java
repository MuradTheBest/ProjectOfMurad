package com.example.projectofmurad.calendar;

public class Alarm {

    private int alarm_id;
    private String eventPrivateId;
    private String eventDateTime;


    public Alarm(int alarm_id, String eventPrivateId, String eventDateTime) {
        this.alarm_id = alarm_id;
        this.eventPrivateId = eventPrivateId;
        this.eventDateTime = eventDateTime;
    }

    public int getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(int alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getEventPrivateId() {
        return eventPrivateId;
    }

    public void setEventPrivateId(String eventPrivateId) {
        this.eventPrivateId = eventPrivateId;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
