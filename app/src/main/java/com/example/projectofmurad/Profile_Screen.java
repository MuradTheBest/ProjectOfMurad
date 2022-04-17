package com.example.projectofmurad;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.notifications.FCMSend;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Screen extends AppCompatActivity {

    private CircleImageView iv_profile_picture;
    private ShimmerFrameLayout shimmer_profile_picture;

    private EditText et_username;
    private EditText et_email;
    private EditText et_phone;

    private CheckBox et_madrich;

    private Button btn_delete_account;
    private Button btn_sign_out;

    private Button btn_verify_identity;
    private Button btn_change_password;

    private UserData currentUserData;

    // constant to compare
    // the activity result code
    public final static int SELECT_PICTURE = 200;
    private final static int GOOGLE_REQUEST_CODE = 4000;

    private Intent gotten_intent;

    private Uri selectedImageUri;
    private String imageUri;

    private boolean editMode;

    private ProgressDialog progressDialog;

    private MaterialButton btn_log_in_with_google;
    private MaterialButton btn_log_in_with_facebook;
    private MaterialButton google_checked;
    private MaterialButton facebook_checked;

    public final static String MADRICH_VERIFICATION_CODE = "123456";

    protected int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        gotten_intent = getIntent();
        editMode = gotten_intent.getBooleanExtra("mode", false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(editMode ? R.drawable.ic_baseline_close_24 : R.drawable.ic_baseline_arrow_back_ios_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this/*, ProgressDialog.THEME_HOLO_LIGHT*/);
        Utils.createCustomDialog(progressDialog);

        if (!FirebaseUtils.isUserLoggedIn()){
            startActivity(new Intent(Profile_Screen.this, Log_In_Screen.class));
        }

//        currentUserData = FirebaseUtils.getCurrentUserData();

        /*String UID = currentUserData.getUID();
        String username = currentUserData.getUsername();
        String email = currentUserData.getEmail();
        String phone = currentUserData.getPhone();
        boolean isMadrich = currentUserData.isMadrich();


        Log.d(LOG_TAG, currentUserData.toString());*/

        et_username = ((TextInputLayout) findViewById(R.id.et_username)).getEditText();;
        et_email = ((TextInputLayout) findViewById(R.id.et_email)).getEditText();
        et_phone = ((TextInputLayout) findViewById(R.id.et_phone)).getEditText();
        et_madrich = findViewById(R.id.et_madrich);

        btn_sign_out = findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unsubscribeFromTopic(new OnUnsubscribeFinished() {
                    @Override
                    public void onUnsubscribe() {
                        FirebaseUtils.getFirebaseAuth().signOut();

                        GoogleSignIn.getClient(Profile_Screen.this,
                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                                .signOut();

                /*AuthUI.getInstance()
                        .signOut(Profile_Screen.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>(){

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                // do something here

                            }
                        });*/

                        startActivity(new Intent(Profile_Screen.this, Log_In_Screen.class));
                    }
                });

            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > length && (s.length() == 2 || s.length() == 6)){
                    s.append("-");
                }
                length = s.length();
            }
        });

        btn_verify_identity = findViewById(R.id.btn_verify_identity);
        btn_verify_identity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = et_email.getText().toString();
                createUpdateEmailDialog(newEmail);
            }
        });

        btn_change_password = findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(v -> createUpdateEmailDialog(null));

        iv_profile_picture = findViewById(R.id.iv_profile_picture);
        iv_profile_picture.setOnClickListener(v -> chooseProfilePicture());
//        iv_profile_picture.setOnClickListener(v -> showImagePicDialog());

        shimmer_profile_picture = findViewById(R.id.shimmer_profile_picture);
        shimmer_profile_picture.startShimmer();

        btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Screen.this);
                builder.setMessage("Confirm deleting of account");
                builder.setCancelable(false);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

