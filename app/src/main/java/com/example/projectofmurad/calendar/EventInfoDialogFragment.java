package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Show;
import com.example.projectofmurad.training.TrainingsAdapterForFirebase;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.helpers.ViewAnimationUtils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.training.TrainingsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link EventInfoDialogFragment#newInstance(CalendarEvent, boolean)} factory method to
 * create an instance of this fragment.
 */
public class EventInfoDialogFragment extends DialogFragment implements TrainingsAdapterForFirebase.OnShowToOthersListener,
                                                                        UsersAdapterForFirebase.OnUserLongClickListener,
                                                                        UsersAdapterForFirebase.OnUserExpandListener,
                                                                        TrainingsAdapter.OnTrainingClickListener,
                                                                        CompoundButton.OnCheckedChangeListener,
                                                                        View.OnClickListener {

    public static final String ARG_IS_SHOWS_DIALOG = "isShowsDialog";

    public static final String TAG = "EventInfoDialogFragment";

    private CalendarEvent event;
    private boolean isShowsDialog;

    private RecyclerView rv_users;

    private ShimmerFrameLayout shimmer_rv_users;
    private ShimmerFrameLayout shimmer_vp_user_and_training;

    public EventInfoDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event Event which details are shown.
     * @param isShowsDialog Determines if to show the fragment like normal or dialog.
     *
     * @return A new instance of fragment EventInfoDialogFragment.
     */
    @NonNull
    public static EventInfoDialogFragment newInstance(CalendarEvent event, boolean isShowsDialog) {
        EventInfoDialogFragment fragment = new EventInfoDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CalendarEvent.KEY_EVENT, event);
        args.putBoolean(ARG_IS_SHOWS_DIALOG, isShowsDialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (CalendarEvent) getArguments().getSerializable(CalendarEvent.KEY_EVENT);
            isShowsDialog = getArguments().getBoolean(ARG_IS_SHOWS_DIALOG);
        }

        setShowsDialog(isShowsDialog);

        if (isShowsDialog){
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(isShowsDialog
                                ? R.layout.fragment_event__info_with_collapsing
                                : R.layout.fragment_event__info_,
                container, false);
    }

    private AppBarLayout app_bar_layout;

    private CollapsingToolbarLayout collapsing_toolbar_layout;

    private MaterialCardView cv_event;

    private SwitchMaterial switch_alarm;

    private MaterialTextView tv_event_name;
    private MaterialTextView tv_event_place;
    private MaterialTextView tv_event_description;

    private MaterialTextView tv_event_start_date_time;
    private MaterialTextView tv_event_end_date_time;

    private ViewPager2 vp_trainings;

    private MaterialButton btn_copy_event;
    private MaterialButton btn_edit_event;
    private MaterialButton btn_share_event;
    private MaterialButton btn_delete_event;

    private LoadingDialog loadingDialog;

    private UsersAdapterForFirebase userAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(requireActivity());

        rv_users = view.findViewById(R.id.rv_users_home_fragment);
        shimmer_rv_users = view.findViewById(R.id.shimmer_rv_users_home_fragment);

        rv_users.addRecyclerListener(holder -> {
            ((UsersAdapterForFirebase.UserViewHolderForFirebase) holder).ll_contact.setVisibility(View.GONE);
            ((UsersAdapterForFirebase.UserViewHolderForFirebase) holder).expanded = false;
        });

        shimmer_vp_user_and_training = view.findViewById(R.id.shimmer_vp_user_and_training);

        cv_event = view.findViewById(R.id.cv_event);

        switch_alarm = view.findViewById(R.id.switch_alarm);
        switch_alarm.setOnCheckedChangeListener(this);
        switch_alarm.setOnClickListener(this);

        tv_event_name = view.findViewById(R.id.tv_event_name);
        tv_event_place = view.findViewById(R.id.tv_event_place);
        tv_event_description = view.findViewById(R.id.tv_event_description);

        tv_event_start_date_time = view.findViewById(R.id.tv_event_start_date_time);
        tv_event_end_date_time = view.findViewById(R.id.tv_event_end_date_time);

        vp_trainings = view.findViewById(R.id.vp_trainings);

        btn_copy_event = view.findViewById(R.id.btn_copy_event);
        btn_edit_event = view.findViewById(R.id.btn_edit_event);
        btn_share_event = view.findViewById(R.id.btn_share_event);
        btn_delete_event = view.findViewById(R.id.btn_delete_event);

        if (isShowsDialog) {
            getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id

            app_bar_layout = view.findViewById(R.id.app_bar_layout);
            collapsing_toolbar_layout = view.findViewById(R.id.collapsing_toolbar_layout);

            btn_copy_event.setOnClickListener(this);
            btn_edit_event.setOnClickListener(this);
            btn_share_event.setOnClickListener(this);
            btn_delete_event.setOnClickListener(this);
        }

        initializeRVUsers();

        SwitchMaterial switch_only_attend = view.findViewById(R.id.switch_only_attend);
        switch_only_attend.setOnCheckedChangeListener((buttonView, isChecked) -> setUpAllUsersRecyclerView(isChecked));

        if (event != null){
            setUpEventData(event);
        }
    }

    private void initializeRVUsers() {
        Query allUserKeys = FirebaseUtils.getCurrentGroupUsers();
        DatabaseReference users = FirebaseUtils.usersDatabase;

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setIndexedQuery(allUserKeys, users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        userAdapter = new UsersAdapterForFirebase(userOptions, requireContext(),
                event.getPrivateId(), event.getEnd(), event.getColor(), this, this);

        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(requireContext());
        linearLayoutManagerWrapper.addOnLayoutCompleteListener(
                () -> new Handler().postDelayed(this::stopRVUsersShimmer, 500));

        rv_users.setLayoutManager(linearLayoutManagerWrapper);
    }

    public void setUpEventData(@NonNull CalendarEvent event){

        tv_event_name.setText(event.getName());
        tv_event_place.setText(event.getPlace());
        tv_event_description.setText(event.getDescription());

        tv_event_start_date_time.setText(String.format(getString(R.string.starting_time_s_s),
                event.getStartDate(), event.getStartTime()));

        tv_event_end_date_time.setText(String.format(getString(R.string.ending_time_s_s),
                event.getEndDate(), event.getEndTime()));

        int textColor = Utils.getContrastColor(event.getColor());

        GradientDrawable gd = Utils.getGradientBackground(event.getColor());

        gd.setCornerRadius(Utils.dpToPx(10, requireContext()));

        cv_event.setBackground(gd);

        tv_event_name.setTextColor(textColor);
        tv_event_place.setTextColor(textColor);
        tv_event_description.setTextColor(textColor);
        tv_event_start_date_time.setTextColor(textColor);
        tv_event_end_date_time.setTextColor(textColor);

        if (isShowsDialog){
            collapsing_toolbar_layout.setContentScrimColor(event.getColor());
            collapsing_toolbar_layout.setCollapsedTitleTextColor(textColor);

            app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                    boolean isContentHide = collapsing_toolbar_layout.getScrimVisibleHeightTrigger()
                            + Math.abs(verticalOffset) > collapsing_toolbar_layout.getHeight();

                    cv_event.setVisibility(isContentHide ? View.INVISIBLE : View.VISIBLE);
                    collapsing_toolbar_layout.setTitle(isContentHide ? event.getName() : null);
                }
            });
        }
        else {
            FirebaseUtils.isMadrich().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isMadrich) {
                    if (!isMadrich){
                        return;
                    }

                    cv_event.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (getChildFragmentManager().findFragmentByTag(EventInfoDialogFragment.TAG) == null){
                                EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(event, true);
                                event_info_dialogFragment.show(getChildFragmentManager(), EventInfoDialogFragment.TAG);
                            }
                            return false;
                        }
                    });
                }
            });

        }

        switch_alarm.setChecked(Utils.checkIfAlarmSet(event.getPrivateId(), Utils.openOrCreateDatabase(requireContext())));

        setUpAllUsersRecyclerView(false);

        Query usersAndTrainings = FirebaseUtils.getGroupTrainingsDatabase().child(event.getPrivateId())
                .orderByChild(UserGroupData.KEY_SHOW).equalTo(Show.ALL.getValue());

        Log.d(Utils.LOG_TAG, "snapshot = " + FirebaseUtils.getGroupTrainingsDatabase().child(event.getPrivateId()).child(""));

        MutableLiveData<ArrayList<String>> userKeys = new MutableLiveData<>(new ArrayList<>());

        userKeys.observe(this, this::initializeTrainingsFirebaseViewPager2);

        usersAndTrainings.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot user : snapshot.getChildren()) {
                    Log.d(Utils.LOG_TAG, "snapshot = " + user.hasChild(UserGroupData.KEY_SHOW));
                    Log.d(Utils.LOG_TAG, "snapshot = " + user.getRef());
                    Log.d(Utils.LOG_TAG, "snapshot = " + user.child(UserGroupData.KEY_SHOW).getValue(int.class));
//                    if (user.child(UserGroupData.KEY_SHOW).getValue(int.class) == Show.ALL.getValue()){
                    keys.add(user.getKey());
//                    }

                }
                userKeys.setValue(keys);

                FirebaseUtils.getCurrentUserTrainingsForEvent(event.getPrivateId()).observe(EventInfoDialogFragment.this,
                        trainings -> {
                            if (trainings != null && !trainings.isEmpty()) {
                                /*ArrayList<String> users = userKeys.getValue();
                                if (!users.contains(FirebaseUtils.getCurrentUID())) {
                                    Log.d("snapshot", "current user has results in this event");
                                    users.add(0, FirebaseUtils.getCurrentUID());
                                }
                                userKeys.setValue(users);*/

                                userKeys.getValue().add(0, FirebaseUtils.getCurrentUID());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initializeTrainingsFirebaseViewPager2(@NonNull ArrayList<String> UIDs){

        TrainingsAdapterForFirebase trainingsAdapterForFirebase = new TrainingsAdapterForFirebase(requireContext(), UIDs,
                event.getPrivateId(), event.getColor(), this);

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

        vp_trainings.setAdapter(trainingsAdapterForFirebase);
        vp_trainings.setClipToPadding(false);
        vp_trainings.setClipChildren(false);
        vp_trainings.setOffscreenPageLimit(3);
        vp_trainings.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        vp_trainings.setNestedScrollingEnabled(true);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        vp_trainings.setPageTransformer(compositePageTransformer);

        int currentUserPosition = -1;
        if (currentUserPosition > -1)
            vp_trainings.setCurrentItem(currentUserPosition, true);

    }

    public void setUpAllUsersRecyclerView(boolean attend){
        startRVUsersShimmer();

        Query userKeys;
        if (attend){
            userKeys = FirebaseUtils.getAttendanceDatabase().child(event.getPrivateId()).orderByChild("attend").equalTo(true);
        }
        else {
            userKeys = FirebaseUtils.getCurrentGroupUsers();
        }

        DatabaseReference users = FirebaseUtils.usersDatabase;

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setIndexedQuery(userKeys, users, UserData.class)
                .setLifecycleOwner(this)
                .build();

        userAdapter.updateOptions(userOptions);
        rv_users.setAdapter(userAdapter);
    }

    @Override
    public void onUserExpand(int position, int oldPosition) {
        if (oldPosition > -1){
            UsersAdapterForFirebase.UserViewHolderForFirebase oldCollapsedItem
                    = ((UsersAdapterForFirebase.UserViewHolderForFirebase) rv_users.findViewHolderForAdapterPosition(oldPosition));

            if (oldCollapsedItem != null){
                ViewAnimationUtils.collapse(oldCollapsedItem.ll_contact);
                oldCollapsedItem.expanded = false;
            }
        }
    }

    @Override
    public boolean onUserLongClick(int position, @NonNull UserData userData) {
        Intent intent = new Intent(requireContext(), All_Attendances.class);
        intent.putExtra(UserData.KEY_UID, userData.getUID());
        startActivity(intent);

        return false;
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
            if (event.isSingle()){
                copySingleEvent(event);
            }
            else {
                createCopyDialog();
            }
        }
        else if(v == btn_edit_event){
            Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
            intent.putExtra(CalendarEvent.KEY_EVENT, event);

            dismiss();
            startActivity(intent);
        }
        else if(v == btn_share_event){
            String text = event.getName() + "\n" + event.getStartTime() + " - " + event.getEndTime()
                    + "\n" + event.getStartDate()
                    + (event.getStartDate().equals(event.getEndDate()) ? "" : "\n - " + event.getEndDate())
                    + "\n" + event.getPlace();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

            startActivity(Intent.createChooser(intent, "Choose app"));
        }
        else if(v == btn_delete_event){

            Log.d(Utils.LOG_TAG, "event  is " + event.toString());

            Toast.makeText(requireContext(), "Event was deleted successfully", Toast.LENGTH_SHORT).show();

            if (event.isSingle()){
                deleteSingleEvent(event.getPrivateId());
            }
            else {
                createDeleteDialog();
            }
        }

    }

    public void startRVUsersShimmer() {
        rv_users.setVisibility(View.INVISIBLE);
        shimmer_rv_users.setVisibility(View.VISIBLE);
        shimmer_rv_users.startShimmer();
    }

    public void stopRVUsersShimmer() {
        shimmer_rv_users.stopShimmer();
        shimmer_rv_users.setVisibility(View.GONE);
        rv_users.setVisibility(View.VISIBLE);
    }

    public void startVPTrainingsShimmer() {
        vp_trainings.setVisibility(View.INVISIBLE);
        shimmer_vp_user_and_training.setVisibility(View.VISIBLE);
        shimmer_vp_user_and_training.startShimmer();

        new Handler().postDelayed(this::stopVPTrainingsShimmer, 500);
    }

    public void stopVPTrainingsShimmer(){
        shimmer_vp_user_and_training.stopShimmer();
        shimmer_vp_user_and_training.setVisibility(View.GONE);
        vp_trainings.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowToOthers(int toWho) {
        FirebaseUtils.getCurrentUserTrainingsRefForEvent(event.getPrivateId()).getParent()
                        .child(UserGroupData.KEY_SHOW).setValue(toWho)
                        .addOnSuccessListener(unused -> Toast.makeText(requireContext(),
                                           "Now your results are visible to " + Show.values()[toWho].toString(),
                                                Toast.LENGTH_SHORT).show());

        startVPTrainingsShimmer();
    }

    public void createDeleteDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        MaterialTextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText("Delete");

        MaterialTextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            deleteSingleEvent(event.getPrivateId());
        });

        MaterialTextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            deleteAllEventsInChain(event.getChainId());
        });

        bottomSheetDialog.show();
    }

    public void deleteSingleEvent(@NonNull String private_key){
        loadingDialog.setMessage("Deleting event");
        loadingDialog.show();

        FirebaseUtils.deleteAll(FirebaseUtils.getCurrentGroupDatabase(), private_key,
                () -> startActivity(new Intent(requireContext(), MainActivity.class)
                        .setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT)));
    }

    public void deleteAllEventsInChain(String chain_key){
        loadingDialog.setMessage("Deleting events in the chain");
        loadingDialog.show();

        FirebaseUtils.deleteAll(FirebaseUtils.getAllEventsDatabase().orderByChild("chainId").equalTo(chain_key),
                CalendarEvent.KEY_EVENT_PRIVATE_ID,
                () -> startActivity(new Intent(requireContext(), MainActivity.class)
                        .setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT)));
    }

    public void createCopyDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        MaterialTextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText(R.string.copy);

        MaterialTextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            copySingleEvent(event);
        });

        MaterialTextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            copyAllEventsInChain(event);
        });

        bottomSheetDialog.show();
    }

    public void copySingleEvent(@NonNull CalendarEvent event){
        String private_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();

        CalendarEvent copy = event.copy();

        copy.setPrivateId(private_key);
        copy.setChainId(private_key);

        copy.clearFrequencyData();

        copy.setFrequency_start(event.getStartDate());
        copy.setFrequency_end(event.getEndDate());

        Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
        intent.putExtra(CalendarEvent.KEY_EVENT, copy);

        startActivity(intent);
    }

    public void copyAllEventsInChain(@NonNull CalendarEvent event) {
        String private_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();
        String chain_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();

        CalendarEvent copy = event.copy();
        copy.setPrivateId(private_key);
        copy.setChainId(chain_key);

        Intent intent = new Intent(requireContext(), Edit_Event_Screen.class);
        intent.putExtra(CalendarEvent.KEY_EVENT, copy);

        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onTrainingClick(int position) {

    }
}