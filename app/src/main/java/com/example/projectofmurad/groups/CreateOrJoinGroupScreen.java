package com.example.projectofmurad.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;

import com.example.projectofmurad.LogInScreen;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.ColorPickerDialog;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * The type Create or join group screen.
 */
public class CreateOrJoinGroupScreen extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_choose_color;
    private TextView tv_username;
    private TextView tv_create_new_group;

    private LinearLayoutCompat ll_create_group;

    private TextInputLayout et_new_group_name;
    private TextInputLayout et_new_group_key;
    private TextInputLayout et_new_trainer_code;
    private TextInputLayout et_new_group_limit;

    private MaterialButton btn_generate_group_key;

    private TextView tv_join_group;

    private LinearLayoutCompat ll_join_group;

    private TextInputLayout et_group_key;
    private TextInputLayout et_trainer_code;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_group_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        if (!FirebaseUtils.isUserLoggedIn()){
            startActivity(Utils.getIntentClearTop(new Intent(this, LogInScreen.class)));
        }

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

        et_new_group_key.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                btn_generate_group_key.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                Utils.setText(et_new_group_key, s.toString().trim());
            }
        });

        tv_choose_color = findViewById(R.id.tv_choose_color);
        tv_choose_color.setOnClickListener(v -> createColorPickerDialog());

        btn_generate_group_key = findViewById(R.id.btn_generate_group_key);
        btn_generate_group_key.setOnClickListener(v -> {
            String randomKey = FirebaseUtils.groups.push().getKey().replace("-", "");
            Utils.setText(et_new_group_key, randomKey);
            et_new_group_key.getEditText().setSelection(randomKey.length());
        });

        MaterialButton btn_create_group = findViewById(R.id.btn_create_group);
        btn_create_group.setOnClickListener(this::createGroup);

        tv_join_group = findViewById(R.id.tv_join_group);
        tv_join_group.setOnClickListener(this);

        ll_join_group = findViewById(R.id.ll_join_group);

        et_group_key = findViewById(R.id.et_group_key);

        et_trainer_code = findViewById(R.id.et_trainer_code);

        MaterialButton btn_join_group = findViewById(R.id.btn_join_group);
        btn_join_group.setOnClickListener(this::joinGroup);

        Utils.addDefaultTextChangedListener(et_new_group_name, et_new_group_key, et_new_trainer_code,
                et_new_group_limit, et_group_key, et_trainer_code);

        tv_create_new_group.callOnClick();

        checkIntent();
    }

    private void checkIntent() {
        Intent intent = getIntent();

        if (!intent.hasExtra(Utils.KEY_LINK)){
            return;
        }

        String link = intent.getStringExtra(Utils.KEY_LINK);

        Pattern groupJoinPattern = Pattern.compile("www.bikeriders.com/group-join-link/[a-zA-Z0-9-._]");

        if (groupJoinPattern.matcher(link).find()){
            tv_join_group.callOnClick();
            String groupKey = link.substring(link.lastIndexOf("/") + 1);
            Utils.setText(et_group_key, groupKey);
        }

    }

    private void createColorPickerDialog() {
        ColorPickerDialog colorPicker = new ColorPickerDialog(this);

        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {

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

    /**
     * Create group.
     *
     * @param view the view
     */
    public void createGroup(View view){
        String name = Utils.getText(et_new_group_name);
        String key = Utils.getText(et_new_group_key);
        String trainerCode = Utils.getText(et_new_trainer_code);
        String limit = Utils.getText(et_new_group_limit);

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
        if (!limit.isEmpty() && Integer.parseInt(limit) < 0) {
            et_new_group_limit.setError("User's limit can't be less than 0");
            filled = false;
        }

        if (filled){
            checkKeyToCreateGroup(key);
        }
    }

    /**
     * Join group.
     *
     * @param view the view
     */
    public void joinGroup(View view){
        String key = Utils.getText(et_group_key);

        if (key.isEmpty()){
            et_group_key.setError("Key required");
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
                                null);
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
                if (!snapshot.exists() || !snapshot.hasChildren()){
                    return;
                }

                for (DataSnapshot group : snapshot.getChildren()){
                    Group g = group.getValue(Group.class);

                    if (Objects.equals(g.getKey(), key)){
                        if(g.getLimit() > 0 && (g.getUsersNumber() + 1 > g.getLimit())){
                            loadingDialog.dismiss();

                            Utils.createAlertDialog(CreateOrJoinGroupScreen.this, null,
                                    "This group is full",
                                    getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                                    null, null,
                                    null);
                            return;
                        }

                        String code = Utils.getText(et_trainer_code);

                        if (!code.isEmpty() && g.getMadrichCode() != Integer.parseInt(code)) {
                            et_trainer_code.setError(getString(R.string.madrcih_code_invalid));
                            loadingDialog.dismiss();
                            return;
                        }

                        if (code.isEmpty()) code = "0";

                        joinGroup(key, g.getColor(), g.getMadrichCode() == Integer.parseInt(code));
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

    /**
     * Create group exists dialog.
     *
     * @param exists the exists
     */
    public void createGroupExistsDialog(boolean exists){
        loadingDialog.dismiss();
        Utils.createAlertDialog(this, null,
                "Group with this key " + (exists ? "already exists" : "doesn't exist"),
                getString(R.string.ok), (dialog, which) -> dialog.dismiss(),
                null, null,
                null);
    }

    /**
     * Create group.
     */
    public void createGroup(){
        String name = Utils.getText(et_new_group_name);
        String key = Utils.getText(et_new_group_key);
        String trainerCode = Utils.getText(et_new_trainer_code);
        String limit = Utils.getText(et_new_group_limit);
        limit = limit.isEmpty() ? "0" : limit;
        int color = tv_choose_color.getCurrentTextColor();

        Group group = new Group(name, key, "", Integer.parseInt(trainerCode), color, 1, Integer.parseInt(limit));
        FirebaseUtils.groups.child(key).setValue(group)
                .addOnSuccessListener(unused ->
                        FirebaseUtils.addGroupToCurrentUser(key, true,
                                () -> {
                                    loadingDialog.dismiss();
                                    FirebaseUtils.changeGroup(CreateOrJoinGroupScreen.this, key, color);
                                    startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class));
                                },
                                e -> {
                                    loadingDialog.dismiss();
                                    Toast.makeText(CreateOrJoinGroupScreen.this,
                                            "Your group was created but joining to it failed" +
                                                    "\nPlease try again", Toast.LENGTH_SHORT).show();
                                }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CreateOrJoinGroupScreen.this,
                            "Creating new group  failed" + "\nPlease try again", Toast.LENGTH_SHORT).show();
                });

    }

    /**
     * Join group.
     *
     * @param key       the key
     * @param color     the color
     * @param isMadrich the is madrich
     */
    public void joinGroup(String key, int color, boolean isMadrich){

        FirebaseUtils.addGroupToCurrentUser(key, isMadrich,
                () -> {
                    loadingDialog.dismiss();
                    FirebaseUtils.groups.child(key).child(Group.KEY_USERS_NUMBER).setValue(ServerValue.increment(1));
                    FirebaseUtils.changeGroup(CreateOrJoinGroupScreen.this, key, color);
                    startActivity(new Intent(CreateOrJoinGroupScreen.this, MainActivity.class));
                },
                e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CreateOrJoinGroupScreen.this,
                            R.string.upps_something_went_wrong,
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClick(View v) {
        tv_create_new_group.setTextColor(getColor(v == tv_create_new_group ? R.color.colorAccent : R.color.gray));
        tv_join_group.setTextColor(getColor(v == tv_join_group ? R.color.colorAccent : R.color.gray));

        ll_create_group.setVisibility(v == tv_create_new_group ? View.VISIBLE : View.GONE);
        ll_join_group.setVisibility(v == tv_join_group ? View.VISIBLE : View.GONE);
    }
}