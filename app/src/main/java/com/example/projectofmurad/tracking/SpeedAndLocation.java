package com.example.projectofmurad.tracking;

public class SpeedAndLocation {

    private double speed;
    private double latitude;
    private double longitude;

    public SpeedAndLocation(double speed, double latitude, double longitude) {
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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
}
