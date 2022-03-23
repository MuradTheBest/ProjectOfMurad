package com.example.projectofmurad.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // chronological sequence of events....
                if (event.getActivityType() == DetectedActivity.STILL){
                    Intent i = new Intent(context, TrackingService.class);

                    if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER){

                        TrackingViewModel.activity_transition_enter.setValue(TrackingViewModel.activity_transition_enter.getValue()+1);
                        TrackingViewModel.activity_transition_exit.setValue(0);

                        if (TrackingViewModel.activity_transition_enter.getValue() == 3){

                            TrackingViewModel.activity_transition_enter.setValue(0);

                            i.setAction(TrackingService.ACTION_AUTO_PAUSE_TRACKING_SERVICE);
                        }

                    }
                    else if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT){

                        TrackingViewModel.activity_transition_exit.setValue(TrackingViewModel.activity_transition_exit.getValue()+1);
                        TrackingViewModel.activity_transition_enter.setValue(0);

                        if (TrackingViewModel.activity_transition_exit.getValue() == 3){

                            TrackingViewModel.activity_transition_exit.setValue(0);

                            i.setAction(TrackingService.ACTION_AUTO_PAUSE_TRACKING_SERVICE);
                        }
                    }

                    context.startService(i);
                }
            }
        }
    }
}