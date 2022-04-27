package com.example.projectofmurad;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class MyApplication extends Application {
    private static Context appContext;

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseUtils.getCurrentGroup().observe((LifecycleOwner) appContext, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
    }


}
