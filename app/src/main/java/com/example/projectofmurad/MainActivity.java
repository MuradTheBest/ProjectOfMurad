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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.CalendarFragment;
import com.example.projectofmurad.calendar.DayDialog;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.notifications.AlarmReceiver;
import com.example.projectofmurad.tracking.TrackingService;
import com.example.projectofmurad.tracking.Tracking_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    View containerView;

    BottomNavigationView bottomNavigationView;

    int LOCATION_REQUEST_CODE = 10001;

    private NavController navController;

    private MainViewModel mainViewModel;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setShowHideAnimationEnabled(true);


        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        containerView = findViewById(R.id.fragment);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        navController = Navigation.findNavController(this, R.id.fragment);
        navController.addOnDestinationChangedListener(this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        UtilsCalendar.setLocale();

        SQLiteDatabase db = openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Utils.createAllTables(db);

        Intent gotten_intent = getIntent();

        if (gotten_intent.getAction() != null){

            switch (gotten_intent.getAction()) {
                case TrackingService.ACTION_MOVE_TO_TRACKING_FRAGMENT:
                    Log.d("murad", "Going to Tracking_Fragment");

                    moveToTrackingFragment();
                    break;
                case CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT:

                    Log.d("murad", "Going to CalendarFragment");

                    int day = gotten_intent.getIntExtra("day", LocalDate.now().getDayOfMonth());
                    int month = gotten_intent.getIntExtra("month", LocalDate.now().getMonthValue());
                    int year = gotten_intent.getIntExtra("year", LocalDate.now().getYear());

//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, CalendarFragment.newInstance(goTo)).commit();


                    Bundle args = new Bundle();
                    args.putInt(CalendarFragment.SELECTED_DATE_DAY, day);
                    args.putInt(CalendarFragment.SELECTED_DATE_MONTH, month);
                    args.putInt(CalendarFragment.SELECTED_DATE_YEAR, year);

//                    Navigation.findNavController(this, R.id.fragment).navigate(R.id.calendar_Fragment, args);

                    mainViewModel.setEventDate(LocalDate.now());

                    bottomNavigationView.setSelectedItemId(R.id.calendar_Fragment);

                    break;
                case DayDialog.ACTION_TO_SHOW_EVENT:
                    showEvent(gotten_intent);
                    break;
            }
        }

    }

    @Override
    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        Log.d(LOG_TAG, "MainActivity got NewIntent");

        showEvent(intent);

    }

    private void showEvent(@NonNull Intent intent){

        if (intent.getExtras() == null || intent.getAction() == null || !intent.getAction().equals(
                DayDialog.ACTION_TO_SHOW_EVENT)){
            return;
        }

        String event_private_id = intent.getStringExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID);

        Log.d("murad", "===============================================");
        Log.d("murad", "event_private_id = " + event_private_id);
        Log.d("murad", "===============================================");

        Log.d(AlarmManagerForToday.TAG, "===============================================");
        Log.d(AlarmManagerForToday.TAG, "event_private_id = " + event_private_id);
        Log.d(AlarmManagerForToday.TAG, "===============================================");

        long start = intent.getLongExtra(UtilsCalendar.KEY_EVENT_START_DATE_TIME, Calendar.getInstance().getTimeInMillis());
        LocalDate goTo = CalendarEvent.getDate(start);
        Log.d("murad", "start is " + new Date(start));


        Log.d(Utils.LOG_TAG, "goTo is " + UtilsCalendar.DateToTextOnline(goTo));

        boolean isAlarm = intent.getBooleanExtra("isAlarm", false);
        Log.d(AlarmManagerForToday.TAG, "isAlarm = " + isAlarm);

        if(isAlarm){
            Log.d(AlarmManagerForToday.TAG, "sending intent to alarm receiver to stop vibration");
            Intent intent_stop_alarm = new Intent(this, AlarmReceiver.class);
            intent_stop_alarm.setAction(AlarmReceiver.ACTION_STOP_VIBRATION);
            intent_stop_alarm.putExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID, event_private_id);

            sendBroadcast(intent_stop_alarm);
        }

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, CalendarFragment.newInstance(goTo, event_private_id)).commit();

        Bundle args = new Bundle();
        args.putInt(CalendarFragment.SELECTED_DATE_DAY, goTo.getDayOfMonth());
        args.putInt(CalendarFragment.SELECTED_DATE_MONTH, goTo.getMonthValue());
        args.putInt(CalendarFragment.SELECTED_DATE_YEAR, goTo.getYear());

        args.putString(CalendarFragment.EVENT_TO_SHOW_PRIVATE_ID, event_private_id);

//            Navigation.findNavController(this, R.id.fragment).navigate(R.id.calendar_Fragment, args);

        mainViewModel.setEventPrivateId(event_private_id);
        mainViewModel.setEventDate(goTo);

        bottomNavigationView.setSelectedItemId(R.id.calendar_Fragment);
    }

    public void moveToTrackingFragment(){
        bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onDestinationChanged(@NonNull NavController navController,
                                     @NonNull NavDestination navDestination,
                                     @Nullable Bundle bundle) {


        Fragment newFragment = new BlankFragment();
        String TAG = "blankFragment";

        switch (navDestination.getId()){
            case R.id.blankFragment:
                TAG = "blankFragment";
                newFragment = new BlankFragment();
//                getSupportActionBar().setTitle("ProjectOfMurad");
                break;
            case R.id.tables_Fragment:
//                getSupportActionBar().setTitle("Tables");
                break;
/*            case R.id.graph_Fragment:
                TAG = "graph_Fragment";
                newFragment = new Graph_Fragment();
                break;*/
            case R.id.preferences_Fragment:
//                getSupportActionBar().setTitle("Preferences");
                break;
            case R.id.calendar_Fragment:
//                getSupportActionBar().setTitle("Chat");
                break;
            case R.id.tracking_Fragment:
//                getSupportActionBar().setTitle("Tracking");
                break;
        }

        if (TAG.equals("blankFragment")){

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
            if (fragment == null){
                fragment = newFragment;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment);
        }


        Handler handler = new Handler();

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
            handler.post(() -> animate((ViewGroup) containerView, Gravity.BOTTOM, 300));
        }
    }
}