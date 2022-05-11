package com.example.projectofmurad.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.MyActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Constants;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CreateOrJoinGroupScreen extends MyActivity implements View.OnClickListener {

    private TextView tv_choose_color;
    private TextView tv_username;
    private TextView tv_create_new_group;

    private LinearLayout ll_create_group;

    private TextInputLayout et_new_group_name;
    private TextInputLayout et_new_group_key;
    private TextInputLayout et_new_trainer_code;
    private TextInputLayout et_new_group_limit;

    private MaterialButton btn_generate_group_key;

    private TextView tv_join_group;

    private LinearLayout ll_join_group;

    private TextInputLayout et_group_key;
    private TextInputLayout et_trainer_code;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group_screen);
        getSupportActionBar().hide();

        loadingDialog = new LoadingDialog(this);

        tv_username = findViewById(R.id.tv_username);

        FirebaseUtils.getCurrentUsername().observe(this, username -> tv_username.setText(String.format(getString(R.string.hi), username)));

        tv_create_new_group = findViewById(R.id.tv_create_new_group);
        tv_create_new_group.setOnClickListener(this);
        ll_create_group = findViewById(R.id.ll_create_group);

        et_new_group_name = findViewById(R.id.et_new_group_name);
        et_new_group_key = findViewById(R.id.et_new_group_key);
        et_new_trainer_code = findViewById(R.id.et_new_trainer_code);
        et_new_group_limit = findViewById(R.id.et_new_group_limit);

        et_new_group_name.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_new_group_name));
        et_new_group_key.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_new_group_key));
        et_new_trainer_code.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_new_trainer_code));
        et_new_group_limit.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_new_group_limit));

        et_new_group_key.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                btn_generate_group_key.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        tv_choose_color = findViewById(R.id.tv_choose_color);
        tv_choose_color.setOnClickListener(v -> createColorPickerDialog());

        btn_generate_group_key = findViewById(R.id.btn_generate_group_key);
        btn_generate_group_key.setOnClickListener(v -> {
            String randomKey = FirebaseUtils.groups.push().getKey().replace("-", "");
            et_new_group_key.getEditText().setText(randomKey);
            et_new_group_key.getEditText().setSelection(randomKey.length());
        });

        MaterialButton btn_create_group = findViewById(R.id.btn_create_group);
        btn_create_group.setOnClickListener(this::createGroup);

        tv_join_group = findViewById(R.id.tv_join_group);
        tv_join_group.setOnClickListener(this);

        ll_join_group = findViewById(R.id.ll_join_group);

        et_group_key = findViewById(R.id.et_group_key);
        et_group_key.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_group_key));

        et_trainer_code = findViewById(R.id.et_trainer_code);
        et_trainer_code.getEditText().addTextChangedListener(Utils.getDefaultTextChangedListener(et_trainer_code));

        MaterialButton btn_join_group = findViewById(R.id.btn_join_group);
        btn_join_group.setOnClickListener(this::joinGroup);

        tv_create_new_group.callOnClick();

        checkIntent();
    }

    private void checkIntent() {

        Intent intent = getIntent();

        if (!intent.hasExtra(Constants.KEY_LINK)){
            return;
        }

        String link = intent.getStringExtra(Constants.KEY_LINK);

        Log.d(Utils.LOG_TAG, "uri is " + link);

        Pattern groupJoinPattern = Pattern.compile("www.bikeriders.com/group-join-link/[a-zA-Z0-9-._]");

        if (groupJoinPattern.matcher(link).find()){
            tv_join_group.callOnClick();
            String groupKey = link.substring(link.lastIndexOf("/") + 1);
            Log.d(Utils.LOG_TAG, "key is " + groupKey);
            et_group_key.getEditText().setText(groupKey);
        }

    }

    private void createColorPickerDialog() {
        ColorPicker colorPicker = Utils.createColorPickerDialog(this, new ColorPicker.OnFastChooseColorListener() {

            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                tv_choose_color.setTextColor(color);
            }

            @Override
            public void onCancel() {}
        });

        colorPicker.addListenerButton(getString(R.string.generate),
                (v, position, color) -> {
                    tv_choose_color.setTextColor(Utils.generateRandomColor());
                    colorPicker.dismissDialog();
                });

        colorPicker.show();
    }

    public void createGroup(View view){
        String name = et_new_group_name.getEditText().getText().toString();
        String key = et_new_group_key.getEditText().getText().toString();
        String trainerCode = et_new_trainer_code.getEditText().getText().toString();
        String limit = et_new_group_limit.getEditText().getText().toString();

        boolean filled = true;

        if (name.isEmpty()) {
            et_new_group_name.setError("Name required");
            filled = false;
        }
        if (key.isEmpty()) {
            et_new_group_key.setError("Key required");
            filled = false;
        }
        if (trainerCode.isEmpty()) {
            et_new_trainer_code.setError("Trainer code required");
            filled = false;
        }
        if (!limit.isEmpty() && Integer.parseInt(limit) < 1) {
            et_new_group_limit.setError("User's limit can't be less than 1");
        }

        if (filled){
            checkKeyToCreateGroup(key);
        }
    }

    public void joinGroup(View view){
        String key = et_group_key.getEditText().getText().toString();

        if (key.isEmpty()){
            ((TextInputLayout) et_group_key.getParent().getParent()).setError("Key required");
        }
        else {
            checkKeyToJoinGroup(key);
        }
    }

    private void checkKeyToCreateGroup(String key) {
        loadingDialog.setMessage(R.string.creating_the_group);
        loadingDialog.show();

        FirebaseUtils.groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() && !snapshot.hasChildren()){
                    return;
                }

                for (DataSnapshot group : snapshot.getChildren()){
                    if (Objects.equals(group.child(Group.KEY_GROUP_KEY).getValue(String.class), key)){
                        createGroupExistsDialog(true);
                        return;
                    }
                }

                createGroup();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkKeyToJoinGroup(String key) {
        loadingDialog.setMessage(R.string.checking_entered_group_key);
        loadingDialog.show();

        FirebaseUtils.getCurrentUserGroups().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> keys) {
                for (String groupKey : keys){
                    if (groupKey.equals(key)) {
                        loadingDialog.dismiss();
                        
                        Utils.createAlertDialog(CreateOrJoinGroupScreen.this,
                                null, "You are already in group with this key",
                                getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                                null, null,
                                null).show();
                        return;
                    }
                }
                checkGroupMatch(key);
            }
        });
    }

    private void checkGroupMatch(String key) {
        FirebaseUtils.groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    return;
                }

                for (DataSnapshot group : snapshot.getChildren()){
                    Group g = group.getValue(Group.class);
                    Log.d(Utils.LOG_TAG, g.toString());

                    if (Objects.equals(g.getKey(), key)){
                        if(g.getLimit() > 0 && (g.getUsersNumber() + 1 > g.getLimit())){
                            loadingDialog.dismiss();

                            Utils.createAlertDialog(CreateOrJoinGroupScreen.this, null,
                                    "This group is full",
                                    getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                                    null, null,
                                    null).show();
                            return;
                        }

                        String code = et_trainer_code.getEditText().getText().toString();

                        if (!code.isEmpty() && g.getMadrichCode() != Integer.parseInt(code)) {
                            et_trainer_code.setError(getString(R.string.madrcih_code_invalid));
                            loadingDialog.dismiss();
                            return;
                        }

                        if (code.isEmpty()) code = "0";

                        joinGroup(key, g.getUsersNumber(), g.getMadrichCode() == Integer.parseInt(code));
                        return;
                    }
                }

                createGroupExistsDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createGroupExistsDialog(boolean exists){
        loadingDialog.dismiss();
        Utils.createAlertDialog(this, null,
                "Group with this key " + (exists ? "already exists" : "doesn't exist"),
                getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                null, null,
                null).show();
    }

    public void createGroup(){
        String name = et_new_group_name.getEditText().getText().toString();
        String key = et_new_group_key.getEditText().getText().toString();
        String trainerCode = et_new_trainer_code.getEditText().getText().toString();
        String limit = et_new_group_limit.getEditText().getText().toString();
        limit = limit.isEmpty() ? "0" : limit;
        int color = tv_choose_color.getCurrentTextColor();

        Group group = new Group(name, key, "", Integer.parseInt(trainerCode), color, 1, Integer.parseInt(limit));
        FirebaseUtils.groups.child(key).setValue(group).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseUtils.addGroupToCurrentUser(key, true,
                                () -> {
                                    loadingDialog.dismiss();
                                    FirebaseUtils.changeGroup(CreateOrJoinGroupScreen.this, key);
                                    startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class));
                                },
                                e -> {
                                    loadingDialog.dismiss();
                                    Toast.makeText(CreateOrJoinGroupScreen.this,
                                            "Your group was created but joining to it failed" +
                                                    "\nPlease try again", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CreateOrJoinGroupScreen.this,
                            "Creating new group  failed" + "\nPlease try again", Toast.LENGTH_SHORT).show();
                });

    }

    public void joinGroup(String key, int usersNumber, boolean isMadrich){

        FirebaseUtils.addGroupToCurrentUser(key, isMadrich,
                new FirebaseUtils.FirebaseCallback() {
                    @Override
                    public void onFirebaseCallback() {
                        loadingDialog.dismiss();
                        FirebaseUtils.groups.child(key).child(Group.KEY_USERS_NUMBER).setValue(usersNumber+1);
                        FirebaseUtils.changeGroup(CreateOrJoinGroupScreen.this, key);
                        startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class));
                    }
                },
                new FirebaseUtils.FirebaseFailureCallback() {
                    @Override
                    public void onFirebaseFailure(Exception e) {
                        loadingDialog.dismiss();
                        Toast.makeText(CreateOrJoinGroupScreen.this,
                                R.string.upps_something_went_wrong,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        tv_create_new_group.setTextColor(getColor(v == tv_create_new_group ? R.color.colorAccent : R.color.gray));
        tv_join_group.setTextColor(getColor(v == tv_join_group ? R.color.colorAccent : R.color.gray));

//        ViewAnimationUtils.expand(v == tv_create_new_group ? ll_create_group : ll_join_group);
//        ViewAnimationUtils.collapse(v == tv_join_group ? ll_create_group : ll_join_group);

//        animate(v == tv_create_new_group ? ll_create_group : ll_join_group);
        ll_create_group.setVisibility(v == tv_create_new_group ? View.VISIBLE : View.GONE);
        ll_join_group.setVisibility(v == tv_join_group ? View.VISIBLE : View.GONE);
    }

    public static void animate(ViewGroup viewGroup){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(300);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

//        TransitionManager.beginDelayedTransition(viewGroup, trans);
        TransitionManager.beginDelayedTransition(viewGroup, changeBounds);


    }
}