package com.example.projectofmurad.tracking;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.training.Training;
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

public class TrackingService extends LifecycleService {

    public static final String ACTION_START_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_start_tracking";
    public static final String ACTION_PAUSE_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_pause_tracking";
    public static final String ACTION_FINISH_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_finish_tracking";

    public static final String ACTION_MOVE_TO_TRACKING_FRAGMENT = Utils.APPLICATION_ID + "action_move_to_tracking_fragment";
    public static final int CODE_FOR_RESULT = 5000;

    public static final int TRACKING_NOTIFICATION_ID = 1423430;

    public static final String TAG = "tracking";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static List<LatLng> locations;
    private static long time;
    private static long totalTime;

    private final Handler handler = new Handler();

    private NotificationManager notificationManager;

    private NotificationCompat.Builder notificationBuilder;

    private long start;
    private LocalDateTime startDateTime;

    private long end;
    private LocalDateTime endDateTime;

    public static MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> isNewTraining = new MutableLiveData<>(true);

    public static MutableLiveData<List<LatLng>> locationsData = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<List<LatLng>> stops = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<Long> timeData = new MutableLiveData<>(0L);
    public static MutableLiveData<Long> totalTimeData = new MutableLiveData<>(0L);

    public static MutableLiveData<Double> avgSpeedData = new MutableLiveData<>(0D);
    public static MutableLiveData<HashMap<String, SpeedAndLocation>> speedsData = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<Double> maxSpeedData = new MutableLiveData<>(0D);

    public static MutableLiveData<String> avgPaceData = new MutableLiveData<>("--'--" + '"' + "/km");
    public static MutableLiveData<HashMap<String, Float>> pacesData = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<String> maxPaceData = new MutableLiveData<>("--'--" + '"' + "/km");

    public static MutableLiveData<Double> totalDistanceData = new MutableLiveData<>(0D);

    public static MutableLiveData<Training> trainingData = new MutableLiveData<>();

    public static MutableLiveData<String> trainingType = new MutableLiveData<>(null);
    public static MutableLiveData<String> eventPrivateId = new MutableLiveData<>(null);

    public static final String PRIVATE_TRAINING = Utils.APPLICATION_ID + "private_training";
    public static final String GROUP_TRAINING = Utils.APPLICATION_ID + "group_training";

    public static void clearData(){
        isRunning.setValue(false);
        isNewTraining.setValue(true);
        locationsData = new MutableLiveData<>();
        stops = new MutableLiveData<>();

        timeData.setValue(0L);
        totalTimeData.setValue(0L);

        avgSpeedData.setValue(0D);
        speedsData.setValue(new HashMap<>());
        maxSpeedData.setValue(0D);

        avgPaceData.setValue("");
        pacesData.setValue(new HashMap<>());
        maxPaceData.setValue("");

        totalDistanceData.setValue(0D);
    }

