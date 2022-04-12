package com.example.projectofmurad.tracking;

import static com.example.projectofmurad.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Tracking_Fragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, CompoundButton.OnCheckedChangeListener,
SaveTrainingDialog.OnAddTrainingListener{

    private GoogleMap map;

    public final static String TRACKING_CHANNEL_ID = Utils.APPLICATION_ID + "tracking_channel_id";


    private boolean mapReady;
    private boolean selfTraining;

    GoogleMapOptions options;

    public int map_type = GoogleMap.MAP_TYPE_NORMAL;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (FirebaseUtils.isUserLoggedIn()){
                Location lastLocation = locationResult.getLastLocation();

                double latitude = lastLocation.getLatitude();
                double longitude = lastLocation.getLongitude();


/*                FirebaseUtils.getCurrentUserDataRef().child("latitude").setValue(latitude);
                FirebaseUtils.getCurrentUserDataRef().child("longitude").setValue(longitude);*/

//                gpsTrack.setPoints(TrackingService.getLocations());

//                updateTrack(lastLocation);
            }

        }

        @Override
        public void onLocationAvailability(
                @NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            boolean locationAvailable = locationAvailability.isLocationAvailable();

            FirebaseUtils.getCurrentUserDataRef().child("locationAvailable").setValue(locationAvailable);
        }
    };

    private void updateTrack(@NonNull Location location) {
        LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());

        List<LatLng> points = gpsTrack.getPoints();


        points.add(lastLocation);
        gpsTrack.setPoints(points);

        int current_position = points.lastIndexOf(lastLocation);
        int previous_position = current_position-1;

        if (previous_position >= 0){
            showDistance(points.get(previous_position), points.get(current_position));
        }
    }

    double totalDistance = 0;

    public void showDistance(@NonNull LatLng previous, @NonNull LatLng current){
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

        float distance = startPoint.distanceTo(endPoint);
        totalDistance += distance;
        Log.d("naumov", "distance = " + distance);
        Log.d("naumov", "totalDistanceData = " + totalDistance);
        Log.d("naumov", "----------------------------------");

/*        double speed = (double) totalDistanceData/seconds;


//        double distanceToShow = Double.parseDouble(new DecimalFormat("####.##").format(totalDistanceData));

        distanceView.setText(new DecimalFormat("####.##").format(totalDistanceData));
        speedView.setText(new DecimalFormat("####.##").format(speed));*/
    }

    private Map<String, Marker> markers;
    private Map<String, Marker> last_markers;

    private DatabaseReference usersLocations;

//    private SwitchCompat switch_last_location;
    private SwitchCompat switch_map_type;

    private Polyline gpsTrack;

    private RelativeLayout rl_training;

    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_speed;
    private TextView tv_max_speed;

    private BroadcastReceiver broadcastReceiver;

    private MaterialButton btn_start_tracking;
    private MaterialButton btn_stop_tracking;
    private MaterialButton btn_finish_tracking;

    private LinearLayout ll_pause_or_finish_training;


    //ToDo start activity on button press and collect data connected to event
    // or create new one if nt exists

    //Todo improve tracking service
    //Todo change logo

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {

        }

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);


        /*Intent i = new Intent(getActivity(), TrackingService.class);
        getActivity().startService(i);*\

        requireActivity().startService(new Intent(getContext(), TrackingService.class));
/*        requireActivity().startForegroundService(new Intent(getContext(), TrackingService.class));
        requireActivity().startService(new Intent(getContext(), mService.class));*/

