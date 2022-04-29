
package com.example.projectofmurad;

import static com.example.projectofmurad.helpers.Utils.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.projectofmurad.groups.ShowGroupsScreen;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import pl.droidsonroids.gif.GifImageView;

public class Splash_Screen extends MyActivity {

    private ConstraintLayout constraintLayout;
    private GifImageView iv_intro;
    private RelativeLayout group_1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);
        getSupportActionBar().hide();

        constraintLayout = findViewById(R.id.constraintLayout);
        iv_intro = findViewById(R.id.iv_intro);

        group_1 = findViewById(R.id.group_1);

        SQLiteDatabase db = openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
        Utils.createAllTables(db);

        group_1.setOnClickListener(this::checkGroup);

    }

    public void checkGroup(View view){
        /*FirebaseUtils.getCurrentUserDataRef().child("groups").child("Ndhsjfbhdjgfhjdghfsj")
                .child("madrich").setValue(true);

        FirebaseUtils.getCurrentUserDataRef().child("groups").child("Ndhsjfbhdjgfhjdghfsj")
                .child("show").setValue(2);*/
        iv_intro.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);

        if(FirebaseUtils.isUserLoggedIn()){
            FirebaseUtils.checkCurrentGroup()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = dataSnapshot.getValue(String.class);
                                FirebaseUtils.changeGroup(Splash_Screen.this, key);
                                goToAnotherScreen(new Intent(Splash_Screen.this, MainActivity.class));
                            }
                            else {
                                goToAnotherScreen(new Intent(Splash_Screen.this, ShowGroupsScreen.class));
                            }
                        }
                    })
                    .addOnFailureListener(e -> goToAnotherScreen(new Intent(Splash_Screen.this, Log_In_Screen.class)));
        }
        else {
            goToAnotherScreen(new Intent(Splash_Screen.this, Log_In_Screen.class));
        }

    }

    public void goToAnotherScreen(Intent intent){
        new Handler().postDelayed(() -> {
            finish();
            startActivity(intent);
        }, 500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    public void checkPermissions(){
        for (String permission : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}){
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Permission " + permission + " is granted");
            } else {
                Log.d(LOG_TAG, "Permission " + permission + " is not granted");
                askPermission(permission);
            }
        }
    }

    private void askPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "Asking for the permission " + permission);
            if (shouldShowRequestPermissionRationale(permission)) {
                Log.d(LOG_TAG, "askPermission: you should show an alert dialog...");
            }

            requestPermissions(new String[]{permission}, 10000);

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Log.d(LOG_TAG, "Permission " + permissions[i] + " is yet not granted");
                    }
                }

            }
            else {
                //Permission not granted
            }
//            triggerRebirth(requireContext());
        }
    }
}

	