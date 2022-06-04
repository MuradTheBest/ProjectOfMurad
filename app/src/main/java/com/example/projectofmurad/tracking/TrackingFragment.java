package com.example.projectofmurad.tracking;

import static com.example.projectofmurad.helpers.utils.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.training.Training;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("MissingPermission")
public class TrackingFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        CompoundButton.OnCheckedChangeListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback {

    private GoogleMap map;

    public final static String TRACKING_CHANNEL_ID = Utils.APPLICATION_ID + "tracking_channel_id";

    public final static String SELECTED_LOCATION_TAG = Utils.APPLICATION_ID + "selected_location_tag";

    public final static String ACTION_MOVE_TO_TRACKING_FRAGMENT_TO_SHOW_TRACK = Utils.APPLICATION_ID + "action_show__track";

    private boolean mapReady;

    GoogleMapOptions options;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private HashMap<String, Marker> markers;

    private Polyline gpsTrack;

    private RelativeLayout rl_training;

    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_speed;
    private TextView tv_max_speed;

    private BroadcastReceiver broadcastReceiver;

    private MaterialButton btn_start_tracking;
    private MaterialButton btn_stop_tracking;

    private MaterialButton btn_help;

    private LinearLayoutCompat ll_pause_or_finish_training;

    private Marker selected_location_marker;

    //ToDo start activity on button press and collect data connected to event
    // or create new one if nt exists