/*
                        unsubscribeFromTopic(new OnUnsubscribeFinished() {
                            @Override
                            public void onUnsubscribe() {

                                FirebaseUtils.usersDatabase.child(FirebaseUtils.getCurrentUID()).removeValue();

                                FirebaseUtils.getCurrentUserProfilePictureRef().delete();

                                FirebaseUser firebaseUser = FirebaseUtils.getCurrentFirebaseUser();

                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(Profile_Screen.this, Splash_Screen.class));
                                            Toast.makeText(getApplicationContext(), "Account was successfully deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        });
*/

                        FirebaseUtils.usersDatabase.child(FirebaseUtils.getCurrentUID()).removeValue();

                        FirebaseUtils.getCurrentUserProfilePictureRef().delete();

                        FirebaseUser firebaseUser = FirebaseUtils.getCurrentFirebaseUser();

                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(Profile_Screen.this, Splash_Screen.class));
                                    Toast.makeText(getApplicationContext(), "Account was successfully deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });

        et_madrich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_madrich.isChecked()){
                    createMadrichVerificationDialog();
                }
                else{
                    createStopBeingMadrichDialog();
                }
            }
        });

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
            et_madrich.setEnabled(false);

            et_username.setTextColor(Color.BLACK);
            et_email.setTextColor(Color.BLACK);
            et_phone.setTextColor(Color.BLACK);
            et_madrich.setTextColor(Color.BLACK);

            iv_profile_picture.setClickable(false);
            iv_profile_picture.setLongClickable(false);

            btn_delete_account.setVisibility(View.GONE);
            btn_change_password.setVisibility(View.GONE);
            btn_sign_out.setVisibility(View.GONE);
        }

        getCurrentUserData();

        btn_log_in_with_google = findViewById(R.id.sign_up_with_google);
        btn_log_in_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (google_checked.getVisibility() == View.VISIBLE){
                    createGoogleUnlinkDialog();
                }
                else {
                    showGoogleSignIn();
                }
            }
        });

        btn_log_in_with_facebook = findViewById(R.id.sign_up_with_facebook);

        google_checked = findViewById(R.id.google_checked);
        facebook_checked = findViewById(R.id.facebook_checked);

    }

    public void createGoogleUnlinkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Unlinking account from Google");
        builder.setMessage("Unlink?");


        builder.setPositiveButton("Unlink", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Unlinking your account from Google");
                progressDialog.show();

                FirebaseUtils.getCurrentFirebaseUser().unlink(GoogleAuthProvider.PROVIDER_ID).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Profile_Screen.this, "Your account was successfully unlinked from Google",
                                            Toast.LENGTH_SHORT).show();
                                    google_checked.setVisibility(View.GONE);
                                    btn_log_in_with_google.setText("Link with Google");
                                    btn_log_in_with_google.setTextColor(Color.BLACK);
                                }
                                else{
                                    Toast.makeText(Profile_Screen.this, "Unlinking from Google failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();
    }

    public void createMadrichVerificationDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.madrich_verification_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Madrich verification");
        builder.setMessage("Enter special madrich verification code:");

        EditText et_verify_madrich = ((TextInputLayout) view.findViewById(R.id.et_verify_madrich)).getEditText();

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = et_verify_madrich.getText().toString();

                if (code.equals(MADRICH_VERIFICATION_CODE)){
                    et_madrich.setChecked(true);
                    FirebaseUtils.getCurrentUserDataRef().child("madrich").setValue(true).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Profile_Screen.this, "Madrich privileges were granted", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    et_madrich.setChecked(false);
                    et_verify_madrich.setError("Invalid code");
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> et_madrich.setChecked(false));

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();
    }

    public void createStopBeingMadrichDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure about losing madrich privileges?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            et_madrich.setChecked(false);
            Toast.makeText(this, "Madrich privileges were taken", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> et_madrich.setChecked(true));

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();
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
                if(checkInput()){
                    onSaveProfileClick();
                }
                break;
            case android.R.id.home:
                startActivity(new Intent(Profile_Screen.this,
                        editMode ? Profile_Screen.class : MainActivity.class));
                break;
        }

        return true;
    }

    public void unsubscribeFromTopic(OnUnsubscribeFinished onUnsubscribeFinished){
        FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.ADD_EVENT_TOPIC).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.EDIT_EVENT_TOPIC).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.DELETE_EVENT_TOPIC).addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(
                                                                    @NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    onUnsubscribeFinished.onUnsubscribe();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public interface OnUnsubscribeFinished{
        void onUnsubscribe();
    }

    public void onSaveProfileClick() {
        String username = et_username.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
        boolean madrich = et_madrich.isChecked();

        FirebaseUtils.getCurrentUserDataRef().child("madrich").setValue(madrich);

//        FirebaseUtils.updateUserData(username, email, phone, selectedImageUri, this);

/*
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
                                ;
                                startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
                            }
                        }
                    });
        }
*/
            updateUserData(username, email, phone, selectedImageUri);
        /*if (selectedImageUri != null){
        }
        else{
            startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
        }*/

    }

    boolean phoneVerified = true;

    public void updateUserData(@NonNull String username, String email, @NonNull String phone, Uri profile_picture){

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        boolean emailVerified = true;
        phone = phone.replaceAll("-", "");


        if (!username.equals(user.getDisplayName())) {
            builder.setDisplayName(username);
            FirebaseUtils.getCurrentUserDataRef().child("username").setValue(username);
        }
        if (!("+972" + phone).equals(user.getPhoneNumber()) && !phone.isEmpty()) {
            Log.d("murad", "phone: " + phone);
            Log.d("murad", "user.getPhoneNumber() : " + user.getPhoneNumber());
            phoneVerified = false;
            phone = "+972" + phone;

            phoneAuth(phone);
        }
        if (!profile_picture.equals(user.getPhotoUrl()) || user.getPhotoUrl() == null){

            Log.d("murad", "profile_picture uri : " + profile_picture.toString());
            Log.d("murad", "user.getPhotoUrl() uri : " + user.getPhotoUrl().toString());

            FirebaseUtils.getProfilePicturesRef().child(FirebaseUtils.getCurrentUID()).putFile(profile_picture)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(Profile_Screen.this, "Uploaded picture is invalid", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            FirebaseUtils.getCurrentUserProfilePictureRef().getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                String profile_picture = task.getResult().toString();
                                                FirebaseUtils.getCurrentUserDataRef().child("profile_picture").setValue(profile_picture).addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(
                                                                    @NonNull Task<Void> task) {

                                                                startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    });
        }

        builder.setPhotoUri(profile_picture);

        boolean finalPhoneVerified = phoneVerified;

        user.updateProfile(builder.build()).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (finalPhoneVerified && task.isSuccessful()){
                            startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
                        }
                    }
                });
    }

    public void phoneAuth(String phone){

        progressDialog.setMessage("Verifying phone number...");
        progressDialog.show();

        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (data.hasChild("phone") && data.child("phone").getValue(String.class).equals(phone)){
                        progressDialog.dismiss();
                        Toast.makeText(Profile_Screen.this, "User with this phone number already exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(FirebaseUtils.getFirebaseAuth())
                                .setPhoneNumber(phone)
                                // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(Profile_Screen.this)                 // Activity (for callback binding)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(
                                            @NonNull PhoneAuthCredential phoneAuthCredential) {

                                /*
                                  This callback will be invoked in two situations:
                                  1 - Instant verification. In some cases the phone number can be instantly
                                  verified without needing to send or enter a verification code.
                                  2 - Auto-retrieval. On some devices Google Play services can automatically
                                  detect the incoming verification SMS and perform verification without
                                  user action.
                                 */

                                        Log.d("murad", "onVerificationCompleted:" + phoneAuthCredential);

                                        progressDialog.dismiss();
                                        signInWithPhoneAuthCredential(phoneAuthCredential);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        Toast.makeText(getApplicationContext(), "Entered phone number is invalid", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verificationId,
                                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                                        Toast.makeText(getApplicationContext(), "The verification code was sent", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                        createSMSVerificationDialog(verificationId, phone);
                                    }

                                    @Override
                                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                                        super.onCodeAutoRetrievalTimeOut(s);
                                    }
                                })
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void createSMSVerificationDialog(String verificationId, String phone){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.sms_verification_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Code verification");
        builder.setMessage("Enter verification code that was sent to " + phone + ":");

        EditText et_verify_sms_code = ((TextInputLayout) view.findViewById(R.id.et_verify_sms_code)).getEditText();

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = et_verify_sms_code.getText().toString();

                if (code.isEmpty() || code.length() < 6){
                    et_verify_sms_code.setError("Invalid verification code");
                }
                else{
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        progressDialog.setMessage("Logging in via phone number, please wait");
        progressDialog.show();

        FirebaseUtils.getCurrentFirebaseUser().linkWithCredential(phoneAuthCredential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String phone = task.getResult().getUser().getPhoneNumber();
                            FirebaseUtils.getCurrentUserDataRef().child("phone").setValue(phone.replace("+972", "0"));
                            startActivity(new Intent(Profile_Screen.this, Profile_Screen.class));
                            phoneVerified = true;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Verification code is invalid", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("murad", e.getLocalizedMessage());
                Log.d("murad", e.getCause().toString());
            }
        });
    }

    public boolean checkInput(){

        HashMap<String, String> inputs = new HashMap<>();
        inputs.put("username", et_username.getText().toString());

//        boolean result = UtilsCalendar.areObjectDetailsValid(this, "", inputs);
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
        else if(!UtilsCalendar.isEmailValid(email)){
            et_email.setError("E-mail invalid");
            msg += ", valid E-mail";
            result = false;
        }

        if(!phone.isEmpty() && !UtilsCalendar.isPhoneValid(phone)){
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
        if (!FirebaseUtils.isUserLoggedIn()){
            return;
        }

        FirebaseUtils.getCurrentUserDataRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /*String UID = snapshot.child("id").getValue(String.class);
                String username = snapshot.child("username").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                boolean madrich = snapshot.child("madrich").getValue(boolean.class);*/

                FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

                String UID = user.getUid();
                String username = user.getDisplayName();
                String email = user.getEmail();
                boolean emailVerified = user.isEmailVerified();

                Log.d("murad", "editMode is " + editMode);
                Log.d("murad", "emailVerified is " + emailVerified);
                Log.d("murad", "email.isEmpty() is " + email.isEmpty());

                String phone = "";
                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()){
                    phone = user.getPhoneNumber().replace("+972", "");
                    phone = addChar(phone, '-', 2);
                    phone = addChar(phone, '-', 6);
                }

                boolean madrich = false;

                if(snapshot.child("madrich").exists()){
                    madrich = snapshot.child("madrich").getValue(boolean.class);
                }

                et_username.setText(username);
                et_email.setText(email);
                et_phone.setText(phone);
                et_madrich.setChecked(madrich);

                et_email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals(email)){
                            ((TextInputLayout) et_email.getParent().getParent()).setError("");

                            btn_verify_identity.setVisibility(View.GONE);
                        }
                        else if(s.toString().isEmpty()){
                            et_email.setText(email);

                            ((TextInputLayout) et_email.getParent().getParent()).setError("");

                            btn_verify_identity.setVisibility(View.GONE);
                        }
                        else {
                            ((TextInputLayout) et_email.getParent().getParent()).setErrorEnabled(true);
                            ((TextInputLayout) et_email.getParent().getParent()).setError("In order to update email you have to verify your identity");

                            btn_verify_identity.setVisibility(View.VISIBLE);
                        }

                    }
                });

                List<? extends UserInfo> providers = user.getProviderData();
                for (UserInfo userInfo : providers){
                    Log.d("murad", "provider is " + userInfo.getProviderId());
                    if (userInfo.getProviderId().equals("google.com")){
                        btn_log_in_with_google.setTextColor(Color.LTGRAY);
                        btn_log_in_with_google.setText("Linked");
                        google_checked.setVisibility(View.VISIBLE);
                    }
                    if (userInfo.getProviderId().equals("")){
                        btn_log_in_with_facebook.setTextColor(Color.LTGRAY);
                        btn_log_in_with_facebook.setText("Linked");
                        facebook_checked.setVisibility(View.VISIBLE);
                    }
                }