    private final Runnable runnable = new Runnable() {
        @Override

        public void run() {

            // If running is true, increment the
            // timeData variable.
            if (isRunning.getValue()) {
                Log.d("tracking", "timeData = " + time);
                timeData.setValue(time);
                time++;

                // Format the timeData into hours, minutes,
                // and timeData.
/*                notificationBuilder.setContentText(timeData);

                Notification notification = notificationBuilder.getNotification();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notification);*/
            }

            Log.d("tracking", "totalTimeData " + totalTime);
            totalTimeData.setValue(totalTime);
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


            if (TrackingService.eventPrivateId.getValue() != null){
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("latitude").setValue(latitude);
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("longitude").setValue(longitude);
            }

            Log.d(TAG, "latitude = " + latitude);
            Log.d(TAG, "longitude = " + longitude);

            locations.add(new LatLng(latitude, longitude));

            locationsData.setValue(locations);

            int current_position = locations.size()-1;
            int previous_position = current_position-1;

            double absoluteDistance = SphericalUtil.computeLength(locationsData.getValue());

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

            if (TrackingService.eventPrivateId.getValue() != null){
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("locationAvailable").setValue(locationAvailable);
            }
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

                if (isNewTraining.getValue() != null) {
                    if (isNewTraining.getValue()) {
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
        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(this);

        builder.setTitle("Start tracking");
        builder.setMessage("In order to continue you have to turn off power saving mode");

        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton(R.string.back, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        clearData();

        isNewTraining.setValue(false);

        Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
        Log.d("tracking", "STARTED");

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ACTION_MOVE_TO_TRACKING_FRAGMENT);
//        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);

        /*PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);*/

        PendingIntent pintent = PendingIntent.getActivity(this, CODE_FOR_RESULT, intent,
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

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        start = System.currentTimeMillis()/1000;
        startDateTime = LocalDateTime.now();

        handler.post(runnable);

        /*timeData.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long seconds) {
                int hours = (int) (seconds / 3600);
                int minutes = (int) ((seconds % 3600) / 60);
                int secs = (int) (seconds % 60);

                // Format the timeData into hours, minutes,
                // and timeData.
                String timeData = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);

                notificationBuilder.setContentText(timeData);
                notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
            }
        });*/

        startForeground(TRACKING_NOTIFICATION_ID, notificationBuilder.build());

        isRunning.setValue(true);
    }

    private void resumeLocationUpdates(){
        Log.d("tracking", "RESUMED");

        isRunning.setValue(true);

        notificationBuilder.setContentText("Tracking started");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void pauseLocationUpdates() {
        isRunning.setValue(false);

        notificationBuilder.setContentText("Tracking stopped");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void finishLocationUpdates(){
        Log.d("tracking", "FINISHED");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        notificationBuilder.setContentText("Tracking finished");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());

        isRunning.setValue(false);
        isNewTraining.setValue(true);

//        addTraining(locationsData);

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

                clearData();
            }
        });


    }

    public Task<Void> uploadTraining(long start, long end){

        String trainingId = "Training" + FirebaseUtils.getCurrentUserPrivateTrainingsRef().push().getKey();

        long time = timeData.getValue();
        long totalTime = totalTimeData.getValue();

        double speed = avgSpeedData.getValue();
        HashMap<String, SpeedAndLocation> speeds = speedsData.getValue();
        double maxSpeed = maxSpeedData.getValue();

        double totalDistance = totalDistanceData.getValue();

        Log.d("murad", speeds.toString());

        List<LatLng> latLngs = locationsData.getValue();

        List<com.example.projectofmurad.tracking.Location> locations = new ArrayList<>();

        latLngs.forEach(latLng -> locations.add(com.example.projectofmurad.tracking.Location.fromLatLng(latLng)));

//        Training trainingData = new Training(trainingId, start, end, timeData, totalTimeData, speed, maxSpeedData, speedsData, totalDistanceData);
        Training training = new Training(trainingId, startDateTime, endDateTime, time, totalTime, speed, maxSpeed, speeds, locations, totalDistance);
        Log.d("tracking", training.toString());

        trainingData.setValue(training);

        return FirebaseUtils.getCurrentUserPrivateTrainingsRef().child(trainingId).setValue(training);
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

        double absoluteDistance = SphericalUtil.computeLength(locationsData.getValue());
        absoluteDistance /= 1000;

        double hours = (double) time / 3600;

        totalDistanceData.setValue(Utils.round(totalDistanceData.getValue() + distance, 3));

        avgSpeedData.setValue(Utils.round(totalDistanceData.getValue()/hours, 3));
        HashMap<String, SpeedAndLocation> addedSpeeds = speedsData.getValue();
        addedSpeeds.put(timeData.getValue() + " sec",
                new SpeedAndLocation(avgSpeedData.getValue(), current.latitude, current.longitude));

        speedsData.setValue(addedSpeeds);

        maxSpeedData.setValue(Math.max(avgSpeedData.getValue(), maxSpeedData.getValue()));

        avgPaceData.setValue(Utils.convertSpeedToPace(avgSpeedData.getValue()));
        maxPaceData.setValue(Utils.convertSpeedToPace(maxSpeedData.getValue()));

        Log.d(TAG, "-----------------------------------tracking-----------------------------");
        Log.d(TAG, "");
        Log.d(TAG, "distance = " + distance);
        Log.d(TAG, "absoluteDistance = " + absoluteDistance);
        Log.d(TAG, "totalDistanceData = " + totalDistanceData.getValue());
        Log.d(TAG, "avgSpeedData = " + avgSpeedData.getValue());
        Log.d(TAG, "maxSpeedData = " + maxSpeedData.getValue());
        Log.d(TAG, "speedsData = " + speedsData.getValue().toString());
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
