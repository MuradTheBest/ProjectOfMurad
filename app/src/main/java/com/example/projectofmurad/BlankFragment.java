package com.example.projectofmurad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.calendar.AlarmDialog;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.Edit_Event_Screen;
import com.example.projectofmurad.calendar.EventSlidePageAdapter;
import com.example.projectofmurad.calendar.UsersAdapterForFirebase;
import com.example.projectofmurad.calendar.ZoomOutPageTransformer;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment implements UsersAdapterForFirebase.OnCallListener,
        UsersAdapterForFirebase.OnMessageListener, UsersAdapterForFirebase.OnUserExpandListener,
        UsersAdapterForFirebase.OnEmailListener, UsersAdapterForFirebase.OnUserListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv_users;
    private UsersAdapterForFirebase userAdapter;

    private ShimmerFrameLayout shimmer_rv_users;

    private String event_private_id;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.go_to_profile:
                startActivity(new Intent(requireContext(), Profile_Screen.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_for_viewpager2, container, false);
    }

    //Todo dashboard screen

    //ToDo show last happened event and show closest one to happen

    public CardView cv_event;
    public TextView tv_there_are_no_upcoming_events;

    public ConstraintLayout constraintLayout;

    public ImageView iv_circle;
    public ImageView iv_edit;
    public ImageView iv_attendance;

    public SwitchCompat switch_alarm;

    public TextView tv_event_name;
    public TextView tv_event_place;
    public TextView tv_event_description;

    public LinearLayout wrapped_layout;
    public TextView tv_event_start_time;
    public TextView tv_hyphen;
    public TextView tv_event_end_time;

    public LinearLayout expanded_layout;
    public TextView tv_event_start_date_time;
    public TextView tv_event_end_date_time;

    public CheckBox checkbox_all_attendances;

    public SQLiteDatabase db;

    private MutableLiveData<CalendarEvent> next_event = new MutableLiveData<>();
    private MutableLiveData<CalendarEvent> last_event = new MutableLiveData<>();

    private MutableLiveData<Boolean> isNext_event_ready = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLast_event_ready = new MutableLiveData<>();

    private EventSlidePageAdapter pagerAdapter;
    private ViewPager2 vp_event;

