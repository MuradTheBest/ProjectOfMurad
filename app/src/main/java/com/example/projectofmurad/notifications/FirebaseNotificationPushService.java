package com.example.projectofmurad.notifications;

import static com.example.projectofmurad.notifications.FCMSend.FCM_TAG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.Calendar_Screen;
import com.example.projectofmurad.calendar.DayDialogFragmentWithRecyclerView2;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class FirebaseNotificationPushService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "remote message from server received");
        Log.d(FCM_TAG, "******************************************************************************************");


        if (remoteMessage.getData().containsKey("event")){

        }


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        int color = Integer.parseInt(remoteMessage.getData().get("color"));
        int type = Integer.parseInt(remoteMessage.getData().get("type"));

        String tag = remoteMessage.getNotification().getTag();

        //ToDo convert from json object to object and vise versa
        String eventJSON = remoteMessage.getData().get("event");


        Gson gson = new Gson();
        CalendarEvent event = gson.fromJson(eventJSON, CalendarEvent.class);

        Log.d("murad", "event json" + event.toString());

        String CHANNEL_ID = "MESSAGE";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message_Notification",
                NotificationManager.IMPORTANCE_HIGH);

        Intent intentToShowEvent = new Intent(this, Calendar_Screen.class);
        intentToShowEvent.setAction(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT);
        intentToShowEvent.putExtra("event_private_id", event.getPrivateId());
        intentToShowEvent.putExtra("action", true);
        intentToShowEvent.putExtra("start_time", event.getStart());
        intentToShowEvent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pintentToShowEvent = PendingIntent.getActivity(this, 0, intentToShowEvent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action actionToShowEvent = new NotificationCompat.Action(null, "View event", pintentToShowEvent);

        /*Intent intentToSetAlarm = new Intent();
        intentToSetAlarm.setAction(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT);
        intentToSetAlarm.putExtra("event_private_id", event.getPrivateId());

        PendingIntent pintentToSetAlarm = PendingIntent.getActivity(this, 10, intentToSetAlarm, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action actionToSetAlarm = new NotificationCompat.Action(null, "Set Alarm", pintentToSetAlarm);*/

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
//                .addAction(actionToSetAlarm)
                .addAction(actionToShowEvent)
                .setColorized(true)
                .setColor(color)
                .setAutoCancel(true)
                .setContentIntent(pintentToShowEvent);

        NotificationManagerCompat.from(this).notify(tag, type, notification.build());

        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseUtils.isUserLoggedIn()){
            FirebaseUtils.getCurrentUserDataRef().child("token").setValue(s);
        }
    }

    public void createSetAlarmDialog(){

    }
}
