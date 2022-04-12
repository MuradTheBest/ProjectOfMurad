package com.example.projectofmurad.notifications;

import static com.example.projectofmurad.notifications.FCMSend.FCM_TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.DayDialog;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Date;

public class FirebaseNotificationPushService extends FirebaseMessagingService {

    SharedPreferences sp;
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "remote message from server received");
        Log.d(FCM_TAG, "******************************************************************************************");

        if (remoteMessage.getData().containsKey(FCMSend.KEY_SENDER_UID) && FirebaseUtils.getCurrentUID() != null
                && remoteMessage.getData().get(FCMSend.KEY_SENDER_UID).equals(FirebaseUtils.getCurrentUID())){
            Log.d(Utils.LOG_TAG, "this is the user who send the notification");
//            return;
        }

        if (!remoteMessage.getData().containsKey(UtilsCalendar.KEY_EVENT)){
            return;
        }

        FirebaseUtils.getCurrentUserData(userData -> checkSubscriptions(userData, remoteMessage));



        super.onMessageReceived(remoteMessage);
    }

    
    private void checkSubscriptions(@NonNull UserData userData, @NonNull RemoteMessage remoteMessage) {

        //ToDo convert from json object to object and vise versa
        String eventJSON = remoteMessage.getData().get("event");

        Gson gson = new Gson();
        CalendarEvent event = gson.fromJson(eventJSON, CalendarEvent.class);

        int color = event.getColor();
        int type = Integer.parseInt(remoteMessage.getData().get("type"));

        sp = getSharedPreferences("savedData", MODE_PRIVATE);

        boolean subscribedToAutoAlarmSet = sp.getBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_SET, false);
        boolean subscribedToAutoAlarmMove = sp.getBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_MOVE, false);
        long before = sp.getLong("before", 0);

        switch (type) {
            case Utils.ADD_EVENT_NOTIFICATION_CODE:
                if (subscribedToAutoAlarmSet) {
                    AlarmManagerForToday.addAlarm(this, event, before);
                }
                break;
            case Utils.EDIT_EVENT_NOTIFICATION_CODE:
                if (subscribedToAutoAlarmMove) {
                    moveAlarm(event, before);
                }
                else {
                    checkIfAlarmWasSet(event);
                }
                break;
            case Utils.DELETE_EVENT_NOTIFICATION_CODE:
                checkIfAlarmWasSet(event);
                break;
        }


        if ((!userData.isSubscribedToAddEvent() && type == Utils.ADD_EVENT_NOTIFICATION_CODE)
                || (!userData.isSubscribedToEditEvent() && type == Utils.EDIT_EVENT_NOTIFICATION_CODE)
                || (!userData.isSubscribedToDeleteEvent() && type == Utils.DELETE_EVENT_NOTIFICATION_CODE)){
            return;
        }


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();


        String tag = remoteMessage.getNotification().getTag();

        Log.d("murad", event.toString());

        String CHANNEL_ID = "MESSAGE";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message_Notification",
                NotificationManager.IMPORTANCE_HIGH);

        Intent intentToShowEvent = new Intent(this, MainActivity.class);
        intentToShowEvent.setAction(DayDialog.ACTION_TO_SHOW_EVENT);
        intentToShowEvent.putExtra(UtilsCalendar.KEY_EVENT_PRIVATE_ID, event.getPrivateId());
        intentToShowEvent.putExtra(UtilsCalendar.KEY_EVENT_START_DATE_TIME, event.getStart());
        Log.d("murad", "event.getStart() is " + new Date(event.getStart()));
        intentToShowEvent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pintentToShowEvent = PendingIntent.getActivity(this, 0, intentToShowEvent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionToShowEvent = new NotificationCompat.Action(null, "View event", pintentToShowEvent);

        /*Intent intentToSetAlarm = new Intent();
        intentToSetAlarm.setAction(DayDialog.ACTION_TO_SHOW_EVENT);
        intentToSetAlarm.putExtra("event_private_id", event.getPrivateId());

        PendingIntent pintentToSetAlarm = PendingIntent.getActivity(this, 10, intentToSetAlarm, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action actionToSetAlarm = new NotificationCompat.Action(null, "Set Alarm", pintentToSetAlarm);*/

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
//                .addAction(actionToSetAlarm)
                .setColorized(true)
                .setColor(color)
                .setAutoCancel(true)
                .setContentIntent(pintentToShowEvent)
                .setFullScreenIntent(pintentToShowEvent, false);

        if(type == Utils.ADD_EVENT_NOTIFICATION_CODE || type == Utils.EDIT_EVENT_NOTIFICATION_CODE){
            notification.addAction(actionToShowEvent);
        }

        NotificationManagerCompat.from(this).notify(tag, type, notification.build());


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseUtils.isUserLoggedIn()){
            FirebaseUtils.getCurrentUserDataRef().child("tokens").push().setValue(s);
            FirebaseUtils.getCurrentUserDataRef().child("tokens").addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot,
                                                 @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot,
                                                   @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot,
                                                 @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void moveAlarm(@NonNull CalendarEvent event, long before) {
        SQLiteDatabase db = Utils.openOrCreateDatabase(this);

        int alarm_id = Utils.alarmIdByEvent(event.getPrivateId(), db);

        if (alarm_id > 0){
            AlarmManagerForToday.cancelAlarm(this, event);
            AlarmManagerForToday.addAlarm(this, event, before);
        }
    }

    public void checkIfAlarmWasSet(@NonNull CalendarEvent event){
        AlarmManagerForToday.cancelAlarm(this, event);
    }

}
