package com.example.projectofmurad.notifications;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FCMSend {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAA9qIrU5w:APA91bFxWrluzt2DFIyXvykPNcyOtCA1jPXS1GAlATo_BpV1NiIb8H8GZsdeZkT8vwHbG2Navlxg_te5aKNQbF5z54YPuaP_unmzdaDIWIid4AISDG3NTNwN2rczNME9qPiFX5Agej7R";

    public static final String FCM_TAG = "fcm";

    public final static String EVENT_TOPIC = "event_topic";

    public final static String ADD_EVENT_TOPIC = "add_event_topic";
    public final static String EDIT_EVENT_TOPIC = "edit_event_topic";
    public final static String DELETE_EVENT_TOPIC = "delete_event_topic";


    public final static String KEY_SENDER_UID = "senderUID";

    private static void sendNotificationToOneUser(@NonNull Context context, @NonNull CalendarEvent event, int type, String token) {

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "sending notification to server");
        Log.d(FCM_TAG, "******************************************************************************************");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        SERVER_KEY = context.getString(R.string.server_key);

        String title = "";
        String body = "";
        String msg = "";


        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject notification = new JSONObject();

            title = "New event added";

            if(event.getFrequencyType().endsWith("amount")){
                msg = "chain ";
            }
            if(event.getFrequencyType().endsWith("end")){
                msg = "chain ";
            }

            String type_text = "";

            switch (type) {
                case Utils.ADD_EVENT_NOTIFICATION_CODE:
                    type_text = " has been added" + "\n I";
                    break;
                case Utils.EDIT_EVENT_NOTIFICATION_CODE:
                    type_text = " has been edited" + "\n Now i";
                    break;
            }

            body = "Event " + msg + event.getName() + type_text
                    + "t will start at " + event.getStartDateTime() + " and "
                    + " end at " + event.getEndDateTime();

            notification.put("tag", event.getPrivateId());

            notification.put("title", title);
            notification.put("body", body);

            json.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", Utils.ADD_EVENT_NOTIFICATION_CODE);
            data.put(UtilsCalendar.KEY_EVENT, new Gson().toJson(event));
            data.put(KEY_SENDER_UID, FirebaseUtils.getCurrentUID());

            json.put("data", data);

            Log.d(FCM_TAG, json.toString());
            Log.d(FCM_TAG, new Gson().toJson(event));
            Log.d(FCM_TAG, event.toString());

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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendNotificationMulticast(@NonNull Context context, @NonNull CalendarEvent event, int type, String[] tokens) {

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "sending notification to server");
        Log.d(FCM_TAG, "******************************************************************************************");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        SERVER_KEY = context.getString(R.string.server_key);

        String title = "";
        String body = "";
        String msg = "";


        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject remoteMessage = new JSONObject();

            JSONArray TOKENS = new JSONArray(tokens);

//            remoteMessage.put("registration_ids", TOKENS);
            remoteMessage.put("multicast_id", TOKENS);
//            remoteMessage.put("multicast_id", new Gson().toJson(tokens));
//            remoteMessage.put("registration_ids", "");
            JSONObject notification = new JSONObject();

            title = "New event added";

            if(event.getFrequencyType().endsWith("amount")){
                msg = "chain ";
            }
            if(event.getFrequencyType().endsWith("end")){
                msg = "chain ";
            }

            String type_text = "";

            switch (type) {
                case Utils.ADD_EVENT_NOTIFICATION_CODE:
                    type_text = " has been added" + "\n I";
                    break;
                case Utils.EDIT_EVENT_NOTIFICATION_CODE:
                    type_text = " has been edited" + "\n Now i";
                    break;
            }

            body = "Event " + msg + event.getName() + type_text
                    + "t will start at " + event.getStartDateTime() + " and "
                    + " end at " + event.getEndDateTime();

            notification.put("tag", event.getPrivateId());

            notification.put("title", title);
            notification.put("body", body);

            remoteMessage.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", Utils.ADD_EVENT_NOTIFICATION_CODE);
            data.put("color", event.getColor());
            data.put("event", new Gson().toJson(event));
//            data.put("event", event);



            remoteMessage.put("data", data);

            Log.d(FCM_TAG, remoteMessage.toString());
            Log.d(FCM_TAG, new Gson().toJson(event));
            Log.d(FCM_TAG, event.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, remoteMessage,
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
                            error.printStackTrace();
                            error.getCause();
                        }
                    })
            {
                @NonNull
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/remoteMessage");
//                    params.put("Accept", "application/remoteMessage");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

            Log.d(FCM_TAG, jsonObjectRequest.toString());
            Log.d(FCM_TAG, jsonObjectRequest.getUrl());
            Log.d(FCM_TAG, jsonObjectRequest.getHeaders().toString());
        } catch (JSONException | AuthFailureError e) {
            e.printStackTrace();
        }
    }

    private static void sendNotificationToTopic(@NonNull Context context, @NonNull CalendarEvent event, int type){
        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "sending notification to server");
        Log.d(FCM_TAG, "******************************************************************************************");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        SERVER_KEY = context.getString(R.string.server_key);

        String title = "Event ";
        String body = "";
        String msg = "";

        String topic = "";

        if (type == Utils.ADD_EVENT_NOTIFICATION_CODE){
            topic = ADD_EVENT_TOPIC;
            title += "added";
        }
        else if (type == Utils.EDIT_EVENT_NOTIFICATION_CODE){
            topic = EDIT_EVENT_TOPIC;
            title += "edited";
        }
        else if (type == Utils.DELETE_EVENT_NOTIFICATION_CODE){
            topic = DELETE_EVENT_TOPIC;
            title += "deleted";
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject json = new JSONObject();
            json.put("to", "/topics/" + topic);
            JSONObject notification = new JSONObject();

            if(event.getFrequencyType().endsWith("amount")){
                msg = "chain ";
            }
            if(event.getFrequencyType().endsWith("end")){
                msg = "chain ";
            }

            String type_text = "";

            switch (type) {
                case Utils.ADD_EVENT_NOTIFICATION_CODE:
                    type_text = " has been added" + "\n It will start at " + event.getStartDateTime() + " and "
                            + " end at " + event.getEndDateTime();
                    break;
                case Utils.EDIT_EVENT_NOTIFICATION_CODE:
                    type_text = " has been edited" + "\n Now It will start at " + event.getStartDateTime() + " and "
                            + " end at " + event.getEndDateTime();
                    break;
                case Utils.DELETE_EVENT_NOTIFICATION_CODE:
                    type_text = " has been deleted" ;
                    break;
            }

            body = "Event " + msg + event.getName() + type_text;

            notification.put("tag", event.getPrivateId());

            notification.put("title", title);
            notification.put("body", body);

            json.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", type);
            data.put(UtilsCalendar.KEY_EVENT, event.toJson());
            data.put(KEY_SENDER_UID, FirebaseUtils.getCurrentUID());

            json.put("data", data);

            Log.d(FCM_TAG, json.toString());
            Log.d(FCM_TAG, event.toJson());
            Log.d(FCM_TAG, event.toString());

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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendNotificationsToAllUsersWithTopic(Context context, CalendarEvent event, int notificationType){
        sendNotificationToTopic(context, event, notificationType);
    }

    public static void sendNotificationsToAllUsers(Context context, CalendarEvent event, int notificationType){

        Log.d(FCM_TAG, "******************************************************************************************");
        Log.d(FCM_TAG, "sending notification to all user");
        Log.d(FCM_TAG, "******************************************************************************************");

        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> TOKENS = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()){

                    data = data.child("tokens");

                    List<String> tokens = new ArrayList<>();
                    if (data.exists()){
                        tokens = (ArrayList<String>) data.getValue();
                    }

                    Log.d(FCM_TAG, tokens.toString());

                    TOKENS.addAll(tokens);

                    for (String token : tokens){
                        Log.d(FCM_TAG, "token is " + token);
                        sendNotificationToOneUser(context, event, notificationType, token);
                    }
/*                    String token = data.child("token").getValue(String.class);
                    sendNotificationToOneUser(context, event, notificationType, token);*/
                }

                String[] tokens = TOKENS.toArray(new String[0]);

                Log.d(FCM_TAG, Arrays.toString(tokens));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendNotificationsToAllUsersExceptSender(Context context, CalendarEvent event, int notificationType){

        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists() && !data.getKey().equals(FirebaseUtils.getCurrentUID())){
                        String token = data.child("token").getValue(String.class);
                        sendNotificationToOneUser(context, event, notificationType, token);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}