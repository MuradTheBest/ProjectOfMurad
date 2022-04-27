package com.example.projectofmurad.tracking;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class SpeedAndLocation implements Serializable {

    private double speed;
    private double latitude;
    private double longitude;

    public SpeedAndLocation() {}

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

    public Location getLocation(){
        return new Location(latitude, longitude);
    }

    public void setLocation(@NonNull Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @NonNull
    @Override
    public String toString() {
        return "SpeedAndLocation{" +
                "speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
