package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class mService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public mService(String name) {
        super(name);
    }

    public mService(){
        super("ts");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();

            FirebaseUtils.getCurrentUserDataRef().child("latitude").setValue(latitude);
            FirebaseUtils.getCurrentUserDataRef().child("longitude").setValue(longitude);

        }

        @Override
        public void onLocationAvailability(
                @NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            boolean locationAvailable = locationAvailability.isLocationAvailable();

            FirebaseUtils.getCurrentUserDataRef().child("locationAvailable").setValue(locationAvailable);
        }
    };

    private LocationRequest locationRequest = new LocationRequest().setWaitForAccurateLocation(true).setInterval(2000);


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
        Log.d("murad", "Tracking Started");
        fusedLocationProviderClient.requestLocationUpdates(
                new LocationRequest().setWaitForAccurateLocation(true).setInterval(2000),
                locationCallback, Looper.myLooper());

        FirebaseUtils.getCurrentUserDataRef().child("latitude").setValue(651);
        FirebaseUtils.getCurrentUserDataRef().child("longitude").setValue(564);

        return START_STICKY;
    }

    /*@Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
        Log.d("murad", "Tracking stopped");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
