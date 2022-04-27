package com.example.projectofmurad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.helpers.ViewAnimationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CreateOrJoinGroupScreen extends AppCompatActivity implements View.OnClickListener {

    TextView tv_choose_color;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private TextView tv_username;
    private TextView tv_create_new_group;
    private LinearLayout ll_create_group;
    private EditText et_new_group_name;
    private EditText et_new_group_key;
    private EditText et_new_trainer_code;
    private EditText et_new_group_limit;
    private MaterialButton btn_generate_group_key;
    private MaterialButton btn_create_group;
    private TextView tv_join_group;
    private LinearLayout ll_join_group;
    private EditText et_group_key;
    private MaterialButton btn_join_group;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_or_create_group_screen);
        getSupportActionBar().hide();

        sp = getSharedPreferences("savedData", MODE_PRIVATE);
        editor = sp.edit();

        progressDialog = new ProgressDialog(this);
        Utils.createCustomDialog(progressDialog);

        tv_username = findViewById(R.id.tv_username);

        FirebaseUtils.getCurrentUsername().observe(this, username -> tv_username.setText("Hi, " + username));

        tv_create_new_group = findViewById(R.id.tv_create_new_group);
        tv_create_new_group.setOnClickListener(this);
        ll_create_group = findViewById(R.id.ll_create_group);

        et_new_group_name = ((TextInputLayout) findViewById(R.id.et_new_group_name)).getEditText();
        et_new_group_key = ((TextInputLayout) findViewById(R.id.et_new_group_key)).getEditText();
        et_new_trainer_code = ((TextInputLayout) findViewById(R.id.et_new_trainer_code)).getEditText();
        et_new_group_limit = ((TextInputLayout) findViewById(R.id.et_new_group_limit)).getEditText();

        et_new_group_key.addTextChangedListener(new TextWatcher() {
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
        btn_generate_group_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomKey = FirebaseUtils.groupsDatabase.push().getKey();
                et_new_group_key.setText(randomKey);
                et_new_group_key.setSelection(randomKey.length());
            }
        });

        btn_create_group = findViewById(R.id.btn_create_group);
        btn_create_group.setOnClickListener(this::createGroup);

        tv_join_group = findViewById(R.id.tv_join_group);
        tv_join_group.setOnClickListener(this);
        ll_join_group = findViewById(R.id.ll_join_group);

        et_group_key = ((TextInputLayout) findViewById(R.id.et_group_key)).getEditText();
//        et_trainer_code = ((TextInputLayout) findViewById(R.id.et_trainer_code)).getEditText();

        btn_join_group = findViewById(R.id.btn_join_group);
        btn_join_group.setOnClickListener(this::joinGroup);

        tv_create_new_group.callOnClick();

        Log.d(Utils.LOG_TAG, FirebaseUtils.getCurrentGroupRef().toString());
//        FirebaseUtils.changeGroup("uhwefdsjiufkdvm");
        Log.d(Utils.LOG_TAG, FirebaseUtils.getCurrentGroupRef().toString());
    }

    protected void createColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(this);

        colorPicker.setDefaultColorButton(R.color.colorAccent);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColorButtonSize(30, 30);
        colorPicker.setColorButtonTickColor(Color.BLACK);
        colorPicker.setDismissOnButtonListenerClick(true);

        colorPicker.getPositiveButton().setVisibility(View.GONE);
        colorPicker.getNegativeButton().setVisibility(View.GONE);

        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                tv_choose_color.setTextColor(color);
            }

            @Override
            public void onCancel() {}
        });

        colorPicker.addListenerButton("Generate",
                (v, position, color) -> {
                    tv_choose_color.setTextColor(Utils.generateRandomColor());
                    colorPicker.dismissDialog();
                });

        colorPicker.getDialogViewLayout().findViewById(R.id.buttons_layout).setVisibility(View.VISIBLE);

        colorPicker.show();
    }

    public void createGroup(View view){
        String name = et_new_group_name.getText().toString();
        String key = et_new_group_key.getText().toString();
        String trainerCode = et_new_trainer_code.getText().toString();
        String limit = et_new_group_limit.getText().toString();

        boolean filled = true;

        if (name.isEmpty()) {
            ((TextInputLayout) et_new_group_name.getParent().getParent()).setError("Name required");
            filled = false;
        }
        if (key.isEmpty()) {
            ((TextInputLayout) et_new_group_key.getParent().getParent()).setError("Key required");
            filled = false;
        }
        if (trainerCode.isEmpty()) {
            ((TextInputLayout) et_new_trainer_code.getParent().getParent()).setError("Trainer code required");
            filled = false;
        }
        if (Integer.parseInt(limit) == 0) {
            ((TextInputLayout) et_new_group_limit.getParent().getParent()).setError("User's limit can't be 0");
        }

        if (filled){
            checkKeyToCreateGroup(key);
        }
    }

    public void joinGroup(View view){
        String key = et_group_key.getText().toString();

        if (key.isEmpty()){
            ((TextInputLayout) et_group_key.getParent().getParent()).setError("Key required");
        }
        else {
            checkKeyToJoinGroup(key);
        }
    }

    private void checkKeyToCreateGroup(String key) {
        progressDialog.setMessage("Creating the group");
        progressDialog.show();

        FirebaseUtils.groupsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    return;
                }

                for (DataSnapshot group : snapshot.getChildren()){
                    if (Objects.equals(group.child("key").getValue(String.class), key)){
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
        progressDialog.setMessage("Joining the group");
        progressDialog.show();

        FirebaseUtils.groupsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    return;
                }

                for (DataSnapshot group : snapshot.getChildren()){
                    Group g = group.getValue(Group.class);

                    if (Objects.equals(g.getKey(), key) && (g.getUsersNumber() + 1 <= g.getLimit())){
                        joinGroup(key);
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
        progressDialog.dismiss();
        progressDialog.setProgress(100);
        progressDialog.show();
        progressDialog.setTitle("Group with this key " + (exists ? "already exists" : "doesn't exist"));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", (dialog, which) -> dialog.dismiss());
    }

    public void createGroup(){
        String name = et_new_group_name.getText().toString();
        String key = et_new_group_key.getText().toString();
        String trainerCode = et_new_trainer_code.getText().toString();
        String limit = et_new_group_limit.getText().toString();
        int color = tv_choose_color.getCurrentTextColor();

        Group group = new Group(name, key, Integer.parseInt(trainerCode), color, Integer.parseInt(limit));
        FirebaseUtils.groupsDatabase.child(key).setValue(group).addOnSuccessListener(
                unused -> FirebaseUtils.addGroupToCurrentUser(key,
                        new FirebaseUtils.FirebaseCallback() {
                            @Override
                            public void onFirebaseCallback() {
                                startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class));
                            }
                        }))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
        });

    }

    public void joinGroup(String key){
        FirebaseUtils.addGroupToCurrentUser(key,
                () -> startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class)));
    }

    @Override
    public void onClick(View v) {
//        ll_create_group.setVisibility(v == tv_create_new_group ? View.VISIBLE : View.GONE);
//        ll_join_group.setVisibility(v == tv_join_group ? View.VISIBLE : View.GONE);

        tv_create_new_group.setTextColor(getColor(v == tv_create_new_group ? R.color.colorAccent : R.color.gray));
        tv_join_group.setTextColor(getColor(v == tv_join_group ? R.color.colorAccent : R.color.gray));

        ViewAnimationUtils.expand(v == tv_create_new_group ? ll_create_group : ll_join_group);
        ViewAnimationUtils.collapse(v == tv_create_new_group ? ll_join_group : ll_create_group);
    }
}