package com.example.projectofmurad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

public class Utils {

    public final static String LOG_TAG = "murad";

    public static final int ALARM_FOR_EVENT_NOTIFICATION_CODE = 100;
    public static final int ADD_EVENT_NOTIFICATION_CODE = 200;
    public static final int EDIT_EVENT_NOTIFICATION_CODE = 300;

    public final static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

//    public static boolean madrich = FirebaseUtils.getCurrentUserData().isMadrich();
    public static boolean madrich;

    private static Context getContext(){
        return MyApplication.getContext();
    }

    public final static String DATABASE_NAME = "db2";

    public final static String TABLE_AlARM_NAME = "tbl_alarm";

    public final static String TABLE_AlARM_COL_EVENT_PRIVATE_ID = "event_private_id";
    public final static String TABLE_AlARM_COL_EVENT_DATE = "event_date";
    public final static String TABLE_AlARM_COL_ALARM_ALREADY_SET = "alarmAlreadySet";

    public final static String TABLE_NOTIFICATION_NAME = "tbl_notification";
    public final static String TABLE_AlARM_COL_NOTIFICATION_ID = "notification_id";

    public final static String TABLE_TODAY_NAME = "tbl_today";

    public final static String TABLE_TODAY_COL_TODAY = "today";


    public static void createAllTables(@NonNull SQLiteDatabase db){
        /*db.execSQL("create table if not exists " +
                " tbl_alarm(event_private_id text, event_date text, alarmAlreadySet numeric)");*/

        db.execSQL("create table if not exists " +
                " tbl_alarm(event_private_id text, event_date text)");

        db.execSQL("create table if not exists " +
                " tbl_today(today text)");

        db.execSQL("create table if not exists " +
                " tbl_notification(notification_id integer primary key autoincrement)");

        /*if (FirebaseUtils.isUserLoggedIn()){
            FirebaseUtils.getCurrentUserDataRef().child("madrich").get().addOnCompleteListener(
                    new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task< DataSnapshot > task) {
                            if (task.isSuccessful()){
                             madrich = task.getResult().getValue(boolean.class);
                            }
                        }
                    });
        }*/


    }

    public static int getNotificationId(@NonNull SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        db.insert(TABLE_NOTIFICATION_NAME, "notification_id", contentValues);
        Cursor cursor = db.rawQuery("select * from tbl_notification",null);
        cursor.moveToLast();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public static void deleteAllTables(@NonNull SQLiteDatabase db) {
        db.execSQL("drop table if exists tbl_alarm");

        db.execSQL("drop table if exists tbl_today");
    }

    public static void addAlarm(@NonNull String event_private_id, String event_date, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

        //Todo create screen will all set alarms

        ContentValues cv = new ContentValues();
        cv.put(Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID, event_private_id);
        cv.put(Utils.TABLE_AlARM_COL_EVENT_DATE, event_date);
//        cv.put(Utils.TABLE_AlARM_COL_ALARM_ALREADY_SET, true);

//        db.insert("tbl_alarm", null, cv);

        db.replace("tbl_alarm", null, cv);


//        Toast.makeText(getContext(), "Alarm was successfully added", Toast.LENGTH_SHORT).show();

//        Toast.makeText(getContext(), "Alarm successfully set", Toast.LENGTH_SHORT).show();
//            db.execSQL("insert into "+Utils.TABLE_AlARM_NAME+" values('"+event_private_id+"', "+true+")");

//        CalendarEvent event = AlarmManagerForToday.findCalendarEventById(event_private_id);

        /*if (event_date.equals(AlarmManagerForToday.getTodayText())){
            Log.d("murad", "Alarm will work today");
            AlarmManagerForToday.addAlarm(context, event, 0);
        }*/
    }

    public static void deleteAlarm(@NonNull String event_private_id, String event_date, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

        ContentValues cv = new ContentValues();
        cv.put(Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID, event_private_id);
        cv.put(Utils.TABLE_AlARM_COL_EVENT_DATE, event_date);
//        cv.put(Utils.TABLE_AlARM_COL_ALARM_ALREADY_SET, false);

//        db.insert("tbl_student", null, cv);
//            db.execSQL("insert into "+Utils.TABLE_AlARM_NAME+" values('"+event_private_id+"', "+true+")");

        db.execSQL("delete from " + TABLE_AlARM_NAME + " where " + TABLE_AlARM_COL_EVENT_PRIVATE_ID
                + " = '" + event_private_id + "'");

//        CalendarEvent event = AlarmManagerForToday.findCalendarEventById(event_private_id);

        /*if (event_date.equals(AlarmManagerForToday.getTodayText())){
            Log.d("murad", "Alarm would have worked today");
            AlarmManagerForToday.cancelAlarm(context, event);
        }*/

//        Toast.makeText(getContext(), "Alarm successfully deleted", Toast.LENGTH_SHORT).show();
    }

    public static void getToday(@NonNull SQLiteDatabase db){

    }

    public static void changeToday(@NonNull SQLiteDatabase db){
//        db.execSQL("insert into "+Utils.TABLE_AlARM_NAME+" values('"+
//                +"', "+true+")");

    }

    public Bitmap getBitmapClippedCircle(@NonNull Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2),
                (float) (height / 2),
                (float) Math.min(width, (height / 2)),
                Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }


