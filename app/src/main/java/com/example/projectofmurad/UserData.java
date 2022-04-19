package com.example.projectofmurad;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UserData {

    private String UID;
    private String email;
    private String username;
    private String phone;
    private String token;

    private ArrayList<String> tokens;

    private boolean madrich;
    private boolean notMadrich = true;

    private String profile_picture;
    private boolean emailVerified;

    private boolean online;
    private boolean offline = true;

    private boolean subscribedToAddEvent;
    private boolean subscribedToEditEvent;
    private boolean subscribedToDeleteEvent;

    private boolean subscribedToAutoAlarmSet;
    private boolean subscribedToAutoAlarmMove;

    private long totalTime;
    private long totalDistance;
    private long totalSpeed;

    public final static String KEY_UID = "key_user_UID";
    public final static String KEY_email = "key_user_email";
    public final static String KEY_username = "key_user_username";
    public final static String KEY_phone = "key_user_phone";
    public final static String KEY_token = "key_user_token";
    public final static String KEY_tokens = "key_user_tokens";
    public final static String KEY_madrich = "key_user_madrich";
    public final static String KEY_profile_picture = "key_user_profile_picture";
    public final static String KEY_emailVerified = "key_user_emailVerified";
    public final static String KEY_online = "key_user_online";
    public final static String KEY_latitude = "key_user_latitude";
    public final static String KEY_longitude = "key_user_longitude";
    public final static String KEY_locationAvailable = "key_user_locationAvailable";

    public final static String KEY_SUBSCRIBED_TO_ADD_EVENT = "key_user_subscribedToAddEvent";
    public final static String KEY_SUBSCRIBED_TO_EDIT_EVENT = "key_user_subscribedToEditEvent";
    public final static String KEY_SUBSCRIBED_TO_DELETE_EVENT = "key_user_subscribedToDeleteEvent";

    public final static String KEY_SUBSCRIBED_TO_AUTO_ALARM_SET = "key_user_subscribedToAutoAlarmSet";
    public final static String KEY_SUBSCRIBED_TO_AUTO_ALARM_MOVE = "key_user_subscribedToAutoAlarmMove";

    public UserData(){}

    public UserData(String UID, String email, String username, String phone) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.madrich = false;
        this.notMadrich = true;
        this.phone = phone;
    }

    public UserData(String UID, String email, String username, String phone, String profile_picture) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.madrich = false;
        this.notMadrich = true;
        this.phone = phone;
        this.profile_picture = profile_picture;
    }

    public UserData(String UID, String email, String username) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.madrich = false;
        this.notMadrich = true;
        this.online = true;
        this.offline = false;
    }

    public UserData(String UID, String email, String username, String phone, boolean madrich, String token) {
        this.UID = UID;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.madrich = madrich;
        this.notMadrich = !madrich;
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
                " \nUID = '" + UID + '\'' +
                ",\nemail = '" + email + '\'' +
                ",\nusername = '" + username + '\'' +
                ",\nphone = '" + phone + '\'' +
                ",\ntoken = '" + token + '\'' +
                ",\ntokens = " + tokens +
                ",\nmadrich = " + madrich +
                ",\nnotMadrich = " + notMadrich +
                ",\nprofile_picture = '" + profile_picture + '\'' +
                ",\nemailVerified = " + emailVerified +
                ",\nonline = " + online +
                ",\noffline = " + offline +
                ",\nsubscribedToAddEvent = " + subscribedToAddEvent +
                ",\nsubscribedToEditEvent = " + subscribedToEditEvent +
                ",\nsubscribedToDeleteEvent = " + subscribedToDeleteEvent +
                ",\nsubscribedToAutoAlarmSet = " + subscribedToAutoAlarmSet +
                ",\nsubscribedToAutoAlarmMove = " + subscribedToAutoAlarmMove +
                "\n}";
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public boolean isMadrich() {
        return madrich;
    }

    public void setMadrich(boolean madrich) {
        this.madrich = madrich;
        this.notMadrich = !madrich;
    }

    public boolean isNotMadrich() {
        return notMadrich;
    }

    public void setNotMadrich(boolean notMadrich) {
        this.notMadrich = notMadrich;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
        this.offline = !online;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
        this.online = !offline;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    public boolean isSubscribedToAddEvent() {
        return subscribedToAddEvent;
    }

    public void setSubscribedToAddEvent(boolean subscribedToAddEvent) {
        this.subscribedToAddEvent = subscribedToAddEvent;
    }

    public boolean isSubscribedToEditEvent() {
        return subscribedToEditEvent;
    }

    public void setSubscribedToEditEvent(boolean subscribedToEditEvent) {
        this.subscribedToEditEvent = subscribedToEditEvent;
    }

    public boolean isSubscribedToDeleteEvent() {
        return subscribedToDeleteEvent;
    }

    public void setSubscribedToDeleteEvent(boolean subscribedToDeleteEvent) {
        this.subscribedToDeleteEvent = subscribedToDeleteEvent;
    }
}