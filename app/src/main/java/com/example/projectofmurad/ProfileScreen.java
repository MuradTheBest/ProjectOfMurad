package com.example.projectofmurad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.example.projectofmurad.utils.Utils;
import com.example.projectofmurad.notifications.FCMSend;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The type Profile screen.
 */
public class ProfileScreen extends UserSigningActivity {

    private CircleImageView iv_profile_picture;
    private ShimmerFrameLayout shimmer_profile_picture;

    private TextInputLayout et_username;
    private TextInputLayout et_email;
    private TextInputLayout et_phone;

    private MaterialCheckBox et_madrich;

    private MaterialButton btn_change_phone;

    /**
     * The constant SELECT_PICTURE.
     */
// constant to compare
    // the activity result code
    public final static int SELECT_PICTURE = 200;

    private Uri selectedImageUri;

    private boolean editMode;

    private MaterialButton btn_log_in_with_google;
    private MaterialButton google_checked;

    /**
     * The Length.
     */
    protected int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        Intent gotten_intent = getIntent();
        editMode = gotten_intent.getBooleanExtra("mode", false);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(editMode ? R.drawable.ic_baseline_close_24 : R.drawable.ic_baseline_arrow_back_ios_24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new LoadingDialog(this);

        if (!FirebaseUtils.isUserLoggedIn()){
            startActivity(new Intent(ProfileScreen.this, LogInScreen.class));
        }

        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_madrich = findViewById(R.id.et_madrich);

        MaterialButton btn_sign_out = findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setMessage("Signing out from account. Please wait...");
                loadingDialog.show();

                unsubscribeFromTopic(new FirebaseUtils.FirebaseCallback() {
                    @Override
                    public void onFirebaseCallback() {
                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                                ProfileScreen.this,
                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

                        googleSignInClient.signOut();

                        loadingDialog.dismiss();

                        FirebaseUtils.getFirebaseAuth().signOut();
                        startActivity(Utils.getIntentClearTop(new Intent(ProfileScreen.this, LogInScreen.class)));
                        finish();
                    }
                });

            }
        });

        et_phone.getEditText().addTextChangedListener(new TextWatcher() {

            final String phone = Utils.getText(et_phone);

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
                btn_change_phone.setVisibility(phone.contentEquals(s) ? View.GONE : View.VISIBLE);
            }
        });

        MaterialButton btn_change_password = findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(v ->
                FirebaseUtils.createReAuthenticateDialog(this,
                        () -> sendPasswordUpdateEmail(FirebaseUtils.getCurrentFirebaseUser().getEmail())));

        btn_change_phone = findViewById(R.id.btn_change_phone);
        btn_change_phone.setOnClickListener(v -> checkPhoneNumber());

        iv_profile_picture = findViewById(R.id.iv_profile_picture);
        iv_profile_picture.setOnClickListener(v -> chooseProfilePicture());

        shimmer_profile_picture = findViewById(R.id.shimmer_profile_picture);

        MaterialButton btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_delete_account.setOnClickListener(
                v -> Utils.createAlertDialog(ProfileScreen.this, null, "Confirm deleting of account",
                        R.string.confirm,
                        (dialog, which) -> FirebaseUtils.createReAuthenticateDialog(ProfileScreen.this, this::deleteAccount),
                        R.string.cancel, (dialog, which) -> dialog.dismiss(),
                        null).show());


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

            et_username.getEditText().getBackground().setTint(Color.WHITE);
            et_email.getEditText().getBackground().setTint(Color.WHITE);
            et_phone.getEditText().getBackground().setTint(Color.WHITE);

            et_username.getEditText().setInputType(EditorInfo.TYPE_NULL);
            et_email.getEditText().setInputType(EditorInfo.TYPE_NULL);
            et_phone.getEditText().setInputType(EditorInfo.TYPE_NULL);

            et_username.setEnabled(false);
            et_email.setEnabled(false);
            et_phone.setEnabled(false);
            et_madrich.setEnabled(false);

            et_username.getEditText().setTextColor(Color.BLACK);
            et_email.getEditText().setTextColor(Color.BLACK);
            et_phone.getEditText().setTextColor(Color.BLACK);
            et_madrich.setTextColor(Color.BLACK);

            iv_profile_picture.setClickable(false);
            iv_profile_picture.setLongClickable(false);

            btn_delete_account.setVisibility(View.GONE);
            btn_change_password.setVisibility(View.GONE);
            btn_sign_out.setVisibility(View.GONE);
        }

        btn_log_in_with_google = findViewById(R.id.btn_google);
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

        google_checked = findViewById(R.id.google_checked);

        getCurrentUserData();
    }

    /**
     * Delete account.
     */
    public void deleteAccount(){

        FirebaseUtils.getCurrentUserProfilePictureRef().delete();

        FirebaseUser firebaseUser = FirebaseUtils.getCurrentFirebaseUser();

        String UID = FirebaseUtils.getCurrentUID();

        firebaseUser.delete().addOnSuccessListener(
                unused -> FirebaseUtils.deleteAll(FirebaseUtils.getDatabase().getReference(), UID,
                        () -> {
                            Toast.makeText(getApplicationContext(), "Account was successfully deleted", Toast.LENGTH_SHORT).show();
                            startActivity(Utils.getIntentClearTop(new Intent(ProfileScreen.this, SplashScreen.class)));
                        }))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Account deleting failed", Toast.LENGTH_SHORT).show());
    }

    /**
     * Create google unlink dialog.
     */
    public void createGoogleUnlinkDialog(){

        Utils.createAlertDialog(this,
                "Unlinking account from Google",
                "Unlink?",
                R.string.unlink, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.setMessage(getString(R.string.unlinking_your_account_from_google));
                        loadingDialog.show();

                        FirebaseUtils.getCurrentFirebaseUser().unlink(GoogleAuthProvider.PROVIDER_ID).addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Your account was successfully unlinked from Google",
                                                    Toast.LENGTH_SHORT).show();
                                            google_checked.setVisibility(View.GONE);
                                            btn_log_in_with_google.setText(R.string.link_with_google);
                                            btn_log_in_with_google.setTextColor(Color.BLACK);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Unlinking from Google failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        loadingDialog.dismiss();
                                    }
                                });
                    }
                },
                R.string.cancel, (dialog, which) -> dialog.dismiss(),
                null).show();
    }

    /**
     * Create madrich verification dialog.
     */
    public void createMadrichVerificationDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.madrich_verification_dialog, null);

        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(this);

        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle("Madrich verification");
        builder.setMessage("Enter special madrich verification code:");

        TextInputLayout et_verify_madrich = view.findViewById(R.id.et_verify_madrich);
        Utils.addDefaultTextChangedListener(et_verify_madrich);

        builder.setPositiveButton(R.string.verify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = Utils.getText(et_verify_madrich);

                if (code.isEmpty() || code.length() < 6) {
                    et_verify_madrich.setError(getString(R.string.invalid_code));
                    return;
                }

                FirebaseUtils.isMadrichVerificationCode(Integer.parseInt(code)).observe(
                        ProfileScreen.this,
                        isMadrichCode ->
                                FirebaseUtils.getCurrentUserGroupDataRef().child(UserGroupData.KEY_MADRICH)
                                        .setValue(isMadrichCode)
                                        .addOnSuccessListener(unused -> {
                                                dialog.dismiss();
                                                et_madrich.setChecked(isMadrichCode);
                                                Toast.makeText(ProfileScreen.this,
                                                                isMadrichCode
                                                                ? "Madrich privileges were granted"
                                                                : "Invalid verification code",
                                                        Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                                dialog.dismiss();
                                                et_madrich.toggle();
                                                    Toast.makeText(ProfileScreen.this,
                                                            R.string.upps_something_went_wrong,
                                                            Toast.LENGTH_SHORT).show();
                                        })
                );
            }
        }, false);

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> et_madrich.setChecked(false));

        builder.show();
    }

    /**
     * Create stop being madrich dialog.
     */
    public void createStopBeingMadrichDialog(){
        Utils.createAlertDialog(this, null,
                "Are you sure about losing madrich privileges?",
                R.string.yes, (dialog, which) ->
                        FirebaseUtils.getCurrentUserGroupDataRef().child(UserGroupData.KEY_MADRICH)
                                .setValue(false)
                                .addOnSuccessListener(unused -> {
                                        et_madrich.setChecked(false);
                                        Toast.makeText(this, "MADRICH privileges were taken", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                        et_madrich.toggle();
                                        Toast.makeText(ProfileScreen.this,
                                            R.string.upps_something_went_wrong,
                                            Toast.LENGTH_SHORT).show();
                                }),
                R.string.no, (dialog, which) -> et_madrich.setChecked(true), null).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int selectedId = item.getItemId();

        Intent intent = new Intent(ProfileScreen.this, ProfileScreen.class);

        switch (selectedId) {
            case R.id.edit_profile:
                intent.putExtra("mode", true);
                startActivity(intent);
                finish();
                break;
            case R.id.save_profile:
                if(checkInput()){
                    onSaveProfileClick();
                }
                break;
            case android.R.id.home:
                startActivity(new Intent(ProfileScreen.this,
                        editMode ? ProfileScreen.class : MainActivity.class));
                break;
        }

        return true;
    }

    /**
     * Unsubscribe from topic.
     *
     * @param onFirebaseCallback the on firebase callback
     */
    public void unsubscribeFromTopic(FirebaseUtils.FirebaseCallback onFirebaseCallback) {
        FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.getTopic(FCMSend.ADD_EVENT_TOPIC))
                .addOnSuccessListener(unused1 -> FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.getTopic(FCMSend.EDIT_EVENT_TOPIC))
                        .addOnSuccessListener(unused2 -> FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(FCMSend.getTopic(FCMSend.DELETE_EVENT_TOPIC))
                                .addOnSuccessListener(unused3 ->
                                        FirebaseUtils.getCurrentUserGroups().observe(this,
                                                keys -> {
                                                    for (String key : keys){
                                                        FirebaseUtils.getFirebaseMessaging().unsubscribeFromTopic(key);
                                                    }
                                                    onFirebaseCallback.onFirebaseCallback();
                                                }))));
    }

    /**
     * On save profile click.
     */
    public void onSaveProfileClick() {
        FirebaseUtils.getCurrentUserData().observe(this, this::updateUserData);
    }

    /**
     * Update user data.
     *
     * @param userData the user data
     */
    public void updateUserData(@NonNull UserData userData){
        loadingDialog.setMessage("Updating the data...");
        loadingDialog.show();

        String username = Utils.getText(et_username);
        userData.setUsername(username);

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();
        userData.setPhone(user.getPhoneNumber());

        if (selectedImageUri != null && !Objects.equals(user.getPhotoUrl(), selectedImageUri)){
            uploadProfilePicture(userData, selectedImageUri);
        }
        else {
            uploadUser(userData);
        }
    }

    /**
     * Upload user.
     *
     * @param userData the user data
     */
    public void uploadUser(@NonNull UserData userData){
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(userData.getUsername());
        builder.setPhotoUri(userData.getPicture() != null ? Uri.parse(userData.getPicture()) : null);

        FirebaseUtils.getCurrentFirebaseUser().updateProfile(builder.build())
                .addOnSuccessListener(unused -> FirebaseUtils.getCurrentUserDataRef().setValue(userData)
                        .addOnSuccessListener(u -> {
                            loadingDialog.dismiss();
                            finish();
                            startActivity(new Intent(ProfileScreen.this, ProfileScreen.class));
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileScreen.this, "", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> {});
    }

    /**
     * Upload profile picture.
     *
     * @param userData the user data
     * @param imageUri the image uri
     */
    public void uploadProfilePicture(UserData userData, @NonNull Uri imageUri){

        FirebaseUtils.getCurrentUserProfilePictureRef().putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            userData.setPicture(uri.toString());
                            uploadUser(userData);
                        }))
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Updating your profile picture failed.\nPlease try again", Toast.LENGTH_SHORT).show();
                        })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "Uploaded picture is invalid.\nPlease try again", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Check phone number.
     */
    public void checkPhoneNumber() {
        String phone = Utils.getText(et_phone);

        if (phone.isEmpty()){
            et_phone.setError(getString(R.string.invalid_phone_number));
            return;
        }

        phone = phone.replaceAll("-", "");
        phone = "+972" + phone;

        if (phone.equals(FirebaseUtils.getCurrentFirebaseUser().getPhoneNumber())) {
            return;
        }

        String finalPhone = phone;
        FirebaseUtils.usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    if (Objects.equals(data.child(UserData.KEY_PHONE).getValue(String.class), finalPhone)){
                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "User with this phone number already exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                phoneAuth(finalPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        loadingDialog.setMessage("Updating phone number, please wait");
        loadingDialog.show();

        FirebaseUtils.getCurrentFirebaseUser().linkWithCredential(phoneAuthCredential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            String phone = task.getResult().getUser().getPhoneNumber();
                            FirebaseUtils.getCurrentUserDataRef().child(UserData.KEY_PHONE).setValue(phone);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Verification code is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Check input boolean.
     *
     * @return the boolean
     */
    public boolean checkInput(){

        boolean result = true;

        String username = Utils.getText(et_username);
        String email = Utils.getText(et_email);

        if(username.isEmpty()){
            et_username.setError("Enter username");
            result = false;
        }

        if(email.isEmpty() || !CalendarUtils.isEmailValid(email)){
            et_email.setError("E-mail invalid");
            result = false;
        }

        return result;
    }

    /**
     * Gets current user data.
     */
    public void getCurrentUserData() {
        if (!FirebaseUtils.isUserLoggedIn()) {
            startActivity(Utils.getIntentClearTop(new Intent(ProfileScreen.this, LogInScreen.class)));
            finish();
            return;
        }

        FirebaseUser user = FirebaseUtils.getCurrentFirebaseUser();

        String username = user.getDisplayName();
        String email = user.getEmail();

        Log.d("murad", "editMode is " + editMode);
        Log.d("murad", "email.isEmpty() is " + email.isEmpty());

        String phone = "";
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()){
            phone = user.getPhoneNumber().replace("+972", "");
            phone = addChar(phone, '-', 2);
            phone = addChar(phone, '-', 6);
        }

        FirebaseUtils.isMadrich().observe(this, isMadrich -> et_madrich.setChecked(isMadrich));

        Utils.setText(et_username, username);
        Utils.setText(et_email, email);
        Utils.setText(et_phone, phone);

        List<? extends UserInfo> providers = user.getProviderData();
        for (UserInfo userInfo : providers){
            Log.d("murad", "provider is " + userInfo.getProviderId());
            if (userInfo.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)){
                btn_log_in_with_google.setTextColor(Color.LTGRAY);
                btn_log_in_with_google.setText(R.string.linked);
                google_checked.setVisibility(View.VISIBLE);
            }
        }

        Uri pp = user.getPhotoUrl();

        Glide.with(this)
                .load(pp)
                .error(R.drawable.sample_profile_picture)
                .placeholder(R.drawable.sample_profile_picture)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        shimmer_profile_picture.stopShimmer();
                        shimmer_profile_picture.setAlpha(1f);
                        return false;
                    }
                })
                .centerCrop().into(iv_profile_picture);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        selectedImageUri = (Uri) savedInstanceState.get("selectedImageUri");
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("selectedImageUri", selectedImageUri);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    /**
     * Add char string.
     *
     * @param str      the str
     * @param ch       the ch
     * @param position the position
     *
     * @return the string
     */
    public String addChar(@NonNull String str, char ch, int position) {
        int len = str.length();
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = ch;
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
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

    @Override
    protected void googleAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d("murad", "credential" + credential);

        loadingDialog.setMessage(getString(R.string.logging_in_please_wait));
        loadingDialog.show();

        FirebaseUtils.getCurrentFirebaseUser().linkWithCredential(credential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Account was successfully connected to Google", Toast.LENGTH_SHORT).show();
                            btn_log_in_with_google.setChecked(true);
                            btn_log_in_with_google.setTextColor(Color.LTGRAY);
                            btn_log_in_with_google.setText(R.string.linked);
                            google_checked.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Connecting account to Google failed", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
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
                    Glide.with(this).load(selectedImageUri).centerCrop().into(iv_profile_picture);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (editMode){
            startActivity(new Intent(ProfileScreen.this, ProfileScreen.class));
        }
        else {
            super.onBackPressed();
        }
    }
}