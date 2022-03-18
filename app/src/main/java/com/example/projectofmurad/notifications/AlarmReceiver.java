package com.example.projectofmurad.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.Calendar_Screen;
import com.example.projectofmurad.calendar.DayDialogFragmentWithRecyclerView2;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String ACTION_SHOW_NOTIFICATION = Utils.APPLICATION_ID + "show_notification";
    public final static String ACTION_STOP_VIBRATION = Utils.APPLICATION_ID + "stop_vibration";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() != null){
            if (intent.getAction().equals(ACTION_STOP_VIBRATION)){
                stopVibration(context, intent);
            }
        }
        else{
            showNotification(context, intent);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopVibration(@NonNull Context context, @NonNull Intent intent){
        Toast.makeText(context, "Stopping vibration", Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        int notification_id = intent.getIntExtra("notification_id", 0);
        String notification_tag = intent.getStringExtra("notification_tag");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_tag, notification_id);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public void showNotification(@NonNull Context context, @NonNull Intent intent){
        //we will use vibrator first
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(6000, VibrationEffect.DEFAULT_AMPLITUDE);
        long[] timings = new long[]{0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        VibrationEffect alarmVibrationEffect = VibrationEffect.createWaveform(timings, VibrationEffect.DEFAULT_AMPLITUDE);
//        vibrator.vibrate(vibrationEffect);
        vibrator.vibrate(timings, 1);

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
        i.setAction(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT);
        i.putExtra("action", true);
        i.putExtra("event_private_id", event.getPrivateId());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d("murad", event.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, i,
                PendingIntent.FLAG_IMMUTABLE);

        /*NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification()
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Firebase notification")
                .setContentText(body)
                .setContentText("The event " + event.getName() + " will start on " + event.getStart_dateTime()
                        + " and end on " + event.getEnd_dateTime())
                .setColor(color)
                .setColorized(true)
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(null, "Stop", null))
                .setVibrate(timings)
                .setSound(alarmUri)
                .setContentIntent(pendingIntent);

        notificationHelper.getManager().notify(1, nb.build());*/

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
//        int notification_id = Utils.getNotificationId(db);
        int notification_id = 100;
        String notification_tag = event.getPrivateId();

        Log.d("murad", "notification_id = " + notification_id);
        Log.d("murad", "notification_tag = " + notification_tag);


        Intent intent_stop_alarm = new Intent(context, AlarmReceiver.class);
        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);
        intent_stop_alarm.putExtra("notification_id", notification_id);
        intent_stop_alarm.putExtra("notification_tag", notification_tag);

        PendingIntent pintent_stop_alarm = PendingIntent.getBroadcast(context, 1, intent_stop_alarm,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action(null, "Stop", pintent_stop_alarm);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Firebase notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setSummaryText("Event alarm"))
                .setContentText("The event " + event.getName() + " will start on " + event.getStart_dateTime()
                        + " and end on " + event.getEnd_dateTime())
                .setColor(color)
                .setColorized(true)
                .setAutoCancel(true)
                .addAction(action)
                .setVibrate(timings)
                .setSound(alarmUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_ALARM);


        NotificationManager notificationManager =
                (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "CHANNEL_ID";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        builder.setChannelId(channelId);

        channel.setVibrationPattern(timings);

// notificationId is a unique int for each notification that you must define

//        notificationManager.notify(notification_id, builder.build());
        notificationManager.notify(notification_tag, notification_id, builder.build());
    }
}

