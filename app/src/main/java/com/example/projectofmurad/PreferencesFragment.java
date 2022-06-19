package com.example.projectofmurad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment implements View.OnClickListener{


    /**
     * Instantiates a new Preferences fragment.
     */
    public PreferencesFragment() {
        // Required empty public constructor
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

    private CircleImageView iv_profile_picture;

    private TextView tv_username;
    private TextView tv_email;
    private TextView tv_phone;
    private TextView tv_madrich;

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

        iv_profile_picture = view.findViewById(R.id.iv_profile_picture);

        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_madrich = view.findViewById(R.id.tv_madrich);

        CardView cv_profile_data = view.findViewById(R.id.cv_profile_data);
        cv_profile_data.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileScreen.class)));

        AppCompatImageView iv_go_to_profile = view.findViewById(R.id.iv_go_to_profile);
        iv_go_to_profile.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileScreen.class)));

        FirebaseUtils.getCurrentUserData().observe(getViewLifecycleOwner(), this::getUserData);
        FirebaseUtils.getCurrentUserGroupData().observe(getViewLifecycleOwner(), this::getUserGroupData);
    }

    private void getUserGroupData(@NonNull UserGroupData userGroupData) {
        tv_madrich.setVisibility(userGroupData.isMadrich() ? View.VISIBLE : View.GONE);

        switch_add_event.setChecked(userGroupData.isSubscribedToAddEvent());
        switch_edit_event.setChecked(userGroupData.isSubscribedToEditEvent());
        switch_delete_event.setChecked(userGroupData.isSubscribedToDeleteEvent());

        switch_notifications.setChecked(userGroupData.isSubscribedToAddEvent()
                || userGroupData.isSubscribedToEditEvent()
                || userGroupData.isSubscribedToDeleteEvent());

        switch_add_event.setEnabled(switch_notifications.isChecked());
        switch_edit_event.setEnabled(switch_notifications.isChecked());
        switch_delete_event.setEnabled(switch_notifications.isChecked());
    }

    /**
     * Get user data.
     *
     * @param userData the user data
     */
    public void getUserData(@NonNull UserData userData){
        Glide.with(this).load(userData.getPicture())
                .error(R.drawable.sample_profile_picture)
                .placeholder(R.drawable.sample_profile_picture)
                .centerCrop().into(iv_profile_picture);

        tv_username.setText(userData.getUsername());
        tv_email.setText(userData.getEmail());
        tv_phone.setText(userData.getPhone());
    }

    @Override
    public void onClick(View v) {
        if (v instanceof SwitchMaterial){
            if (v == switch_notifications){
                switch_add_event.setEnabled(switch_notifications.isChecked());
                switch_edit_event.setEnabled(switch_notifications.isChecked());
                switch_delete_event.setEnabled(switch_notifications.isChecked());
            }
            else {
                SwitchMaterial switchMaterial = (SwitchMaterial) v;

                String topic = (v == switch_add_event ? FCMSend.getTopic(FCMSend.ADD_EVENT_TOPIC)
                        : (v == switch_edit_event) ? FCMSend.getTopic(FCMSend.EDIT_EVENT_TOPIC)
                        : FCMSend.getTopic(FCMSend.DELETE_EVENT_TOPIC));

                String subscription = (v == switch_add_event) ? "subscribedToAddEvent"
                        : (v == switch_edit_event) ? "subscribedToEditEvent"
                        : "subscribedToDeleteEvent";

                String subscribedMsg = (v == switch_add_event) ? "You will be notified when there will be new event added"
                        : (v == switch_edit_event) ? "You will be notified when any event will be edited"
                        : "You will be notified when any event will be deleted";

                String unsubscribedMsg = (v == switch_add_event) ? "You will stop being notified when there will be new event added"
                        : (v == switch_edit_event) ? "You will stop being notified when any event will be edited"
                        : "You will stop being notified when any event will be deleted";


                if (switchMaterial.isChecked()) {
                    FirebaseUtils.getFirebaseMessaging().subscribeToTopic(topic).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUtils.getCurrentUserGroupDataRef().child(subscription).setValue(true);
                                        Utils.showToast(getContext(), subscribedMsg);
                                    }
                                    else {
                                        Utils.showToast(getContext(), "Ups... Something went wrong");
                                        Log.d(Utils.LOG_TAG, task.getException().getMessage());
                                        switchMaterial.toggle();
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
                                        Utils.showToast(getContext(), unsubscribedMsg);
                                    }
                                    else {
                                        Utils.showToast(getContext(), "Ups... Something went wrong");
                                        Log.d(Utils.LOG_TAG, task.getException().getMessage());
                                        switchMaterial.toggle();
                                    }
                                }
                            });
                }

                FirebaseUtils.getCurrentUserDataRef().child(subscription).setValue(switchMaterial.isChecked()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    if(switchMaterial.isChecked()){
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
                                    switchMaterial.toggle();
                                }
                            }
                        });
            }
        }
    }
}