//        initializeBroadcastReceiver();

        if (TrackingService.isRunning.getValue() == null){
            TrackingService.isRunning.setValue(false);
        }

        if (TrackingService.isNewTraining.getValue() == null){
            TrackingService.isNewTraining.setValue(true);
        }


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {
                if (TrackingService.isRunning.getValue() && intent.getAction().equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)){
                    PowerManager powerManager = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);

                    Toast.makeText(requireContext(), "Power mode changed", Toast.LENGTH_SHORT).show();

                    if (powerManager.isPowerSaveMode()){
                        /*if (getContext() == null){
                            sendTurnOffPowerSavingNotification();
                        }
                        else {
                            createTurnOffPowerSavingDialog();
                        }*/

                        sendTurnOffPowerSavingNotification();
                    }
                    else {
                        NotificationManager notificationManager =
                                (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.cancel(4321);
                    }
                }
            }
        };

    }

    public void sendTurnOffPowerSavingNotification(){
        Intent i = new Intent(requireContext(), MainActivity.class);
//        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);

        PendingIntent pintent = PendingIntent.getBroadcast(requireContext(), 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), TRACKING_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Warning")
                .setContentText("Turn off power saving mode in order to continue tracking")
                .setColor(Color.RED)
                .setAutoCancel(true)
                .setContentIntent(pintent);

        NotificationManager notificationManager =
                (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationChannel channel = new NotificationChannel(
                TRACKING_CHANNEL_ID,
                "Channel for tracking",
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        builder.setChannelId(TRACKING_CHANNEL_ID);

        notificationManager.notify(4321, builder.build());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_, container, false);
    }

    private FrameLayout fl_bottom_sheet;

    private BottomSheetBehavior bottom_sheet;

    private GridLayout bottom_sheet_training_expanded;

    private TextView tv_training_duration;
    private TextView tv_training_total_duration;
    private TextView tv_training_distance;
    private TextView tv_training_average_speed;
    private TextView tv_training_max_speed;
    private TextView tv_training_average_pace;
    private TextView tv_training_max_pace;

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        requireActivity().getActionBar().hide();

        initializeBottomSheet(view);

        markers = new HashMap<>();
        last_markers = new HashMap<>();

        switch_map_type = view.findViewById(R.id.switch_map_type);
        switch_map_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                map.setMapType(isChecked ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);
                map_type = isChecked ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL;
            }
        });

        options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);
        options.compassEnabled(true);
        options.mapToolbarEnabled(true);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        /*fusedLocationProviderClient.requestLocationUpdates(
                new LocationRequest().setWaitForAccurateLocation(true).setInterval(2000),
                locationCallback, Looper.myLooper());*/



        btn_start_tracking = view.findViewById(R.id.btn_start_tracking);
        btn_start_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        ll_pause_or_finish_training = view.findViewById(R.id.ll_pause_or_finish_training);

        btn_stop_tracking = view.findViewById(R.id.btn_stop_tracking);
        btn_stop_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_stop_tracking.getText().toString().equals("Pause")){
                    pauseTracking();
                }
                else {
                    resumeTracking();
                }

            }
        });

        btn_finish_tracking = view.findViewById(R.id.btn_finish_tracking);
        btn_finish_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTracking();

            }
        });

        /*if (TrackingService.isRunning.getValue() && !isTrackingServiceRunning()){

            resumeTracking();

            *//*requireActivity().startService(new Intent(getContext(), TrackingService.class));
            // Build intent that displays the App settings screen.
            Intent intent = new Intent();
            intent.setAction(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",
                    BuildConfig.APPLICATION_ID, null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*//*

        }*/

        if (TrackingService.isRunning.getValue()){
            startTracking();
        }
    }

    private void initializeBottomSheet(@NonNull View view){
        fl_bottom_sheet = view.findViewById(R.id.bottom_sheet_training);

        bottom_sheet_training_expanded = view.findViewById(R.id.bottom_sheet_training_expanded);

        bottom_sheet = BottomSheetBehavior.from(fl_bottom_sheet);

        bottom_sheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            float slideOffSet;
            float oldSlideOffSet;

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (slideOffSet > oldSlideOffSet) {
                            bottom_sheet_training_expanded.setVisibility(View.VISIBLE);
                            rl_training.setVisibility(View.GONE);
                        }
                        else if (slideOffSet < oldSlideOffSet) {
                            rl_training.setVisibility(View.VISIBLE);
                            bottom_sheet_training_expanded.setVisibility(View.GONE);
                        }
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(LOG_TAG, "dragging");

                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:


                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        rl_training.setVisibility(View.VISIBLE);
                        bottom_sheet_training_expanded.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
/*                        bottom_sheet_training_expanded.setVisibility(View.VISIBLE);
                        rl_training.setVisibility(View.GONE);*/
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                oldSlideOffSet = slideOffSet;
                slideOffSet = slideOffset;
                Log.d(LOG_TAG, "oldSlideOffSet = " + oldSlideOffSet);
                Log.d(LOG_TAG, "slideOffSet = " + slideOffset);
                /*if (slideOffset > 0 && slideOffset < 1){
                    rl_training.setVisibility(View.GONE);
                }
                else if(slideOffset == 0){
                    rl_training.setVisibility(View.VISIBLE);
                }*/

                if (slideOffSet > 0.49f && slideOffSet < 0.51f){

                    if (slideOffSet > oldSlideOffSet){
                        rl_training.setVisibility(View.GONE);
                        bottom_sheet_training_expanded.setVisibility(View.VISIBLE);
                    }
                    else if(slideOffSet < oldSlideOffSet){
                        bottom_sheet_training_expanded.setVisibility(View.GONE);
                        rl_training.setVisibility(View.VISIBLE);
                    }
                    Log.d(LOG_TAG, "rl_training.getVisibility() is " + rl_training.getVisibility());
                    Log.d(LOG_TAG, "bottom_sheet_training_expanded.getVisibility() is " + bottom_sheet_training_expanded.getVisibility());

                    /*if (rl_training.getVisibility() == View.VISIBLE){
                        rl_training.setVisibility(View.GONE);
                        bottom_sheet_training_expanded.setVisibility(View.VISIBLE);
                    }
                    else if(bottom_sheet_training_expanded.getVisibility() == View.VISIBLE){
                        bottom_sheet_training_expanded.setVisibility(View.GONE);
                        rl_training.setVisibility(View.VISIBLE);
                    }*/

                }
            }
        });

        initializeCollapsedBottomSheet(view);
        initializeExpandedBottomSheet(view);
    }

    private void initializeCollapsedBottomSheet(@NonNull View view){
        rl_training = view.findViewById(R.id.rl_training);
        tv_time = view.findViewById(R.id.tv_time);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_speed = view.findViewById(R.id.tv_speed);
        tv_max_speed = view.findViewById(R.id.tv_max_speed);
    }

    private void initializeExpandedBottomSheet(@NonNull View view){

        tv_training_duration = view.findViewById(R.id.tv_training_duration);
        tv_training_total_duration = view.findViewById(R.id.tv_training_total_duration);
        tv_training_distance = view.findViewById(R.id.tv_training_distance);
        tv_training_average_speed = view.findViewById(R.id.tv_training_average_speed);
        tv_training_max_speed = view.findViewById(R.id.tv_training_max_speed);
        tv_training_average_pace = view.findViewById(R.id.tv_training_average_pace);
        tv_training_max_pace = view.findViewById(R.id.tv_training_max_pace);
    }

    @Override
    public void onAddTraining(Training training) {
        new MyRepository(requireActivity().getApplication()).insert(training);
    }

    public void startTracking(){

        PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isPowerSaveMode()) {
            createTurnOffPowerSavingDialog();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(LOG_TAG, "checkBackgroundLocationPermission from startTracking");
            if (!checkBackgroundLocationPermission()){
                return;
            }
        }


        if (TrackingService.trainingType.getValue() != null && TrackingService.trainingType.getValue().equals(TrackingService.GROUP_TRAINING)){
            String event_private_id = TrackingService.eventPrivateId.getValue();
            usersLocations = FirebaseUtils.attendanceDatabase.child(event_private_id);
            usersLocations.addValueEventListener(valueEventListener);
        }

        btn_start_tracking.setVisibility(View.GONE);
        ll_pause_or_finish_training.setVisibility(View.VISIBLE);
        btn_stop_tracking.setText("Pause");

        // Define the IntentFilter.
        IntentFilter intentFilter = new IntentFilter();

        // Adding system broadcast actions sent by the system when the power save mode is changed.
        intentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        requireContext().registerReceiver(broadcastReceiver, intentFilter);

        initializeActivityBroadcastReceiver();

