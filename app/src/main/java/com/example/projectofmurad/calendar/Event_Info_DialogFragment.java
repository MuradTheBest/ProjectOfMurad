package com.example.projectofmurad.calendar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.SuperUserTraining;
import com.example.projectofmurad.UserAndTraining;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.RVOnItemTouchListenerForVP2;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.helpers.ViewAnimationUtils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.training.Training;
import com.example.projectofmurad.training.TrainingAdapterForFirebase;
import com.example.projectofmurad.training.TrainingsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 * Use the {@link Event_Info_DialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Event_Info_DialogFragment extends DialogFragment implements UsersAdapterForFirebase.OnUserExpandListener,
        UsersAdapterForFirebase.OnUserClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        TrainingsAdapter.OnTrainingClickListener {

    public static final String ARG_IS_SHOWS_DIALOG = "isShowsDialog";

    public static final String TAG = "event_info_dialog_fragment";


    private CalendarEvent event;
    private boolean isShowsDialog;

    private RecyclerView rv_users;

    private ShimmerFrameLayout shimmer_rv_users;

    public Event_Info_DialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event Event which details are shown.
     * @param isShowsDialog Determines if to show the fragment like normal or dialog.
     *
     * @return A new instance of fragment Event_Info_DialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static Event_Info_DialogFragment newInstance(CalendarEvent event, boolean isShowsDialog) {
        Event_Info_DialogFragment fragment = new Event_Info_DialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(UtilsCalendar.KEY_EVENT, event);
        args.putBoolean(ARG_IS_SHOWS_DIALOG, isShowsDialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (CalendarEvent) getArguments().getSerializable(UtilsCalendar.KEY_EVENT);
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
        return inflater.inflate(isShowsDialog ? R.layout.fragment_event__info_with_collapsing : R.layout.fragment_event__info_, container, false);
    }

    private ConstraintLayout cl_event;

    private AppBarLayout app_bar_layout;

    private CollapsingToolbarLayout collapsing_toolbar_layout;

    private MaterialToolbar toolbar;

    private CardView cv_event;
    private TextView tv_there_are_no_upcoming_events;

    private ConstraintLayout constraintLayout;

    private SwitchCompat switch_alarm;

    private TextView tv_event_name;
    private TextView tv_event_place;
    private TextView tv_event_description;

    private TextView tv_event_start_date_time;
    private TextView tv_event_end_date_time;

    private ViewPager2 vp_trainings;

    private LinearLayout ll_manage_event;
    private MaterialButton btn_copy_event;
    private MaterialButton btn_edit_event;
    private MaterialButton btn_share_event;
    private MaterialButton btn_delete_event;

    private ProgressDialog deletingProgressDialog;

    private int currentUserPosition = -1;

    private TextView tv_there_is_no_event;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cl_event = view.findViewById(R.id.cl_event);

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

        tv_event_start_date_time = view.findViewById(R.id.tv_event_start_date_time);
        tv_event_end_date_time = view.findViewById(R.id.tv_event_end_date_time);

        vp_trainings = view.findViewById(R.id.vp_trainings);

        ll_manage_event = view.findViewById(R.id.ll_manage_event);

        btn_copy_event = view.findViewById(R.id.btn_copy_event);
        btn_edit_event = view.findViewById(R.id.btn_edit_event);
        btn_share_event = view.findViewById(R.id.btn_share_event);
        btn_delete_event = view.findViewById(R.id.btn_delete_event);

        if (isShowsDialog){
            getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id

            app_bar_layout = view.findViewById(R.id.app_bar_layout);
            collapsing_toolbar_layout = view.findViewById(R.id.collapsing_toolbar_layout);
            toolbar = view.findViewById(R.id.toolbar);
//            toolbar.setVisibility(View.GONE);

            btn_copy_event.setOnClickListener(this);
            btn_edit_event.setOnClickListener(this);
            btn_share_event.setOnClickListener(this);
            btn_delete_event.setOnClickListener(this);
        }
        else {

        }

        tv_there_is_no_event = view.findViewById(R.id.tv_there_is_no_event);

        if (event != null){
            setUpEventData(event);
        }
        else {

        }

    }

    public void setUpEventData(@NonNull CalendarEvent event){

        ColorDrawable colorDrawable = new ColorDrawable(event.getColor());

//        cl_event.setBackground(colorDrawable);

        tv_event_name.setText(event.getName());
        Log.d("murad","name: " + event.getName());

        tv_event_place.setText(event.getPlace());
        Log.d("murad","place: " +  event.getPlace());

        tv_event_description.setText(event.getDescription());
        Log.d("murad", "description " + event.getDescription());


        tv_event_start_date_time.setText(String.format(getString(R.string.starting_time_s_s),
                UtilsCalendar.OnlineTextToLocal(event.getStartDate()), event.getStartTime()));

        tv_event_end_date_time.setText(String.format(getString(R.string.ending_time_s_s),
                UtilsCalendar.OnlineTextToLocal(event.getEndDate()), event.getEndTime()));

//        cv_event.getBackground().setTint(event.getColor());

        int textColor = Utils.getContrastColor(event.getColor());

        int gradientColor = (textColor == Color.WHITE) ? Color.LTGRAY : Color.DKGRAY;

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {event.getColor(), event.getColor(), gradientColor});

//        gd.setShape(GradientDrawable.RECTANGLE);

        gd.setCornerRadius(20);

        cv_event.setBackground(gd);

        tv_event_name.setTextColor(textColor);

        if (isShowsDialog){
            collapsing_toolbar_layout.setContentScrimColor(event.getColor());
            collapsing_toolbar_layout.setTitleEnabled(true);

            app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                    Log.d(Utils.LOG_TAG, "=============================================================");
                    Log.d(Utils.LOG_TAG, "verticalOffset = " + verticalOffset);
                    Log.d(Utils.LOG_TAG, "getScrimVisibleHeightTrigger = " + collapsing_toolbar_layout.getScrimVisibleHeightTrigger());
                    Log.d(Utils.LOG_TAG, "getTotalScrollRange = " + appBarLayout.getTotalScrollRange());

                    Log.d(Utils.LOG_TAG, "isLifted " + appBarLayout.isLifted());
                    Log.d(Utils.LOG_TAG, "isLiftOnScroll " + appBarLayout.isLiftOnScroll());

                    //triggered when scrim starts
                    if((appBarLayout.getTotalScrollRange() - verticalOffset)*2 < collapsing_toolbar_layout.getScrimVisibleHeightTrigger()) {

                        collapsing_toolbar_layout.setTitle(event.getName());
                    }
                    else {
                        collapsing_toolbar_layout.setTitle("");
                    }

                    Log.d(Utils.LOG_TAG, "=============================================================");
                }
            });
        }

        String event_private_id = event.getPrivateId();

        checkIfAlarmSet(event_private_id);

        shimmer_rv_users.startShimmer();


