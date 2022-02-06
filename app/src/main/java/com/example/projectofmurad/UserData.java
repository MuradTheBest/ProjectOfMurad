package com.example.projectofmurad;

import androidx.annotation.NonNull;

public class UserData {

    private String UID;
    private String email;
    private String username;
    private String phone;
    private boolean isMadrich;

    public UserData(){}

    public UserData(String UID, String email, String username) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.isMadrich = false;
    }

    public UserData(String UID, String email, String username, String phone, boolean isMadrich) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.isMadrich = isMadrich;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isMadrich() {
        return isMadrich;
    }

    public void setMadrich(boolean madrich) {
        isMadrich = madrich;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserData{" +
                "UID = '" + UID + '\'' +
                ", email = '" + email + '\'' +
                ", username = '" + username + '\'' +
                ", isMadrich = " + isMadrich +
                '}';
    }
}