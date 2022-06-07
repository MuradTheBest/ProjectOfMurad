package com.example.projectofmurad.notifications;


import static com.example.projectofmurad.notifications.FCMSend.FCM_TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.DayDialog;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseNotificationPushService extends FirebaseMessagingService {
    
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "remote message from server received");
        Log.d(FCM_TAG, "******************************************************************************************");

        if (FirebaseUtils.isUserLoggedIn()){
            return;
        }

        if (remoteMessage.getData().isEmpty()){
            Log.d(FCM_TAG, "remoteMessage.getData() is empty");
            return;
        }

        String groupKey = remoteMessage.getData().get("group");

        if (groupKey == null){
            Log.d(FCM_TAG, "groupKey is empty");
            return;
        }

        if (Objects.equals(remoteMessage.getData().get(FCMSend.KEY_SENDER_UID), FirebaseUtils.getCurrentUID())) {
            Log.d(FCM_TAG, "sender is current user");
//            return;
        }

        if (!Objects.equals(remoteMessage.getData().get(FCMSend.KEY_RECEIVER_UID), FirebaseUtils.getCurrentUID())) {
            Log.d(FCM_TAG, "remoteMessage contains receiver uid however current user is not receiver");
            return;
        }

        pushNotification(remoteMessage);

        super.onMessageReceived(remoteMessage);
    }

    private void pushNotification(@NonNull RemoteMessage remoteMessage) {

        Log.d(FCM_TAG, "pushing notification");

        String colorJSON = remoteMessage.getData().get("color");
        int color = new Gson().fromJson(colorJSON, int.class);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String tag = remoteMessage.getNotification().getTag();
        String text = remoteMessage.getData().get("text");
        int type = Integer.parseInt(remoteMessage.getData().getOrDefault("type", "0"));
        String groupKey = remoteMessage.getData().get("group");

        String CHANNEL_ID = "Events Notifications";

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Remote Notification",
                NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setColorized(true)
                .setColor(color)
                .setAutoCancel(true);

        if (remoteMessage.getData().containsKey("event")){
            String eventJSON = remoteMessage.getData().get("event");
            CalendarEvent event = CalendarEvent.fromJson(eventJSON);
            notificationBuilder = pushEventNotification(groupKey, event, notificationBuilder, type);
        }
        else if (remoteMessage.getData().containsKey(FCMSend.KEY_RECEIVER_UID)){
            FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(groupKey);
            if (FirebaseUtils.CURRENT_GROUP_KEY.equals(groupKey)) {
                Log.d(FCM_TAG, "user's current group is the group that he was kicked out from ");
                startActivity(Utils.getIntentClearTop(new Intent(this, ShowGroupsScreen.class)));
            }
        }

        NotificationManagerCompat.from(this).notify(tag, type, notificationBuilder.build());
    }

    public NotificationCompat.Builder pushEventNotification(String groupKey, @NonNull CalendarEvent event, NotificationCompat.Builder notificationBuilder, int type){
        Intent intentToShowEvent = new Intent(this, MainActivity.class);
        intentToShowEvent.setAction(DayDialog.ACTION_TO_SHOW_EVENT);
        intentToShowEvent.putExtra(Group.KEY_GROUP_KEY, groupKey);
        intentToShowEvent.putExtra(CalendarEvent.KEY_EVENT_PRIVATE_ID, event.getPrivateId());
        intentToShowEvent.putExtra(CalendarEvent.KEY_EVENT_START, event.getStart());
        intentToShowEvent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pintentToShowEvent = PendingIntent.getActivity(this, 0, intentToShowEvent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionToShowEvent = new NotificationCompat.Action(null, "View event", pintentToShowEvent);

        if(type == Utils.ADD_EVENT_NOTIFICATION_CODE || type == Utils.EDIT_EVENT_NOTIFICATION_CODE){
            notificationBuilder.addAction(actionToShowEvent);
        }

        notificationBuilder.setContentIntent(pintentToShowEvent).setFullScreenIntent(pintentToShowEvent, false);

        return notificationBuilder;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

}
