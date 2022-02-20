package com.example.projectofmurad;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

import androidx.annotation.NonNull;

public class Utils {

    public static String TAG = "murad";

    public final static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    public static boolean isMadrich = FirebaseUtils.getCurrentUserData().isMadrich();

    private static Context getContext(){
        return MyApplication.getContext();
    }

    public final static String DATABASE_NAME = "db2";

    public final static String TABLE_AlARM_NAME = "tbl_alarm";

    public final static String TABLE_AlARM_COL_EVENT_PRIVATE_ID = "event_private_id";
    public final static String TABLE_AlARM_COL_EVENT_DATE = "event_date";
    public final static String TABLE_AlARM_COL_ALARM_ALREADY_SET = "alarmAlreadySet";

    public final static String TABLE_TODAY_NAME = "tbl_today";

    public final static String TABLE_TODAY_COL_TODAY = "today";


    public static void createAllTables(@NonNull SQLiteDatabase db){
        /*db.execSQL("create table if not exists " +
                " tbl_alarm(event_private_id text, event_date text, alarmAlreadySet numeric)");*/

        db.execSQL("create table if not exists " +
                " tbl_alarm(event_private_id text, event_date text)");

        db.execSQL("create table if not exists " +
                " tbl_today(today text)");
    }

    public static void deleteAllTables(@NonNull SQLiteDatabase db) {
        db.execSQL("drop table if exists tbl_alarm");

        db.execSQL("drop table if exists tbl_today");
    }

    public static void addAlarm(@NonNull String event_private_id, String event_date, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

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
}
