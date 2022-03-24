package com.example.projectofmurad.tracking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrackingService extends LifecycleService {

    public static final String ACTION_START_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_start_tracking";
    public static final String ACTION_PAUSE_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_pause_tracking";
    public static final String ACTION_FINISH_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_finish_tracking";

    public static final String ACTION_AUTO_RESUME_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_auto_resume_tracking";
    public static final String ACTION_AUTO_PAUSE_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_auto_pause_tracking";
    public static final String ACTION_AUTO_RESUME_OR_PAUSE_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_auto_resume_or_pause_tracking";

    public static final String ACTION_MOVE_TO_TRACKING_FRAGMENT = Utils.APPLICATION_ID + "action_move_to_tracking_fragment";

    public static final int TRACKING_NOTIFICATION_ID = 0;

    public static final String TAG = "tracking";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static List<LatLng> locations;
    private static long time;
    private static long totalTime;

    private Handler handler = new Handler();

    private NotificationManager notificationManager;

    private NotificationCompat.Builder notificationBuilder;

    private long start;
    private LocalDateTime startDateTime;

    private long end;
    private LocalDateTime endDateTime;

    private final Runnable runnable = new Runnable() {
        @Override

        public void run() {

            // If running is true, increment the
            // time variable.
            if (TrackingViewModel.isRunning.getValue()) {
                Log.d("tracking", "time = " + time);
                TrackingViewModel.time.setValue(time);
                time++;

                int hours = (int) (time / 3600);
                int minutes = (int) ((time % 3600) / 60);
                int secs = (int) (time % 60);

                // Format the time into hours, minutes,
                // and time.
                String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);

/*                notificationBuilder.setContentText(time);

                Notification notification = notificationBuilder.getNotification();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notification);*/
            }

            Log.d("tracking", "totalTime " + totalTime);
            TrackingViewModel.totalTime.setValue(totalTime);
            totalTime++;

            // Post the code again
            // with a delay of 1 second.
            handler.postDelayed(this, 1000);
        }
    };

    private final LocationCallback locationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (!FirebaseUtils.isUserLoggedIn()){
                pauseLocationUpdates();
                return;
            }

//            double latitude = locationResult.getLastLocation().getLatitude();
//            double longitude = locationResult.getLastLocation().getLongitude();

            double latitude = Utils.round(locationResult.getLastLocation().getLatitude(), 4);
            double longitude = Utils.round(locationResult.getLastLocation().getLongitude(), 4);

            FirebaseUtils.getCurrentUserDataRef().child("latitude").setValue(latitude);
            FirebaseUtils.getCurrentUserDataRef().child("longitude").setValue(longitude);

            Log.d(TAG, "latitude = " + latitude);
            Log.d(TAG, "longitude = " + longitude);

            locations.add(new LatLng(latitude, longitude));

            TrackingViewModel.locations.setValue(locations);

            int current_position = locations.size()-1;
            int previous_position = current_position-1;

            double absoluteDistance = SphericalUtil.computeLength(TrackingViewModel.locations.getValue());

            Log.d(TAG, "absoluteDistance = " + absoluteDistance);

            if (previous_position >= 0){
                calculateDistance(locations.get(previous_position), locations.get(current_position));
            }

