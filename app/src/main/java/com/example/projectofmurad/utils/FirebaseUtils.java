package com.example.projectofmurad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.MyApplication;
import com.example.projectofmurad.R;
import com.example.projectofmurad.UserData;
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.groups.UserGroupData;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utils class for all the actions will Firebase
 */
public abstract class FirebaseUtils {

    /**
     * Get firebase auth firebase auth.
     *
     * @return the firebase auth
     */
    @NonNull
    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    /**
     * Get database firebase database.
     *
     * @return the firebase database
     */
    @NonNull
    public static FirebaseDatabase getDatabase(){
        return FirebaseDatabase.getInstance();
    }

    /**
     * Gets firebase messaging.
     *
     * @return the firebase messaging
     */
    @NonNull
    public static FirebaseMessaging getFirebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    /**
     * Gets firebase storage.
     *
     * @return the firebase storage
     */
    @NonNull
    public static StorageReference getFirebaseStorage() {
        return FirebaseStorage.getInstance().getReference();
    }

    /**
     * Get current firebase user firebase user.
     *
     * @return the firebase user
     */
    public static FirebaseUser getCurrentFirebaseUser(){
        return getFirebaseAuth().getCurrentUser();
    }

    /**
     * Gets profile pictures ref.
     *
     * @return the profile pictures ref
     */
    @NonNull
    public static StorageReference getProfilePicturesRef() {
        return getFirebaseStorage().child("Profile pictures");
    }

    /**
     * Gets current user profile picture ref.
     *
     * @return the current user profile picture ref
     */
    @NonNull
    public static StorageReference getCurrentUserProfilePictureRef() {
        return getProfilePicturesRef().child(getCurrentUID());
    }

    /**
     * Get user tracking ref database reference.
     *
     * @param event_private_id the event private id
     * @param UID              the uid
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getUserTrackingRef(String event_private_id, String UID){
        return getAttendanceDatabase().child(event_private_id).child(UID);
    }

    /**
     * Get current user tracking ref database reference.
     *
     * @param event_private_id the event private id
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getCurrentUserTrackingRef(String event_private_id){
        return getUserTrackingRef(event_private_id, getCurrentUID());
    }

    /**
     * The constant groups.
     */
    public static final DatabaseReference groups = getDatabase().getReference("Groups").getRef();
    /**
     * The constant groupDatabases.
     */
    public static final DatabaseReference groupDatabases = getDatabase().getReference("Group Databases").getRef();

    /**
     * The constant CURRENT_GROUP_KEY.
     */
    public static String CURRENT_GROUP_KEY = MyApplication.getContext()
            .getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            .getString(UserData.KEY_CURRENT_GROUP, "");

    /**
     * The constant CURRENT_GROUP_COLOR.
     */
    public static int CURRENT_GROUP_COLOR = MyApplication.getContext()
            .getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
            .getInt(CURRENT_GROUP_KEY, MyApplication.getContext().getColor(R.color.colorAccent));

    /**
     * Is current group boolean.
     *
     * @param key the key
     *
     * @return the boolean
     */
    public static boolean isCurrentGroup(String key){
        return Objects.equals(CURRENT_GROUP_KEY, key);
    }

    /**
     * Gets current group database.
     *
     * @return the current group database
     */
    @NonNull
    public static DatabaseReference getCurrentGroupDatabase() {
        Log.d(Utils.LOG_TAG, getGroupDatabase(CURRENT_GROUP_KEY).toString());
        return getGroupDatabase(CURRENT_GROUP_KEY);
    }

    /**
     * Gets group database.
     *
     * @param groupKey the group key
     *
     * @return the group database
     */
    @NonNull
    public static DatabaseReference getGroupDatabase(String groupKey) {
        return groupDatabases.child(groupKey).getRef();
    }

    /**
     * Change group.
     *
     * @param context the context
     * @param key     the key
     */
    public static void changeGroup(@NonNull Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(UserData.KEY_CURRENT_GROUP, key);
        editor.apply();

        CURRENT_GROUP_KEY = key;
    }