//                FirebaseUtils.getProfilePictureFromFB(UID, Profile_Screen.this, iv_profile_picture, shimmer_profile_picture);

                Uri pp = user.getPhotoUrl();
                selectedImageUri = pp;
                Glide.with(Profile_Screen.this).load(pp).centerCrop().into(iv_profile_picture);
                iv_profile_picture.setVisibility(View.VISIBLE);


                shimmer_profile_picture.stopShimmer();
                shimmer_profile_picture.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void createUpdateEmailDialog(String newEmail){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.password_verification_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Update email");
        builder.setMessage("You have to enter password in order to continue");

        EditText et_verify_password = ((TextInputLayout) view.findViewById(R.id.et_verify_password)).getEditText();

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = et_verify_password.getText().toString();
                String email = user.getEmail();

                if (password.isEmpty()){
                    Toast.makeText(Profile_Screen.this, "PLease enter password", Toast.LENGTH_SHORT).show();
                }
                else{
                    AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(authCredential).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Profile_Screen.this, "Identity approved",
                                                Toast.LENGTH_SHORT).show();

                                        if (newEmail == null){
                                            sendUpdatePasswordVerification();
                                        }
                                        else {
                                            sendUpdateEmailVerification(newEmail);
                                        }

                                    }
                                    else {
                                        et_verify_password.setError("Invalid password");

                                        Toast.makeText(Profile_Screen.this, "Invalid password",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();

    }

    public void sendUpdateEmailVerification(String newEmail){

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(
                            @NonNull Task<Void> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(Profile_Screen.this, "Sending email verification failed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.setMessage("Waiting for new email verification");
                            Utils.createCustomDialog(progressDialog);

                            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Got",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(user.isEmailVerified()){
                                                updateEmail(newEmail);
                                            }
                                            else {
                                                Toast.makeText(Profile_Screen.this, "New email was not verified " +
                                                                "\nPlease try again",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            progressDialog.show();
                        }

                    }
                });
    }

    public void updateEmail(String newEmail){
        FirebaseUtils.getCurrentFirebaseUser().updateEmail(newEmail).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseUtils.getCurrentUserDataRef().child("email").setValue(newEmail);
                            Toast.makeText(Profile_Screen.this, "New email was successfully updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Profile_Screen.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createResetPasswordDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.password_verification_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("You have to enter password in order to continue");

        EditText et_reset_password = ((TextInputLayout) view.findViewById(R.id.et_reset_password)).getEditText();

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = et_reset_password.getText().toString();
                String email = user.getEmail();

                if (password.isEmpty()){
                    Toast.makeText(Profile_Screen.this, "PLease enter password", Toast.LENGTH_SHORT).show();
                }
                else{
                    AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(authCredential).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Profile_Screen.this, "Identity approved",
                                                Toast.LENGTH_SHORT).show();

                                        sendUpdatePasswordVerification();
                                    }
                                    else {
                                        et_reset_password.setError("Invalid password");

                                        Toast.makeText(Profile_Screen.this, "Invalid password",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();

    }

    private ProgressDialog loadingBar;

    private void sendUpdatePasswordVerification() {
        loadingBar = new ProgressDialog(this);
        Utils.createCustomDialog(loadingBar);
        loadingBar.setMessage("Sending password reset mail...");
        loadingBar.setCanceledOnTouchOutside(false);

        loadingBar.setButton(DialogInterface.BUTTON_POSITIVE,"Ok",
                (dialog, which) -> loadingBar.dismiss());

        loadingBar.show();

        // calling sendPasswordResetEmail open your email and write the new password and then you can login
        FirebaseUtils.getFirebaseAuth().sendPasswordResetEmail(FirebaseUtils.getCurrentFirebaseUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    loadingBar.setMessage("Password reset mail was sent");
                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(Profile_Screen.this,"Error occurred",Toast.LENGTH_LONG).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(Profile_Screen.this,"Failed",Toast.LENGTH_LONG).show();
            }
        });

    }

    public String addChar(@NonNull String str, char ch, int position) {
        int len = str.length();
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = ch;
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
    }

    public String addCharWithSubString(@NonNull String str, char ch, int position) {
        return str.substring(0, position) + ch + str.substring(position);
    }

    public String addCharWithStringBuilder(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    // this function is triggered when
    // the Select Image Button is clicked
    private void chooseProfilePicture() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }

    public void showGoogleSignIn(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1059282703260-j1otqlruj256io9ouf22dtcqd2neg65n.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
    }

    protected void googleAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Toast.makeText(this, "credential" + credential, Toast.LENGTH_SHORT).show();
        Log.d("murad", "credential" + credential);

        progressDialog.setMessage("Logging via Google, please wait...");

        FirebaseUtils.getCurrentFirebaseUser().linkWithCredential(credential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Profile_Screen.this, "Account was successfully connected to Google", Toast.LENGTH_SHORT).show();
                            btn_log_in_with_google.setChecked(true);
                            btn_log_in_with_google.setTextColor(Color.LTGRAY);
                            btn_log_in_with_google.setText("Linked");
                            google_checked.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss();
                    }
                });
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

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d("murad", "getting account ");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null){
                    Log.d("murad", "Google account is null ");
                    return;
                }
                // Signed in successfully, show authenticated UI.
                googleAuth(account.getIdToken());
            }
            catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                Log.d("murad", "Google sign in failed ", e);
                Log.d("murad", "Google sign in failed " + e.getMessage());
                Log.d("murad", "Google sign in failed " + e.getCause());
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Profile_Screen.this,
                editMode ? Profile_Screen.class : MainActivity.class));
    }
}