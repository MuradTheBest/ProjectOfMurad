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
import com.example.projectofmurad.calendar.UtilsCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    public final static String TAG = AlarmManagerForToday.TAG;

    public final static String ACTION_SHOW_NOTIFICATION = Utils.APPLICATION_ID + "show_notification";
    public final static String ACTION_STOP_VIBRATION = Utils.APPLICATION_ID + "stop_vibration";

    public final static int ALARM_NOTIFICATION_ID = 500;

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
        Toast.makeText(context, "Vibration stopped", Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        String event_private_id = intent.getStringExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ALARM_NOTIFICATION_ID);

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);

        Utils.deleteAlarm(event_private_id, db);
    }

    
    @SuppressLint("MissingPermission")
    public void showNotification(@NonNull Context context, @NonNull Intent intent){
        //we will use vibrator first
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(6000, VibrationEffect.DEFAULT_AMPLITUDE);
        long[] timings = new long[]{0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
        VibrationEffect alarmVibrationEffect = VibrationEffect.createWaveform(timings, VibrationEffect.DEFAULT_AMPLITUDE);
//        vibrator.vibrate(vibrationEffect);
        vibrator.vibrate(timings, 1);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
/*
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        String body = intent.getStringExtra("notification_body");

        CalendarEvent event = (CalendarEvent) intent.getSerializableExtra(UtilsCalendar.KEY_EVENT);
        int color = event.getColor();

        if (event == null){
            Log.d("murad", "event is null");
            return;
        }
        else {
            Log.d("murad", "event is not null");
        }


        Intent i = new Intent(context, Calendar_Screen.class);
        i.setAction(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT);
        i.putExtra("isAlarm", true);
        i.putExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID, event.getPrivateId());
        Log.d(AlarmManagerForToday.TAG, "private id for event for alarm notification to show it is " + event.getPrivateId());
        i.putExtra(UtilsCalendar.KEY_EVENT_START_DATE_TIME, event.getStart());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d("murad", event.toString());

        int requestCode = intent.getIntExtra("requestCode", 2);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode /* Request code */, i,
                PendingIntent.FLAG_IMMUTABLE);

        /*NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification()
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Firebase notification")
                .setContentText(body)
                .setContentText("The event " + event.getName() + " will start on " + event.getStartDateTime()
                        + " and end on " + event.getEndDateTime())
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
        String notification_tag = event.getPrivateId();

        Log.d("murad", "notification_id = " + ALARM_NOTIFICATION_ID);
        Log.d("murad", "notification_tag = " + notification_tag);


        Intent intent_stop_alarm = new Intent(context, AlarmReceiver.class);
        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);
        intent_stop_alarm.putExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID, notification_tag);

        PendingIntent pintent_stop_alarm = PendingIntent.getBroadcast(context, 1, intent_stop_alarm,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action(null, "Stop", pintent_stop_alarm);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Firebase notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setSummaryText("Event alarm"))
                .setContentText("The event " + event.getName() + " will start on " + event.getStartDateTime()
                        + " and end on " + event.getEndDateTime())
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
        notificationManager.notify(notification_tag, ALARM_NOTIFICATION_ID, builder.build());
    }
}