//        startLocationUpdates();

        if (!TrackingService.isRunning.getValue()){
            Intent intent = new Intent(getContext(), TrackingService.class);
            intent.setAction(TrackingService.ACTION_START_TRACKING_SERVICE);

            requireActivity().startService(intent);

            startObserving();

        }

        bottom_sheet.setDraggable(true);
    }

    private void startObserving(){

        TrackingService.locationsData.observe(getViewLifecycleOwner(), new Observer<List<LatLng>>() {
            @Override
            public void onChanged(List<LatLng> latLngs) {
                if (mapReady){
/*                    LatLng lastLocation = latLngs.get(latLngs.size()-1);

                    Log.d("murad", "Latitude: " + lastLocation.latitude
                            + "\nLongitude: " + lastLocation.longitude);*/

                    gpsTrack.setPoints(latLngs);

//                map.animateCamera(CameraUpdateFactory.newLatLng(lastLocation));
                }

            }
        });

        TrackingService.timeData.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long seconds) {

                int hours = (int) (seconds / 3600);
                int minutes = (int) ((seconds % 3600) / 60);
                int secs = (int) (seconds % 60);

                // Format the timeData into hours, minutes,
                // and timeData.
                String duration = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);

                tv_time.setText(duration);
                tv_training_duration.setText(duration);
            }
        });

        TrackingService.totalTimeData.observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long seconds) {

                int hours = (int) (seconds / 3600);
                int minutes = (int) ((seconds % 3600) / 60);
                int secs = (int) (seconds % 60);

                // Format the timeData into hours, minutes,
                // and timeData.
                String totalDuration = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);

                tv_training_total_duration.setText(totalDuration);
            }
        });

        TrackingService.totalDistanceData.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double totalDistance) {
                tv_distance.setText(new DecimalFormat("####.##").format(totalDistance) + " km");
                tv_training_distance.setText(new DecimalFormat("####.##").format(totalDistance) + " km");
            }
        });

        TrackingService.avgSpeedData.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double speed) {
                tv_speed.setText(new DecimalFormat("##.##").format(speed) + " km/h");
                tv_training_average_speed.setText(new DecimalFormat("##.##").format(speed) + " km/h");
            }
        });

        TrackingService.maxSpeedData.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double maxSpeed) {
                tv_max_speed.setText(new DecimalFormat("##.##").format(maxSpeed) + " km/h");
                tv_training_max_speed.setText(new DecimalFormat("##.##").format(maxSpeed) + " km/h");
            }
        });

        TrackingService.avgPaceData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String avgPace) {
                tv_training_average_pace.setText(avgPace);
            }
        });

        TrackingService.maxPaceData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String maxPace) {
                tv_training_max_pace.setText(maxPace);
            }
        });
    }

    public void pauseTracking() {
        btn_stop_tracking.setText("Resume");
        Intent intent = new Intent(getContext(), TrackingService.class);
        intent.setAction(TrackingService.ACTION_PAUSE_TRACKING_SERVICE);
        requireActivity().startService(intent);
    }

    public void resumeTracking(){

        PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isPowerSaveMode()) {
            createTurnOffPowerSavingDialog();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(LOG_TAG, "checkBackgroundLocationPermission from startTracking");

            if (!checkBackgroundLocationPermission()){
                return;
            }
        }

        ll_pause_or_finish_training.setVisibility(View.VISIBLE);
        btn_start_tracking.setVisibility(View.GONE);
        btn_stop_tracking.setText("Pause");

        Intent intent = new Intent(getContext(), TrackingService.class);
        intent.setAction(TrackingService.ACTION_START_TRACKING_SERVICE);

        requireActivity().startService(intent);
    }

    public void finishTracking(){

        btn_start_tracking.setVisibility(View.VISIBLE);
        ll_pause_or_finish_training.setVisibility(View.GONE);

        TrackingService.locationsData.removeObservers(getViewLifecycleOwner());

        TrackingService.timeData.removeObservers(getViewLifecycleOwner());
        tv_time.setText("00:00:00");

        removeObservers();

        requireActivity().unregisterReceiver(broadcastReceiver);

        TrackingService.trainingData.observe(getViewLifecycleOwner(), new Observer<Training>() {
            @Override
            public void onChanged(Training t) {

                if (TrackingService.trainingType.getValue() == null){
                    SaveTrainingDialog saveTrainingDialog = new SaveTrainingDialog(requireContext(), t, Tracking_Fragment.this);
                    saveTrainingDialog.show();
                }
                else if (TrackingService.trainingType.getValue().equals(TrackingService.GROUP_TRAINING)){
                    if (TrackingService.eventPrivateId.getValue() != null){
                        ProgressDialog progressDialog = new ProgressDialog(requireContext());
                        Utils.createCustomDialog(progressDialog);
                        progressDialog.setMessage("Adding the trainingData to selected event...");

                        FirebaseUtils.addTrainingForEvent(TrackingService.eventPrivateId.getValue(), t).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();

                                        if (!task.isSuccessful()){
                                            Toast.makeText(requireContext(), "Adding the training to this event failed \n" +
                                                    "The training will be added to private trainings", Toast.LENGTH_SHORT).show();
                                            onAddTraining(t);
                                        }

                                        Toast.makeText(requireContext(), "The training was added to this event successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        ChooseEventClickDialog chooseEventClickDialog = new ChooseEventClickDialog(requireContext(),
                                LocalDate.now(), t, Tracking_Fragment.this);

                        chooseEventClickDialog.show();
                    }
                }
                else if (TrackingService.trainingType.getValue().equals(TrackingService.PRIVATE_TRAINING)){
                    onAddTraining(t);
                }
            }
        });

        Intent intent = new Intent(getContext(), TrackingService.class);
        requireActivity().stopService(intent);

        createSaveTrainingDialog();


        createTurnOnPowerSavingDialog();
    }

    private void removeObservers(){

        TrackingService.locationsData.removeObservers(getViewLifecycleOwner());

        TrackingService.timeData.removeObservers(getViewLifecycleOwner());

        TrackingService.totalTimeData.removeObservers(getViewLifecycleOwner());

        TrackingService.totalDistanceData.removeObservers(getViewLifecycleOwner());

        TrackingService.avgSpeedData.removeObservers(getViewLifecycleOwner());

        TrackingService.maxSpeedData.removeObservers(getViewLifecycleOwner());

        TrackingService.avgPaceData.removeObservers(getViewLifecycleOwner());

        TrackingService.maxPaceData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TrackingService.isRunning.getValue() != null && TrackingService.isRunning.getValue()){
            startTracking();
        }


    }

    public void initializeActivityBroadcastReceiver(){
        checkActivityRecognitionPermission();
        List<ActivityTransition> transitions = new ArrayList<>();;

        /*transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());*/

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        /*transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());*/

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Intent intent = new Intent(requireContext(), ActivityBroadcastReceiver.class);
        PendingIntent myPendingIntent = PendingIntent.getBroadcast(requireContext(), 1122, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        @SuppressLint("MissingPermission") Task<Void> task = ActivityRecognition.getClient(requireContext())
                .requestActivityTransitionUpdates(request, myPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success
                        Log.d(LOG_TAG, "receiving activity recognitions");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(LOG_TAG, "receiving activity recognitions failed");
                    }
                }
        );
    }

