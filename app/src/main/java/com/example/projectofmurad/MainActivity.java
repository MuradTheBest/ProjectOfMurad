package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectofmurad.calendar.UtilsCalendar;
import com.example.projectofmurad.tracking.TrackingService;
import com.example.projectofmurad.tracking.Tracking_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    View containerView;

    BottomNavigationView bottomNavigationView;

    int LOCATION_REQUEST_CODE = 10001;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setShowHideAnimationEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        containerView = findViewById(R.id.fragment);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Handler handler = new Handler();

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController navController,
                                                     @NonNull NavDestination navDestination,
                                                     @Nullable Bundle bundle) {

                        getSupportActionBar().show();

                        if(navDestination.getId() == R.id.tracking_Fragment){
                            getSupportActionBar().hide();
                            bottomNavigationView.setVisibility(View.GONE);

                            animate(bottomNavigationView, Gravity.BOTTOM, 300);
                            handler.postDelayed(() -> animate((ViewGroup) containerView, Gravity.BOTTOM, 300), 400);

                        }
                        else if (bottomNavigationView.getVisibility() == View.GONE){
                            bottomNavigationView.setVisibility(View.VISIBLE);

                            animate(bottomNavigationView, Gravity.TOP, 300);
                            handler.postDelayed(() -> animate((ViewGroup) containerView, Gravity.BOTTOM, 300), 400);
                        }
                    }
                });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        UtilsCalendar.setLocale();

        Log.d(LOG_TAG, "Subscribing to weather topic");

        MyFirebaseMessagingService.getToken();
        // [START subscribe_topics]
/*        FirebaseMessaging.getInstance().subscribeToTopic("Adding Event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Notification was successfully sent";
                        if (!task.isSuccessful()) {
                            msg = "Notification sending failed";
                        }
                        Log.d(LOG_TAG, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });*/
        // [END subscribe_topics]

//        AlarmManagerForToday.check(MainActivity.this);

        SQLiteDatabase db = openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Utils.createAllTables(db);

        Intent gotten_intent = getIntent();

        if (gotten_intent.getAction() != null
                && gotten_intent.getAction().equals(TrackingService.ACTION_MOVE_TO_TRACKING_FRAGMENT)){

            Log.d("murad", "Going to Tracking_Fragment");

            bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment currentFragment = new BlankFragment();
        Fragment newFragment = new BlankFragment();
        String TAG = "blankFragment";

        switch (item.getItemId()) {
            case R.id.blankFragment:
                TAG = "blankFragment";
                newFragment = new BlankFragment();
                break;
            case R.id.tables_Fragment:
                TAG = "tables_Fragment";
                newFragment = new Tables_Fragment();
                break;
            case R.id.graph_Fragment:
                TAG = "graph_Fragment";
                newFragment = new Graph_Fragment();
                break;
            case R.id.chat_Fragment:
                TAG = "chat_Fragment";
                newFragment = new Chat_Fragment();
                break;
            case R.id.tracking_Fragment:
                TAG = "tracking_Fragment";
                newFragment = new Tracking_Fragment();
//                getSupportFragmentManager().beginTransaction().hide(currentFragment);
                break;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);

        if (fragment == null) {
            fragment = newFragment;
        }

        /*if (FirebaseUtils.getCurrentUID().equals("AiqKQM3H8jhavJCFU3B4NmLa8ea2") && LOG_TAG.equals("blankFragment")){
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.bottomNavigationView, fragment, LOG_TAG);
        }*/
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavigationView, fragment, TAG);

        return true;
    }

    public void animate(ViewGroup viewGroup, int gravity, int duration){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());
        if (gravity == Gravity.TOP) {
            Slide slide = new Slide(Gravity.TOP);
            slide.setDuration(duration);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }
        else if (gravity == 0) {
            AutoTransition slide = new AutoTransition();
            slide.setDuration(duration);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }
        else if (gravity == Gravity.BOTTOM) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setDuration(duration);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }


    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getVisibility() == View.GONE){
            bottomNavigationView.setVisibility(View.VISIBLE);

            animate(bottomNavigationView, Gravity.TOP, 300);
//            animate((ViewGroup) containerView, Gravity.TOP, 300);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(LOG_TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, LOCATION_REQUEST_CODE+1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                Log.d(LOG_TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                Log.d(LOG_TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, LOCATION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
//                getLastLocation();
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.bottomNavigationView, new Tracking_Fragment(), LOG_TAG);
            }
            else {
                //Permission not granted
            }
        }
    }
}