//            Toast.makeText(TrackingService.this, "Latitude: " + latitude
//                    + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLocationAvailability(
                @NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            boolean locationAvailable = locationAvailability.isLocationAvailable();

            FirebaseUtils.getCurrentUserDataRef().child("locationAvailable").setValue(locationAvailable);
        }
    };

    //ToDo improve location request
    private final LocationRequest locationRequest = new LocationRequest()
            .setWaitForAccurateLocation(true)
            .setInterval(2000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(1);

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        return super.registerReceiver(receiver, filter);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locations = new ArrayList<>();


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand( Intent intent, int flags, int startId) {

        String action = intent.getAction();

        switch (action) {
            case ACTION_START_TRACKING_SERVICE:
                Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
                Log.d("murad", "Tracking Started");

                if (TrackingViewModel.isNewTraining.getValue() != null) {
                    if (TrackingViewModel.isNewTraining.getValue()) {
                        startLocationUpdates();
                    }
                    else {
                        resumeLocationUpdates();
                    }
                }
                break;
            case ACTION_PAUSE_TRACKING_SERVICE:
                pauseLocationUpdates();
                break;
            case ACTION_AUTO_RESUME_TRACKING_SERVICE:

                break;
            case ACTION_AUTO_PAUSE_TRACKING_SERVICE:

                break;
            case PowerManager.ACTION_POWER_SAVE_MODE_CHANGED:
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

                Toast.makeText(this, "Power mode changed", Toast.LENGTH_SHORT).show();

                if (powerManager.isPowerSaveMode()) {
                    createTurnOffPowerSavingDialog();
                }
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void createTurnOffPowerSavingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Start tracking");
        builder.setMessage("In order to continue you have to turn off power saving mode");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        Utils.createCustomAlertDialog(alertDialog);

        alertDialog.show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        this.getCacheDir().delete();
       /* Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        restartServiceIntent.setAction(ACTION_START_TRACKING_SERVICE);

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);*/

        super.onTaskRemoved(rootIntent);
    }

    /*@Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }*/

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        TrackingViewModel.clearData();

        TrackingViewModel.isNewTraining.setValue(false);

        Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
        Log.d("tracking", "STARTED");

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ACTION_MOVE_TO_TRACKING_FRAGMENT);
//        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);

        /*PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);*/

        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Tracking started")
//                .setContentText("0:00:00")
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(pintent)
//                .setUsesChronometer(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);


        String channelId = "CHANNEL_ID";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH);

        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);

        notificationBuilder.setChannelId(channelId);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        start = System.currentTimeMillis()/1000;
        startDateTime = LocalDateTime.now();

        handler.post(runnable);

        /*TrackingViewModel.time.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long seconds) {
                int hours = (int) (seconds / 3600);
                int minutes = (int) ((seconds % 3600) / 60);
                int secs = (int) (seconds % 60);

                // Format the time into hours, minutes,
                // and time.
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);

                notificationBuilder.setContentText(time);
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
            }
        });*/

        startForeground(TRACKING_NOTIFICATION_ID, notificationBuilder.build());

        TrackingViewModel.isRunning.setValue(true);
    }

    private void resumeLocationUpdates(){
        Log.d("tracking", "RESUMED");

        TrackingViewModel.isRunning.setValue(true);

        notificationBuilder.setContentText("Tracking started");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void pauseLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        TrackingViewModel.isRunning.setValue(false);

        notificationBuilder.setContentText("Tracking stopped");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void finishLocationUpdates(){
        Log.d("tracking", "FINISHED");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        notificationBuilder.setContentText("Tracking finished");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());

        TrackingViewModel.isRunning.setValue(false);
        TrackingViewModel.isNewTraining.setValue(true);

//        addTraining(locations);

        stopForeground(true);
        stopSelf();

        notificationManager.cancel(TRACKING_NOTIFICATION_ID);

        end = System.currentTimeMillis()/1000;
        endDateTime = LocalDateTime.now();

//        end = Calendar.getInstance().getTimeInMillis();

        uploadTraining(start, end).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                start = 0;
                end = 0;
                time = 0;
                totalTime = 0;

                handler.removeCallbacks(runnable);

                TrackingViewModel.clearData();
            }
        });


    }

    public Task<Void> uploadTraining(long start, long end){

        String trainingId = FirebaseUtils.getCurrentUserTrainingsRef().push().getKey();

        long time = TrackingViewModel.time.getValue();
        long totalTime = TrackingViewModel.totalTime.getValue();

        double speed = TrackingViewModel.avgSpeed.getValue();
        HashMap<String, Double> speeds = TrackingViewModel.speeds.getValue();
        double maxSpeed = TrackingViewModel.maxSpeed.getValue();

        double totalDistance = TrackingViewModel.totalDistance.getValue();

        Log.d("murad", speeds.toString());


//        Training training = new Training(trainingId, start, end, time, totalTime, speed, maxSpeed, speeds, totalDistance);
        Training training = new Training(trainingId, startDateTime, endDateTime, time, totalTime, speed, maxSpeed, speeds, totalDistance);
        Log.d("tracking", training.toString());

        TrackingViewModel.training.setValue(training);

        return FirebaseUtils.getCurrentUserTrainingsRef().child(trainingId).setValue(training);
    }

    public void calculateDistance(@NonNull LatLng previous, @NonNull LatLng current){
       /* double lat = Math.abs(previous.latitude - current.latitude);
        double lng = Math.abs(previous.longitude - current.longitude);

        double d = Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));*/
        //Location startloc=Location.distanceBetween(previous.latitude, previous.latitude, current.latitude, current.longitude, );

        Location startPoint = new Location("previous");
        startPoint.setLatitude(previous.latitude);
        startPoint.setLongitude(previous.longitude);

        Location endPoint = new Location("current");
        endPoint.setLatitude(current.latitude);
        endPoint.setLongitude(current.longitude);

//        double distance = Utils.round(startPoint.distanceTo(endPoint), 3);

        double distance = SphericalUtil.computeDistanceBetween(previous, current);
        distance /= 1000;

        double absoluteDistance = SphericalUtil.computeLength(TrackingViewModel.locations.getValue());
        absoluteDistance /= 1000;

        double hours = (double) time / 3600;

        TrackingViewModel.totalDistance.setValue(Utils.round(TrackingViewModel.totalDistance.getValue() + distance, 3));

        TrackingViewModel.avgSpeed.setValue(Utils.round(TrackingViewModel.totalDistance.getValue()/hours, 3));
        HashMap<String, Double> addedSpeeds = TrackingViewModel.speeds.getValue();
        addedSpeeds.put(""+TrackingViewModel.time.getValue(), TrackingViewModel.avgSpeed.getValue());
        TrackingViewModel.speeds.setValue(addedSpeeds);

        TrackingViewModel.maxSpeed.setValue(Math.max(TrackingViewModel.avgSpeed.getValue(), TrackingViewModel.maxSpeed.getValue()));

        TrackingViewModel.avgPace.setValue(Utils.convertSpeedToPace(TrackingViewModel.avgSpeed.getValue()));
        TrackingViewModel.maxPace.setValue(Utils.convertSpeedToPace(TrackingViewModel.maxSpeed.getValue()));

        Log.d(TAG, "-----------------------------------tracking-----------------------------");
        Log.d(TAG, "");
        Log.d(TAG, "distance = " + distance);
        Log.d(TAG, "absoluteDistance = " + absoluteDistance);
        Log.d(TAG, "totalDistance = " + TrackingViewModel.totalDistance.getValue());
        Log.d(TAG, "avgSpeed = " + TrackingViewModel.avgSpeed.getValue());
        Log.d(TAG, "maxSpeed = " + TrackingViewModel.maxSpeed.getValue());
        Log.d(TAG, "speeds = " + TrackingViewModel.speeds.getValue().toString());
        Log.d(TAG, "");
        Log.d(TAG, "-------------------------------tracking---------------------------------");

    }

    private double speedConverter(double speed){
        return speed*18/5;
    }

    @Override
    public boolean stopService(@NonNull Intent name) {

        if (name.getAction() != null && name.getAction().equals(ACTION_FINISH_TRACKING_SERVICE)){
        }
            finishLocationUpdates();

            Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
            Log.d("murad", "Tracking Stopped");

        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        pauseLocationUpdates();


        finishLocationUpdates();

        Toast.makeText(this, "Tracking Finished", Toast.LENGTH_SHORT).show();
        Log.d("murad", "Tracking Finished");

    }
}
