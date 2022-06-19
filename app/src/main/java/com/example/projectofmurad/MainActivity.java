package com.example.projectofmurad;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.CalendarFragment;
import com.example.projectofmurad.calendar.DayDialog;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.notifications.AlarmReceiver;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.example.projectofmurad.tracking.TrackingService;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private View containerView;
    private BottomNavigationView bottomNavigationView;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        containerView = findViewById(R.id.fragmentContainerView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        navController.addOnDestinationChangedListener(this);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        Intent gotten_intent = getIntent();

        if (gotten_intent.getAction() != null){
            checkIntent(gotten_intent);
        }

    }

    /**
     * Move to tracking fragment.
     */
    public void moveToTrackingFragment() {
        bottomNavigationView.setSelectedItemId(R.id.tracking_Fragment);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    /**
     * Check intent.
     *
     * @param gotten_intent the gotten intent
     */
    public void checkIntent(@NonNull Intent gotten_intent){
        switch (gotten_intent.getAction()) {
            case TrackingService.ACTION_MOVE_TO_TRACKING_FRAGMENT:
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

        Log.d(MyAlarmManager.TAG, "===============================================");
        Log.d(MyAlarmManager.TAG, "event_private_id = " + event_private_id);
        Log.d(MyAlarmManager.TAG, "===============================================");

        String groupKey = intent.getStringExtra(Group.KEY_GROUP_KEY);
        FirebaseUtils.changeGroup(this, groupKey);

        long start = intent.getLongExtra(CalendarEvent.KEY_EVENT_START, Calendar.getInstance().getTimeInMillis());
        LocalDate goTo = CalendarEvent.getDate(start);
        Log.d("murad", "start is " + new Date(start));


        Log.d(Utils.LOG_TAG, "goTo is " + CalendarUtils.DateToTextOnline(goTo));

        boolean isAlarm = intent.getBooleanExtra("isAlarm", false);
        Log.d(MyAlarmManager.TAG, "isAlarm = " + isAlarm);

        if(isAlarm) {
            Log.d(MyAlarmManager.TAG, "sending intent to alarm receiver to stop vibration");
            Intent intent_stop_alarm = new Intent(this, AlarmReceiver.class);
            intent_stop_alarm.setAction(AlarmReceiver.ACTION_STOP_VIBRATION);
            intent_stop_alarm.putExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID, event_private_id);

            sendBroadcast(intent_stop_alarm);
        }

        mainViewModel.setEventPrivateId(event_private_id);
        mainViewModel.setEventDate(goTo);

        bottomNavigationView.setSelectedItemId(R.id.calendar_Fragment);
    }

    /**
     * Animate.
     *
     * @param viewGroup the view group
     * @param gravity   the gravity
     * @param duration  the duration
     */
    public void animate(ViewGroup viewGroup, int gravity, int duration){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
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
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestinationChanged(@NonNull NavController navController,
                                     @NonNull NavDestination navDestination,
                                     @Nullable Bundle bundle) {

        Handler handler = new Handler();

        if(navDestination.getId() == R.id.tracking_Fragment){
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