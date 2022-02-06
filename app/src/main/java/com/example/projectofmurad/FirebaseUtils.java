package com.example.projectofmurad;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseUtils {

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
    public static FirebaseMessaging getFirebaseMessaging(){
        return FirebaseMessaging.getInstance();
    }

    //ToDo change reference to "Events"
    public static final DatabaseReference eventsDatabase = getDatabase().getReference("EventsDatabase").getRef();
    public static final DatabaseReference usersDatabase = getDatabase().getReference("Users").getRef();

    private static UserData currentUserData;

    public static UserData getCurrentUserData(){
        currentUserData = new UserData();

        usersDatabase.child(getCurrentUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUserData = snapshot.getValue(UserData.class);
                        }
                        else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return currentUserData;
    }

    @NonNull
    public static String getCurrentUID(){
        return getCurrentFirebaseUser().getUid();
    }

    public static boolean isUserLoggedIn(){
        return getCurrentFirebaseUser() != null;
    }
}
