package com.example.projectofmurad.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.helpers.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("MissingPermission")
public class AlarmManagerForToday {

    public static final String TAG = "AlarmManagerForToday";

    public static boolean checkIfAlarmSet(@NonNull Context context, String eventPrivateId){
        boolean alarmSet = false;

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);

        Cursor cursor = db.rawQuery("select * from " + Utils.TABLE_AlARM_NAME + " where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + eventPrivateId + "'",  null);

        if(cursor.moveToNext()){
            alarmSet = true;
        }
        cursor.close();

        return alarmSet;
    }

    public static void addAlarm(@NonNull Context context, @NonNull CalendarEvent event, long before){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        String beforeText = "";

        if (before != 0){
//            LocalTime timeBefore = CalendarEvent.getTime(before);

            beforeText = Utils.longToTimeText(before);
        }

        if (beforeText.isEmpty()){
            intent.putExtra("notification_body", "The event " + event.getName() + " started. \n" +
                    "It will finish at " + event.getEndTime());
        }
        else {
            intent.putExtra("notification_body", "The event " + event.getName() + " will start in "
                    + beforeText + ". \n" + "It will finish at " + event.getEndTime());
        }

        intent.putExtra(CalendarEvent.KEY_EVENT, event);

        Log.d(TAG, "===============================================================================================");
        Log.d(TAG, "Setting alarm");
        Log.d(TAG, " ");
        Log.d(TAG, "The event " + event.getName() + " started. \n" +
                "It will finish on " + event.getEndDateTime());
        Log.d(TAG, "" + event.getColor());

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);
        int requestCode = Utils.addAlarm(event.getPrivateId(), event.getStartDateTime(), db);

        intent.putExtra("requestCode", requestCode);

        Log.d(TAG, "requestCode = " + requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        int year = event.receiveStartDateTime().getYear();
        int month = event.receiveStartDateTime().getMonthValue();
        int day = event.receiveStartDateTime().getDayOfMonth();

        int hour = event.receiveStartDateTime().getHour();
        int minute = event.receiveStartDateTime().getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(year, month-1, day, hour, minute);

        calendar.set(Calendar.SECOND, 0);

        long alarm = calendar.getTimeInMillis() - before;

        Date date = new Date(alarm);

//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
        if (alarm > System.currentTimeMillis()){
        }
//        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
    }

    public static void cancelAlarm(@NonNull Context context, @NonNull CalendarEvent event){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

//        CalendarEvent event = findCalendarEventById(event_private_id);

        intent.putExtra("notification_body", "The event " + event.getName() + " started. " +
                "It will finish on " + event.getEndDateTime());
        intent.putExtra("notification_color", event.getColor());
        intent.putExtra("event", event);

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);

        int requestCode = Utils.deleteAlarm(event.getPrivateId(), db);

        if (requestCode > 0){
            Log.d(TAG, "===============================================================================================");
            Log.d(TAG, "Cancelling alarm");
            Log.d(TAG, " ");
            Log.d(TAG, "The event " + event.getName() + " started. \n" +
                    "It will finish at " + event.getEndTime());
            Log.d(TAG, "" + event.getColor());
            Log.d(TAG, "===============================================================================================");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);
        }
    }
}
