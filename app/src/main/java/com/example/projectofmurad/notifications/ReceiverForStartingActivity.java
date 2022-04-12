package com.example.projectofmurad.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.Utils;

public class ReceiverForStartingActivity extends BroadcastReceiver {

    public final static String ACTION_TO_START_ACTIVITY_FROM_NOTIFICATION = Utils.APPLICATION_ID + ".action_to_start_activity_from_notification";

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Intent i = new Intent(context, MainActivity.class);
        i.setAction(intent.getAction());
        i.putExtras(intent.getExtras());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);
    }


}