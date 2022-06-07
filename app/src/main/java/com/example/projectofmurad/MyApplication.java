package com.example.projectofmurad;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

/**
 * The type My application.
 */
public class MyApplication extends Application {

    private static Context appContext;

    /**
     * Gets context.
     *
     * @return the context
     */
    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