/*
    public void createSaveTrainingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setTitle("Stop tracking");
        builder.setMessage("You can now turn on back power saving mode");

        builder.setItems(new String[]{"Save as private trainingData", "Save as group trainingData"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:



                                break;
                            case 1:



                                break;
                        }
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        Utils.createCustomAlertDialog(alertDialog);

        alertDialog.show();
    }
*/



    public void createSaveTrainingDialog(){
        /*Dialog dialog = new Dialog(requireContext());
        dialog.setTitle("Choose trainingData type");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_training_dialog);

        RadioGroup rg_choose_training = dialog.findViewById(R.id.rg_choose_training);

        RadioButton rb_private_training = dialog.findViewById(R.id.rb_private_training);
        RadioButton rb_group_training = dialog.findViewById(R.id.rb_group_training);

        rb_private_training.setOnCheckedChangeListener(this);
        rb_group_training.setOnCheckedChangeListener(this);

        LinearLayout ll_private_training = dialog.findViewById(R.id.ll_private_training);
        LinearLayout ll_group_training = dialog.findViewById(R.id.ll_group_training);

        Utils.createCustomDialog(dialog);

        dialog.show();*/

    }

    @Override
    public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.rb_private_training){

        }
    }

    public void createTurnOnPowerSavingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(true);
        builder.setTitle("Stop tracking");
        builder.setMessage("You can now turn on back power saving mode");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        Utils.createCustomAlertDialog(alertDialog);

        alertDialog.show();
    }

    public void createTurnOffPowerSavingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

    public boolean isTrackingServiceRunning(){
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service : activityManager
            .getRunningServices(Integer.MAX_VALUE)){
                if (TrackingService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getMyLocation();
        return false;
    }

    /*@Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(requireContext(), "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();

        Log.d("murad", "Current location:\n" + location);
        LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());

        map.clear();
        map.addMarker(new MarkerOptions().position(current_location).title("Current Location"));
    }*/

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapReady = true;

        map = googleMap;
        /*LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        gpsTrack = map.addPolyline(new PolylineOptions().color(Color.BLUE));

        map.setMapType(map_type);



        map.setOnMarkerClickListener(this);

        Log.d(LOG_TAG, "checking permissions from onMapReady");
        if (checkPermissions()){
            map.setMyLocationEnabled(true);

            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            getMyLocation();

            //ToDo track only users that attend to this trainingData
//            usersLocations.addValueEventListener(valueEventListener);
            gpsTrack.setJointType(JointType.ROUND);


        }


    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //ToDo to not trigger users' locationsData' markers' reload when current user's location changed
            for (DataSnapshot user : snapshot.getChildren()){
                if (user.exists() && !FirebaseUtils.isCurrentUID(user.getKey())){
                    Log.d("map", "*************************************************************");
                    String UID = user.getKey();
                    Log.d("map", "tracking id is " + UID);

                    if (user.hasChild("latitude") && user.hasChild("longitude") && user.hasChild("profile_picture") && user.hasChild("username")){
                        String username = user.child("username").getValue().toString();

                        String profile_picture = user.child("profile_picture").getValue(String.class);

                        double latitude = user.child("latitude").getValue(double.class);
                        double longitude = user.child("longitude").getValue(double.class);


                        Log.d("map", "username is " + username);
                        Log.d("map", "latitude is " + latitude);
                        Log.d("map", "longitude is " + longitude);


                        LatLng latLng = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(username)
                                .flat(true)
                                .zIndex(0)
                                /*.snippet(username)*/;

                        if (user.hasChild("locationAvailable") && !user.child("locationAvailable").getValue(boolean.class)){
                            markerOptions.alpha(0.5f);
                        }

                        //to check if the fragment was changed
                        if (getActivity() != null){
                            getUserPPBitmap(UID, profile_picture,markerOptions);
                        }

                    }

                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void getUserPPBitmap(String UID, String profile_picture, MarkerOptions markerOptions){
        Glide.with(this)
                .asBitmap()
                .load(profile_picture)
                .dontTransform()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {

                        float scale = getContext().getResources().getDisplayMetrics().density;

                        int pixels = (int) (40 * scale + 0.5f);

                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);
                        if (bitmap == null){
                            Toast.makeText(requireContext(), "Bitmap is null", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Bitmap is null");
                        }
                        else {
                            Log.d(LOG_TAG, "Bitmap is not null");
                        }

                        if(bitmap != null){
                            bitmap = getBitmapClippedCircle(bitmap);
//                            bitmap = geStrokeBitmap(bitmap);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                            Marker old_marker = markers.get(UID);

                            if (old_marker != null){
//                            Log.d(LOG_TAG, "Old marker's tag is " + old_marker.getTag().toString());
//                            last_markers.put(UID, old_marker);
                                old_marker.remove();
                                Log.d(LOG_TAG, "Old marker is successfully removed");
                            }

                            Marker marker = map.addMarker(markerOptions);
                            assert marker != null;
                            marker.showInfoWindow();

                            marker.setTag(UID);

                            markers.put(UID, marker);
                        }

                    }


                });
    }

    public Bitmap geStrokeBitmap(@NonNull Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Paint paintStroke = new Paint();

        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.RED);
        paintStroke.setAntiAlias(true);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        float round = (float) bitmap.getHeight() / 2;
        canvas.drawRoundRect(rectF, round, round, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.drawRoundRect(rectF, round, round, paintStroke);

        return output;
    }

    public Bitmap getBitmapClippedCircle(@NonNull Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2),
                (float) (height / 2),
                (float) Math.min(width, (height / 2)),
                Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    public void getMyLocation() {

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                try {
                    LatLng current_location  = new LatLng(location.getLatitude(), location.getLongitude());
//                    map.moveCamera(CameraUpdateFactory.newLatLng(current_location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location,  18));
//                    map.moveCamera(CameraUpdateFactory.zoomBy(1f));
//                    map.clear();
//                    map.addMarker(new MarkerOptions().position(current_location).title("Current Location"));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    getMyLocation();
//                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    /*@Override
    public void onStop() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallback).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtils.getCurrentUserDataRef().child("latitude").removeValue();
                            FirebaseUtils.getCurrentUserDataRef().child("longitude").removeValue();

                        }
                    }
                });

        super.onStop();.

    }*/

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "checking permissions from onStart");
        checkPermissions();
    }

    private final String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public boolean checkPermissions(){
        boolean hasPermissions = true;

        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission " + permission + " is granted");
            }
            else {
                Log.d(LOG_TAG, "Permission " + permission + " is not granted");
                hasPermissions = false;
                askLocationPermission(permission);
            }
        }

        /*if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            Log.d(LOG_TAG, "Permission is not granted");
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            Log.d(LOG_TAG, "Permission is not granted");
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            Log.d(LOG_TAG, "Permission is not granted");
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission is granted");
        } else {
            Log.d(LOG_TAG, "Permission is not granted");
            askLocationPermission();
        }
*/
        return hasPermissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean checkBackgroundLocationPermission(){
        boolean hasPermissions = true;

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Permission " + Manifest.permission.ACCESS_BACKGROUND_LOCATION + " is granted");
        }
        else {
            Log.d(LOG_TAG, "Permission " + Manifest.permission.ACCESS_BACKGROUND_LOCATION + " is not granted");
            hasPermissions = false;
            askLocationPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        return hasPermissions;
    }

    public void checkActivityRecognitionPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission " + Manifest.permission.ACTIVITY_RECOGNITION + " is granted");
            }
            else {
                Log.d(LOG_TAG, "Permission " + Manifest.permission.ACTIVITY_RECOGNITION + " is not granted");
                askLocationPermission(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        }

    }

    private void askLocationPermission(String permission) {
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Asking for the permission " + permission);
            if (shouldShowRequestPermissionRationale(permission)) {
                Log.d(LOG_TAG, "askLocationPermission: you should show an alert dialog...");
            }

            Log.d(LOG_TAG, "requesting " + permission);

            if (permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                Log.d(LOG_TAG, "requesting " + permission + " with code " + 20000);
                requestPermissions(new String[]{permission}, 20000);
            }
            else if(permission.equals(Manifest.permission.ACTIVITY_RECOGNITION)){
                Log.d(LOG_TAG, "requesting " + permission + " with code " + 30000);
                requestPermissions(new String[]{permission}, 30000);
            }
            else{
                Log.d(LOG_TAG, "requesting " + permission + " with code " + 10000);
                requestPermissions(new String[]{permission}, 10000);
            }

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0){
            if (requestCode == 10000) {

                for (int i = 0; i < grantResults.length; i++) {

                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                        return;
                    }
                    else {
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is granted");
                    }
                }
                Log.d(LOG_TAG, "checking permissions from onRequestPermissionsResult");
                checkPermissions();
//            triggerRebirth(requireContext());
            }
            else if (requestCode == 30000) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                        return;
                    }
                }
//            triggerRebirth(requireContext());
            }
        }

        if (requestCode == 20000) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                    return;
                }
            }

            startTracking();
//            triggerRebirth(requireContext());
        }

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

//        Toast.makeText(requireContext(), "UID: " + marker.getTag().toString(), Toast.LENGTH_SHORT).show();
        return false;
    }
}