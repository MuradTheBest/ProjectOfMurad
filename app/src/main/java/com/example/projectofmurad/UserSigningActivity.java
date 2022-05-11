package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;
import static com.example.projectofmurad.helpers.Utils.getDefaultTextChangedListener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.calendar.UtilsCalendar;
import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Super class of all activities that require user's interaction with {@link FirebaseAuth}
 */
public class UserSigningActivity extends MyActivity {

    protected MaterialButton btn_google;
    protected MaterialButton btn_facebook;
    protected MaterialButton btn_phone;

    protected LoadingDialog loadingDialog;

    protected int length = 0;

    protected final static int GOOGLE_REQUEST_CODE = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();

        loadingDialog = new LoadingDialog(this);
    }

    protected void createPhoneAuthenticationDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.phone_verification_dialog, null);

        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(this);

        builder.setView(view);
        builder.setTitle("Phone number verification");
        builder.setMessage("SMS with verification code will be sent to entered number:");

        TextInputLayout et_verify_phone = view.findViewById(R.id.et_verify_phone);
        et_verify_phone.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > length && (s.length() == 2 || s.length() == 6)){
                    s.append("-");
                }
                length = s.length();
                et_verify_phone.setError(null);
            }
        });

        builder.setPositiveButton(R.string.text_continue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phone = et_verify_phone.getEditText().getText().toString();
                if (phone.isEmpty() || !UtilsCalendar.isPhoneValid(phone)){
                    et_verify_phone.setError(getString(R.string.invalid_phone_number));
                }
                else{
                    phone = phone.replaceAll("-", "");
                    phone = "+972" + phone;
                    dialog.dismiss();
                    phoneAuth(phone);
                }

            }
        }, false);

        builder.setNegativeButton(R.string.back, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    protected void phoneAuth(String phone){

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseUtils.getFirebaseAuth())
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
        View view = LayoutInflater.from(this).inflate(R.layout.sms_verification_dialog, null);

        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(this);

        builder.setView(view);
        builder.setTitle("Code verification");
        builder.setMessage("Enter verification code that was sent to " + phone + ":");

        TextInputLayout et_verify_sms_code = view.findViewById(R.id.et_verify_sms_code);
        et_verify_sms_code.getEditText().addTextChangedListener(getDefaultTextChangedListener(et_verify_sms_code));

        builder.setPositiveButton(R.string.verify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = et_verify_sms_code.getEditText().getText().toString();

                if (code.isEmpty() || code.length() < 6){
                    et_verify_sms_code.setError("Invalid verification code");
                }
                else {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        }, false);

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    protected void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        loadingDialog.setMessage(getString(R.string.logging_in_please_wait));
        loadingDialog.show();

        FirebaseUtils.getFirebaseAuth().signInWithCredential(phoneAuthCredential).addOnCompleteListener(authCompleteListener);
    }

    protected final OnCompleteListener<AuthResult> authCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.signing_failed, Toast.LENGTH_SHORT).show();
                Log.d("murad", "signInWithCredential:failure", task.getException());
                // Sign in failed, display a message and update the UI
                Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                return;
            }

            Log.d("murad", "signInWithCredential:success");

            FirebaseUser user = task.getResult().getUser();

            HashMap<String, Object> userData = new HashMap<>();
            userData.put("uid", user.getUid());
            userData.put("username", user.getDisplayName());
            userData.put("email", user.getEmail());
            userData.put(UserData.KEY_PHONE, user.getPhoneNumber());

            if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().contains("/a/")){
                userData.put("picture", user.getPhotoUrl().toString());
            }

            FirebaseUtils.usersDatabase.child(user.getUid()).updateChildren(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismiss();
                            if (task.isSuccessful()){
                                finish();
                                startActivity(new Intent(getApplicationContext(), Splash_Screen.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), R.string.failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };

    protected void showGoogleSignIn(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_sign_in_api_key))
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

            try {
                Log.d("murad", "getting account");
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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        loadingDialog.setMessage(getString(R.string.logging_in_please_wait));
        loadingDialog.show();

        FirebaseUtils.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(authCompleteListener);
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
                                                            loadingDialog.dismiss();

                                                            startActivity(new Intent(getApplicationContext(), ShowGroupsScreen.class));
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