package com.example.projectofmurad.calendar;

public class Alarm {

    private int alarmId;
    private String eventPrivateId;
    private String eventDateTime;

    public Alarm(int alarmId, String eventPrivateId, String eventDateTime) {
        this.alarmId = alarmId;
        this.eventPrivateId = eventPrivateId;
        this.eventDateTime = eventDateTime;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
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