    /**
     * Change group.
     *
     * @param context    the context
     * @param key        the key
     * @param groupColor the group color
     */
    public static void changeGroup(@NonNull Context context, String key, int groupColor) {
        SharedPreferences sp = context.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(UserData.KEY_CURRENT_GROUP, key);
        editor.putInt(key, groupColor);
        editor.apply();

        CURRENT_GROUP_KEY = key;
        CURRENT_GROUP_COLOR = groupColor;
    }

    /**
     * Gets current group name.
     *
     * @return the current group name
     */
    @NonNull
    public static LiveData<String> getCurrentGroupName() {
        MutableLiveData<String> currentGroupName = new MutableLiveData<>();

        groups.child(CURRENT_GROUP_KEY).child(Group.KEY_NAME).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupName = snapshot.getValue(String.class);
                        currentGroupName.setValue(groupName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return currentGroupName;
    }

    /**
     * Gets current group picture.
     *
     * @return the current group picture
     */
    @NonNull
    public static LiveData<String> getCurrentGroupPicture() {
        MutableLiveData<String> currentGroupPicture = new MutableLiveData<>();

        groups.child(CURRENT_GROUP_KEY).child(Group.KEY_PICTURE).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupPicture = snapshot.getValue(String.class);
                        currentGroupPicture.setValue(groupPicture);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return currentGroupPicture;
    }

    /**
     * Check current group task.
     *
     * @return the task
     */
    @NonNull
    public static Task<DataSnapshot> checkCurrentGroup() {
        return getCurrentUserDataRef().child(UserData.KEY_CURRENT_GROUP).get();
    }

    /**
     * Get current group users database reference.
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getCurrentGroupUsers(){
        return getCurrentGroupDatabase().child("Users");
    }

    /**
     * Get user group data ref database reference.
     *
     * @param UID the uid
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getUserGroupDataRef(String UID){
        return getCurrentGroupUsers().child(UID);
    }

    /**
     * Get current user group data ref database reference.
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getCurrentUserGroupDataRef(){
        return getUserGroupDataRef(getCurrentUID());
    }

    /**
     * Add group to current user.
     *
     * @param groupKey                the group key
     * @param isMadrich               the is madrich
     * @param firebaseCallback        the firebase callback
     * @param firebaseFailureCallback the firebase failure callback
     */
    public static void addGroupToCurrentUser(String groupKey, boolean isMadrich,
                                             FirebaseCallback firebaseCallback,
                                             @NonNull FirebaseFailureCallback firebaseFailureCallback){

        UserGroupData userGroupData = new UserGroupData(getCurrentUID(), groupKey, isMadrich);

        getGroupDatabase(groupKey).child("Users").child(getCurrentUID()).setValue(userGroupData)
                .addOnSuccessListener(unused1 -> getCurrentUserDataRef().child(UserData.KEY_CURRENT_GROUP).setValue(groupKey)
                        .addOnSuccessListener(unused2 -> getFirebaseMessaging().subscribeToTopic(groupKey)
                                .addOnSuccessListener(unused3 -> firebaseCallback.onFirebaseCallback())
                                .addOnFailureListener(firebaseFailureCallback::onFirebaseFailure))
                        .addOnFailureListener(firebaseFailureCallback::onFirebaseFailure))
                .addOnFailureListener(firebaseFailureCallback::onFirebaseFailure);
    }

    /**
     * Get current user groups live data.
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<List<String>> getCurrentUserGroups(){
        MutableLiveData<List<String>> groups = new MutableLiveData<>();

        groupDatabases.orderByChild("Users/" + getCurrentUID() + "/uid").equalTo(getCurrentUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> keys = new ArrayList<>();

                        if (!snapshot.exists() && !snapshot.hasChildren()) {
                            groups.setValue(keys);
                            return;
                        }

                        for (DataSnapshot group : snapshot.getChildren()) {
                            keys.add(group.getKey());
                        }

                        groups.setValue(keys);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}

                });

        return groups;
    }

    /**
     * The constant usersDatabase.
     */
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();

    /**
     * Gets events database.
     *
     * @return the events database
     */
    @NonNull
    public static DatabaseReference getEventsDatabase() {
        return getCurrentGroupDatabase().child("Events").getRef();
    }

    /**
     * Get all events database database reference.
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getAllEventsDatabase(){
        return getCurrentGroupDatabase().child("All Events").getRef();
    }

    /**
     * Get attendance database database reference.
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getAttendanceDatabase(){
        return getCurrentGroupDatabase().child("Attendance").getRef();
    }

    /**
     * The interface Firebase callback.
     */
    public interface FirebaseCallback {
        /**
         * On firebase callback.
         */
        void onFirebaseCallback();
    }

    /**
     * The interface Firebase failure callback.
     */
    public interface FirebaseFailureCallback {
        /**
         * On firebase failure.
         *
         * @param e the e
         */
        void onFirebaseFailure(Exception e);
    }

    /**
     * Gets events for date ref.
     *
     * @param localDate the local date
     *
     * @return the events for date ref
     */
    @NonNull
    public static DatabaseReference getEventsForDateRef(@NonNull LocalDate localDate) {
        return getEventsDatabase().child(localDate.toString());
    }

    /**
     * Get current user data ref database reference.
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getCurrentUserDataRef(){
        return getUserDataByUIDRef(getCurrentUID()).getRef();
    }

    /**
     * Get current user data live data.
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<UserData> getCurrentUserData(){
        MutableLiveData<UserData> userData = new MutableLiveData<>();

        getCurrentUserDataRef().addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserData currentUserData = snapshot.getValue(UserData.class);
                            userData.setValue(currentUserData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return userData;
    }

    /**
     * Get current user group data live data.
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<UserGroupData> getCurrentUserGroupData(){
        MutableLiveData<UserGroupData> userGroupData = new MutableLiveData<>();

        getCurrentGroupDatabase().child("Users").child(getCurrentUID()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserGroupData currentUserGroupDataData = snapshot.getValue(UserGroupData.class);
                    userGroupData.setValue(currentUserGroupDataData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return userGroupData;
    }

    /**
     * Get current username live data.
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<String> getCurrentUsername(){
        MutableLiveData<String> username = new MutableLiveData<>();

        getCurrentUserDataRef().child(UserData.KEY_USERNAME).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String user_name = snapshot.getValue(String.class);
                            username.setValue(user_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        return username;
    }

    /**
     * Is madrich live data.
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<Boolean> isMadrich(){
        MutableLiveData<Boolean> isMadrich = new MutableLiveData<>();

        getCurrentUserGroupDataRef().child(UserGroupData.KEY_MADRICH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isMadrich.setValue(snapshot.exists() && snapshot.getValue(boolean.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return isMadrich;
    }

    /**
     * Get user data by uid ref database reference.
     *
     * @param UID the uid
     *
     * @return the database reference
     */
    @NonNull
    public static DatabaseReference getUserDataByUIDRef(String UID){
        return usersDatabase.child(UID).getRef();
    }

    /**
     * Get current uid string.
     *
     * @return the string
     */
    @NonNull
    public static String getCurrentUID(){
        return getCurrentFirebaseUser().getUid();
    }

    /**
     * Is current uid boolean.
     *
     * @param UID the uid
     *
     * @return the boolean
     */
    public static boolean isCurrentUID(String UID){
        return Objects.equals(getCurrentUID(), UID);
    }

    /**
     * Is user logged in boolean.
     *
     * @return the boolean
     */
    public static boolean isUserLoggedIn(){
        return getCurrentFirebaseUser() != null;
    }

    /**
     * Is madrich verification code live data.
     *
     * @param code the code
     *
     * @return the live data
     */
    @NonNull
    public static LiveData<Boolean> isMadrichVerificationCode(int code){
        MutableLiveData<Boolean> isMadrichCode = new MutableLiveData<>();

        groups.child(CURRENT_GROUP_KEY).child(Group.KEY_MADRICH_CODE).get().addOnSuccessListener(
                dataSnapshot -> isMadrichCode.setValue(Objects.equals(dataSnapshot.getValue(int.class), code)));

        return isMadrichCode;
    }

    /**
     * Deletes all the data in {@link FirebaseDatabase} with this key starting from rootRef and deeper.
     *
     * @param rootRef          Starting location for deleting
     * @param key              Key of location which value has to be deleted
     * @param firebaseCallback Callback that will happen when deleting will be finished
     */
    public static void deleteAll(@NonNull Query rootRef, String key, FirebaseCallback firebaseCallback){
        Log.d("snapshot", "Attempt to delete");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("snapshot", "Starting delete");
                        deleteData(snapshot, key);
                        firebaseCallback.onFirebaseCallback();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     *
     * @param snapshot
     * @param key
     */
    private static void deleteData(@NonNull DataSnapshot snapshot, String key){
        if (!snapshot.exists() && !snapshot.hasChildren()) return;

        Log.d("snapshot", snapshot.getRef().toString());

        for (DataSnapshot data : snapshot.getChildren()) {
            if (Objects.equals(data.getKey(), key) || Objects.equals(data.getValue(), key)){
                data.getRef().removeValue();
            }
            deleteData(data, key);
        }
    }

    private static void deleteData2(@NonNull DataSnapshot snapshot, String key){
        if (!snapshot.exists() && !snapshot.hasChildren()) return;

        Log.d("snapshot", snapshot.getRef().toString());

        if (Objects.equals(snapshot.getKey(), key)){
            snapshot.getRef().removeValue();
            return;
        }

        for (DataSnapshot data : snapshot.getChildren()) {
            deleteData2(data, key);
        }
    }

    /**
     * Create re authenticate dialog.
     *
     * @param context          the context
     * @param firebaseCallback the firebase callback
     */
    public static void createReAuthenticateDialog(Context context, FirebaseCallback firebaseCallback){
        View view = LayoutInflater.from(context).inflate(R.layout.reauthenticate_dialog, null);

        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(context);

        builder.setView(view);
        builder.setTitle(R.string.in_order_to_continue_you_are_required_to_prove_your_identity);

        TextInputLayout et_email_address = view.findViewById(R.id.et_email_address);
        TextInputLayout et_password = view.findViewById(R.id.et_password);

        Utils.addDefaultTextChangedListener(et_email_address, et_password);

        builder.setPositiveButton(R.string.text_continue,
                (dialog, which) -> checkFields(context, et_email_address, et_password, firebaseCallback), false);

        builder.setNegativeButton(R.string.back, (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private static void checkFields(Context context, @NonNull TextInputLayout et_email_address,
                             @NonNull TextInputLayout et_password, FirebaseCallback firebaseCallback) {

        String email = Utils.getText(et_email_address);
        String password = Utils.getText(et_password);

        boolean editTextsFilled = true;

        if (email.isEmpty() || !CalendarUtils.isEmailValid(email)) {
            et_email_address.setError(context.getString(R.string.invalid_email));
            editTextsFilled = false;
        }

        if(password.isEmpty()){
            et_password.setError(context.getString(R.string.invalid_password));
            editTextsFilled = false;
        }

        if(editTextsFilled){
            reAuthenticate(context, email, password, firebaseCallback);
        }
    }

    /**
     * Re authenticate.
     *
     * @param context          the context
     * @param email            the email
     * @param password         the password
     * @param firebaseCallback the firebase callback
     */
    public static void reAuthenticate(Context context, String email, String password, FirebaseCallback firebaseCallback){
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setMessage(context.getString(R.string.logging_in_please_wait));
        loadingDialog.show();

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

        getCurrentFirebaseUser().reauthenticate(authCredential)
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    firebaseCallback.onFirebaseCallback();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(context, "Entered login and password are wrong", Toast.LENGTH_SHORT).show();
                });
    }
}