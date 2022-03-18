package com.example.projectofmurad.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MyApplication;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AlarmManagerForToday {

    private static final String TAG = "AlarmManagerForToday";

    public static LocalDate getToday(){
        return LocalDate.now();
    }

    public static LocalDate tmp;

    public static String getTodayText(){
        return UtilsCalendar.DateToTextOnline(getToday());
    }

    private static Context getContext(){
        return MyApplication.getContext();
    }

    public static SharedPreferences sp;

    private final static String KEY_TODAY = "key_today";

//    private static final SQLiteDatabase db = getContext().openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);

    public static void check(Context context){
        Log.d(TAG, "-------------------------------------------------------------------------------------------");

        if (context == null){
            Log.d(TAG, "Context is null");
            Log.d(TAG, "-------------------------------------------------------------------------------------------");
            return;
        }

        Log.d(TAG, "Context is not null");

        String today = getTodayText();

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Utils.createAllTables(db);

//        DatabaseReference todayEvents = FirebaseUtils.eventsDatabase.child(today);

//        Query query_alarm_true = todayEvents.orderByChild("alarm").equalTo(true);
        /*Query query_alarm_true = todayEvents.orderByChild("alarm_UIDs/" + FirebaseUtils.getCurrentUID()).equalTo(true);
        DatabaseReference ref = query_alarm_true.getRef();
        Query query_alarmAlreadySet_false = ref.orderByChild("alarmAlreadySet").equalTo(false);

        query_alarmAlreadySet_false.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {

                    CalendarEvent event = data.getValue(CalendarEvent.class);

                    Log.d(TAG, "Is alarm already set? " + event.isAlarmAlreadySet());

                    LocalTime time = UtilsCalendar.TextToTime(data.child("start_time").getValue(String.class));
                    LocalDate date = UtilsCalendar.TextToDateForFirebase(data.child("start_date").getValue(String.class));

                    String text_date = UtilsCalendar.DateToTextOnline(date);
                    Log.d(TAG, "Date is " + text_date);

                    String text_time = UtilsCalendar.TimeToText(time);
                    Log.d(TAG, "Time is " + text_time);

//                    createAlarm(context, time, event);
                    startAlarm(context, time, event);
//                    startAlarm(context, time);

                    data.child("alarmAlreadySet").getRef().setValue(true);
                    Log.d(TAG, "-------------------------------------------------------------------------------------------");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        sp = context.getSharedPreferences("savedData", Context.MODE_PRIVATE);
        today = sp.getString(KEY_TODAY, null);

        Log.d(TAG, "today's text is " + getTodayText());
        Log.d(TAG, "sp's text is " + today);

        if (today == null){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_TODAY, getTodayText());
            editor.apply();
        }
        else if(!today.equals(getTodayText())){

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY_TODAY, getTodayText());
            editor.apply();

//            addAllAlarmsForToday(context, db);
        }


    }

    public static boolean checkIfAlarmSet(@NonNull Context context, String eventPrivateId){
        boolean alarmSet = false;

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + eventPrivateId + "'",  null);

        if(cursor.moveToNext()){
            alarmSet = true;
        }
        cursor.close();

        return alarmSet;
    }

    public static void createAlarm(@NonNull Context context, @NonNull LocalTime start_time, @NonNull CalendarEvent event){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        PendingIntent pendingIntent = null;

        long time = start_time.toSecondOfDay() * 1000L;

        int year = event.receiveStart_date().getYear();
        int month = event.receiveStart_date().getMonthValue();
        int day = event.receiveStart_date().getDayOfMonth();
        int hour = event.receiveStart_time().getHour();
        int minute = event.receiveStart_time().getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        time = calendar.getTimeInMillis() + System.currentTimeMillis();
        Log.d(TAG, "Current time in millis" + System.currentTimeMillis());
        Log.d(TAG, "Time in millis = " + time);

        Toast.makeText(context, "ALARM ON", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("notification_body", "The event " + event.getName() + " started. \n" +
                "It will finish at " + event.getEndTime());
        intent.putExtra("notification_color", event.getColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        }


/*

        if(System.currentTimeMillis() > time) {
            if (Calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
*/

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//        alarmManager.cancel(pendingIntent);

        //   alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
    }

    @SuppressLint("MissingPermission")
    public static void addAlarm(@NonNull Context context, @NonNull CalendarEvent event, long before){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        String beforeText = "";

        if (before != 0){
//            LocalTime timeBefore = CalendarEvent.getTime(before);

            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(before);
            time.roll(Calendar.HOUR_OF_DAY, -2);

            Log.d(TAG, time.toString());

            int beforeHour = time.get(Calendar.HOUR_OF_DAY);
            int beforeMinute = time.get(Calendar.MINUTE);


/*            int beforeHour = timeBefore.getHour();
            int beforeMinute = timeBefore.getMinute();*/

            if (beforeHour == 1){
                beforeText = beforeText + beforeHour + " hour and " ;
            }
            else if(beforeHour > 1){
                beforeText = beforeText + beforeHour + " hours and ";
            }

            if (beforeMinute == 1){
                beforeText = beforeText + beforeMinute + " minute";
            }
            else if(beforeMinute > 1){
                beforeText = beforeText + beforeMinute + " minutes";
            }
            else{
                beforeText = beforeText.replace(" and ", "");
            }
        }

        if (beforeText.isEmpty()){
            intent.putExtra("notification_body", "The event " + event.getName() + " started. \n" +
                    "It will finish at " + event.getEndTime());
        }
        else {
            intent.putExtra("notification_body", "The event " + event.getName() + " will start in "
                    + beforeText + ". \n" + "It will finish at " + event.getEndTime());
        }

        intent.putExtra("notification_color", event.getColor());
        intent.putExtra("event", event);

        Log.d(TAG, "===============================================================================================");
        Log.d(TAG, "Setting alarm");
        Log.d(TAG, " ");
        Log.d(TAG, "The event " + event.getName() + " started. \n" +
                "It will finish on " + event.getEnd_dateTime());
        Log.d(TAG, "" + event.getColor());

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Utils.addAlarm(event.getPrivateId(), event.getStartDate(), db);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        int year = event.receiveStartDateTime().getYear();
        int month = event.receiveStartDateTime().getMonthValue();
        int day = event.receiveStartDateTime().getDayOfMonth();

        int hour = event.receiveStartDateTime().getHour();
        int minute = event.receiveStartDateTime().getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(year, month-1, day, hour, minute);
//        calendar.roll(Calendar.MINUTE, -10);

        calendar.set(Calendar.SECOND, 0);

        long alarm = calendar.getTimeInMillis() - before;

        Date date = new Date(alarm);

        Log.d(TAG, "Current alarm in millis = " + System.currentTimeMillis());
        Log.d(TAG, "Time in millis = " + alarm);
        Log.d(TAG, "Time = " + event.getStart_dateTime());
        Log.d(TAG, "Time = " + date);

        Log.d(TAG, "===============================================================================================");


/*        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }*/

        //Todo create cancel chosen alarm method
        /*
          How? Cancel all alarms with same intent using cancel() method
          save in local database all set alarms
          recreate them
         */

        if (alarm > System.currentTimeMillis()){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
        }
//        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);


    }

    public static void cancelAlarm(@NonNull Context context, @NonNull CalendarEvent event){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

//        CalendarEvent event = findCalendarEventById(event_private_id);

        intent.putExtra("notification_body", "The event " + event.getName() + " started. \n" +
                "It will finish on " + event.getEnd_dateTime());
        intent.putExtra("notification_color", event.getColor());
        intent.putExtra("event", event);

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Utils.deleteAlarm(event.getPrivateId(), event.getStartDate(), db);

        Log.d(TAG, "===============================================================================================");
        Log.d(TAG, "Cancelling alarm");
        Log.d(TAG, " ");
        Log.d(TAG, "The event " + event.getName() + " started. \n" +
                "It will finish at " + event.getEndTime());
        Log.d(TAG, "" + event.getColor());
        Log.d(TAG, "===============================================================================================");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    @NonNull
    public static CalendarEvent findCalendarEventById(String event_private_id){
        final CalendarEvent[] event = {new CalendarEvent()};

        FirebaseUtils.eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(getToday())).child(event_private_id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            event[0] = snapshot.getValue(CalendarEvent.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return event[0];
    }

    @SuppressLint("MissingPermission")
    public static void addAllAlarmsForToday(@NonNull Context context, @NonNull SQLiteDatabase db){

        /*FirebaseUtils.eventsDatabase.child(getTodayText()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()){
                            CalendarEvent event = data.getValue(CalendarEvent.class);
                            LocalTime start_time = event.receiveStart_time();

                            addAlarm(context, start_time, event);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        Cursor cursor = db.rawQuery("select * from tbl_alarm where " + Utils.TABLE_AlARM_COL_EVENT_DATE + " = '" + getTodayText() + "'", null);
        while (cursor.moveToNext()){
            String event_private_id = cursor.getString(0);
            CalendarEvent event = findCalendarEventById(event_private_id);

            addAlarm(context, event, 0);
        }
        cursor.close();
    }


}
