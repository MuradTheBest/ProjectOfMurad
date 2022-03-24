package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.LinearLayoutManagerWrapper;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.RVOnItemTouchListenerForVP2;
import com.example.projectofmurad.SuperUserTraining;
import com.example.projectofmurad.TrainingAdapterForFirebase;
import com.example.projectofmurad.TrainingsAdapter;
import com.example.projectofmurad.UserAndTraining;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.tracking.Training;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Event_Info_DialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Info_DialogFragment extends DialogFragment implements
        UsersAdapterForFirebase.OnCallListener,
        UsersAdapterForFirebase.OnMessageListener, UsersAdapterForFirebase.OnUserExpandListener,
        UsersAdapterForFirebase.OnEmailListener, UsersAdapterForFirebase.OnUserListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        TrainingsAdapter.OnTrainingClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_IS_SHOWS_DIALOG = "isShowsDialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CalendarEvent event;
    private boolean isShowsDialog;

    private RecyclerView rv_users;
    private UsersAdapterForFirebase userAdapter;

    private ShimmerFrameLayout shimmer_rv_users;

    private String event_private_id;

    public Event_Info_DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment Event_Info_DialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Event_Info_DialogFragment newInstance(String param1, String param2, CalendarEvent event, boolean isShowsDialog) {
        Event_Info_DialogFragment fragment = new Event_Info_DialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putSerializable(CalendarEvent.KEY_EVENT, event);
        args.putBoolean(ARG_IS_SHOWS_DIALOG, isShowsDialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            event = (CalendarEvent) getArguments().getSerializable(CalendarEvent.KEY_EVENT);
            if (event == null){

            }
            isShowsDialog = getArguments().getBoolean(ARG_IS_SHOWS_DIALOG);
        }

        setShowsDialog(isShowsDialog);

        if (isShowsDialog){
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event__info_, container, false);
    }

    private CardView cv_event;
    private TextView tv_there_are_no_upcoming_events;

    private ConstraintLayout constraintLayout;

    private SwitchCompat switch_alarm;

    private TextView tv_event_name;
    private TextView tv_event_place;
    private TextView tv_event_description;

    private LinearLayout wrapped_layout;
    private TextView tv_event_start_time;
    private TextView tv_hyphen;
    private TextView tv_event_end_time;

    private LinearLayout expanded_layout;
    private TextView tv_event_start_date_time;
    private TextView tv_event_end_date_time;

    private CheckBox checkbox_all_attendances;

    private SQLiteDatabase db;

    private ViewPager2 vp_trainings;

    private LinearLayout ll_manage_event;
    private MaterialButton btn_copy_event;
    private MaterialButton btn_edit_event;
    private MaterialButton btn_share_event;
    private MaterialButton btn_delete_event;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isShowsDialog){
            getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        }

        db = requireContext().openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);

        event_private_id = event.getPrivateId();

        rv_users = view.findViewById(R.id.rv_users_home_fragment);
        shimmer_rv_users = view.findViewById(R.id.shimmer_rv_users_home_fragment);

        cv_event = view.findViewById(R.id.cv_event);
        tv_there_are_no_upcoming_events = view.findViewById(R.id.tv_there_are_no_upcoming_events);

        constraintLayout = view.findViewById(R.id.constraintLayout);

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

        vp_trainings = view.findViewById(R.id.vp_trainings);

        setUpEventData(event);


        ll_manage_event = view.findViewById(R.id.ll_manage_event);
        if (!isShowsDialog){
            ll_manage_event.setVisibility(View.GONE);
        }

        btn_copy_event = view.findViewById(R.id.btn_copy_event);
        btn_copy_event.setOnClickListener(this);

        btn_edit_event = view.findViewById(R.id.btn_edit_event);
        btn_edit_event.setOnClickListener(this);

        btn_share_event = view.findViewById(R.id.btn_share_event);
        btn_share_event.setOnClickListener(this);

        btn_delete_event = view.findViewById(R.id.btn_delete_event);
        btn_delete_event.setOnClickListener(this);


    }

    public void setUpEventData(@NonNull CalendarEvent event){

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

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setQuery(users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        userAdapter = new UsersAdapterForFirebase(userOptions, event.getPrivateId(), requireContext(), this, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + userAdapter.getItemCount());
        Log.d("murad", "userOptions.getItemCount() = " + userOptions.getSnapshots().size());

        rv_users.setAdapter(userAdapter);
        Log.d("murad", "rv_events.getChildCount() = " + rv_users.getChildCount());

        rv_users.setLayoutManager(new LinearLayoutManagerWrapper(requireContext()));

        rv_users.addOnItemTouchListener(new RVOnItemTouchListenerForVP2(rv_users,
                MainViewModel.toSwipeViewModelForTrainings));

//        rv_users.setNestedScrollingEnabled(false);

        /*rv_users.getLayoutManager().setAutoMeasureEnabled(true);
        rv_users.setNestedScrollingEnabled(false);
        rv_users.setHasFixedSize(false);*/

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            rv_users.setVisibility(View.VISIBLE);
            shimmer_rv_users.stopShimmer();
            shimmer_rv_users.setVisibility(View.GONE);
        }, 1000);

        Query usersAndTrainingsKeys = FirebaseUtils.allEventsDatabase.child(event_private_id).child("Users");
        HashMap<String, ArrayList<Training>> map = new HashMap<>();

        ArrayList<UserAndTraining> userAndTrainingArrayList = new ArrayList<>();

        usersAndTrainingsKeys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()){
                    ArrayList<Training> trainings = new ArrayList<>();
                    map.put(user.getKey(), trainings);

                    ArrayList<Training> trainingArrayList = new ArrayList<>();

                    for (DataSnapshot training : user.getChildren()){
                        Training t = training.getValue(Training.class);

                        map.get(user.getKey()).add(t);

                        trainingArrayList.add(t);
                    }

                    UserAndTraining userAndTraining = new UserAndTraining(user.getKey(), trainingArrayList);

                    userAndTrainingArrayList.add(userAndTraining);
                }

                if (getContext() != null){
                    TrainingsAdapter trainingsAdapter = new TrainingsAdapter(requireContext(), userAndTrainingArrayList, Event_Info_DialogFragment.this);

                    Log.d("murad", map.toString());

                    initializeTrainingViewPager2(trainingsAdapter);
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Query usersAndTrainingsData = FirebaseUtils.allEventsDatabase.child(event_private_id).child("Users");

        Log.d("murad", "usersAndTrainingsKeys is " + usersAndTrainingsKeys.getRef().getKey());

        FirebaseRecyclerOptions<SuperUserTraining> userAndTrainingOptions
                = new FirebaseRecyclerOptions.Builder<SuperUserTraining>()
                .setQuery(usersAndTrainingsKeys, SuperUserTraining.class)
                .setLifecycleOwner(this)
                .build();

        /*PagingConfig pagingConfig = new PagingConfig(1);

        DatabasePagingOptions<UserAndTraining> databasePagingOptions = new DatabasePagingOptions.Builder<UserAndTraining>()
                .setQuery(usersAndTrainingsKeys, pagingConfig, UserAndTraining.class)
                .setLifecycleOwner(this)
                .build();*/


        TrainingAdapterForFirebase trainingAdapterForFirebase = new TrainingAdapterForFirebase(userAndTrainingOptions);




    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeTrainingViewPager2(TrainingsAdapter trainingsAdapter){

        DisplayMetrics displayMetrics = new DisplayMetrics();

        if (getParentFragment() != null) {
            getParentFragment().requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        else {
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        int width = displayMetrics.widthPixels;
        vp_trainings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getX() < width / 2) {
                    vp_trainings.setCurrentItem(vp_trainings.getCurrentItem() - 1);
                }
                else if (motionEvent.getX() > width / 2) {
                    vp_trainings.setCurrentItem(vp_trainings.getCurrentItem() + 1);
                }
                return false;
            }
        });


        vp_trainings.setAdapter(trainingsAdapter);
        vp_trainings.setClipToPadding(false);
        vp_trainings.setClipChildren(false);
        vp_trainings.setOffscreenPageLimit(3);
        vp_trainings.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        vp_trainings.setNestedScrollingEnabled(true);

