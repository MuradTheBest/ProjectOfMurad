package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.Calendar_Screen;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        //we will use vibrator first
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(6000, VibrationEffect.DEFAULT_AMPLITUDE);
        vibrator.vibrate(vibrationEffect);

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
/*
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        String body = intent.getStringExtra("notification_body");
        int color = intent.getIntExtra("notification_color", R.color.colorAccent);

        CalendarEvent event = (CalendarEvent) intent.getSerializableExtra("event");
        if (event == null){
            Log.d("murad", "event is null");
            return;
        }
        else {
            Log.d("murad", "event is not null");
        }



        Intent i = new Intent(context, Calendar_Screen.class);
        i.setAction("eventShow");
        i.putExtra("action", true);
        i.putExtra("event_private_id", event.getEvent_private_id().toString());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d("murad", event.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, i,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification()
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Firebase notification")
                .setContentText(body)
                .setContentText("The event " + event.getName() + " will start on " + event.getStart_dateTime()
                        + " and end on " + event.getEnd_dateTime())
                .setColor(color)
                .setColorized(true)
                .setAutoCancel(true)
                .setSound(alarmUri)
                .setContentIntent(pendingIntent);

        notificationHelper.getManager().notify(1, nb.build());
    }
}

