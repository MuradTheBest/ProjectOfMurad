package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.content.Intent;
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
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.home.HomeFragment;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.notifications.AlarmReceiver;
import com.example.projectofmurad.tracking.Location;
import com.example.projectofmurad.tracking.TrackingService;
import com.example.projectofmurad.tracking.Tracking_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends MyActivity implements NavController.OnDestinationChangedListener {

    private View containerView;
    private BottomNavigationView bottomNavigationView;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, FirebaseUtils.CURRENT_GROUP_KEY);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        containerView = findViewById(R.id.fragmentContainerView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        navController.addOnDestinationChangedListener(this);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        CalendarUtils.setLocale();

        SQLiteDatabase db = Utils.openOrCreateDatabase(this);
        Utils.createAllTables(db);

        Intent gotten_intent = getIntent();

        if (gotten_intent.getAction() != null){
            checkIntent(gotten_intent);
        }

    }

    public void moveToTrackingFragment() {
        bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(LOG_TAG, "MainActivity got NewIntent");

        checkIntent(intent);
    }

    public void checkIntent(@NonNull Intent gotten_intent){
        switch (gotten_intent.getAction()) {
            case TrackingService.ACTION_MOVE_TO_TRACKING_FRAGMENT:
                Log.d("murad", "Going to Tracking_Fragment");

                if (gotten_intent.getExtras() != null){
                    double latitude = gotten_intent.getDoubleExtra("latitude", 0);
                    double longitude = gotten_intent.getDoubleExtra("longitude", 0);

                    Location location = new Location(latitude, longitude);

                    mainViewModel.setLocation(location);

/*                        List<Location> locations = new ArrayList<>();
                        locations.add(new Location(36.1452, 48.63320));
                        locations.add(new Location(50.1452, 20.63320));

                        mainViewModel.setLocations(locations);*/
                }

                moveToTrackingFragment();
                break;
            case Tracking_Fragment.ACTION_MOVE_TO_TRACKING_FRAGMENT_TO_SHOW_TRACK:
                Log.d("murad", "Going to Tracking_Fragment");

                if (gotten_intent.getExtras() != null){
                    ArrayList<Location> locations = gotten_intent.getParcelableArrayListExtra("locations");

                    mainViewModel.setLocations(locations);
                }

                moveToTrackingFragment();
                break;
            case CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT:

                Log.d("murad", "Going to CalendarFragment");

                mainViewModel.setEventDate(LocalDate.now());

                bottomNavigationView.setSelectedItemId(R.id.calendar_Fragment);

                break;
            case DayDialog.ACTION_TO_SHOW_EVENT:
                showEvent(gotten_intent);
                break;
        }
    }

    private void showEvent(@NonNull Intent intent){

        if (intent.getExtras() == null
                || intent.getAction() == null
                || !intent.getAction().equals(DayDialog.ACTION_TO_SHOW_EVENT)){
            return;
        }


        String event_private_id = intent.getStringExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID);

        Log.d("murad", "===============================================");
        Log.d("murad", "event_private_id = " + event_private_id);
        Log.d("murad", "===============================================");

        Log.d(AlarmManagerForToday.TAG, "===============================================");
        Log.d(AlarmManagerForToday.TAG, "event_private_id = " + event_private_id);
        Log.d(AlarmManagerForToday.TAG, "===============================================");

        String groupKey = intent.getStringExtra(Group.KEY_GROUP_KEY);
        FirebaseUtils.changeGroup(this, groupKey);

        long start = intent.getLongExtra(CalendarEvent.KEY_EVENT_START, Calendar.getInstance().getTimeInMillis());
        LocalDate goTo = CalendarEvent.getDate(start);
        Log.d("murad", "start is " + new Date(start));


        Log.d(Utils.LOG_TAG, "goTo is " + CalendarUtils.DateToTextOnline(goTo));

        boolean isAlarm = intent.getBooleanExtra("isAlarm", false);
        Log.d(AlarmManagerForToday.TAG, "isAlarm = " + isAlarm);

        if(isAlarm){
            Log.d(AlarmManagerForToday.TAG, "sending intent to alarm receiver to stop vibration");
            Intent intent_stop_alarm = new Intent(this, AlarmReceiver.class);
            intent_stop_alarm.setAction(AlarmReceiver.ACTION_STOP_VIBRATION);
            intent_stop_alarm.putExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID, event_private_id);

            sendBroadcast(intent_stop_alarm);
        }

        mainViewModel.setEventPrivateId(event_private_id);
        mainViewModel.setEventDate(goTo);

        bottomNavigationView.setSelectedItemId(R.id.calendar_Fragment);
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
        if (bottomNavigationView.getVisibility() == View.GONE) {
            bottomNavigationView.setVisibility(View.VISIBLE);

            animate(bottomNavigationView, Gravity.TOP, 300);
//            animate((ViewGroup) containerView, Gravity.TOP, 300);
        }
        else if (bottomNavigationView.getSelectedItemId() == R.id.blankFragment) {
            mainViewModel.setScrollUp(true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController,
                                     @NonNull NavDestination navDestination,
                                     @Nullable Bundle bundle) {


        Fragment newFragment = new HomeFragment();
        String TAG = "blankFragment";

        switch (navDestination.getId()){
            case R.id.blankFragment:
                TAG = "blankFragment";
                newFragment = new HomeFragment();
//                getSupportActionBar().setTitle("ProjectOfMurad");
                break;
            case R.id.graph_Fragment:
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

        }


        Handler handler = new Handler();

//        getSupportActionBar().show();

        if(navDestination.getId() == R.id.tracking_Fragment){
//            getSupportActionBar().hide();
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