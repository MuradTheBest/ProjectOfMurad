package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class Event_Attendance_Screen extends AppCompatActivity implements
        UsersAdapterForFirebase.OnUserListener {

    private TextView tv_event_name;
    private TextView tv_event_place;
    private TextView tv_event_description;

    private TextView tv_event_start_date_time;
    private TextView tv_event_end_date_time;

    private UserData[] usersArrayList;

    private RecyclerView rv_users;
    private UsersAdapterForFirebase userAdapter;

    private ShimmerFrameLayout shimmer_rv_users;

    private String event_private_id;
    /*private String event_name;
    private String event_description;
    private String event_place;
    private String event_start_date;
    private String event_start_time;
    private String event_end_date;
    private String event_end_time;*/

    private Intent gotten_intent;

    private CalendarEvent event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendance_screen);

        gotten_intent = getIntent();

        event_private_id = gotten_intent.getStringExtra("event_private_id");

        DatabaseReference allEvents = FirebaseUtils.allEventsDatabase;


        tv_event_name = findViewById(R.id.tv_event_name);
        tv_event_place = findViewById(R.id.tv_event_place);
        tv_event_description = findViewById(R.id.tv_event_description);

        tv_event_start_date_time = findViewById(R.id.tv_event_start_date_time);
        tv_event_end_date_time = findViewById(R.id.tv_event_end_date_time);

        allEvents.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()){

                    event = task.getResult().getValue(CalendarEvent.class);

                    assert event != null;
                    tv_event_name.setText(event.getName());
                    tv_event_place.setText(event.getPlace());
                    tv_event_description.setText(event.getDescription());

                    Resources res = getResources();

                    tv_event_start_date_time.setText(String.format(res.getString(R.string.starting_time_s_s),
                            Utils_Calendar.OnlineTextToLocal(event.getStart_date()), event.getStart_time()));

                    tv_event_end_date_time.setText(String.format(res.getString(R.string.ending_time_s_s),
                            Utils_Calendar.OnlineTextToLocal(event.getEnd_date()), event.getEnd_time()));
                }
            }
        });

        /*event_name = gotten_intent.getStringExtra("event_name");
        event_description = gotten_intent.getStringExtra("event_description");
        event_place = gotten_intent.getStringExtra("event_place");
        event_start_date = gotten_intent.getStringExtra("event_start_date");
        event_start_time = gotten_intent.getStringExtra("event_start_time");
        event_end_date = gotten_intent.getStringExtra("event_end_date");
        event_end_time = gotten_intent.getStringExtra("event_end_time");*/


        rv_users = findViewById(R.id.rv_users);
        shimmer_rv_users = findViewById(R.id.shimmer_rv_users);
        shimmer_rv_users.startShimmer();

        Query query = FirebaseUtils.usersDatabase.orderByChild("isMadrich");

        FirebaseRecyclerOptions<UserData> options
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setQuery(query, UserData.class)
                .setLifecycleOwner(this)
                .build();

        userAdapter = new UsersAdapterForFirebase(options, event_private_id, this, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + userAdapter.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_users.setAdapter(userAdapter);
        rv_users.startLayoutAnimation();
        Log.d("murad", "rv_events.getChildCount() = " + rv_users.getChildCount());
        rv_users.setLayoutManager(new LinearLayoutManager(this));

        /*ObservableSnapshotArray<UserData> userDataArrayList = userAdapter.getSnapshots();
//        usersArrayList = (UserData[]) userDataArrayList.toArray();
        for (UserData userData : userDataArrayList){
            Log.d(TAG, userData.toString());
        }

        Log.d(TAG, "event_private_id is " + event_private_id);

        FirebaseUtils.eventsDatabase.child(event_private_id).child("attendance_UIDs").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()){
                            Log.d(TAG, "---------------------------------------------------------------" );
                            String UID = data.getKey();
                            Log.d(TAG, "UID = " + UID);

                            boolean attend = data.getValue(boolean.class);
                            Log.d(TAG, "attend = " + attend);

                            int position = userDataArrayList.indexOf(UID);
                            Log.d(TAG, "position = " + position);
                            Log.d(TAG, "---------------------------------------------------------------" );


                            ((CheckBox) rv_users.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.checkbox_attendance)).setChecked(attend);
//                            rv_users.notify();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            rv_users.setVisibility(View.VISIBLE);
            shimmer_rv_users.stopShimmer();
            shimmer_rv_users.setVisibility(View.GONE);
        }, 2000);

    }

    @Override
    public void onUserClick(int position, @NonNull UserData userData) {
        String selected_UID = userData.getUID();

        Intent intent = new Intent(this, All_Attendances.class);
        intent.putExtra("selected_UID", selected_UID);
        intent.putExtra("event_private_id", event_private_id);

        startActivity(intent);
    }
}