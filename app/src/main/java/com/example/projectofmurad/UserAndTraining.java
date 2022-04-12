package com.example.projectofmurad;

import com.example.projectofmurad.tracking.Training;

import java.util.ArrayList;

public class UserAndTraining {

    private String UID;
    private String eventPrivateId;
    private ArrayList<Training> training;

    public UserAndTraining() {}

    public UserAndTraining(String UID, ArrayList<Training> training) {
        this.UID = UID;
        this.training = training;
    }

    public UserAndTraining(String UID, String eventPrivateId, ArrayList<Training> training) {
        this.UID = UID;
        this.training = training;
        this.eventPrivateId = eventPrivateId;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public ArrayList<Training> getTraining() {
        return training;
    }

    public void setTraining(ArrayList<Training> training) {
        this.training = training;
    }

    public String getEventPrivateId() {
        return eventPrivateId;
    }

    public void setEventPrivateId(String eventPrivateId) {
        this.eventPrivateId = eventPrivateId;
    }
}
