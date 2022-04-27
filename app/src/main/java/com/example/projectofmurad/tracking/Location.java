package com.example.projectofmurad.tracking;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class Location implements Serializable, Parcelable {

    private double latitude;
    private double longitude;

    public Location() {}

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Location(@NonNull Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

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

    public LatLng toLatLng(){
        return new LatLng(latitude, longitude);
    }

    @NonNull
    public static Location fromLatLng(@NonNull LatLng latLng){
        return new Location(latLng.latitude, latLng.longitude);
    }

    @NonNull
    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
