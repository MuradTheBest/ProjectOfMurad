package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.training.Training;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseManager {

    Context context;

    public FirebaseManager(Context context) {
        this.context = context;

        FirebaseUtils.getCurrentGroup().observe((LifecycleOwner) context, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
    }

    @NonNull
    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @NonNull
    public static FirebaseDatabase getDatabase(){
        return FirebaseDatabase.getInstance();
    }

    @NonNull
    public static FirebaseMessaging getFirebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    @NonNull
    public static StorageReference getFirebaseStorage() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseUser getCurrentFirebaseUser(){
        return getFirebaseAuth().getCurrentUser();
    }

    public static long getCurrentUserLastSignIn(){
        return getCurrentFirebaseUser().getMetadata().getLastSignInTimestamp();
    }

    @NonNull
    public static StorageReference getProfilePicturesRef() {
        return getFirebaseStorage().child("Profile pictures");
    }

    @NonNull
    public static StorageReference getCurrentUserProfilePictureRef() {
        return getProfilePicturesRef().child(getCurrentUID());
    }

    @NonNull
    public static DatabaseReference getCurrentUserTrainingsRef() {
        return trainingsDatabase.child("Users").child(getCurrentUID());
//        return getCurrentUserDataRef().child("Trainings");
    }

    @NonNull
    public static DatabaseReference getCurrentUserTrackingRef(String event_private_id){
        return attendanceDatabase.child(event_private_id).child(getCurrentUID());
    }

    public static final DatabaseReference groupsDatabase = getDatabase().getReference("Groups").getRef();
    public static String CURRENT_GROUP_KEY = "";

    @NonNull
    public static DatabaseReference getCurrentGroupRef() {
        return groupsDatabase.child(CURRENT_GROUP_KEY);
    }

    @NonNull
    public static LiveData<String> getCurrentGroup(){
        MutableLiveData<String> currentGroup = new MutableLiveData<>();

        getCurrentUserDataRef().child("currentGroup").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful() || !task.getResult().exists()) {
                            return;
                        }

                        String groupKey = task.getResult().getKey();
                        CURRENT_GROUP_KEY = groupKey;
                        currentGroup.setValue(groupKey);
                    }
                });

        return currentGroup;
    }

    public static void addGroupToCurrentUser(String key, FirebaseCallback firebaseCallback){
        FirebaseManager.getCurrentUserDataRef().child("groups").get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        List<String> groups = dataSnapshot.getValue(
                                List.class);
                        groups.add(key);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("currentGroup", key);
                        map.put("groups", groups);

                        FirebaseManager.getCurrentUserDataRef().updateChildren(map).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        firebaseCallback.onFirebaseCallback();
                                    }
                                });

                    }
                });
    }

    public static final DatabaseReference eventsDatabase = getDatabase().getReference("Events").getRef();
    public static final DatabaseReference allEventsDatabase = getDatabase().getReference("AllEvents").getRef();
    public static final DatabaseReference attendanceDatabase = getDatabase().getReference("Attendance").getRef();
    public static final DatabaseReference trackingDatabase = getDatabase().getReference("Tracking").getRef();
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();
    public static final DatabaseReference trainingsDatabase = getDatabase().getReference("Trainings").getRef();

    public void group(){
        return ;
    }

    @NonNull
    public static DatabaseReference getEventsDatabase(){
        return getDatabase().getReference().child("Events").getRef();
    }

    @NonNull
    public static DatabaseReference getAllEventsDatabase(){
        return getDatabase().getReference("AllEvents").getRef();
    }

    @NonNull
    public static DatabaseReference getAttendanceDatabase(){
        return getDatabase().getReference("Attendance").getRef();
    }

    @NonNull
    public static DatabaseReference getTrackingDatabase(){
        return getDatabase().getReference("Tracking").getRef();
    }

    @NonNull
    public static DatabaseReference getUsersDatabase(){
        return getDatabase().getReference("Users").getRef();
    }

    @NonNull
    public static DatabaseReference getTrainingsDatabase(){
        return getDatabase().getReference("Trainings").getRef();
    }

    @NonNull
    public static DatabaseReference getUserTrainingsRefForEvent(String UID, String eventPrivateId){
        return trainingsDatabase.child("Events").child(eventPrivateId).child(UID).child("Trainings");
    }

    @NonNull
    public static DatabaseReference getCurrentUserTrainingsRefForEvent(String eventPrivateId){
        return getUserTrainingsRefForEvent(getCurrentUID(), eventPrivateId);
    }

    @NonNull
    public static Task<Void> addTrainingForEvent(String eventPrivateId, @NonNull Training training){
        getCurrentUserDataRef().child("show").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        int show = task.getResult().getValue(int.class);
                        getCurrentUserTrainingsRefForEvent(eventPrivateId).getParent().child("show").setValue(show);
                    }
                });
        return getCurrentUserTrainingsRefForEvent(eventPrivateId).child(training.getPrivateId()).setValue(training);
    }

    @NonNull
    public static MutableLiveData<ArrayList<Training>> getUserTrainingsForEvent(String UID, String event_private_id) {
        MutableLiveData<ArrayList<Training>> trainings = new MutableLiveData<>();

        getUserTrainingsRefForEvent(UID, event_private_id).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(LOG_TAG, snapshot.getRef().toString());
                        ArrayList<Training> trainingArrayList = new ArrayList<>();

                        for (DataSnapshot training : snapshot.getChildren()){
                            Training t = training.getValue(Training.class);
                            Log.d(LOG_TAG, t.toString());
                            trainingArrayList.add(t);
                        }
                        Log.d(LOG_TAG, trainingArrayList.toString());
                        trainings.setValue(trainingArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return trainings;
    }

    @NonNull
    public static LiveData<ArrayList<Training>> getCurrentUserTrainingsForEvent(String event_private_id){
        return getUserTrainingsForEvent(getCurrentUID(), event_private_id);
    }

    public interface FirebaseCallback {
        void onFirebaseCallback();
    }

    public interface GroupCallback {
        void onGroupCallback(String key);
    }

    @NonNull
    public static DatabaseReference getCurrentUserDataRef(){
        return usersDatabase.child(getCurrentUID()).getRef();
    }

    public static void getCurrentUserData(OnUserDataCallBack onUserDataCallBack){

        getCurrentUserDataRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserData currentUserData = snapshot.getValue(UserData.class);
                            onUserDataCallBack.onUserDataCallBack(currentUserData);
                            Log.d(LOG_TAG, currentUserData.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @NonNull
    public static LiveData<UserData> getCurrentUserData(){
        MutableLiveData<UserData> userData = new MutableLiveData<>();

        getCurrentUserDataRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
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

    @NonNull
    public static LiveData<String> getCurrentUsername(){
        MutableLiveData<String> username = new MutableLiveData<>();

        getCurrentUserDataRef().child("username").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String user_name = snapshot.getValue(String.class);
                            username.setValue(user_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return username;
    }

    @NonNull
    public static LiveData<Boolean> isMadrich(){
        MutableLiveData<Boolean> isMadrich = new MutableLiveData<>();

        getCurrentUserDataRef().child("madrich").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean madrich = snapshot.getValue(boolean.class);
                            isMadrich.setValue(madrich);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return isMadrich;
    }

    @NonNull
    public static DatabaseReference getUserDataByUIDRef(String UID){
        return usersDatabase.child(UID).getRef();
    }

    public interface OnUserDataCallBack{
        void onUserDataCallBack(UserData userData);
    }

    @NonNull
    public static String getCurrentUID(){
        return getCurrentFirebaseUser().getUid();
    }

    public static boolean isCurrentUID(@NonNull String UID){
        return getCurrentUID().equals(UID);
    }

    public static boolean isUserLoggedIn(){
        return getCurrentFirebaseUser() != null;
    }

    public static void getProfilePictureFromFB(String UID, Context context, ImageView imageView){
        getProfilePictureFromFB(UID, context, imageView, null);
    }

    public static void getProfilePictureFromFB(String UID, Context context, @NonNull ImageView imageView, ShimmerFrameLayout shimmerFrameLayout){
        imageView.setVisibility(View.INVISIBLE);

        DatabaseReference ref = FirebaseManager.usersDatabase.child(UID).child("profile_picture");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()) {
                    String profile_picture = task.getResult().getValue(String.class);
                    if (context != null){
                        Glide.with(context).load(profile_picture).centerCrop().into(imageView);
                    }
                }
                else {
                    Glide.with(context).load(R.drawable.images).centerCrop().into(imageView);
                }

                imageView.setVisibility(View.VISIBLE);

                if (shimmerFrameLayout != null){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            }
        });
    }


}