    public static void triggerRebirth(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    public static int generateRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    /**
     * get uri to drawable or any other resource type if u wish
     * @param context - context
     * @param drawableId - drawable res id
     * @return - uri
     */
    public static Uri getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
    }

    /**
     * get uri to any resource type Via Context Resource instance
     * @param context - context
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */
    public static Uri getUriToResource(@NonNull Context context,
                                       @AnyRes int resId)
            throws Resources.NotFoundException {
        /* Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        return getUriToResource(res, resId);
    }

    /**
     * get uri to any resource type via given Resource Instance
     * @param res - resources instance
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */
    public static Uri getUriToResource(@NonNull Resources res,
                                       @AnyRes int resId)
            throws Resources.NotFoundException {
        /*
          Creates a Uri which parses the given encoded URI string.
          @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        /* return uri */
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
    }

    @NonNull
    public static AlertDialog.Builder createSimpleAlertDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }

    @NonNull
    @Contract("_ -> param1")
    public static ProgressDialog createCustomProgressDialog(@NonNull ProgressDialog progressDialog){
        progressDialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
        return progressDialog;
    }

    @NonNull
    @Contract("_ -> param1")
    public static AlertDialog createCustomAlertDialog(@NonNull AlertDialog alertDialog){
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
        return alertDialog;
    }

    @NonNull
    @Contract("_ -> param1")
    public static androidx.appcompat.app.AlertDialog createCustomAlertDialog(@NonNull androidx.appcompat.app.AlertDialog alertDialog){
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
        return alertDialog;
    }

    @NonNull
    @Contract("_ -> param1")
    public static Dialog createCustomDialog(@NonNull Dialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
        return dialog;
    }

    public static void log(String msg){
//        Log.d("murad", "I'm in line #" + new Exception().getStackTrace()[0].getLineNumber());

        StackTraceElement l = new Exception().getStackTrace()[0];
        Log.d("murad", l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber()
                + " | " + msg);
    }

    public static void log(String tag, String msg){
//        Log.d("murad", "I'm in line #" + new Exception().getStackTrace()[0].getLineNumber());

        StackTraceElement l = new Exception().getStackTrace()[0];
        Log.d(tag, l.getClassName() + "/" + l.getMethodName() + ":" + l.getLineNumber()
                + " | " + msg);
    }

    @NonNull
    public static LinearLayoutManagerWrapper getLayoutForRecyclerView(Context context){
        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(context);
        linearLayoutManagerWrapper.setReverseLayout(true);
//        linearLayoutManagerWrapper.setStackFromEnd(true);
        return linearLayoutManagerWrapper;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @NonNull
    public static String convertSpeedToPace(double speed){

        if (speed == 0){
            return "00'00" + '"' + "/km";
        }

        speed = convertSpeedToMinPerKm(speed);

        String speedText = String.valueOf(speed);
        Log.d(Utils.LOG_TAG, "speedText " + speedText);

        String[] pace = speedText.split("\\.");

        Log.d(Utils.LOG_TAG, "pace = " + Arrays.toString(pace));

        Log.d(Utils.LOG_TAG, "pace[1] = " + pace[1]);

        double ratio = Double.parseDouble("0." + pace[1]);

        Log.d(Utils.LOG_TAG, "ratio " + ratio);
        int seconds = (int) (60 * ratio);
        Log.d(Utils.LOG_TAG, "seconds " + seconds);

        int length = String.valueOf(seconds).length();

        if (length > 2){
            length = 2;
        }
        pace[1] = String.valueOf(seconds).substring(0, length);

        return pace[0] + "'" + pace[1] +'"' + "/km";
    }

    public static double convertSpeedToMinPerKm(double speed){
        speed /= 60;

        speed = 1/speed;

        speed = Utils.round(speed, 2);

        return speed;
    }
}
