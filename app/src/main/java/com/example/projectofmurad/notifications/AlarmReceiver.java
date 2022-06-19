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
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.DayDialog;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.utils.Utils;

/**
 * The type Alarm receiver.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * The constant ACTION_STOP_VIBRATION.
     */
    public final static String ACTION_STOP_VIBRATION = Utils.APPLICATION_ID + "stop_vibration";

    /**
     * The constant ALARM_NOTIFICATION_ID.
     */
    public final static int ALARM_NOTIFICATION_ID = 500;

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

    /**
     * Stop vibration.
     *
     * @param context the context
     * @param intent  the intent
     */
    @SuppressLint("MissingPermission")
    public void stopVibration(@NonNull Context context, @NonNull Intent intent){

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        String event_private_id = intent.getStringExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        StatusBarNotification[] alarmList = notificationManager.getActiveNotifications();

        for (StatusBarNotification statusBarNotification : alarmList) {
            if (statusBarNotification.getId() == ALARM_NOTIFICATION_ID) {
                String tag = statusBarNotification.getTag();
                notificationManager.cancel(tag, ALARM_NOTIFICATION_ID);
            }
        }

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);

        Utils.deleteAlarm(event_private_id, db);
    }

    /**
     * Show notification.
     *
     * @param context the context
     * @param intent  the intent
     */
    @SuppressLint({"MissingPermission", "NotificationTrampoline"})
    public void showNotification(@NonNull Context context, @NonNull Intent intent){

        long[] timings = new long[]{0, 1000, 1000, 1000, 1000, 1000, 1000,
                1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
                1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
                1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        String body = intent.getStringExtra("notification_body");

        CalendarEvent event = (CalendarEvent) intent.getSerializableExtra(CalendarEvent.KEY_EVENT);
        int color = event.getColor();

        Log.d("murad", "event is not null");

        Intent i = new Intent(context, MainActivity.class);
        i.setAction(DayDialog.ACTION_TO_SHOW_EVENT);
        i.putExtra("isAlarm", true);
        i.putExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID, event.getPrivateId());
        i.putExtra(CalendarEvent.KEY_EVENT_START, event.getStart());
        i.putExtra(Group.KEY_GROUP_KEY, event.getGroupKey());
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("murad", event.toString());

        int requestCode = intent.getIntExtra("requestCode", 2);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode /* Request code */, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String notification_tag = event.getPrivateId();

        Intent intent_stop_alarm = new Intent(context, AlarmReceiver.class);
        intent_stop_alarm.setAction(ACTION_STOP_VIBRATION);
        intent_stop_alarm.putExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID, notification_tag);

        PendingIntent pintent_stop_alarm = PendingIntent.getBroadcast(context, 1, intent_stop_alarm,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action action = new NotificationCompat.Action(null, "Stop", pintent_stop_alarm);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.alarm_icon)
                .setContentTitle("Alarm for event")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setSummaryText("Event alarm"))
                .setColor(color)
                .setColorized(true)
                .setAutoCancel(true)
                .addAction(action)
                .setVibrate(timings)
                .setLights(color, 3000, 3000)
                .setSound(alarmUri)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, false)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_ALARM);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "alarm_channel_id_2";
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Channel for alarms",
                NotificationManager.IMPORTANCE_HIGH);

        channel.enableVibration(true);
        channel.setVibrationPattern(timings);
        channel.enableLights(true);
        channel.setLightColor(color);

        builder.setChannelId(channelId);

        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(notification_tag, ALARM_NOTIFICATION_ID, builder.build());
    }
}

