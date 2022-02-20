package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tracking_Fragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            LatLng sydney = new LatLng(-34, 151);
            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    private GoogleMap map;
    GoogleMapOptions options;

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

    private Map<String, Marker> markers;
    private Map<String, Marker> last_markers;

    private DatabaseReference usersLocations;

//    private SwitchCompat switch_last_location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {

        }

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        /*Intent i = new Intent(getActivity(), TrackingService.class);
        getActivity().startService(i);

        requireActivity().startService(new Intent(getContext(), TrackingService.class));
//        requireActivity().startForegroundService(new Intent(getContext(), TrackingService.class));
        requireActivity().startService(new Intent(getContext(), mService.class));*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        requireActivity().getActionBar().hide();

        markers = new HashMap<>();
        last_markers = new HashMap<>();

        /*switch_last_location = view.findViewById(R.id.switch_last_location);
        switch_last_location.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        for (Marker marker : last_markers.values()){
                            marker.setAlpha(0.5f);
                            marker.setVisible(isChecked);
                        }
                    }
                });*/

        options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_HYBRID);
        options.compassEnabled(true);
        options.mapToolbarEnabled(true);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        fusedLocationProviderClient.requestLocationUpdates(
                new LocationRequest().setWaitForAccurateLocation(true).setInterval(2000),
                locationCallback, Looper.myLooper());


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
        map = googleMap;
        /*LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        if (checkPermissions()){
            map.setMyLocationEnabled(true);

            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            getMyLocation();
        }

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        usersLocations = FirebaseUtils.usersDatabase;

        //ToDo track only users that attend to this training
        usersLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //ToDo to not trigger users' locations' markers' reload when current user's location changed
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists() && !FirebaseUtils.isCurrentUID(data.getKey())){

                        String UID = data.child("uid").getValue().toString();



                        if (data.hasChild("latitude") && data.hasChild("longitude") && data.hasChild("profile_picture")){
                            String username = Objects.requireNonNull(
                                    data.child("username").getValue()).toString();

                            String profile_picture = Objects.requireNonNull(
                                    data.child("profile_picture").getValue()).toString();

                            double latitude = data.child("latitude").getValue(double.class);
                            double longitude = data.child("longitude").getValue(double.class);

                            LatLng latLng = new LatLng(latitude, longitude);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title(username)
                                    .flat(true)
                                    .zIndex(0)
                                    /*.snippet(username)*/;

                            Glide.with(requireContext())
                                    .asBitmap()
                                    .load(profile_picture)
                                    .dontTransform()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource,
                                                                    @Nullable Transition<? super Bitmap> transition) {

                                            if (resource != null){
                                                final float scale = getContext().getResources().getDisplayMetrics().density;
                                                int pixels = (int) (40 * scale + 0.5f);
                                                Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);
                                                if (bitmap == null){
                                                    Toast.makeText(requireContext(), "Bitmap is null", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Bitmap is null");
                                                }
                                                else {
                                                    Log.d(TAG, "Bitmap is not null");
                                                }
                                                if(bitmap != null){
                                                    bitmap = getBitmapClippedCircle(bitmap);
                                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                                    if (data.hasChild("locationAvailable") && !data.child("locationAvailable").getValue(boolean.class)){
                                                        markerOptions.alpha(0.5f);
                                                    }

                                                    Marker old_marker = markers.get(UID);

                                                    if (old_marker != null){
//                            Log.d(TAG, "Old marker's tag is " + old_marker.getTag().toString());
//                            last_markers.put(UID, old_marker);
                                                        old_marker.remove();
                                                        Log.d(TAG, "Old marker is successfully removed");
                                                    }

                                                    Marker marker = map.addMarker(markerOptions);
                                                    assert marker != null;
                                                    marker.showInfoWindow();

                                                    marker.setTag(UID);

                                                    markers.put(UID, marker);
                                                }
                                            }

                                        }


                                    });


                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        map.setOnMarkerClickListener(this);
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
/*
    @Override
    public void onDestroy() {

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

        super.onDestroy();

    }

    @Override
    public void onDestroyView() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        FirebaseUtils.getCurrentUserDataRef().child("latitude").removeValue();
        FirebaseUtils.getCurrentUserDataRef().child("longitude").removeValue();

        super.onDestroyView();
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
            Log.d(TAG, "Permission is granted");
        } else {
            Log.d(TAG, "Permission is not granted");
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    public boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission is granted");
            return true;
        } else {
            Log.d(TAG, "Permission is not granted");
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission is granted");
            return true;
        } else {
            Log.d(TAG, "Permission is not granted");
            askLocationPermission();
        }

        return false;
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Asking for the permission");
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }

            requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10000);

        }
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d(TAG, "Permission granted");
                map.setMyLocationEnabled(true);

                map.setOnMyLocationButtonClickListener(this);
                map.setOnMyLocationClickListener(this);
                getMyLocation();
                onMapReady(map);
            }
            else {
                //Permission not granted
            }
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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}