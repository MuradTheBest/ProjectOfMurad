package com.example.projectofmurad;

import static com.example.projectofmurad.Utils.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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
import java.util.List;

public class FirebaseUtils {

//    public Context context = MyApplication.getContext();

    private static Context getContext(){
        return MyApplication.getContext();
    }

    public static FirebaseUser getCurrentFirebaseUser(){
        return getFirebaseAuth().getCurrentUser();
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

    public static final DatabaseReference eventsDatabase = getDatabase().getReference("Events").getRef();
    public static final DatabaseReference allEventsDatabase = getDatabase().getReference("AllEvents").getRef();
    public static final DatabaseReference attendanceDatabase = getDatabase().getReference("Attendance").getRef();
    public static final DatabaseReference trackingDatabase = getDatabase().getReference("Tracking").getRef();
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();

    @NonNull
    public static DatabaseReference getCurrentUserDataRef(){
        return usersDatabase.child(getCurrentUID()).getRef();
    }

    private static UserData currentUserData;

    public static UserData getCurrentUserData(){
        currentUserData = new UserData();

        usersDatabase.child(getCurrentUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUserData = snapshot.getValue(UserData.class);
                            assert currentUserData != null;
                            Log.d(TAG, currentUserData.toString());
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

        return currentUserData;
    }

    @NonNull
    public static DatabaseReference getUserDataByUIDRef(String UID){
        return usersDatabase.child(UID).getRef();
    }

    private static UserData userData;

    @NonNull
    public static UserData getUserDataByUID(String UID){
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
//        Log.d(TAG, "CurrentUID is " + getCurrentFirebaseUser().getUid());
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
                    Glide.with(context).load(profile_picture).centerCrop().into(imageView);

                }
                else {
                    imageView.setImageResource(R.drawable.sample_profile);
                }

                imageView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }
}