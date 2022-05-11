package com.example.projectofmurad.tracking;

public class TrackingUser {

    private double latitude;
    private double longitude;
    private boolean locationAvailable;

    public TrackingUser(String UID, double latitude, double longitude, String picture, String username, boolean locationAvailable) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAvailable = locationAvailable;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isLocationAvailable() {
        return locationAvailable;
    }

    public void setLocationAvailable(boolean locationAvailable) {
        this.locationAvailable = locationAvailable;
    }
}
