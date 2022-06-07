package com.example.projectofmurad.groups;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.calendar.UsersAdapter;
import com.example.projectofmurad.helpers.ColorPickerDialog;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.example.projectofmurad.utils.ViewAnimationUtils;
import com.example.projectofmurad.notifications.FCMSend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * The type Group info screen madrich.
 */
public class GroupInfoScreenMadrich extends GroupInfoScreen implements View.OnLongClickListener,
        UsersAdapter.OnUserExpandListener,
        UsersAdapter.OnUserLongClickListener {

    private Menu menu;


    // constant to compare
    // the activity result code
    public final static int SELECT_PICTURE = 200;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        et_group_name.setOnLongClickListener(this);
        et_group_description.setOnLongClickListener(this);
        et_group_key.setOnLongClickListener(this);
        et_trainer_code.setOnLongClickListener(this);
        et_group_limit.setOnLongClickListener(this);

        tv_choose_color.setOnLongClickListener(v -> createColorPickerDialog());
        iv_group_picture.setOnLongClickListener(v -> chooseGroupPicture());
    }

    private boolean createColorPickerDialog() {
        edit(true);

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

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.save_group).setVisible(false);
        menu.findItem(R.id.delete_group).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    // methods to control the operations that will happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if (et_group_name.getVisibility() == View.VISIBLE){
                    getGroupData();
                }
                else {
                    onBackPressed();
                }
                break;
            case R.id.save_group:
                saveGroup();
                break;
            case R.id.delete_group:
                FirebaseUtils.createReAuthenticateDialog(this, this::deleteGroup);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteGroup() {
        FirebaseUtils.groups.child(group.getKey()).removeValue();
        FirebaseUtils.getGroupDatabase(group.getKey()).removeValue();

        FirebaseUtils.deleteAll(FirebaseUtils.usersDatabase, group.getKey(), new FirebaseUtils.FirebaseCallback() {
            @Override
            public void onFirebaseCallback() {
                Toast.makeText(GroupInfoScreenMadrich.this,
                        String.format(getString(R.string.group_was_deleted_successfully), group.getName()),
                        Toast.LENGTH_SHORT).show();

                FCMSend.sendNotificationAboutGroup(GroupInfoScreenMadrich.this, group);

                startActivity(Utils.getIntentClearTop(new Intent(GroupInfoScreenMadrich.this,
                        ShowGroupsScreen.class)));
                finish();
            }
        });

    }

    private void saveGroup() {
        String name = Utils.getText(et_group_name);
        String description = Utils.getText(et_group_description);
        String groupKey = Utils.getText(et_group_key);
        String trainerCode = Utils.getText(et_trainer_code);
        String limit = Utils.getText(et_group_limit);

        boolean editTextsFilled = true;

        if(name.isEmpty()){
            et_group_name.setError("Name invalid");
            editTextsFilled = false;
        }

        if(trainerCode.isEmpty()){
            et_trainer_code.setError("Trainer code invalid");
            editTextsFilled = false;
        }

        if(!limit.isEmpty() && Integer.parseInt(limit) > 0 && Integer.parseInt(limit) < group.getUsersNumber()){
            et_group_limit.setError("Limit invalid");
            editTextsFilled = false;
        }

        int color = tv_choose_color.getCurrentTextColor();

        if(!editTextsFilled){
            return;
        }

        Group newGroup = new Group(name, groupKey, description, Integer.parseInt(trainerCode),
                color, group.getUsersNumber(), Integer.parseInt(limit));

        FirebaseUtils.createReAuthenticateDialog(this, new FirebaseUtils.FirebaseCallback() {
            @Override
            public void onFirebaseCallback() {
                if (selectedImageUri == null){
                    newGroup.setPicture(group.getPicture());
                    uploadGroup(newGroup);
                }
                else {
                    uploadGroupPictureToFirebase(newGroup, selectedImageUri);
                }
            }
        });
    }

    private void uploadGroupPictureToFirebase(@NonNull Group newGroup, Uri selectedImageUri) {
        StorageReference ref = FirebaseUtils.getFirebaseStorage().child("Groups").child(newGroup.getKey());
        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            newGroup.setPicture(uri.toString());
                            uploadGroup(newGroup);
                        })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                                "Updating the picture failed.\nPlease try again", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                        "Uploaded picture is invalid.\nPlease try again", Toast.LENGTH_SHORT).show());
    }

    private void uploadGroup(@NonNull Group newGroup) {
        DatabaseReference ref = FirebaseUtils.groups.child(newGroup.getKey());
        ref.setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg;

                Intent intent = new Intent(GroupInfoScreenMadrich.this, GroupInfoScreenMadrich.class);

                if (task.isSuccessful()){
                    msg = String.format(getString(R.string.data_of_group_name_was_successfully_updated), newGroup.getName());
                    intent.putExtra(Group.KEY_GROUP, newGroup);
                }
                else {
                    msg = getString(R.string.upps_something_went_wrong);
                    intent.putExtra(Group.KEY_GROUP, group);
                }

                Toast.makeText(GroupInfoScreenMadrich.this, msg, Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void getGroupData() {
        toolbar.inflateMenu(R.menu.group_menu);
        menu = toolbar.getMenu();

        setSupportActionBar(toolbar);

        Drawable homeIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_close_24);
        Drawable saveIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_done_24);
        Drawable deleteIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_delete_24);

        int contrastColor = Utils.getContrastColor(group.getColor());

        homeIcon.setTint(contrastColor);
        saveIcon.setTint(contrastColor);
        deleteIcon.setTint(contrastColor);

        menu.findItem(R.id.save_group).setIcon(saveIcon);
        getSupportActionBar().setHomeAsUpIndicator(homeIcon);
        menu.findItem(R.id.delete_group).setIcon(deleteIcon);

        super.getGroupData();

        enableEverything(true);
        edit(false);
    }

    // this function is triggered when
    // the Select Image Button is clicked
    private boolean chooseGroupPicture() {

        // create an instance of the  intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it  with the returned requestCode
        startActivityForResult(Intent.createChooser(i, getString(R.string.select_group_picture)), SELECT_PICTURE);

        return false;
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // compare the resultCode with the
        // SELECT_PICTURE constant
        // Get the url of the image from data
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data.getData() != null) {
            selectedImageUri = data.getData();
            edit(true);
            Glide.with(this).load(selectedImageUri).error(R.drawable.sample_profile_picture).centerInside().into(iv_group_picture);
        }
    }

    @Override
    public boolean onLongClick(@NonNull View v) {
        Log.d(Utils.LOG_TAG, "onLongClick");
        edit(true);
        return false;
    }

    /**
     * Edit.
     *
     * @param edit the edit
     */
    public void edit(boolean edit) {
        ViewAnimationUtils.expandOrCollapse(et_group_name, edit);

        et_group_name.getEditText().setInputType(edit ? EditorInfo.TYPE_CLASS_TEXT : EditorInfo.TYPE_NULL);
        et_group_description.getEditText().setInputType(edit
                ? (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE)
                : (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_NULL));
        et_group_key.getEditText().setInputType(edit ? EditorInfo.TYPE_CLASS_TEXT : EditorInfo.TYPE_NULL);
        et_trainer_code.getEditText().setInputType(edit ? EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD : EditorInfo.TYPE_NULL);
        et_group_limit.getEditText().setInputType(edit ? EditorInfo.TYPE_CLASS_NUMBER : EditorInfo.TYPE_NULL);

        et_group_name.setEndIconVisible(edit);
        et_group_description.setEndIconVisible(edit);
        et_group_key.setEndIconVisible(edit);
        et_trainer_code.setEndIconVisible(edit);
        et_group_limit.setEndIconVisible(edit);

        menu.findItem(R.id.save_group).setVisible(edit);
        getSupportActionBar().setHomeAsUpIndicator(edit ? R.drawable.ic_baseline_close_24 : R.drawable.ic_baseline_arrow_back_24);
        menu.findItem(R.id.delete_group).setVisible(!edit);
    }

    @Override
    public boolean onUserLongClick(int position, @NonNull UserData userData) {
        Utils.createAlertDialog(this,
                "Are you sure that you want to remove user " + userData.getUsername() + " from this group?",
                "All data in this group for user " + userData.getUsername() + " will be deleted.",
                R.string.yes, (dialog, which) -> FirebaseUtils.createReAuthenticateDialog(
                        GroupInfoScreenMadrich.this, () -> removeUser(userData)),
                R.string.no, (dialog, which) -> dialog.dismiss(),
                null).show();

        return false;
    }

    private void removeUser(@NonNull UserData userData) {
        loadingDialog.setMessage("Removing user " + userData.getUsername() + "from group " + group.getName());
        loadingDialog.show();

        DatabaseReference startRef = FirebaseUtils.getGroupDatabase(group.getKey());

        FirebaseUtils.deleteAll(startRef, userData.getUID(),
                () -> FirebaseUtils.groups.child(group.getKey()).child(Group.KEY_USERS_NUMBER)
                        .setValue(ServerValue.increment(-1))
                        .addOnCompleteListener(
                                u -> {
                                    loadingDialog.dismiss();
                                    Toast.makeText(GroupInfoScreenMadrich.this,
                                            "User was successfully removed from this group",
                                            Toast.LENGTH_SHORT).show();
                                }));

        FirebaseUtils.getUserDataByUIDRef(userData.getUID()).child(UserData.KEY_CURRENT_GROUP)
                .child(group.getKey()).removeValue()
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    FCMSend.sendNotificationAboutUser(GroupInfoScreenMadrich.this, group, userData.getUID());
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(GroupInfoScreenMadrich.this, "Failed", Toast.LENGTH_SHORT).show();
                });

    }
}