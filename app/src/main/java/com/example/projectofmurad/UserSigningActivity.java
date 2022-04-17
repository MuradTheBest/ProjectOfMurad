package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.notifications.FCMSend;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UserSigningActivity extends AppCompatActivity {

    protected MaterialButton btn_log_in_with_google;
    protected MaterialButton btn_log_in_with_facebook;
    protected MaterialButton btn_log_in_with_phone;

    protected ProgressDialog progressDialog;

    protected int length = 0;

    protected final static int GOOGLE_REQUEST_CODE = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        Utils.createCustomDialog(progressDialog);
    }

    protected void createPhoneAuthenticationDialog(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.phone_verification_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Phone number verification");
        builder.setMessage("SMS with verification code will be sent to entered number:");

        EditText et_verify_phone = ((TextInputLayout) view.findViewById(R.id.et_verify_phone)).getEditText();
        et_verify_phone.addTextChangedListener(new TextWatcher() {

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

        et_verify_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
				/*if (hasFocus){
					et_verify_phone.setHint("##-###-####");
				}*/
            }
        });

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phone = et_verify_phone.getText().toString();
                if (!phone.isEmpty()){
                    phone = phone.replaceAll("-", "");
                    phone = "+972" + phone;
                    dialog.dismiss();
                    phoneAuth(phone);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Back", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();

    }

    protected void phoneAuth(String phone){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseUtils.getFirebaseAuth())
                        .setPhoneNumber(phone)
                        // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(
                                    @NonNull PhoneAuthCredential phoneAuthCredential) {

                                /**
                                 * This callback will be invoked in two situations:
                                 * 1 - Instant verification. In some cases the phone number can be instantly
                                 * verified without needing to send or enter a verification code.
                                 * 2 - Auto-retrieval. On some devices Google Play services can automatically
                                 * detect the incoming verification SMS and perform verification without
                                 * user action.
                                 *
                                 *
                                 */

                                Log.d("murad", "onVerificationCompleted:" + phoneAuthCredential);

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

    protected void createSMSVerificationDialog(String verificationId, String phone){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.sms_verification_dialog, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

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

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Utils.createCustomDialog(alertDialog);

        alertDialog.show();
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        progressDialog.setMessage("Logging in via phone number, please wait");
        progressDialog.show();

        FirebaseUtils.getFirebaseAuth().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, authCompleteListener);
    }

    protected final OnCompleteListener<AuthResult> authCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Logging failed", Toast.LENGTH_SHORT).show();
                Log.d("murad", "signInWithCredential:failure", task.getException());
                // Sign in failed, display a message and update the UI
                Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "The entered verification code is invalid", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            Log.d("murad", "signInWithCredential:success");

            FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

            String UID = user.getUid();
            String username = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            Uri photoUri = user.getPhotoUrl();

            if (user.getPhotoUrl() == null || (user.getPhotoUrl() != null && user.getPhotoUrl().toString().contains("/a/"))){

                Uri sample_profile = Utils.getUriToDrawable(getApplicationContext(), R.drawable.images);
                Log.d("murad", "new sample_profile url: " + sample_profile.toString());

                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                builder.setPhotoUri(sample_profile);

                user.updateProfile(builder.build()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                FirebaseUtils.getProfilePicturesRef().child(UID).putFile(sample_profile).addOnCompleteListener(
                                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(
                                                    @NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseUtils.getProfilePicturesRef().child(UID).getDownloadUrl().addOnCompleteListener(
                                                            new OnCompleteListener<Uri>() {
                                                                @Override
                                                                public void onComplete(
                                                                        @NonNull Task<Uri> task) {
                                                                    if (task.isSuccessful()){
                                                                        String profile_picture = task.getResult().toString();

                                                                        UserData data = new UserData(UID, email, username, phone, profile_picture);
                                                                        Log.d("murad", data.toString());

                                                                        FirebaseUtils.usersDatabase.child(UID).setValue(data).addOnCompleteListener(
                                                                                new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            getToken();
                                                                                            subscribeToTopics();
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
                        });
            }
            else{

                String profile_picture = user.getPhotoUrl().toString();

                Log.d("murad", "existing profile_picture: " + profile_picture);

                UserData data = new UserData(UID, email, username, phone, profile_picture);
                Log.d("murad", data.toString());

                FirebaseUtils.usersDatabase.child(UID).setValue(data).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    getToken();
                                    subscribeToTopics();
                                }
                            }
                        });

            }
        }
    };

    protected void showGoogleSignIn(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(R.string.google_sign_in_api_key))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            Log.d(LOG_TAG, task.getException().getLocalizedMessage());
            Log.d(LOG_TAG, task.getException().getMessage());

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

    protected void googleAuth(String idToken) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d("murad", "credential" + credential);

        progressDialog.setMessage("Logging via Google, please wait...");
        progressDialog.show();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, authCompleteListener);
    }

    protected void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        System.out.println("Token " + token);

                        FirebaseUtils.getCurrentUserDataRef().child("tokens").get().addOnCompleteListener(
                                new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                        ArrayList<String> tokens;

                                        if (task.getResult().exists()){
                                            tokens = (ArrayList<String>) task.getResult().getValue();
                                        }
                                        else {
                                            tokens = new ArrayList<>();
                                        }

                                        tokens.add(token);

                                        FirebaseUtils.getCurrentUserDataRef().child("tokens").setValue(tokens).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(
                                                            @NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressDialog.dismiss();

                                                            startActivity(new Intent(getApplicationContext(), Profile_Screen.class));
                                                        }
                                                        else {
                                                            Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "Something went wrong. Try again",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                    }
                });
    }

    protected void subscribeToTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic(FCMSend.ADD_EVENT_TOPIC).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtils.getCurrentUserDataRef().child(UserData.KEY_SUBSCRIBED_TO_ADD_EVENT).setValue(true);
                            Log.d(FCMSend.FCM_TAG, "subscription to topic " + FCMSend.ADD_EVENT_TOPIC + " is successful");
                        }
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic(FCMSend.EDIT_EVENT_TOPIC).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtils.getCurrentUserDataRef().child(UserData.KEY_SUBSCRIBED_TO_EDIT_EVENT).setValue(true);
                            Log.d(FCMSend.FCM_TAG, "subscription to topic " + FCMSend.EDIT_EVENT_TOPIC + " is successful");
                        }
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic(FCMSend.DELETE_EVENT_TOPIC).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtils.getCurrentUserDataRef().child(UserData.KEY_SUBSCRIBED_TO_DELETE_EVENT).setValue(true);
                            Log.d(FCMSend.FCM_TAG, "subscription to topic " + FCMSend.DELETE_EVENT_TOPIC + " is successful");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getting Root View that gets focus
        View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(UserSigningActivity.this);
                }
            }
        });
    }

    public static void hideKeyboard(@NonNull Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( context.getCurrentFocus().getWindowToken(), 0);
    }
}