/*        MainViewModel.toSwipeViewModelForTrainings.observe(getViewLifecycleOwner(),
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        vp_trainings.setUserInputEnabled(aBoolean);
                        Log.d("murad", "toSwipeViewModelForTrainings is " + aBoolean);
                    }
                });*/

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        vp_trainings.setPageTransformer(compositePageTransformer);

        Toast.makeText(getContext(), "vp_trainings.getChildCount() = " + vp_trainings.getChildCount(), Toast.LENGTH_SHORT).show();
        Toast.makeText(requireContext(), "trainingsAdapter.getItemCount() = " + trainingsAdapter.getItemCount(), Toast.LENGTH_SHORT).show();

        final int[] pos = {0};


/*
        vp_trainings.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                Log.d("murad", "onPageScrolled ");
                Log.d("murad","position = " + position);
                Log.d("murad","positionOffset = " + positionOffset);
                Log.d("murad", "positionOffsetPixels = " + positionOffsetPixels);

                */
/*MainViewModel.toSwipeFragments.setValue(false);

                if (pos[0] != position){
                    MainViewModel.toSwipeFragments.setValue(true);
                    pos[0] = position;
                }*//*

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

//                MainViewModel.toSwipeFragments.setValue(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

//                MainViewModel.toSwipeFragments.setValue(false);

            }
        });
*/

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
        if (v == switch_alarm){
           if (switch_alarm.isChecked()){
               Log.d("murad", "Alarm added");

               AlarmDialog alarmDialog = new AlarmDialog(requireContext(), event, switch_alarm, 2, 0);

               alarmDialog.show();
           }
           else {
               Log.d("murad", "Alarm deleted");
               Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show();
//                    Utils.deleteAlarm(event_private_id, event_date, event, db, context);
               AlarmManagerForToday.cancelAlarm(requireContext(), event);
           }
        }
        else if(v == btn_copy_event){
            String newPrivateId = FirebaseUtils.allEventsDatabase.push().getKey();
            String newChainId = FirebaseUtils.allEventsDatabase.push().getKey();

            event.setPrivateId(newPrivateId);
            event.setChainId(newChainId);

            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra(CalendarEvent.KEY_EVENT, event);

            requireActivity().startActivity(intent);
        }
        else if(v == btn_edit_event){
            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra(CalendarEvent.KEY_EVENT, event);

            requireActivity().startActivity(intent);
        }
        else if(v == btn_share_event){
            String text = event.getName() + "\n" + event.getStartTime() + " - " + event.getEndTime()
                    + "\n" + event.getStartDate()
                    + (event.getStartDate().equals(event.getEndDate()) ? "" : "\n - " + event.getEndDate())
                    + "\n" + event.getPlace();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

            requireActivity().startActivity(Intent.createChooser(intent, "Choose app"));
        }
        else if(v == btn_delete_event){
            absoluteDelete(event.getPrivateId(), new OnDeleteFinishedCallback() {
                @Override
                public void onDeleteFinished() {
                    Toast.makeText(requireContext(), "Event was deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), Calendar_Screen.class);
                    requireActivity().startActivity(intent);
                }
            });
        }

    }

    public void absoluteDelete(String key, OnDeleteFinishedCallback onDeleteFinishedCallback){

        DatabaseReference allEventsDatabase = FirebaseUtils.allEventsDatabase;

        ProgressDialog deletingProgressDialog = new ProgressDialog(requireContext());

        deletingProgressDialog.setMessage("Deleting event");
        deletingProgressDialog.setIndeterminate(true);
        deletingProgressDialog.show();

        Query query = allEventsDatabase.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    data.getRef().removeValue();
                }
//                onDeleteFinishedCallback.onDeleteFinished();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference eventsDatabaseReference = FirebaseUtils.eventsDatabase;

        eventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot date : snapshot.getChildren()){

                    Log.d("murad", "========================================================");
                    Log.d("murad", "Date key is " + date.getKey());
                    Log.d("murad", "events on this date: " + date.getChildrenCount());

                    for (DataSnapshot event : date.getChildren()){

                        Log.d("murad", "Event key is " + date.getKey());
                        Log.d("murad", event.getValue(CalendarEvent.class).toString());

                        if (event.child(CalendarEvent.KEY_EVENT_CHAIN_ID).getValue(String.class).equals(key)){
                            Log.d("murad", "Event found");
                            event.getRef().removeValue();
                        }
                    }


                    Log.d("murad", "========================================================");
                }
                onDeleteFinishedCallback.onDeleteFinished();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnDeleteFinishedCallback {
        void onDeleteFinished();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onTrainingClick(int position) {
        /*if (position == 0){
            vp_trainings.setCurrentItem(1, true);
        }
        if (position == 1){
            vp_trainings.setCurrentItem(0, true);
        }*/
    }
}