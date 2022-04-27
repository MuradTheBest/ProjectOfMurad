package com.example.projectofmurad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity {

    public static String currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        SharedPreferences sp = getSharedPreferences("savedData", MODE_PRIVATE);
        currentGroup = sp.getString("currentGroup", "");

    }

    @Override
    protected void onResume() {
        FirebaseUtils.checkCurrentGroup()
                .addOnSuccessListener(dataSnapshot -> MyActivity.super.onResume())
                .addOnFailureListener(e -> startActivity(new Intent(MyActivity.this, Log_In_Screen.class)));
    }
}