/*    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = requireContext().openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);

        rv_users = view.findViewById(R.id.rv_users_home_fragment);
        shimmer_rv_users = view.findViewById(R.id.shimmer_rv_users_home_fragment);

        cv_event = view.findViewById(R.id.cv_event);
        tv_there_are_no_upcoming_events = view.findViewById(R.id.tv_there_are_no_upcoming_events);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        iv_circle = view.findViewById(R.id.iv_circle);
        iv_edit = view.findViewById(R.id.iv_edit);
        iv_attendance = view.findViewById(R.id.iv_attendance);
        iv_attendance.setOnClickListener(this);

        switch_alarm = view.findViewById(R.id.switch_alarm);
        switch_alarm.setOnCheckedChangeListener(this);
        switch_alarm.setOnClickListener(this);

        tv_event_name = view.findViewById(R.id.tv_event_name);
        tv_event_place = view.findViewById(R.id.tv_event_place);
        tv_event_description = view.findViewById(R.id.tv_event_description);

        wrapped_layout = view.findViewById(R.id.wrapped_layout);
        tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
        tv_hyphen = view.findViewById(R.id.tv_hyphen);
        tv_event_end_time = view.findViewById(R.id.tv_event_end_time);

        expanded_layout = view.findViewById(R.id.expanded_layout);
        tv_event_start_date_time = view.findViewById(R.id.tv_event_start_date_time);
        tv_event_end_date_time = view.findViewById(R.id.tv_event_end_date_time);

        checkbox_all_attendances = view.findViewById(R.id.checkbox__all_attendances);
        checkbox_all_attendances.setOnCheckedChangeListener(this);

        iv_edit.setOnClickListener(this);

        Query query = FirebaseUtils.allEventsDatabase.orderByChild("start")
                .startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists()){
                        next_event = data.getValue(CalendarEvent.class);
                        tv_there_are_no_upcoming_events.setVisibility(View.GONE);

                        setUpNextEventData(next_event);

                    }
                    else {
                        cv_event.setVisibility(View.GONE);
                        tv_there_are_no_upcoming_events.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel.toSwipeFragments.setValue(true);

        vp_event = view.findViewById(R.id.vp_event);

/*        Query query = FirebaseUtils.allEventsDatabase.orderByChild("start")
                .startAt(Calendar.getInstance().getTimeInMillis()).limitToFirst(1);*/

        isNext_event_ready.setValue(false);
        isLast_event_ready.setValue(false);

        Query query = FirebaseUtils.allEventsDatabase;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.exists()){
                        next_event.setValue(data.getValue(CalendarEvent.class));
                        last_event.setValue(data.getValue(CalendarEvent.class));

                        MainViewModel.toSwipeFragments.setValue(true);

                        isNext_event_ready.setValue(true);
                        isLast_event_ready.setValue(true);

                        Log.d("murad", "next_event is " + next_event.toString());
                        Log.d("murad", "last_event is " + last_event.toString());

//                        tv_there_are_no_upcoming_events.setVisibility(View.GONE);

                    }
                    else {
                        cv_event.setVisibility(View.GONE);
                        tv_there_are_no_upcoming_events.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        next_event.observe(getViewLifecycleOwner(), event -> isNext_event_ready.setValue(true));

        last_event.observe(getViewLifecycleOwner(), event -> isLast_event_ready.setValue(true));

        isNext_event_ready.observe(getViewLifecycleOwner(), aBoolean -> {
            if (isLast_event_ready.getValue()){
                setPagerAdapter();
            }
        });

        isLast_event_ready.observe(getViewLifecycleOwner(), aBoolean -> {
            if (isNext_event_ready.getValue()){
                setPagerAdapter();
            }
        });

        vp_event.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                if (MainViewModel.toSwipeFragments.getValue()){
                }
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (MainViewModel.toSwipeFragments.getValue()){
                }
                    super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (MainViewModel.toSwipeFragments.getValue()){
                }
                    super.onPageScrollStateChanged(state);
            }
        });

        MainViewModel.toSwipeFragments.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(requireContext(), "toSwipeFragments is " + aBoolean, Toast.LENGTH_SHORT).show();
                Log.d("murad", "toSwipeFragments is " + aBoolean);
                vp_event.setUserInputEnabled(aBoolean);

            }
        });


    }

    private void setPagerAdapter() {

        pagerAdapter = new EventSlidePageAdapter(BlankFragment.this, next_event.getValue(), last_event.getValue());
        vp_event.setAdapter(pagerAdapter);
        vp_event.setPageTransformer(new ZoomOutPageTransformer());

        vp_event.setCurrentItem(1, true);
        vp_event.setNestedScrollingEnabled(true);
    }


    public void setUpNextEventData(@NonNull CalendarEvent event){
        iv_circle.getDrawable().setTint(event.getColor());

        tv_event_name.setText(event.getName());
        Log.d("murad","name: " + event.getName());

        tv_event_place.setText(event.getPlace());
        Log.d("murad","place: " +  event.getPlace());

        tv_event_description.setText(event.getDescription());
        Log.d("murad", "description " + event.getDescription());

/*
        if (selectedDate != null){
            if(event.getStartDate().equals(event.getEndDate())){
                tv_event_start_time.setText(event.getStartTime());
                Log.d("murad","Starting time: " + event.getStartTime());

                tv_event_end_time.setText(event.getEndTime());
                Log.d("murad","Ending time: " + event.getEndTime());

            }
            else if(event.getStartDate().equals(UtilsCalendar.DateToTextOnline(selectedDate))){
                tv_event_start_time.setText(event.getStartTime());
                Log.d("murad","Starting time: " + event.getStartTime());

            }
            else if(event.getEndDate().equals(UtilsCalendar.DateToTextOnline(selectedDate))){
                tv_event_end_time.setText(event.getEndTime());
                Log.d("murad","Ending time: " + event.getEndTime());

            }
            else{
                tv_hyphen.setText(R.string.all_day);
            }

            if(event.getTimestamp() == 0){
                tv_event_start_time.setText("");
                tv_hyphen.setText(R.string.all_day);
                tv_event_end_time.setText("");
            }
        }
*/

        Resources res = getResources();

        /*tv_event_start_date_time.setText(String.format(res.getString(R.string.starting_time_s_s),
                UtilsCalendar.OnlineTextToLocal(event.getStartDate()), event.getStartTime()));

        tv_event_end_date_time.setText(String.format(res.getString(R.string.ending_time_s_s),
                UtilsCalendar.OnlineTextToLocal(event.getEndDate()), event.getEndTime()));*/

        cv_event.getBackground().setTint(event.getColor());

        String event_private_id = event.getPrivateId();

        DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(FirebaseUtils.getCurrentUID());

        final boolean[] attend = {false};
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    attend[0] = task.getResult().getValue(boolean.class);
                }
                checkbox_all_attendances.setChecked(attend[0]);
                checkbox_all_attendances.setVisibility(View.GONE);

            }
        });

        boolean alarmSet = false;

        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        while (cursor.moveToNext()){
            alarmSet = true;
        }

        cursor.close();

        switch_alarm.setChecked(alarmSet);

        shimmer_rv_users.startShimmer();

