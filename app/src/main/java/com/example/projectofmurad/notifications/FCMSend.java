package com.example.projectofmurad.notifications;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Fcm send.
 */
public class FCMSend {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAA9qIrU5w:APA91bFxWrluzt2DFIyXvykPNcyOtCA1jPXS1GAlATo_BpV1NiIb8H8GZsdeZkT8vwHbG2Navlxg_te5aKNQbF5z54YPuaP_unmzdaDIWIid4AISDG3NTNwN2rczNME9qPiFX5Agej7R";

    /**
     * The constant FCM_TAG.
     */
    public static final String FCM_TAG = "fcm";

    /**
     * The constant ADD_EVENT_TOPIC.
     */
    public final static String ADD_EVENT_TOPIC = "add_event_topic";
    /**
     * The constant EDIT_EVENT_TOPIC.
     */
    public final static String EDIT_EVENT_TOPIC = "edit_event_topic";
    /**
     * The constant DELETE_EVENT_TOPIC.
     */
    public final static String DELETE_EVENT_TOPIC = "delete_event_topic";

    /**
     * The constant KEY_SENDER_UID.
     */
    public final static String KEY_SENDER_UID = "sender_uid";
    /**
     * The constant KEY_RECEIVER_UID.
     */
    public final static String KEY_RECEIVER_UID = "receiver_uid";

    /**
     * Gets topic.
     *
     * @param type the type
     *
     * @return the topic
     */
    @NonNull
    public static String getTopic(String type) {
        return FirebaseUtils.CURRENT_GROUP_KEY + "|" + type;
    }

    private static void sendNotificationToTopic(@NonNull Context context, @NonNull CalendarEvent event, String title, int type){
        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "sending notification to server");
        Log.d(FCM_TAG, "******************************************************************************************");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String body = "";
        String msg = "";
        String text = "";

        String topic = "";

        switch (type) {
            case Utils.ADD_EVENT_NOTIFICATION_CODE:
                topic = ADD_EVENT_TOPIC;
                text = " was added";
                body = "It will start at " + event.getStartDateTime()
                        + " and end at " + event.getEndDateTime();
                break;
            case Utils.EDIT_EVENT_NOTIFICATION_CODE:
                topic = EDIT_EVENT_TOPIC;
                text = " was edited";
                body = "Now It will start at " + event.getStartDateTime()
                        + " and end at " + event.getEndDateTime()
                        + ". If you have set alarm for this event, cancel it and set new one.";
                break;
            case Utils.DELETE_EVENT_NOTIFICATION_CODE:
                topic = DELETE_EVENT_TOPIC;
                text = " was deleted";
                break;
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", "/topics/" + topic);
            JSONObject notification = new JSONObject();

            text = "Event " + msg + event.getName() + text;

            notification.put("tag", event.getPrivateId());
            notification.put("title", title);
            notification.put("body", body);

            json.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", type);
            data.put("text", text);
            data.put("color", new Gson().toJson(event.getColor()));
            data.put(Group.KEY_GROUP_KEY, event.getGroupKey());
            data.put(CalendarEvent.KEY_EVENT, event.toJson());
            data.put(KEY_SENDER_UID, FirebaseUtils.getCurrentUID());

            json.put("data", data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(FCM_TAG, "FCM " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(FCM_TAG, error.getMessage());
                            Log.d(FCM_TAG, error.getNetworkTimeMs() + "");
                            error.printStackTrace();
                            error.getCause();
                        }
                    })
            {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send notification about group.
     *
     * @param context the context
     * @param group   the group
     */
    public static void sendNotificationAboutGroup(Context context, @NonNull Group group){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(FCM_TAG, "sending notification about group");

        String body = "";
        String text = "Group " + group.getName() + " was deleted by trainer";
        String title = "Group deleted";

        String topic = group.getKey();

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", "/topics/" + topic);

            JSONObject notification = new JSONObject();
            notification.put("tag", group.getKey());
            notification.put("title", title);
            notification.put("body", body);

            json.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", Utils.GROUP_NOTIFICATION_CODE);
            data.put("text", text);
            data.put("color", new Gson().toJson(group.getColor()));
            data.put("group", group.getKey());
            data.put(KEY_SENDER_UID, FirebaseUtils.getCurrentUID());

            json.put("data", data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(FCM_TAG, "FCM " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(FCM_TAG, error.getMessage());
                            Log.d(FCM_TAG, error.getNetworkTimeMs() + "");
                            error.printStackTrace();
                            error.getCause();
                        }
                    })
            {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send notification about user.
     *
     * @param context the context
     * @param group   the group
     * @param UID     the uid
     */
    public static void sendNotificationAboutUser(Context context, @NonNull Group group, String UID){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String body = "You were removed from group " + group.getName() +
                " by madrich. All your data in this group was deleted";
        String text = "";
        String title = "Removed from group";

        String topic = group.getKey();

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", "/topics/" + topic);

            JSONObject notification = new JSONObject();
            notification.put("tag", group.getKey());
            notification.put("title", title);
            notification.put("body", body);

            json.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", Utils.GROUP_NOTIFICATION_CODE);
            data.put("text", text);
            data.put(KEY_RECEIVER_UID, UID);
            data.put("group", group.getKey());
            data.put("color", new Gson().toJson(group.getColor()));
            data.put(KEY_SENDER_UID, FirebaseUtils.getCurrentUID());

            json.put("data", data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(FCM_TAG, "FCM " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(FCM_TAG, error.getMessage());
                            Log.d(FCM_TAG, error.getNetworkTimeMs() + "");
                            error.printStackTrace();
                            error.getCause();
                        }
                    })
            {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send notifications to all users with topic.
     *
     * @param context          the context
     * @param event            the event
     * @param notificationType the notification type
     */
    public static void sendNotificationsToAllUsersWithTopic(Context context, CalendarEvent event, int notificationType){
        FirebaseUtils.getCurrentGroupName().observe((LifecycleOwner) context, groupName -> sendNotificationToTopic(context, event, groupName, notificationType));
    }
}