//        Query users = FirebaseUtils.usersDatabase.orderByChild("madrich");
        Query users = FirebaseUtils.usersDatabase.orderByValue();
//        Query users = FirebaseUtils.usersDatabase.orderByChild("madrich").startAt(true);

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setQuery(users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        UsersAdapterForFirebase userAdapter = new UsersAdapterForFirebase(userOptions, requireContext(),
                event.getPrivateId(), event.getColor(), this, this);

        rv_users.setAdapter(userAdapter);

        rv_users.setLayoutManager(new LinearLayoutManagerWrapper(requireContext()));

        rv_users.addOnItemTouchListener(new RVOnItemTouchListenerForVP2(rv_users,
                MainViewModel.getToSwipeViewModelForTrainings()));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            rv_users.setVisibility(View.VISIBLE);
            shimmer_rv_users.stopShimmer();
            shimmer_rv_users.setVisibility(View.GONE);
        }, 1000);

        Query usersAndTrainingsKeys = FirebaseUtils.trainingsDatabase.child("Events").child(event_private_id);
        HashMap<String, ArrayList<Training>> map = new HashMap<>();

        usersAndTrainingsKeys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<UserAndTraining> userAndTrainingArrayList = new ArrayList<>();

                int i = 0;
                for (DataSnapshot user : snapshot.getChildren()){
                    ArrayList<Training> trainings = new ArrayList<>();
                    map.put(user.getKey(), trainings);

                    if (FirebaseUtils.isCurrentUID(user.getKey())){
                        currentUserPosition = i;
                    }

                    ArrayList<Training> trainingArrayList = new ArrayList<>();

                    for (DataSnapshot training : user.getChildren()){
                        Training t = training.getValue(Training.class);

                        map.get(user.getKey()).add(t);

                        trainingArrayList.add(t);
                    }

//                    UserAndTraining userAndTraining = new UserAndTraining(user.getKey(), trainingArrayList);
                    UserAndTraining userAndTraining = new UserAndTraining(user.getKey(), event_private_id, trainingArrayList);

                    userAndTrainingArrayList.add(userAndTraining);
                    i++;
                }

                if (getContext() != null){
                    TrainingsAdapter trainingsAdapter = new TrainingsAdapter(requireContext(), userAndTrainingArrayList, event.getColor(), Event_Info_DialogFragment.this);

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
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        vp_trainings.setPageTransformer(compositePageTransformer);

        Toast.makeText(getContext(), "vp_trainings.getChildCount() = " + vp_trainings.getChildCount(), Toast.LENGTH_SHORT).show();
        Toast.makeText(requireContext(), "trainingsAdapter.getItemCount() = " + trainingsAdapter.getItemCount(), Toast.LENGTH_SHORT).show();

        if (currentUserPosition > -1) vp_trainings.setCurrentItem(currentUserPosition, true);

    }

    @Override
    public void onUserExpand(int position, int oldPosition) {
        if (oldPosition > -1){
            Log.d("murad","=================position==================");

            Log.d("murad","position is " + position);
            Log.d("murad", "oldPosition is " + oldPosition);
            Log.d("murad", "expanded is "/* + ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition))
                    .expanded*/);
            Log.d("murad","=================position==================");


//            ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition))
//                    .ll_contact.setVisibility(View.GONE);

            UsersAdapterForFirebase.UserViewHolderForFirebase oldCollapsedItem = ((UsersAdapterForFirebase.UserViewHolderForFirebase)
                    rv_users.findViewHolderForAdapterPosition(oldPosition));

            if (oldCollapsedItem != null){
                ViewAnimationUtils.collapse(oldCollapsedItem.ll_contact);

                oldCollapsedItem.expanded = false;

//            rv_users.scrollToPosition(position);
            }
            else {

            }

        }
    }

    @Override
    public void onUserClick(int position, @NonNull UserData userData) {
        Intent intent = new Intent(requireContext(), All_Attendances.class);
        intent.putExtra(UserData.KEY_UID, userData.getUID());
        startActivity(intent);
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

            Log.d(Utils.LOG_TAG, "event  is " + event.toString());

            if (event.getPrivateId().equals(event.getChainId())){
                copySingleEvent(event);
            }
            else {
                createCopyDialog();
            }
        }
        else if(v == btn_edit_event){
            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra(UtilsCalendar.KEY_EVENT, event);

            dismiss();
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

            Log.d(Utils.LOG_TAG, "event  is " + event.toString());

            Toast.makeText(requireContext(), "Event was deleted successfully", Toast.LENGTH_SHORT).show();

            if (event.getPrivateId().equals(event.getChainId())){
                deleteSingleEvent(event.getChainId(), () -> requireActivity().startActivity(
                        new Intent(requireContext(),
                                MainActivity.class).setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT)));
            }
            else {
                createDeleteDialog();
            }
        }

    }

    private void getEventData(OnGetEventDataListener onGetEventDataListener){
        FirebaseUtils.allEventsDatabase.child(event.getChainId()).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()){
                            CalendarEvent superEvent = task.getResult().getValue(CalendarEvent.class);

                            event.setFrequency_start(superEvent.getFrequency_start());
                            event.setFrequency_end(superEvent.getFrequency_end());

                            onGetEventDataListener.onGetEventData();
                        }
                    }
                });

    }

    public void checkIfAlarmSet(String event_private_id){

    }

    public interface OnGetEventDataListener{
        void onGetEventData();
    }

    public void createDeleteDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        Utils.createCustomBottomSheetDialog(bottomSheetDialog);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText("Delete");

        TextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                deleteSingleEvent(event.getPrivateId(), () -> requireActivity().startActivity(new Intent(requireContext(),
                        MainActivity.class).setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT)));
            }
        });

        TextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                deleteAllEventsInChain(event.getChainId(), () -> requireActivity().startActivity(new Intent(requireContext(),
                        MainActivity.class).setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT)));
            }
        });

        bottomSheetDialog.show();
    }

    public void deleteSingleEvent(@NonNull String private_key, OnDeleteFinishedCallback onDeleteFinishedCallback){
        DatabaseReference allEventsDatabase = FirebaseUtils.allEventsDatabase;

        deletingProgressDialog = new ProgressDialog(requireContext());

        deletingProgressDialog.setMessage("Deleting event");
        deletingProgressDialog.setIndeterminate(true);
        deletingProgressDialog.show();

        Query query = allEventsDatabase.orderByKey().equalTo(private_key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    data.getRef().removeValue();
                }
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
                    Log.d("murad", "Date private_key is " + date.getKey());
                    Log.d("murad", "events on this date: " + date.getChildrenCount());

                    for (DataSnapshot event : date.getChildren()) {

                        Log.d("murad", "Event private_key is " + date.getKey());
                        Log.d("murad", event.getValue(CalendarEvent.class).toString());

                        if (event.getKey().equals(private_key)){
                            Log.d("murad", "Event found");
                            event.getRef().removeValue();
                        }
                    }


                    Log.d("murad", "========================================================");
                }
                FCMSend.sendNotificationsToAllUsersWithTopic(requireContext(), event, Utils.DELETE_EVENT_NOTIFICATION_CODE);
                onDeleteFinishedCallback.onDeleteFinished();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteAllEventsInChain(String chain_key, OnDeleteFinishedCallback onDeleteFinishedCallback){
        DatabaseReference allEventsDatabase = FirebaseUtils.allEventsDatabase;

        deletingProgressDialog = new ProgressDialog(requireContext());

        deletingProgressDialog.setMessage("Deleting event");
        deletingProgressDialog.setIndeterminate(true);
        deletingProgressDialog.show();

        Query query = allEventsDatabase.orderByKey().equalTo(chain_key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot eventDS : snapshot.getChildren()){
                    if (eventDS.child(UtilsCalendar.KEY_EVENT_CHAIN_ID).getValue(String.class).equals(chain_key)){
                        Log.d("murad", "Event found");
                        eventDS.getRef().removeValue();
                    }
                }
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
                    Log.d("murad", "Date chain_key is " + date.getKey());
                    Log.d("murad", "events on this date: " + date.getChildrenCount());

                    for (DataSnapshot eventDS : date.getChildren()){

                        Log.d("murad", "Event chain_key is " + date.getKey());
                        Log.d("murad", eventDS.getValue(CalendarEvent.class).toString());

                        if (eventDS.child(UtilsCalendar.KEY_EVENT_CHAIN_ID).getValue(String.class).equals(chain_key)){
                            Log.d("murad", "Event found");
                            eventDS.getRef().removeValue();
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

    public void createCopyDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        Utils.createCustomBottomSheetDialog(bottomSheetDialog);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText("Copy");

        TextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            copySingleEvent(event);
        });

        TextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            copyAllEventsInChain(event);
        });

        bottomSheetDialog.show();
    }

    public void copySingleEvent(@NonNull CalendarEvent event){
        String private_key = "Event" + FirebaseUtils.allEventsDatabase.push().getKey();

        event.setPrivateId(private_key);
        event.setChainId(private_key);

        event.clearFrequencyData();

        event.setFrequency_start(event.getStartDate());
        event.setFrequency_end(event.getEndDate());

        Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
        intent.putExtra(UtilsCalendar.KEY_EVENT, event);

        dismiss();
        requireActivity().startActivity(intent);
    }

    public void copyAllEventsInChain(@NonNull CalendarEvent event){
        String private_key = "Event" + FirebaseUtils.allEventsDatabase.push().getKey();
        String chain_key = "Event" + FirebaseUtils.allEventsDatabase.push().getKey();

        getEventData(() -> {
            event.setPrivateId(private_key);
            event.setChainId(chain_key);

            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra(UtilsCalendar.KEY_EVENT, event);

            dismiss();
            startActivity(intent);
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