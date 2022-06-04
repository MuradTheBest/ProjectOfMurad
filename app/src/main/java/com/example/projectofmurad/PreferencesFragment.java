package com.example.projectofmurad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.calendar.AlarmDialog;
import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.notifications.FCMSend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment implements View.OnClickListener,
        AlarmDialog.OnAutoAlarmSetListener {


    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    private SwitchMaterial switch_notifications;
    private SwitchMaterial switch_add_event;
    private SwitchMaterial switch_edit_event;
    private SwitchMaterial switch_delete_event;

    private SwitchMaterial switch_auto_alarm_set;
    private SwitchMaterial switch_auto_alarm_move;
    private TextView tv_alarm_before;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private CircleImageView iv_profile_picture;

    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_phone;
    private TextView tv_madrich;

    private SwitchMaterial switch_visible_to_no_one;
    private SwitchMaterial switch_visible_to_madrich;
    private SwitchMaterial switch_visible_to_all;

    private final OnFailureListener onFailureListener = e -> Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btn_change_group = view.findViewById(R.id.btn_change_group);
        btn_change_group.setOnClickListener(v -> startActivity(new Intent(requireContext(), ShowGroupsScreen.class)));

        switch_notifications = view.findViewById(R.id.switch_notifications);
        switch_notifications.setOnClickListener(this);

        switch_add_event = view.findViewById(R.id.switch_add_event);
        switch_add_event.setOnClickListener(this);

        switch_edit_event = view.findViewById(R.id.switch_edit_event);
        switch_edit_event.setOnClickListener(this);

        switch_delete_event = view.findViewById(R.id.switch_delete_event);
        switch_delete_event.setOnClickListener(this);

        switch_auto_alarm_set = view.findViewById(R.id.switch_auto_alarm_set);
        switch_auto_alarm_set.setOnClickListener(this);

        switch_auto_alarm_move = view.findViewById(R.id.switch_auto_alarm_move);
        switch_auto_alarm_move.setOnClickListener(this);

        tv_alarm_before = view.findViewById(R.id.tv_alarm_before);

        iv_profile_picture = view.findViewById(R.id.iv_profile_picture);

        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_madrich = view.findViewById(R.id.tv_madrich);

        switch_visible_to_no_one = view.findViewById(R.id.switch_visible_to_no_one);
        switch_visible_to_no_one.setOnClickListener(this);
        switch_visible_to_madrich = view.findViewById(R.id.switch_visible_to_madrich);
        switch_visible_to_madrich.setOnClickListener(this);
        switch_visible_to_all = view.findViewById(R.id.switch_visible_to_all);
        switch_visible_to_all.setOnClickListener(this);

        CardView cv_profile_data = view.findViewById(R.id.cv_profile_data);
        cv_profile_data.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileScreen.class)));

        AppCompatImageView iv_go_to_profile = view.findViewById(R.id.iv_go_to_profile);
        iv_go_to_profile.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileScreen.class)));

        sp = requireActivity().getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        FirebaseUtils.getCurrentUserData().observe(getViewLifecycleOwner(), this::getUserData);
        FirebaseUtils.getCurrentUserGroupData().observe(getViewLifecycleOwner(), this::getUserGroupData);
    }

    private void getUserGroupData(@NonNull UserGroupData userGroupData) {
        Log.d(Utils.LOG_TAG, userGroupData.toString());

        tv_madrich.setVisibility(userGroupData.isMadrich() ? View.VISIBLE : View.GONE);

        switch_add_event.setChecked(userGroupData.isSubscribedToAddEvent());
        switch_edit_event.setChecked(userGroupData.isSubscribedToEditEvent());
        switch_delete_event.setChecked(userGroupData.isSubscribedToDeleteEvent());

        switch_notifications.setChecked(userGroupData.isSubscribedToAddEvent()
                || userGroupData.isSubscribedToEditEvent()
                || userGroupData.isSubscribedToDeleteEvent());

        switch (userGroupData.getShow()){
            case 2:
                switch_visible_to_all.setChecked(true);
                switch_visible_to_all.setEnabled(false);
                break;
            case 1:
                switch_visible_to_madrich.setChecked(true);
                switch_visible_to_madrich.setEnabled(false);
                break;
            case 0:
                switch_visible_to_no_one.setChecked(true);
                switch_visible_to_no_one.setEnabled(false);
                break;
        }
    }

    public void getUserData(@NonNull UserData userData){

        switch_add_event.setEnabled(switch_notifications.isChecked());
        switch_edit_event.setEnabled(switch_notifications.isChecked());
        switch_delete_event.setEnabled(switch_notifications.isChecked());

        Glide.with(this).load(userData.getPicture())
                .error(R.drawable.sample_profile_picture)
                .placeholder(R.drawable.sample_profile_picture).centerCrop().into(iv_profile_picture);

        tv_username.setText(userData.getUsername());
        tv_email.setText(userData.getEmail());
        tv_phone.setText(userData.getPhone());

        boolean autoAlarmSet = sp.getBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_SET, false);
        boolean autoAlarmMove = sp.getBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_MOVE, false);
        String prior = sp.getString("prior", "");
        tv_alarm_before.setText(String.format("Alarms will work %s before event's start", prior));

        tv_alarm_before.setTextColor(autoAlarmSet ? Color.BLACK : Color.LTGRAY);

        switch_auto_alarm_set.setChecked(autoAlarmSet);
        switch_auto_alarm_move.setChecked(autoAlarmMove);
    }

    @Override
    public void onClick(View v) {
/*        if (v == switch_notifications){

            switch_add_event.setChecked(switch_notifications.isChecked());
            switch_edit_event.setChecked(switch_notifications.isChecked());
            switch_delete_event.setChecked(switch_notifications.isChecked());

            switch_add_event.setEnabled(switch_notifications.isChecked());
            switch_edit_event.setEnabled(switch_notifications.isChecked());
            switch_delete_event.setEnabled(switch_notifications.isChecked());

            switch_add_event.callOnClick();
            switch_edit_event.callOnClick();
            switch_delete_event.callOnClick();
        }
        if (v == switch_add_event){
            if (switch_add_event.isChecked()){
                FirebaseUtils.getFirebaseMessaging().subscribeToTopic(FCMSend.ADD_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToAddEvent").setValue(true);
                                    Toast.makeText(getContext(), "You will be notified when there will be new event added", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_add_event.toggle();
                                }
                            }
                        });
            }
            else {
                FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.ADD_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToAddEvent").setValue(false);
                                    Toast.makeText(getContext(), "You will stop being notified when there will be new event added", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_add_event.toggle();
                                }
                            }
                        });
            }
        }
        if (v == switch_edit_event){
            if (switch_edit_event.isChecked()){
                FirebaseUtils.getFirebaseMessaging().subscribeToTopic(FCMSend.EDIT_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToEditEvent").setValue(true);
                                    Toast.makeText(getContext(), "You will be notified when any event will be edited", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_edit_event.toggle();
                                }
                            }
                        });
            }
            else {
                FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.EDIT_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToEditEvent").setValue(false);
                                    Toast.makeText(getContext(), "You will stop being notified when any event will be edited", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_edit_event.toggle();
                                }
                            }
                        });
            }

        }
        if (v == switch_delete_event){
            if (switch_delete_event.isChecked()){
                FirebaseUtils.getFirebaseMessaging().subscribeToTopic(FCMSend.DELETE_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToDeleteEvent").setValue(true);
                                    Toast.makeText(getContext(), "You will be notified when any event will be deleted", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_delete_event.toggle();
                                }
                            }
                        });
            }
            else {
                FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.DELETE_EVENT_TOPIC).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUtils.getCurrentUserDataRef().child("subscribedToDeleteEvent").setValue(false);
                                    Toast.makeText(getContext(), "You will stop being notified when any event will be deleted", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switch_delete_event.toggle();
                                }
                            }
                        });
            }
        }
        if (v == switch_auto_alarm_set){
            if (switch_auto_alarm_set.isChecked()){
                AlarmDialog alarmDialog = new AlarmDialog(requireContext(), switch_auto_alarm_set, this);
                alarmDialog.show();
            }
            else {
                editor.putBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_SET, false);
            }

            editor.apply();
        }*/

        if (v instanceof SwitchMaterial){
            boolean isChecked = ((SwitchMaterial) v).isChecked();

            if (v == switch_notifications){
                switch_add_event.setChecked(switch_notifications.isChecked());
                switch_edit_event.setChecked(switch_notifications.isChecked());
                switch_delete_event.setChecked(switch_notifications.isChecked());

                switch_add_event.setEnabled(switch_notifications.isChecked());
                switch_edit_event.setEnabled(switch_notifications.isChecked());
                switch_delete_event.setEnabled(switch_notifications.isChecked());

                switch_add_event.callOnClick();
                switch_edit_event.callOnClick();
                switch_delete_event.callOnClick();
            }
            else if(v == switch_visible_to_no_one){
                FirebaseUtils.getCurrentUserGroupDataRef().child("show").setValue(Show.NO_ONE.getValue()).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                switch_visible_to_no_one.setEnabled(false);

                                switch_visible_to_all.setChecked(false);
                                switch_visible_to_madrich.setEnabled(true);

                                switch_visible_to_madrich.setChecked(false);
                                switch_visible_to_all.setEnabled(true);

                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        "From now your results will be visible to no one", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(onFailureListener);
            }
            else if(v == switch_visible_to_madrich){
                FirebaseUtils.getCurrentUserGroupDataRef().child("show").setValue(Show.MADRICH.getValue()).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                switch_visible_to_madrich.setEnabled(false);

                                switch_visible_to_no_one.setChecked(false);
                                switch_visible_to_all.setChecked(false);

                                switch_visible_to_no_one.setEnabled(true);
                                switch_visible_to_all.setEnabled(true);

                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        "From now your results will be visible to madrichs only", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(onFailureListener);
            }
            else if(v == switch_visible_to_all){
                FirebaseUtils.getCurrentUserGroupDataRef().child("show").setValue(Show.ALL.getValue()).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                switch_visible_to_all.setEnabled(false);

                                switch_visible_to_no_one.setChecked(false);
                                switch_visible_to_madrich.setChecked(false);

                                switch_visible_to_no_one.setEnabled(true);
                                switch_visible_to_madrich.setEnabled(true);

                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        "From now your results will be visible to everyone", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(onFailureListener);
            }
            else if (v == switch_auto_alarm_set){
                if (switch_auto_alarm_set.isChecked()){
                    AlarmDialog alarmDialog = new AlarmDialog(requireContext(), switch_auto_alarm_set, this);
                    alarmDialog.show();
                }
                else {
                    editor.putBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_SET, false);
                    tv_alarm_before.setTextColor(Color.LTGRAY);
                    editor.apply();
                }

            }
            else if (v == switch_auto_alarm_move){
                editor.putBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_MOVE, switch_auto_alarm_move.isChecked());
                editor.apply();
            }
            else {

                SwitchMaterial switchCompat = (SwitchMaterial) v;

                String topic = (v == switch_add_event ? FCMSend.getTopic(FCMSend.ADD_EVENT_TOPIC)
                        : (v == switch_edit_event) ? FCMSend.getTopic(FCMSend.EDIT_EVENT_TOPIC)
                        : FCMSend.getTopic(FCMSend.DELETE_EVENT_TOPIC));

                String subscription = (v == switch_add_event) ? "subscribedToAddEvent"
                        : (v == switch_edit_event) ? "subscribedToEditEvent"
                        : "subscribedToDeleteEvent";

                String subscribedMsg = (v == switch_add_event ? "You will be notified when there will be new event added"
                        : (v == switch_edit_event) ? "You will be notified when any event will be edited"
                        : "You will be notified when any event will be deleted");

                String unsubscribedMsg = (v == switch_add_event ? "You will stop being notified when there will be new event added"
                        : (v == switch_edit_event) ? "You will stop being notified when any event will be edited"
                        : "You will stop being notified when any event will be deleted");


                if (switchCompat.isChecked()) {
                    FirebaseUtils.getFirebaseMessaging().subscribeToTopic(topic).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUtils.getCurrentUserGroupDataRef().child(subscription).setValue(true);
//                                        Toast.makeText(getContext(), subscribedMsg, Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), subscribedMsg);
                                    }
                                    else {
//                                        Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), "Ups... Something went wrong");
                                        switchCompat.toggle();
                                    }
                                }
                            });
                }
                else {
                    FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(topic).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUtils.getCurrentUserGroupDataRef().child(subscription).setValue(false);
//                                        Toast.makeText(getContext(), unsubscribedMsg, Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), unsubscribedMsg);
                                    }
                                    else {
//                                        Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), "Ups... Something went wrong");
                                        switchCompat.toggle();
                                    }
                                }
                            });
                }

                FirebaseUtils.getCurrentUserDataRef().child(subscription).setValue(switchCompat.isChecked()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    if(switchCompat.isChecked()){
//                                        Toast.makeText(getContext(), subscribedMsg, Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), subscribedMsg);
                                    }
                                    else {
//                                        Toast.makeText(getContext(), unsubscribedMsg, Toast.LENGTH_SHORT).show();
                                        Utils.showToast(getContext(), unsubscribedMsg);
                                    }

                                }
                                else {
                                    Utils.showToast(getContext(), "Ups... Something went wrong");
//                                    Toast.makeText(getContext(), "Ups... Something went wrong", Toast.LENGTH_SHORT).show();
                                    switchCompat.toggle();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onAutoAlarmSet(long before, String prior) {

        Log.d(Utils.LOG_TAG, "before is " + before);
        Log.d(Utils.LOG_TAG, "prior is " + prior);


        editor.putBoolean(UserData.KEY_SUBSCRIBED_TO_AUTO_ALARM_SET, true);
        editor.putLong("before", before);
        editor.putString("prior", prior);

        tv_alarm_before.setText(String.format("Alarms will work %s before event's start", prior));
        tv_alarm_before.setTextColor(Color.BLACK);

        editor.apply();
    }
}