    //Todo improve tracking service
    //Todo change logo

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);


        /*Intent i = new Intent(getActivity(), TrackingService.class);
        getActivity().startService(i);*\

        requireActivity().startService(new Intent(getContext(), TrackingService.class));
/*        requireActivity().startForegroundService(new Intent(getContext(), TrackingService.class));*/

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
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_, container, false);
    }

    private BottomSheetBehavior bottom_sheet;

    private GridLayout bottom_sheet_training_expanded;

    private TextView tv_training_duration;
    private TextView tv_training_total_duration;
    private TextView tv_training_distance;
    private TextView tv_training_average_speed;
    private TextView tv_training_max_speed;
    private TextView tv_training_average_pace;
    private TextView tv_training_max_pace;

    private MainViewModel mainViewModel;

    private View mapView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeBottomSheet(view);

        markers = new HashMap<>();

        //    private SwitchCompat switch_last_location;
        SwitchMaterial switch_map_type = view.findViewById(R.id.switch_map_type);
        switch_map_type.setOnCheckedChangeListener(
                (buttonView, isChecked) -> map.setMapType(isChecked ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL));

        options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);
        options.compassEnabled(true);
        options.mapToolbarEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        MaterialToolbar materialToolbar = view.findViewById(R.id.materialToolbar);
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.share_my_location) {
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                            TrackingFragment.this::shareMyLocation)
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Can't get your current location",
                                    Toast.LENGTH_SHORT).show());
                }
                return false;
            }
        });

        AppCompatImageView iv_share_location = view.findViewById(R.id.iv_share_location);
        iv_share_location.setOnClickListener(v -> fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(TrackingFragment.this::shareMyLocation)
                .addOnFailureListener(e -> Toast.makeText(requireContext(),
                        "Can;t get your current location",
                        Toast.LENGTH_SHORT).show()));

        btn_start_tracking = view.findViewById(R.id.btn_start_tracking);
        btn_start_tracking.setOnClickListener(v -> startTracking());

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

        MaterialButton btn_finish_tracking = view.findViewById(R.id.btn_finish_tracking);
        btn_finish_tracking.setOnClickListener(v -> finishTracking());

        btn_help = view.findViewById(R.id.btn_help);
        btn_help.setOnClickListener(v ->
                FirebaseUtils.getCurrentUserTrackingRef(TrackingService.eventPrivateId.getValue()).child("help")
                        .setValue(btn_help.getText().equals("Help!")));

        if (TrackingService.eventPrivateId.getValue() != null){
            FirebaseUtils.getCurrentUserTrackingRef(TrackingService.eventPrivateId.getValue()).child("help").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isAskingForHelp = snapshot.exists() && snapshot.getValue(boolean.class);
                            btn_help.getBackground().setTint(isAskingForHelp ? requireContext().getColor(R.color.colorAccent) : Color.RED);
                            btn_help.setText(isAskingForHelp ? "Asked" : "Help!");
                            btn_help.setTextColor(isAskingForHelp ? Color.WHITE : Color.BLACK);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


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

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void shareMyLocation(@NonNull Location location) {
        String uri = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here is my location");
        intent.putExtra(Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void initializeBottomSheet(@NonNull View view){
        FrameLayout fl_bottom_sheet = view.findViewById(R.id.bottom_sheet_training);

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

    public void startTracking(){

        PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isPowerSaveMode()) {
            createPowerSavingDialog(false);
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
            Query usersLocations = FirebaseUtils.getAttendanceDatabase().child(event_private_id)
                    .orderByChild("attend").equalTo(true);
            btn_help.setVisibility(View.VISIBLE);
            usersLocations.addValueEventListener(valueEventListener);
        }

        btn_start_tracking.setVisibility(View.GONE);
        ll_pause_or_finish_training.setVisibility(View.VISIBLE);
        btn_stop_tracking.setText("Pause");

        // Define the IntentFilter.
        IntentFilter intentFilter = new IntentFilter();

        // Adding system broadcast actions sent by the system when the power save mode is changed.
        intentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        requireActivity().registerReceiver(broadcastReceiver, intentFilter);

        if (!TrackingService.isRunning.getValue()){
            Intent intent = new Intent(getContext(), TrackingService.class);
            intent.setAction(TrackingService.ACTION_START_TRACKING_SERVICE);

            requireActivity().startService(intent);

            startObserving();
        }

        bottom_sheet.setDraggable(true);
    }

    private void startObserving(){

        TrackingService.locationsData.observe(getViewLifecycleOwner(), latLngs -> {
            if (mapReady){
                gpsTrack.setPoints(latLngs);
            }
        });

        TrackingService.timeData.observe(getViewLifecycleOwner(), seconds -> {
            tv_time.setText(Training.getDurationFromTime(seconds));
            tv_training_duration.setText(Training.getDurationFromTime(seconds));
        });

        TrackingService.totalTimeData.observe(getViewLifecycleOwner(),
                seconds -> tv_training_total_duration.setText(Training.getDurationFromTime(seconds)));

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

        TrackingService.avgPaceData.observe(getViewLifecycleOwner(), avgPace -> tv_training_average_pace.setText(avgPace));

        TrackingService.maxPaceData.observe(getViewLifecycleOwner(), maxPace -> tv_training_max_pace.setText(maxPace));
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
            createPowerSavingDialog(false);
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

        List<LatLng> latLngs = TrackingService.locationsData.getValue();
        LatLngBounds bounds = Utils.getLatLngBounds(latLngs);

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,10));

        TrackingService.trainingData.observe(getViewLifecycleOwner(), this::createSaveTrainingDialog);

        Intent intent = new Intent(requireContext(), TrackingService.class);
        requireActivity().stopService(intent);

        btn_help.setVisibility(View.GONE);

        createPowerSavingDialog(true);
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

    public void createSaveTrainingDialog(Training t){
        if (TrackingService.trainingType.getValue() == null){
            SaveTrainingDialog saveTrainingDialog = new SaveTrainingDialog(requireContext(), t);
            saveTrainingDialog.show();
        }
        else if (TrackingService.trainingType.getValue().equals(TrackingService.GROUP_TRAINING)){
            if (TrackingService.eventPrivateId.getValue() != null){

                LoadingDialog loadingDialog = new LoadingDialog(requireContext());
                loadingDialog.setMessage("Adding the training data to selected event...");

                FirebaseUtils.addTrainingForEvent(TrackingService.eventPrivateId.getValue(), t).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismiss();

                                if (!task.isSuccessful()){
                                    Toast.makeText(requireContext(), "Adding the training to this event failed \n" +
                                            "The training will be added to private trainings", Toast.LENGTH_SHORT).show();
                                    FirebaseUtils.getCurrentUserPrivateTrainingsRef().child(t.getPrivateId()).setValue(t);
                                }

                                Toast.makeText(requireContext(), "The training was added to this event successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                ChooseEventClickDialog chooseEventClickDialog = new ChooseEventClickDialog(requireContext(), LocalDate.now(), t);

                chooseEventClickDialog.show();
            }
        }
        else if (TrackingService.trainingType.getValue().equals(TrackingService.PRIVATE_TRAINING)){
            FirebaseUtils.getCurrentUserPrivateTrainingsRef().child(t.getPrivateId()).setValue(t);
        }
    }

    @Override
    public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.rb_private_training){

        }
    }

    public void createPowerSavingDialog(boolean on){
        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(requireContext());
        builder.setTitle(on ? "Stop tracking" : "Start tracking");
        builder.setMessage(on
                ? "You can now turn on back power saving mode"
                : "In order to continue you have to turn off power saving mode");

        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());

        builder.show();
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapReady = true;

        map = googleMap;
        /*LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        gpsTrack = map.addPolyline(new PolylineOptions().color(Color.BLUE));

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            ImageView locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();

            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 50, 100);

            locationButton.setImageResource(R.drawable.ic_baseline_my_location_24);
            locationButton.setBackgroundColor(Color.WHITE);
            locationButton.setPadding(10, 10, 10, 10);
            locationButton.setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.colorAccent)));
        }

        map.setOnMarkerClickListener(this);

        if (checkPermissions()){
            map.setMyLocationEnabled(true);

            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);

            map.setOnMapClickListener(this);

            //ToDo track only users that attend to this trainingData
//            usersLocations.addValueEventListener(valueEventListener);
            gpsTrack.setJointType(JointType.ROUND);

            if (mainViewModel.getLocation().getValue() != null){
                mainViewModel.getLocation().observe(getViewLifecycleOwner(),
                        new Observer<com.example.projectofmurad.tracking.Location>() {
                            @Override
                            public void onChanged(com.example.projectofmurad.tracking.Location location) {
                                LatLng selected_location  = location.toLatLng();

                                Log.d(TrackingService.TAG, "location.getLatitude() = " + location.getLatitude());
                                Log.d(TrackingService.TAG, "location.getLongitude() = " + location.getLongitude());

                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(selected_location,  18));

                                selected_location_marker = map.addMarker(new MarkerOptions()
                                        .position(selected_location)
                                        .title("Marker"));

                                selected_location_marker.setTag(SELECTED_LOCATION_TAG);

                                mainViewModel.resetLocation();
                            }
                        });
            }
            else if (mainViewModel.getLocations().getValue() != null){
                mainViewModel.getLocations().observe(getViewLifecycleOwner(),
                        new Observer<List<com.example.projectofmurad.tracking.Location>>() {
                            @Override
                            public void onChanged(List<com.example.projectofmurad.tracking.Location> locations) {
                                List<LatLng> latLngs = new ArrayList<>();
                                locations.forEach(location -> latLngs.add(location.toLatLng()));

                                gpsTrack.setPoints(latLngs);

                                LatLngBounds bounds = Utils.getLatLngBounds(latLngs);
                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

                                mainViewModel.resetLocations();
                            }
                        });
            }
            else {
                getMyLocation();
            }
        }

    }

    final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.exists() && !snapshot.hasChildren()){
                return;
            }

            //ToDo to not trigger users' locationsData' markers' reload when current user's location changed
            for (DataSnapshot user : snapshot.getChildren()){
                if (user.exists() && !FirebaseUtils.isCurrentUID(user.getKey()) &&
                        user.hasChild("latitude") && user.hasChild("longitude")){

                    String UID = user.getKey();
                    double latitude = user.child("latitude").getValue(double.class);
                    double longitude = user.child("longitude").getValue(double.class);
                    boolean help = user.hasChild("help") && user.child("help").getValue(boolean.class);

                    FirebaseUtils.getUserDataByUIDRef(UID).get().addOnSuccessListener(
                            new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        return;
                                    }

                                    UserData userData = snapshot.getValue(UserData.class);

                                    String username = userData.getUsername();
                                    String picture = userData.getPicture();

                                    Log.d("map", "username is " + username);
                                    Log.d("map", "latitude is " + latitude);
                                    Log.d("map", "longitude is " + longitude);

                                    LatLng latLng = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(latLng)
                                            .title(username + (help ? "HELP!!!" : ""))
                                            .flat(true)
                                            .zIndex(0)
                                            /*.snippet(username)*/;

                                    if (user.hasChild("locationAvailable")
                                            && !user.child("locationAvailable").getValue(boolean.class)){
                                        markerOptions.alpha(0.5f);
                                    }

                                    //to check if the fragment was changed
                                    if (getContext() != null){
                                        getUserBitmap(UID, picture, markerOptions);
                                    }
                                }
                            });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    //creates bitmap with user's profile picture
    private void getUserBitmap(String UID, String picture, MarkerOptions markerOptions){
        Glide.with(this)
                .asBitmap()
                .load(picture)
                .error(R.drawable.sample_profile_picture)
                .dontTransform()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {

                        float scale = getResources().getDisplayMetrics().density;

                        int pixels = (int) (40 * scale + 0.5f);

                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);

                        if (bitmap != null){
                            bitmap = getBitmapClippedCircle(bitmap);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                            Marker old_marker = markers.get(UID);

                            if (old_marker != null){
                                old_marker.remove();
                            }

                            Marker marker = map.addMarker(markerOptions);
                            marker.showInfoWindow();

                            marker.setTag(UID);

                            markers.put(UID, marker);
                        }

                    }

                });
    }

    //makes bitmap circle
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 18));
    }

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

        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (selected_location_marker != null){
            selected_location_marker.remove();
        }
    }
}