package com.example.projectofmurad;

import androidx.annotation.NonNull;

public class UserData {
    private String UID;
    private String email;
    private String username;
    private String phone;
    private String picture;
    private String currentGroup;

    public final static String KEY_USER_DATA = "userData";
    public final static String KEY_UID = "uid";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_USERNAME = "username";
    public final static String KEY_PHONE = "phone";
    public final static String KEY_PICTURE = "picture";
    public static final String KEY_CURRENT_GROUP = "currentGroup";

    public final static String KEY_SUBSCRIBED_TO_AUTO_ALARM_SET = "key_user_subscribedToAutoAlarmSet";
    public final static String KEY_SUBSCRIBED_TO_AUTO_ALARM_MOVE = "key_user_subscribedToAutoAlarmMove";

    public UserData(){}

    public UserData(String UID, String email, String username, String phone) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }

    public UserData(String UID, String email, String username) {
        this.UID = UID;
        this.email = email;
        this.username = username;
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

    @NonNull
    @Override
    public String toString() {
        return "UserData{" +
                "UID='" + UID + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", picture='" + picture + '\'' +
                ", currentGroup='" + currentGroup + '\'' +
                '}';
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

}