//        Query query = FirebaseUtils.usersDatabase.orderByChild("madrich").startAt(true);
        Query users = FirebaseUtils.usersDatabase.orderByChild("notMadrich");

        FirebaseRecyclerOptions<UserData> options
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setQuery(users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        userAdapter = new UsersAdapterForFirebase(options, next_event.getValue().getPrivateId(), requireContext(), this, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + userAdapter.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_users.setAdapter(userAdapter);
        rv_users.startLayoutAnimation();
        Log.d("murad", "rv_events.getChildCount() = " + rv_users.getChildCount());

        rv_users.setLayoutManager(new LinearLayoutManagerWrapper(requireContext()));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            rv_users.setVisibility(View.VISIBLE);
            shimmer_rv_users.stopShimmer();
            shimmer_rv_users.setVisibility(View.GONE);
        }, 1000);
    }

    @Override
    public void OnCall(int position, String phone) {

    }

    @Override
    public void OnMessage(int position, String phone) {

    }

    @Override
    public void onUserExpand(int position, int oldPosition) {
        if (oldPosition > -1){
            Log.d("murad","=================position==================");

            Log.d("murad","position is " + position);
            Log.d("murad", "oldPosition is " + oldPosition);
            Log.d("murad", "expanded is " + ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition))
                    .expanded);
            Log.d("murad","=================position==================");
            ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition))
                    .ll_contact.setVisibility(View.GONE);
            ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition))
                    .expanded = false;
        }
    }

    @Override
    public void OnEmail(int position, String email) {

    }

    @Override
    public void onUserClick(int position, UserData userData) {

    }

    public void onEventClick(int position, @NonNull CalendarEvent event) {
//        this.dismiss();

        Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
        intent.putExtra("event", event);

        requireContext().startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_edit){
            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra("event", next_event.getValue());

            requireContext().startActivity(intent);
        }
        else if (v == switch_alarm){

            AtomicBoolean gotChecked = new AtomicBoolean(true);

            if (switch_alarm.isChecked()){
                Log.d("murad", "Alarm added");

                AlarmDialog alarmDialog = new AlarmDialog(requireContext() , next_event.getValue(), 0, 2, 0);
                alarmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!alarmDialog.isGotChecked()){
                            switch_alarm.setChecked(false);
                        }
                    }
                });

                alarmDialog.show();

            }
            else {
                Log.d("murad", "Alarm deleted");
                Toast.makeText(requireContext(), "Alarm canceled", Toast.LENGTH_SHORT).show();
//                    Utils.deleteAlarm(event_private_id, event_date, event, db, context);
                Log.d("murad", "swipe gotChecked = " + gotChecked.get());
                if (gotChecked.get()){
                    Log.d("murad", "swipe gotChecked = " + gotChecked.get());
                    AlarmManagerForToday.cancelAlarm(requireContext(), next_event.getValue());
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}