package com.example.projectofmurad.helpers.utils;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public abstract class Utils {

    public static final String KEY_LINK = "key_link";
    public static final String SHARED_PREFERENCES_FILE_NAME = "savedData";

    public final static String LOG_TAG = "murad";
    public final static String EVENT_TAG = "event";

    public static final int ADD_EVENT_NOTIFICATION_CODE = 200;
    public static final int EDIT_EVENT_NOTIFICATION_CODE = 300;
    public static final int DELETE_EVENT_NOTIFICATION_CODE = 400;

    public static final int GROUP_NOTIFICATION_CODE = 2000;

    public final static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    public final static String DATABASE_NAME = "db3";

    public final static String TABLE_AlARM_NAME = "tbl_alarm";

    public final static String TABLE_AlARM_COL_ALARM_ID = "alarm_id";
    public final static String TABLE_AlARM_COL_EVENT_PRIVATE_ID = "event_private_id";
    public final static String TABLE_AlARM_COL_EVENT_DATE_TIME = "event_date_time";
    public final static String TABLE_AlARM_COL_ALARM_ALREADY_SET = "alarmAlreadySet";

    public final static String TABLE_AlARM_COL_NOTIFICATION_ID = "notification_id";


    public static SQLiteDatabase openOrCreateDatabase(@NonNull Context context){
        return context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public static void createAllTables(@NonNull SQLiteDatabase db){
        db.execSQL("create table if not exists " +
                " tbl_alarm(alarm_id integer primary key autoincrement, event_private_id text, event_date_time text)");
    }

    public static void deleteAllTables(@NonNull SQLiteDatabase db) {
        db.execSQL("drop table if exists tbl_alarm");
    }

    public static int addAlarm(@NonNull String event_private_id, String event_dateTime, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

        //Todo create screen will all set alarms

        ContentValues cv = new ContentValues();
        cv.put(TABLE_AlARM_COL_EVENT_PRIVATE_ID, event_private_id);
        cv.put(TABLE_AlARM_COL_EVENT_DATE_TIME, event_dateTime);

        db.insert(TABLE_AlARM_NAME, TABLE_AlARM_COL_ALARM_ID, cv);

        return alarmIdByEvent(event_private_id, db);
    }

    public static int deleteAlarm(@NonNull String event_private_id, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

        int alarm_id = alarmIdByEvent(event_private_id, db);

        db.execSQL("delete from " + TABLE_AlARM_NAME + " where " + TABLE_AlARM_COL_EVENT_PRIVATE_ID
                + " = '" + event_private_id + "'");

        return alarm_id;
    }

    public static boolean checkIfAlarmSet(String event_private_id, @NonNull SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        boolean alarmSet = cursor.moveToNext();
        cursor.close();

        return alarmSet;
    }

    public static int alarmIdByEvent(String event_private_id, @NonNull SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + TABLE_AlARM_NAME
                + " where " + TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'", null);

        int alarm_id = -1;

        while (cursor.moveToNext()){
            alarm_id = cursor.getInt(0);
        }
        cursor.close();

        return alarm_id;
    }

    public static void deleteAllAlarms(@NonNull SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from " + TABLE_AlARM_NAME, null);
        while (cursor.moveToNext()){
            int alarm_id = cursor.getInt(0);
        }

        cursor.close();

        db.execSQL("drop table if exists " + TABLE_AlARM_NAME);
    }

    @ColorInt
    public static int generateRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public static void createCustomDialog(@NonNull AppCompatDialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
    }

    @NonNull
    @Contract("_ -> param1")
    public static AlertDialog createCustomDialog(@NonNull AlertDialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
        return dialog;
    }

    public static void createCustomDialog(@NonNull Dialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
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

        String[] pace = speedText.split("\\.");

        double ratio = Double.parseDouble("0." + pace[1]);

        int seconds = (int) (60 * ratio);

        int length = String.valueOf(seconds).length();

        if (length > 2){
            length = 2;
        }
        pace[1] = String.valueOf(seconds).substring(0, length);

        int[] result = new int[2];
        result[0] = Integer.parseInt(pace[0]);
        result[1] = Integer.parseInt(pace[1]);

        return String.format(Locale.getDefault(), "%02d'%02d" + '"' + "/km", result[0], result[1]);
    }

    public static double convertSpeedToMinPerKm(double speed){
        if (speed == 0)
            return 0;

        speed /= 60;

        speed = 1/speed;

        speed = round(speed, 2);

        return speed;
    }

    @NonNull
    public static String longToTimeText(long before){
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(before);
        time.roll(Calendar.HOUR_OF_DAY, -2);

        before = before/60/1000;

        int beforeHour = (int) (before/60);
        int beforeMinute = (int) (before%60);

        String beforeText = "";

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

        Log.d(Utils.LOG_TAG, beforeText);

        return beforeText;
    }

    public static void showToast(Context context, String msg){
        if (context != null){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param color
     * @return
     */
    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color);
        double blackContrast = ColorUtils.calculateContrast(Color.BLACK, color);

        return (whiteContrast > blackContrast) ? Color.WHITE : Color.BLACK;
    }

    /**
     *
     * @param color
     * @return
     */
    @ColorInt
    public static int getContrastBackgroundColor(@ColorInt int color) {
        return (color != Color.WHITE) ? Color.LTGRAY : Color.DKGRAY;
    }

    /**
     * Creates {@link GradientDrawable} from received background color a
     * nd the color opposite to contrast text color for such a background.
     * It will be used as background for {@link RecyclerView} items.
     * @param color Background color of {@link RecyclerView} item.
     * @return
     */
    @NonNull
    public static GradientDrawable getGradientBackground(@ColorInt int color) {
        int textColor = getContrastColor(color);

        int gradientColor = getContrastBackgroundColor(textColor);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {color, color, gradientColor});

        gd.setShape(GradientDrawable.RECTANGLE);

        return gd;
    }

    @NonNull
    public static LatLngBounds getLatLngBounds(@NonNull List<LatLng> latLngs){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs){
            builder.include(latLng);
        }

        return builder.build();
    }

    public static int dpToPx(float dp, @NonNull Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Creates custom {@link AlertDialog} based on provided parameters
     * @param context Context in which the dialog will be created
     * @param title Title of the dialog
     * @param message Message that will be shown in dialog box
     * @param pBText Text for positive button
     * @param onPBClickListener {@link DialogInterface.OnClickListener} for positive button
     * @param nBText Text for negative button
     * @param onNBClickListener {@link DialogInterface.OnClickListener} for negative button
     * @param onCancelListener {@link DialogInterface.OnCancelListener}
     * @return Dialog created from supplied data that will be shown
     */
    @NonNull
    public static AlertDialog createAlertDialog(Context context, String title, String message,
                                                String pBText, DialogInterface.OnClickListener onPBClickListener,
                                                String nBText, DialogInterface.OnClickListener onNBClickListener,
                                                DialogInterface.OnCancelListener onCancelListener) {

        MyAlertDialogBuilder builder = new MyAlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(pBText, onPBClickListener);
        builder.setNegativeButton(nBText, onNBClickListener);
        builder.setOnCancelListener(onCancelListener);

        return builder.create();
    }

    @NonNull
    public static AlertDialog createAlertDialog(Context context, String title, String message,
                                                @StringRes int pBTextInt, DialogInterface.OnClickListener onPBClickListener,
                                                @StringRes int nBTextInt, DialogInterface.OnClickListener onNBClickListener,
                                                DialogInterface.OnCancelListener onCancelListener){

        return createAlertDialog(context, title, message,
                context.getString(pBTextInt), onPBClickListener,
                context.getString(nBTextInt), onNBClickListener,
                onCancelListener);
    }

    @NonNull
    @Contract("_ -> new")
    private static TextWatcher getDefaultTextChangedListener(TextInputLayout textInputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayout.setError(null);
            }
        };
    }

    public static void addDefaultTextChangedListener(@NonNull TextInputLayout... textInputLayouts){
        Arrays.stream(textInputLayouts).forEach(t ->
                Objects.requireNonNull(t.getEditText()).addTextChangedListener(getDefaultTextChangedListener(t)));
    }

    public static Intent getIntentClearTop(@NonNull Intent intent){
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @NonNull
    public static String getText(@NonNull TextInputLayout textInputLayout){
        return Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
    }

    public static void setText(@NonNull TextInputLayout textInputLayout, String text){
        Objects.requireNonNull(textInputLayout.getEditText()).setText(text);
    }

    public static void setText(@NonNull TextInputLayout textInputLayout, @StringRes int resId){
        Objects.requireNonNull(textInputLayout.getEditText()).setText(resId);
    }

    @NonNull
    public static String getFormalGroupKey(@NonNull String key){
        return key.replace("Group-", "");
    }

    @NonNull
    public static String getInFormalGroupKey(@NonNull String key){
        return "Group-" + key;
    }
}
