package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.training.Training;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class FirebaseUtils {

//    public Context context = MyApplication.getContext();

    private static Context getContext(){
        return MyApplication.getContext();
    }

    public static FirebaseUser getCurrentFirebaseUser(){
        return getFirebaseAuth().getCurrentUser();
    }

    public static long getCurrentUserLastSignIn(){
        return getCurrentFirebaseUser().getMetadata().getLastSignInTimestamp();
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

    public static final DatabaseReference eventsDatabase = getDatabase().getReference("Events").getRef();
    public static final DatabaseReference allEventsDatabase = getDatabase().getReference("AllEvents").getRef();
    public static final DatabaseReference attendanceDatabase = getDatabase().getReference("Attendance").getRef();
    public static final DatabaseReference trackingDatabase = getDatabase().getReference("Tracking").getRef();
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();
    public static final DatabaseReference trainingsDatabase = getDatabase().getReference("Trainings").getRef();
    public static final DatabaseReference tablesDatabase = getDatabase().getReference("Tables").getRef();

    @NonNull
    public static DatabaseReference getCurrentUserTrainingsRefForEvent(String eventPrivateId){
        return trainingsDatabase.child("Events").child(eventPrivateId).child(getCurrentUID());
    }

    @NonNull
    public static Task<Void> addTrainingForEvent(String eventPrivateId, @NonNull Training training){
        return getCurrentUserTrainingsRefForEvent(eventPrivateId).child(training.getPrivateId()).setValue(training);
    }

    @NonNull
    public static LiveData<ArrayList<Training>> getCurrentUserTrainingsForEvent(String event_private_id){
        MutableLiveData<ArrayList<Training>> trainings = new MutableLiveData<>();

        ArrayList<Training> trainingArrayList = new ArrayList<>();

        getCurrentUserTrainingsRefForEvent(event_private_id).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(LOG_TAG, snapshot.getRef().toString());

                        for (DataSnapshot training : snapshot.getChildren()){
                            Training t = training.getValue(Training.class);
                            Log.d(LOG_TAG, t.toString());
                            trainingArrayList.add(t);
                        }
                        Log.d(LOG_TAG, trainingArrayList.toString());
                        trainings.postValue(trainingArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return trainings;
    }

    public interface onSimpleFirebaseCallback{
        void SimpleFirebaseCallback();
    }

    @NonNull
    public static DatabaseReference getCurrentUserDataRef(){
        return usersDatabase.child(getCurrentUID()).getRef();
    }

    public static void getCurrentUserData(OnUserDataCallBack onUserDataCallBack){

        usersDatabase.child(getCurrentUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserData currentUserData = snapshot.getValue(UserData.class);
                            onUserDataCallBack.onUserDataCallBack(currentUserData);
                            Log.d(LOG_TAG, currentUserData.toString());
                        }
                        else {
                            Toast.makeText(getContext(), "This user doesn't exist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//        return currentUserData;
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
        return UID.equals(getCurrentUID());
    }

    public static boolean isUserLoggedIn(){
        return getCurrentFirebaseUser() != null;
    }

    public static void getProfilePictureFromFB(String UID, Context context, ImageView imageView){

        DatabaseReference ref = FirebaseUtils.usersDatabase.child(UID).child("profile_picture");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    String profile_picture = task.getResult().getValue(String.class);
                    Glide.with(context).load(profile_picture).centerCrop().into(imageView);
                }
                else {
                    imageView.setImageResource(R.drawable.sample_profile);
                }
            }
        });
        /*StorageReference sr = FirebaseUtils.getProfilePicturesRef().child(UID).getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                    }
                })*/
    }

    public static void getProfilePictureFromFB(String UID, Context context, ImageView imageView, ShimmerFrameLayout shimmerFrameLayout){

        DatabaseReference ref = FirebaseUtils.usersDatabase.child(UID).child("profile_picture");

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()) {
                    String profile_picture = task.getResult().getValue(String.class);
                    if (context != null){
                        Glide.with(context).load(profile_picture).centerCrop().into(imageView);
                    }
                }
                else {
                    imageView.setImageResource(R.drawable.sample_profile);
                }

                imageView.setVisibility(View.VISIBLE);

                if (shimmerFrameLayout != null){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            }
        });

        /*Uri pp = getCurrentFirebaseUser().getPhotoUrl();
        Glide.with(context).load(pp).centerCrop().into(imageView);
        imageView.setVisibility(View.VISIBLE);

        if (shimmerFrameLayout != null){
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }*/
    }


}