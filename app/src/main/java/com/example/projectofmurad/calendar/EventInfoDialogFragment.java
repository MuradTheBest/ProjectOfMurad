package com.example.projectofmurad.calendar;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserAttendancesFragment;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.example.projectofmurad.utils.ViewAnimationUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link EventInfoDialogFragment#newInstance(CalendarEvent, boolean)} factory method to
 * create an instance of this fragment.
 */
public class EventInfoDialogFragment extends DialogFragment implements UsersAdapter.OnUserLongClickListener,
                                                                        UsersAdapter.OnUserExpandListener,
                                                                        View.OnClickListener {

    /**
     * The constant ARG_IS_SHOWS_DIALOG.
     */
    public static final String ARG_IS_SHOWS_DIALOG = "isShowsDialog";

    /**
     * The constant TAG.
     */
    public static final String TAG = "EventInfoDialogFragment";

    private CalendarEvent event;
    private boolean isShowsDialog;

    private RecyclerView rv_users;

    private ShimmerFrameLayout shimmer_rv_users;

    /**
     * Instantiates a new Event info dialog fragment.
     */
    public EventInfoDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event         Event which details are shown.
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
            setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(isShowsDialog
                                ? R.layout.fragment_event__info_with_collapsing
                                : R.layout.fragment_event__info_, container, false);
    }

    private AppBarLayout app_bar_layout;

    private CollapsingToolbarLayout collapsing_toolbar_layout;

    private MaterialCardView cv_event;

    private SwitchMaterial switch_alarm;

    private TextView tv_event_name;
    private TextView tv_event_place;
    private TextView tv_event_description;

    private TextView tv_event_start_date_time;
    private TextView tv_event_end_date_time;

    private MaterialButton btn_copy_event;
    private MaterialButton btn_edit_event;
    private MaterialButton btn_share_event;
    private MaterialButton btn_delete_event;

    private LoadingDialog loadingDialog;

    private UsersAdapter userAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(requireActivity());

        rv_users = view.findViewById(R.id.rv_users_home_fragment);
        shimmer_rv_users = view.findViewById(R.id.shimmer_rv_users_home_fragment);

        rv_users.addRecyclerListener(holder -> {
            ((UsersAdapter.UserViewHolder) holder).ll_contact.setVisibility(View.GONE);
            ((UsersAdapter.UserViewHolder) holder).expanded = false;
        });

        cv_event = view.findViewById(R.id.cv_event);

        switch_alarm = view.findViewById(R.id.switch_alarm);
        switch_alarm.setOnClickListener(this);

        tv_event_name = view.findViewById(R.id.tv_event_name);
        tv_event_place = view.findViewById(R.id.tv_event_place);
        tv_event_description = view.findViewById(R.id.tv_event_description);

        tv_event_start_date_time = view.findViewById(R.id.tv_event_start_date_time);
        tv_event_end_date_time = view.findViewById(R.id.tv_event_end_date_time);

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
        Query allUserKeys = FirebaseUtils.getCurrentGroupUsers().orderByValue();
        DatabaseReference users = FirebaseUtils.usersDatabase;

        FirebaseRecyclerOptions<UserData> userOptions
                = new FirebaseRecyclerOptions.Builder<UserData>()
                .setLifecycleOwner(this)
                .setIndexedQuery(allUserKeys, users, UserData.class)
                .build();

        userAdapter = new UsersAdapter(userOptions, requireContext(),
                event.getPrivateId(), event.getEnd(), event.getColor(), this, this);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(requireContext());
        layoutManager.setOnLayoutCompleteListener(() -> new Handler().postDelayed(this::stopRVUsersShimmer, 500));

        rv_users.setLayoutManager(layoutManager);
    }

    /**
     * Set up event data.
     *
     * @param event the event
     */
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

            FirebaseUtils.isMadrich().observe(this, isMadrich -> {
                btn_copy_event.setVisibility(isMadrich ? View.VISIBLE : View.GONE);
                btn_edit_event.setVisibility(isMadrich ? View.VISIBLE : View.GONE);
                btn_delete_event.setVisibility(isMadrich ? View.VISIBLE : View.GONE);
            });
        }
        else {
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

        switch_alarm.setChecked(Utils.checkIfAlarmSet(event.getPrivateId(), Utils.openOrCreateDatabase(requireContext())));

        setUpAllUsersRecyclerView(false);
    }

    /**
     * Set up all users recycler view.
     *
     * @param attend the attend
     */
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
            UsersAdapter.UserViewHolder oldCollapsedItem
                    = ((UsersAdapter.UserViewHolder) rv_users.findViewHolderForAdapterPosition(oldPosition));

            if (oldCollapsedItem != null){
                ViewAnimationUtils.collapse(oldCollapsedItem.ll_contact);
                oldCollapsedItem.expanded = false;
            }
        }
    }

    @Override
    public boolean onUserLongClick(int position, @NonNull UserData userData) {
        FirebaseUtils.isMadrich().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isMadrich) {
                if (isMadrich) {
                    FragmentManager fm = getParentFragmentManager();
                    if (fm.findFragmentByTag(UserAttendancesFragment.TAG) == null){
                        UserAttendancesFragment userAttendancesFragment
                                = UserAttendancesFragment.newInstance(userData.getUID(), userData.getUsername());
                        userAttendancesFragment.show(fm, UserAttendancesFragment.TAG);
                    }
                }
            }
        });

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == switch_alarm){
           if (switch_alarm.isChecked()){
               AlarmDialog alarmDialog = new AlarmDialog(requireContext(), event, switch_alarm);
               alarmDialog.show();
           }
           else {
               Toast.makeText(requireContext(), "Alarm deleted", Toast.LENGTH_SHORT).show();
               MyAlarmManager.cancelAlarm(requireContext(), event);
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
            Intent intent = new Intent(requireContext(), AddOrEditEventScreen.class);
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
                deleteSingleEvent(event);
            }
            else {
                createDeleteDialog();
            }

        }
    }

    /**
     * Start rv users shimmer.
     */
    public void startRVUsersShimmer() {
        rv_users.setVisibility(View.INVISIBLE);
        shimmer_rv_users.setVisibility(View.VISIBLE);
        shimmer_rv_users.startShimmer();
    }

    /**
     * Stop rv users shimmer.
     */
    public void stopRVUsersShimmer() {
        shimmer_rv_users.stopShimmer();
        shimmer_rv_users.setVisibility(View.GONE);
        rv_users.setVisibility(View.VISIBLE);
    }

    /**
     * Create delete dialog.
     */
    public void createDeleteDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText("Delete");

        Log.d(Utils.LOG_TAG, event.toString());

        TextView tv_only_this_event = bottomSheetDialog.findViewById(R.id.tv_only_this_event);
        tv_only_this_event.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            deleteSingleEvent(event);
        });

        TextView tv_all_events_in_chain = bottomSheetDialog.findViewById(R.id.tv_all_events_in_chain);
        tv_all_events_in_chain.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            deleteAllEventsInChain(event.getChainId());
        });

        bottomSheetDialog.show();
    }

    /**
     * Delete single event.
     *
     * @param event the event
     */
    public void deleteSingleEvent(@NonNull CalendarEvent event){
        loadingDialog.setMessage("Deleting event");
        loadingDialog.show();

        FirebaseUtils.deleteAll(FirebaseUtils.getCurrentGroupDatabase(), event.getPrivateId(),
                () -> {
                        startActivity(new Intent(requireContext(), MainActivity.class)
                            .setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT));
                        FCMSend.sendNotificationsToAllUsersWithTopic(requireContext(), event, Utils.DELETE_EVENT_NOTIFICATION_CODE);
                });
    }

    /**
     * Delete all events in chain.
     *
     * @param chain_key the chain key
     */
    public void deleteAllEventsInChain(String chain_key){
        loadingDialog.setMessage("Deleting all events in the chain");
        loadingDialog.show();

        FirebaseUtils.getAllEventsDatabase().orderByChild("chainId").equalTo(chain_key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot event : snapshot.getChildren()){
                            String private_key = event.getKey();
                            FirebaseUtils.deleteAll(FirebaseUtils.getCurrentGroupDatabase(), private_key, null);
                        }

                        startActivity(new Intent(requireContext(), MainActivity.class)
                                .setAction(CalendarFragment.ACTION_MOVE_TO_CALENDAR_FRAGMENT));

                        FCMSend.sendNotificationsToAllUsersWithTopic(requireContext(), event, Utils.DELETE_EVENT_NOTIFICATION_CODE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /**
     * Create copy dialog.
     */
    public void createCopyDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setDismissWithAnimation(true);

        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottom_sheet_dialog_title = bottomSheetDialog.findViewById(R.id.tv_bottom_sheet_dialog_title);
        tv_bottom_sheet_dialog_title.setText(R.string.copy);

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

    /**
     * Copy single event.
     *
     * @param event the event
     */
    public void copySingleEvent(@NonNull CalendarEvent event){
        String private_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();

        CalendarEvent copy = event.copy();

        copy.setPrivateId(private_key);
        copy.setChainId(private_key);

        copy.clearFrequencyData();

        copy.updateChainStartDate(copy.receiveStartDate());
        copy.updateChainEndDate(copy.receiveEndDate());

        Intent intent = new Intent(requireContext(), AddOrEditEventScreen.class);
        intent.putExtra(CalendarEvent.KEY_EVENT, copy);

        startActivity(intent);
    }

    /**
     * Copy all events in chain.
     *
     * @param event the event
     */
    public void copyAllEventsInChain(@NonNull CalendarEvent event) {
        String private_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();
        String chain_key = "Event" + FirebaseUtils.getAllEventsDatabase().push().getKey();

        CalendarEvent copy = event.copy();

        copy.setPrivateId(private_key);
        copy.setChainId(chain_key);

        Intent intent = new Intent(requireContext(), AddOrEditEventScreen.class);
        intent.putExtra(CalendarEvent.KEY_EVENT, copy);

        startActivity(intent);
    }
}