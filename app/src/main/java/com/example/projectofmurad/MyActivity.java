package com.example.projectofmurad;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity {

    public static String currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
//        FirebaseUtils.checkCurrentGroup(this);
        super.onResume();
        /*FirebaseUtils.checkCurrentGroup()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String key = dataSnapshot.getValue(String.class);
                            FirebaseUtils.CURRENT_GROUP_KEY = key;
                            Toast.makeText(MyActivity.this, key, Toast.LENGTH_SHORT).show();
                            MyActivity.super.onResume();
                        }
                    }
                })
                .addOnFailureListener(e -> startActivity(new Intent(MyActivity.this, Log_In_Screen.class)));
*/
    }
}