package com.example.projectofmurad;

public class Utils {

    public final static String TAG = "murad";

    public final static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    public static boolean isMadrich = FirebaseUtils.getCurrentUserData().isMadrich();


}
