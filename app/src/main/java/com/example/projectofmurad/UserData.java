package com.example.projectofmurad;

import androidx.annotation.NonNull;

public class UserData {

    private String UID;
    private String email;
    private String username;
    private String phone;
    private String token;

    private boolean madrich;
    private boolean notMadrich = true;

    private String profile_picture;
    private boolean emailVerified;

    private boolean online;
    private boolean offline = true;

    private double latitude;
    private double longitude;
    private boolean locationAvailable;

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
                "\n UID = '" + UID + '\'' +
                ", \n email = '" + email + '\'' +
                ", \n username = '" + username + '\'' +
                ", \n phone = '" + phone + '\'' +
                ", \n token = '" + token + '\'' +
                ", \n madrich = " + madrich +
                ", \n notMadrich = " + notMadrich +
                ", \n profile_picture = '" + profile_picture + '\'' +
                ", \n online = " + online +
                ", \n offline = " + offline +
                ", \n latitude = " + latitude +
                ", \n longitude = " + longitude +
                ", \n locationAvailable = " + locationAvailable +
                "\n}";
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isLocationAvailable() {
        return locationAvailable;
    }

    public void setLocationAvailable(boolean locationAvailable) {
        this.locationAvailable = locationAvailable;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}