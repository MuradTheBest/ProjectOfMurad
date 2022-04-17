package com.example.projectofmurad.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MyApplication;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("MissingPermission")
public class AlarmManagerForToday {

    public static final String TAG = "AlarmManagerForToday";

    public static LocalDate getToday(){
        return LocalDate.now();
    }

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

                    Log.d(LOG_TAG, "Is alarm already set? " + event.isAlarmAlreadySet());

                    LocalTime timeData = UtilsCalendar.TextToTime(data.child("start_time").getValue(String.class));
                    LocalDate date = UtilsCalendar.TextToDateForFirebase(data.child("start_date").getValue(String.class));

                    String text_date = UtilsCalendar.DateToTextOnline(date);
                    Log.d(LOG_TAG, "Date is " + text_date);

                    String text_time = UtilsCalendar.TimeToText(timeData);
                    Log.d(LOG_TAG, "Time is " + text_time);

//                    createAlarm(context, timeData, event);
                    startAlarm(context, timeData, event);
//                    startAlarm(context, timeData);

                    data.child("alarmAlreadySet").getRef().setValue(true);
                    Log.d(LOG_TAG, "-------------------------------------------------------------------------------------------");

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

        intent.putExtra(UtilsCalendar.KEY_EVENT, event);

        Log.d(TAG, "===============================================================================================");
        Log.d(TAG, "Setting alarm");
        Log.d(TAG, " ");
        Log.d(TAG, "The event " + event.getName() + " started. \n" +
                "It will finish on " + event.getEndDateTime());
        Log.d(TAG, "" + event.getColor());

        SQLiteDatabase db = context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
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
//        calendar.roll(Calendar.MINUTE, -10);

        calendar.set(Calendar.SECOND, 0);

        long alarm = calendar.getTimeInMillis() - before;

        Date date = new Date(alarm);

        Log.d(TAG, "Current alarm in millis = " + System.currentTimeMillis());
        Log.d(TAG, "Time in millis = " + alarm);
        Log.d(TAG, "Time = " + event.getStartDateTime());
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

        intent.putExtra("notification_body", "The event " + event.getName() + " started. \n" +
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
}
