package com.example.projectofmurad.notifications;

import android.content.Context;
import android.os.StrictMode;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAA9qIrU5w:APA91bFHE98eTclTHbx2zorhi97uhpZrwpJ3_dyPj9LB6Qlf3epcnDlKctJkfe2AWgjyc2ULMfa_y4i6S-o0P98TdJRoSu337Hvs23Qf4CkUchFV043IT0E8ekvh80SN1rkNR0Anmi2y";

    public static void pushNotification(Context context, @NonNull CalendarEvent event, int type, String token) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String title = "";
        String body = "";
        String msg = "";


        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject remoteMessage = new JSONObject();
            remoteMessage.put("to", token);
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
                    + "t will start at " + event.getStart_dateTime() + " and "
                    + " end at " + event.getEnd_dateTime();

            notification.put("tag", event.getPrivateId());

            notification.put("title", title);
            notification.put("body", body);

            remoteMessage.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("type", Utils.ADD_EVENT_NOTIFICATION_CODE);
            data.put("color", event.getColor());
            data.put("event", new Gson().toJson(event));

            remoteMessage.put("data", data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, remoteMessage,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("FCM" + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/remoteMessage");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void pushNotificationsToAllUsers(Context context, CalendarEvent event, int notificationType){

        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    /*if (data.exists() && !data.getKey().equals(FirebaseUtils.getCurrentUID())){
                        String token = data.child("token").getValue(String.class);
                        pushNotification(context, token, title, message, event);
                    }*/
                    String token = data.child("token").getValue(String.class);
                    pushNotification(context, event, notificationType, token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void pushNotificationsToAllUsersExceptSender(Context context, CalendarEvent event, int notificationType){

        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists() && !data.getKey().equals(FirebaseUtils.getCurrentUID())){
                        String token = data.child("token").getValue(String.class);
                        pushNotification(context, event, notificationType, token);
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
