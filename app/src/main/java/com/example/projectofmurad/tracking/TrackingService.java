package com.example.projectofmurad.tracking;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * The type Tracking service.
 */
public class TrackingService extends LifecycleService {

    /**
     * The constant ACTION_START_TRACKING_SERVICE.
     */
    public static final String ACTION_START_TRACKING_SERVICE = Utils.APPLICATION_ID + "action_start_tracking";

    /**
     * The constant ACTION_MOVE_TO_TRACKING_FRAGMENT.
     */
    public static final String ACTION_MOVE_TO_TRACKING_FRAGMENT = Utils.APPLICATION_ID + "action_move_to_tracking_fragment";

    /**
     * The constant CODE_FOR_RESULT.
     */
    public static final int CODE_FOR_RESULT = 5000;

    /**
     * The constant TRACKING_CHANNEL_ID.
     */
    public final static String TRACKING_CHANNEL_ID = Utils.APPLICATION_ID + "tracking_channel_id";
    /**
     * The constant TRACKING_NOTIFICATION_ID.
     */
    public static final int TRACKING_NOTIFICATION_ID = 1423430;
    /**
     * The constant POWER_MODE_CHANGED_NOTIFICATION_ID.
     */
    public static final int POWER_MODE_CHANGED_NOTIFICATION_ID = 4321;

    /**
     * The constant TAG.
     */
    public static final String TAG = "tracking";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (!FirebaseUtils.isUserLoggedIn()){
                finishLocationUpdates();
                return;
            }

//            double latitude = locationResult.getLastLocation().getLatitude();
//            double longitude = locationResult.getLastLocation().getLongitude();

            double latitude = Utils.round(locationResult.getLastLocation().getLatitude(), 4);
            double longitude = Utils.round(locationResult.getLastLocation().getLongitude(), 4);

            Log.d(Utils.LOG_TAG, "latitude = " + latitude);
            Log.d(Utils.LOG_TAG, "longitude = " + longitude);

            if (TrackingService.eventPrivateId.getValue() != null){
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("latitude").setValue(latitude);
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("longitude").setValue(longitude);
            }
        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            boolean locationAvailable = locationAvailability.isLocationAvailable();

            if (TrackingService.eventPrivateId.getValue() != null){
                FirebaseUtils.getCurrentUserTrackingRef(eventPrivateId.getValue()).child("locationAvailable").setValue(locationAvailable);
            }
        }
    };

    private final LocationRequest locationRequest = new LocationRequest()
            .setWaitForAccurateLocation(true)
            .setInterval(2000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(1);

    private NotificationManager notificationManager;

    private NotificationCompat.Builder notificationBuilder;

    /**
     * The constant eventPrivateId.
     */
    public static MutableLiveData<String> eventPrivateId = new MutableLiveData<>(null);
    /**
     * The constant isRunning.
     */
    public static MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        switch (action) {
            case ACTION_START_TRACKING_SERVICE:
                Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
                Log.d("murad", "Tracking Started");
                startLocationUpdates();

                break;
            case PowerManager.ACTION_POWER_SAVE_MODE_CHANGED:
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

                Toast.makeText(this, "Power mode changed", Toast.LENGTH_SHORT).show();

                if (powerManager.isPowerSaveMode()) {
                    sendTurnOffPowerSavingNotification();
                }
                else {
                    notificationManager.cancel(POWER_MODE_CHANGED_NOTIFICATION_ID);
                }
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Send turn off power saving notification.
     */
    public void sendTurnOffPowerSavingNotification(){
        Intent i = new Intent(this, MainActivity.class);

        PendingIntent pintent = PendingIntent.getBroadcast(this, 10, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TRACKING_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Warning")
                .setContentText("Turn off power saving mode in order to continue tracking")
                .setColor(Color.RED)
                .setAutoCancel(true)
                .setContentIntent(pintent);

        NotificationChannel channel = new NotificationChannel(
                TRACKING_CHANNEL_ID,
                "Channel for tracking",
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(channel);

        builder.setChannelId(TRACKING_CHANNEL_ID);

        notificationManager.notify(POWER_MODE_CHANGED_NOTIFICATION_ID, builder.build());
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show();
        Log.d("tracking", "STARTED");

        isRunning.setValue(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(ACTION_MOVE_TO_TRACKING_FRAGMENT);

        PendingIntent pintent = PendingIntent.getActivity(this, CODE_FOR_RESULT, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, TRACKING_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Tracking started")
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(pintent)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationChannel channel = new NotificationChannel(
                TRACKING_CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH);

        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);

        notificationBuilder.setChannelId(TRACKING_CHANNEL_ID);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        startForeground(TRACKING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void finishLocationUpdates(){
        Log.d("tracking", "FINISHED");

        isRunning.setValue(false);

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        notificationBuilder.setContentText("Tracking finished");
        notificationManager.notify(TRACKING_NOTIFICATION_ID, notificationBuilder.build());

        stopForeground(true);
        stopSelf();

        notificationManager.cancel(TRACKING_NOTIFICATION_ID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finishLocationUpdates();

        Toast.makeText(this, "Tracking Finished", Toast.LENGTH_SHORT).show();
        Log.d("murad", "Tracking Finished");
    }
}
