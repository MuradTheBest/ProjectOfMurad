package com.example.projectofmurad.tracking;

import static com.example.projectofmurad.utils.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * The type Tracking fragment.
 */
@SuppressLint("MissingPermission")
public class TrackingFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleMap map;


    /**
     * The Options.
     */
    GoogleMapOptions options;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private HashMap<String, Marker> markers;

    private MaterialButton btn_start_tracking;
    private MaterialButton btn_finish_tracking;

    private MaterialButton btn_help;

    private LinearLayoutCompat ll_help_or_finish_training;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_, container, false);
    }

    private View mapView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        ImageView iv_share_location = view.findViewById(R.id.iv_share_location);
        iv_share_location.setOnClickListener(v -> fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(TrackingFragment.this::shareMyLocation)
                .addOnFailureListener(e -> Toast.makeText(requireContext(),
                        "Can't get your current location",
                        Toast.LENGTH_SHORT).show()));

        btn_start_tracking = view.findViewById(R.id.btn_start_tracking);
        btn_start_tracking.setOnClickListener(v -> startTracking());

        ll_help_or_finish_training = view.findViewById(R.id.ll_help_or_finish_training);

        btn_finish_tracking = view.findViewById(R.id.btn_finish_tracking);
        btn_finish_tracking.setOnClickListener(v -> finishTracking());

        btn_help = view.findViewById(R.id.btn_help);
        btn_help.setOnClickListener(v ->
                FirebaseUtils.getCurrentUserTrackingRef(TrackingService.eventPrivateId.getValue()).child("help")
                        .setValue(btn_help.getText().equals("Help!")));

        if (TrackingService.eventPrivateId.getValue() != null){
            FirebaseUtils.getCurrentUserTrackingRef(TrackingService.eventPrivateId.getValue()).child("help")
                    .addValueEventListener(new ValueEventListener() {
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

        if (TrackingService.isRunning.getValue()) {
            startTracking();
        }

    }

    private void shareMyLocation(@NonNull Location location) {
        String uri = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here is my location");
        intent.putExtra(Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    /**
     * Start tracking.
     */
    public void startTracking(){
        if (TrackingService.eventPrivateId.getValue() == null){
            Utils.createAlertDialog(requireContext(), "No event connected to training",
                    "You have to first choose event in order to start tracking",
                    "Ok", (dialog, which) -> dialog.dismiss(),
                    null, null, null)
                    .show();
            return;
        }

        PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isPowerSaveMode()) {
            createPowerSavingDialog(false);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkBackgroundLocationPermission()){
                return;
            }
        }

        String event_private_id = TrackingService.eventPrivateId.getValue();
        Log.d(LOG_TAG,  "TrackingService.eventPrivateId.getValue()" + TrackingService.eventPrivateId.getValue());
        Query usersLocations = FirebaseUtils.getAttendanceDatabase().child(event_private_id).orderByChild("attend").equalTo(true);
        usersLocations.addValueEventListener(valueEventListener);

        btn_start_tracking.setVisibility(View.GONE);
        ll_help_or_finish_training.setVisibility(View.VISIBLE);

        // Define the IntentFilter.
        IntentFilter intentFilter = new IntentFilter();

        // Adding system broadcast actions sent by the system when the power save mode is changed.
        intentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        Intent intent = new Intent(requireContext(), TrackingService.class);
        intent.setAction(TrackingService.ACTION_START_TRACKING_SERVICE);
        requireActivity().startService(intent);
    }

    /**
     * Finish tracking.
     */
    public void finishTracking(){
        btn_start_tracking.setVisibility(View.VISIBLE);
        ll_help_or_finish_training.setVisibility(View.GONE);

        Intent intent = new Intent(requireContext(), TrackingService.class);
        requireActivity().stopService(intent);

        createPowerSavingDialog(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TrackingService.isRunning.getValue() != null && TrackingService.isRunning.getValue()){
            startTracking();
        }
    }

    /**
     * Create power saving dialog.
     *
     * @param on the on
     */
    public void createPowerSavingDialog(boolean on){
        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(requireContext());
        builder.setTitle(on ? "Stop tracking" : "Start tracking");
        builder.setMessage(on
                ? "You can now turn on back power saving mode"
                : "In order to continue you have to turn off power saving mode");

        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getMyLocation();
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;

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

            getMyLocation();
        }

    }

    /**
     * The Value event listener.
     */
    final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.exists() && !snapshot.hasChildren()){
                return;
            }

            for (DataSnapshot user : snapshot.getChildren()){
                if (user.exists() && !FirebaseUtils.isCurrentUID(user.getKey()) &&
                        user.hasChild("latitude") && user.hasChild("longitude")){

                    String UID = user.getKey();
                    double latitude = user.child("latitude").getValue(double.class);
                    double longitude = user.child("longitude").getValue(double.class);
                    boolean help = user.hasChild("help") && user.child("help").getValue(boolean.class);

                    Log.d(LOG_TAG, "UID = " + UID);
                    Log.d(LOG_TAG, "latitude = " + latitude);
                    Log.d(LOG_TAG, "longitude = " + longitude);
                    Log.d(LOG_TAG, "help = " + help);

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

                                    Log.d(LOG_TAG, "username is " + username);
                                    Log.d(LOG_TAG, "latitude is " + latitude);
                                    Log.d(LOG_TAG, "longitude is " + longitude);

                                    LatLng latLng = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(latLng)
                                            .title(username + (help ? "\nHELP!!!" : ""))
                                            .flat(true)
                                            .zIndex(0)
                                            /*.snippet(username)*/;

                                    if (user.hasChild("locationAvailable")
                                            && !user.child("locationAvailable").getValue(boolean.class)){
                                        markerOptions.alpha(0.5f);
                                    }

                                    //to check if the fragment was changed
                                    if (getActivity() != null){
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
                .load(picture != null ? picture : R.drawable.sample_profile_picture)
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

    /**
     * Gets bitmap clipped circle.
     *
     * @param bitmap the bitmap
     *
     * @return the bitmap clipped circle
     */
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

    /**
     * Gets my location.
     */
    public void getMyLocation() {

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
               new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        try {
                            LatLng current_location  = new LatLng(location.getLatitude(), location.getLongitude());
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location,  18));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            getMyLocation();
                        }
                    }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermissions();
    }

    private final String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    /**
     * Check permissions boolean.
     *
     * @return the boolean
     */
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

        return hasPermissions;
    }

    /**
     * Check background location permission boolean.
     *
     * @return the boolean
     */
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
            }
            else if (requestCode == 30000) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                        return;
                    }
                }
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
        }

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        return false;
    }

}