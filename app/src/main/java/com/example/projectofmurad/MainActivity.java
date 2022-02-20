package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

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

import com.example.projectofmurad.calendar.Utils_Calendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

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


        Utils_Calendar.setLocale();

        Log.d(TAG, "Subscribing to weather topic");

        MyFirebaseMessagingService.getToken();
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("Adding Event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Notification was successfully sent";
                        if (!task.isSuccessful()) {
                            msg = "Notification sending failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END subscribe_topics]

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });

        AlarmManagerForToday.check(MainActivity.this);
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
        currentFragment = fragment;

        if (FirebaseUtils.getCurrentUID().equals("AiqKQM3H8jhavJCFU3B4NmLa8ea2") && TAG.equals("blankFragment")){
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.bottomNavigationView, fragment, TAG);
        }

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
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, LOCATION_REQUEST_CODE+1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
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
                        R.id.bottomNavigationView, new Tracking_Fragment(), TAG);
            }
            else {
                //Permission not granted
            }
        }
    }
}