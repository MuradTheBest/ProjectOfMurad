package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.calendar.Utils_Calendar;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Screen extends AppCompatActivity {

    private CircleImageView iv_profile_picture;
    private ShimmerFrameLayout shimmer_profile_picture;

    private EditText et_username;
    private EditText et_email;
    private EditText et_phone;

    private CheckBox et_isMadrich;

    private Button btn_save_profile;

    private UserData currentUserData;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    private Intent gotten_intent;

    private Uri selectedImageUri;
    private String imageUri;

    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        gotten_intent = getIntent();
        editMode = gotten_intent.getBooleanExtra("mode", false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        getSupportActionBar().setDisplayShowHomeEnabled(editMode);
        getSupportActionBar().setHomeButtonEnabled(editMode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(editMode);

        currentUserData = FirebaseUtils.getCurrentUserData();

        String UID = currentUserData.getUID();
        String username = currentUserData.getUsername();
        String email = currentUserData.getEmail();
        String phone = currentUserData.getPhone();
        boolean isMadrich = currentUserData.isMadrich();


        Log.d(TAG, currentUserData.toString());


        et_username = ((TextInputLayout) findViewById(R.id.et_username)).getEditText();;
        et_email = ((TextInputLayout) findViewById(R.id.et_email)).getEditText();
        et_phone = ((TextInputLayout) findViewById(R.id.et_phone)).getEditText();
        et_isMadrich = findViewById(R.id.et_isMadrich);

        iv_profile_picture = findViewById(R.id.iv_profile_picture);
        iv_profile_picture.setOnClickListener(v -> imageChooser());

        shimmer_profile_picture = findViewById(R.id.shimmer_profile_picture);
        shimmer_profile_picture.startShimmer();

        btn_save_profile = findViewById(R.id.btn_save_profile);

        if (!editMode){

            et_username.getBackground().setTint(Color.WHITE);
            et_email.getBackground().setTint(Color.WHITE);
            et_phone.getBackground().setTint(Color.WHITE);

            et_username.setInputType(EditorInfo.TYPE_NULL);
            et_email.setInputType(EditorInfo.TYPE_NULL);
            et_phone.setInputType(EditorInfo.TYPE_NULL);

            et_username.setEnabled(false);
            et_email.setEnabled(false);
            et_phone.setEnabled(false);
//            et_isMadrich.setEnabled(false);
            et_isMadrich.setClickable(false);

            et_username.setTextColor(Color.BLACK);
            et_email.setTextColor(Color.BLACK);
            et_phone.setTextColor(Color.BLACK);
            et_isMadrich.setTextColor(Color.BLACK);

            iv_profile_picture.setClickable(false);
            iv_profile_picture.setLongClickable(false);


        }

        getCurrentUserData();

    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {

        menu.findItem(R.id.save_profile).setVisible(editMode);
        menu.findItem(R.id.edit_profile).setVisible(!editMode);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.profile_screen_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int selectedId = item.getItemId();

        Intent intent = new Intent(Profile_Screen.this, Profile_Screen.class);

        switch (selectedId) {
            case R.id.edit_profile:
                intent.putExtra("mode", true);
                startActivity(intent);

                break;
            case R.id.save_profile:
                if (checkInput()){
                    onSaveProfileClick();
                }
                break;
            case android.R.id.home:
                startActivity(intent);
                break;
        }

        return true;
    }

    public void onSaveProfileClick() {
        String username = et_username.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
        boolean isMadrich = et_isMadrich.isChecked();

        FirebaseUtils.getCurrentUserDataRef().child("username").setValue(username);
        FirebaseUtils.getCurrentUserDataRef().child("email").setValue(email);
        FirebaseUtils.getCurrentUserDataRef().child("phone").setValue(phone);
        FirebaseUtils.getCurrentUserDataRef().child("isMadrich").setValue(isMadrich);

        if (selectedImageUri != null){
            FirebaseUtils.getProfilePicturesRef().child(FirebaseUtils.getCurrentUID()).putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    String profile_picture = taskSnapshot.getUploadSessionUri().toString();
                            FirebaseUtils.getProfilePicturesRef().child(FirebaseUtils.getCurrentUID()).getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                String profile_picture = task.getResult().toString();
                                                FirebaseUtils.getCurrentUserDataRef().child("profile_picture").setValue(profile_picture);
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
                            }
                        }
                    });
        }

    }

    public boolean checkInput(){

        HashMap<String, String> inputs = new HashMap<>();
        inputs.put("username", et_username.getText().toString());

//        boolean result = Utils_Calendar.areObjectDetailsValid(this, "", inputs);
        boolean result = true;

        String username = et_username.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();

        String msg = "";

        if(username.isEmpty()){
            et_username.setError("Enter username");
            msg += ", username";
            result = false;
        }

        if(email.isEmpty()){
            et_email.setError("Enter e-mail");
            msg += ", E-mail";
            result = false;
        }
        else if(!Utils_Calendar.isEmailValid(email)){
            et_email.setError("E-mail invalid");
            msg += ", valid E-mail";
            result = false;
        }

        if(phone.isEmpty()){
            et_phone.setError("Enter phone");
            msg += ", phone";
            result = false;
        }
        else if(!Utils_Calendar.isPhoneValid(phone)){
            et_phone.setError("phone invalid");
            msg += ", valid phone";
            result = false;
        }

        msg = msg.replaceFirst(", ", "");

        if (!result){
            Toast.makeText(this, "Please enter " + msg, Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public void getCurrentUserData(){
        FirebaseUtils.getCurrentUserDataRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String UID = snapshot.child("uid").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                boolean isMadrich = snapshot.child("madrich").getValue(boolean.class);

                et_username.setText(username);
                et_email.setText(email);
                et_phone.setText(phone);
                et_isMadrich.setChecked(isMadrich);

                FirebaseUtils.getProfilePictureFromFB(UID, Profile_Screen.this, iv_profile_picture, shimmer_profile_picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    // this function is triggered when
    // the Select Image Button is clicked
    private void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                if (data.getData() != null) {
                    selectedImageUri = data.getData();
                    // update the preview image in the layout
                    iv_profile_picture.setImageURI(selectedImageUri);
                }
            }
        }
    }

}