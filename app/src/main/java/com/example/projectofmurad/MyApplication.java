package com.example.projectofmurad;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    private static Context appContext;

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        appContext = this;
        appContext = getApplicationContext();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
