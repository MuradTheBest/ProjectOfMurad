package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.LOG_TAG;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.projectofmurad.tracking.Training;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        return getCurrentUserDataRef().child("Trainings");
    }

    public static final DatabaseReference eventsDatabase = getDatabase().getReference("Events").getRef();
    public static final DatabaseReference allEventsDatabase = getDatabase().getReference("AllEvents").getRef();
    public static final DatabaseReference attendanceDatabase = getDatabase().getReference("Attendance").getRef();
    public static final DatabaseReference trackingDatabase = getDatabase().getReference("Tracking").getRef();
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();
    public static final DatabaseReference trainingsDatabase = getDatabase().getReference("Trainings").getRef();

    @NonNull
    public static DatabaseReference getCurrentUserTrainingsRefForEvent(String eventPrivateId){
        return allEventsDatabase.child(eventPrivateId).child("Users").child(getCurrentUID())/*.child("Trainings")*/;
    }

    @NonNull
    public static Task<Void> addTrainingForEvent(String eventPrivateId, @NonNull Training training){
        return getCurrentUserTrainingsRefForEvent(eventPrivateId).child(training.getPrivateId()).setValue(training);
    }

    public interface onSimpleFirebaseCallback{
        void SimpleFirebaseCallback();
    }

    public interface OnUpdateListener{
        void onUpdate();
    }

    private static boolean emailUpdateComplete = true;
    private static boolean phoneUpdateComplete = true;
    private static boolean profileUpdateComplete = true;

    public static void updateUserData(String username, String email, String phone, Uri profile_picture, Context context){
        update(username, email, phone, profile_picture, context, new OnUpdateListener() {
            @Override
            public void onUpdate() {

                if (emailUpdateComplete && phoneUpdateComplete && profileUpdateComplete){
                    //Updated
                }

            }
        });
    }

    private static void update(@NonNull String username, String email, String phone, Uri profile_picture, Context context, OnUpdateListener onUpdateListener){
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        if (!username.equals(getCurrentFirebaseUser().getDisplayName())) {
            profileUpdateComplete = false;
            builder.setDisplayName(username);
        }
        if (!email.equals(getCurrentFirebaseUser().getEmail())) {
            emailUpdateComplete = false;
//            FirebaseUtils.getCurrentFirebaseUser().updateEmail(email);
            FirebaseUtils.getCurrentFirebaseUser().verifyBeforeUpdateEmail(email).addOnCompleteListener(
                    (Activity) context, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                emailUpdateComplete = true;
                                onUpdateListener.onUpdate();
                            }
                        }
                    });
        }
        if (!phone.equals(getCurrentFirebaseUser().getPhoneNumber())) {
            phoneUpdateComplete = false;
            updatePhoneNumber(phone, context, onUpdateListener);
        }
        if (profile_picture != getCurrentFirebaseUser().getPhotoUrl()){
            profileUpdateComplete = false;
            builder.setPhotoUri(profile_picture);
        }

        FirebaseUtils.getCurrentFirebaseUser().updateProfile(builder.build()).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        profileUpdateComplete = true;
                        onUpdateListener.onUpdate();
                    }
                });
    }

    private static void updatePhoneNumber(String phone, Context context, OnUpdateListener onUpdateListener){
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder().setPhoneNumber(phone).setActivity((Activity) context).requireSmsValidation(true).setCallbacks(
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(
                            @NonNull PhoneAuthCredential phoneAuthCredential) {
                        FirebaseUtils.getCurrentFirebaseUser().updatePhoneNumber(phoneAuthCredential);
                        phoneUpdateComplete = true;
                        onUpdateListener.onUpdate();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).setTimeout(120L, TimeUnit.SECONDS).build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    @NonNull
    public static DatabaseReference getCurrentUserDataRef(){
        return usersDatabase.child(getCurrentUID()).getRef();
    }

    private static UserData currentUserData;

    public static void getCurrentUserData(OnUserDataCallBack onUserDataCallBack){
        currentUserData = new UserData();

        usersDatabase.child(getCurrentUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUserData = snapshot.getValue(UserData.class);
                            onUserDataCallBack.onUserDataCallBack(currentUserData);
                            assert currentUserData != null;
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

    private static UserData userData;

    public interface OnUserDataCallBack{
        void onUserDataCallBack(UserData userData);
    }

    @NonNull
    public static UserData getUserDataByUID(String UID, OnUserDataCallBack onUserDataCallBack){
        userData = new UserData();

        usersDatabase.child(UID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            userData = snapshot.getValue(UserData.class);
                        }
                        else {
                            Toast.makeText(getContext(), "This user doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return userData;
    }

    @NonNull
    public static String getCurrentUID(){
//        Log.d(LOG_TAG, "CurrentUID is " + getCurrentFirebaseUser().getUid());
        return getCurrentFirebaseUser().getUid();
    }

    @NonNull
    public static List<String> getAllUIDs(){
        List<String> UIDs = new ArrayList<>();
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    UIDs.add(data.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return UIDs;
    }

    @NonNull
    public static List<String> getAllUIDs(String except){
        List<String> UIDs = new ArrayList<>();
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    UIDs.add(data.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UIDs.remove(getCurrentUID());

        return UIDs;
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
        /*StorageReference sr = FirebaseUtils.getProfilePicturesRef().child(userID).getDownloadUrl().addOnCompleteListener(
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