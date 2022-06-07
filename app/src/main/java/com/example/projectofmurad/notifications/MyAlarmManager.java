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
import com.example.projectofmurad.groups.Group;
import com.example.projectofmurad.utils.Utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * The type My alarm manager.
 */
@SuppressLint("MissingPermission")
public class MyAlarmManager {

    /**
     * The constant TAG.
     */
    public static final String TAG = "AlarmManagerForToday";

    /**
     * Check if alarm set boolean.
     *
     * @param context        the context
     * @param eventPrivateId the event private id
     *
     * @return the boolean
     */
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

    /**
     * Add alarm.
     *
     * @param context the context
     * @param event   the event
     * @param before  the before
     */
    public static void addAlarm(@NonNull Context context, @NonNull CalendarEvent event, long before){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        String beforeText = "";

        if (before != 0){
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
        intent.putExtra(Group.KEY_GROUP_KEY, event.getGroupKey());

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

        if (alarm > System.currentTimeMillis()){
            alarmManager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
        }
    }

    /**
     * Cancel alarm.
     *
     * @param context the context
     * @param event   the event
     */
    public static void cancelAlarm(@NonNull Context context, @NonNull CalendarEvent event){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("notification_body", "The event " + event.getName() + " started. " +
                "It will finish on " + event.getEndDateTime());
        intent.putExtra("notification_color", event.getColor());
        intent.putExtra("event", event);

        SQLiteDatabase db = Utils.openOrCreateDatabase(context);

        int requestCode = Utils.deleteAlarm(event.getPrivateId(), db);

        if (requestCode > 0){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);
        }
    }
}
