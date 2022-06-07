package com.example.projectofmurad.utils;

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
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.MyAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

/**
 * The type Utils.
 */
public abstract class Utils {

    /**
     * The constant KEY_LINK.
     */
    public static final String KEY_LINK = "key_link";
    /**
     * The constant SHARED_PREFERENCES_FILE_NAME.
     */
    public static final String SHARED_PREFERENCES_FILE_NAME = "savedData";

    /**
     * The constant LOG_TAG.
     */
    public final static String LOG_TAG = "murad";
    /**
     * The constant EVENT_TAG.
     */
    public final static String EVENT_TAG = "event";

    /**
     * The constant ADD_EVENT_NOTIFICATION_CODE.
     */
    public static final int ADD_EVENT_NOTIFICATION_CODE = 200;
    /**
     * The constant EDIT_EVENT_NOTIFICATION_CODE.
     */
    public static final int EDIT_EVENT_NOTIFICATION_CODE = 300;
    /**
     * The constant DELETE_EVENT_NOTIFICATION_CODE.
     */
    public static final int DELETE_EVENT_NOTIFICATION_CODE = 400;

    /**
     * The constant GROUP_NOTIFICATION_CODE.
     */
    public static final int GROUP_NOTIFICATION_CODE = 2000;

    /**
     * The constant APPLICATION_ID.
     */
    public final static String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    /**
     * The constant DATABASE_NAME.
     */
    public final static String DATABASE_NAME = "db3";

    /**
     * The constant TABLE_AlARM_NAME.
     */
    public final static String TABLE_AlARM_NAME = "tbl_alarm";

    /**
     * The constant TABLE_AlARM_COL_ALARM_ID.
     */
    public final static String TABLE_AlARM_COL_ALARM_ID = "alarm_id";
    /**
     * The constant TABLE_AlARM_COL_EVENT_PRIVATE_ID.
     */
    public final static String TABLE_AlARM_COL_EVENT_PRIVATE_ID = "event_private_id";
    /**
     * The constant TABLE_AlARM_COL_EVENT_DATE_TIME.
     */
    public final static String TABLE_AlARM_COL_EVENT_DATE_TIME = "event_date_time";


    /**
     * Open or create database sq lite database.
     *
     * @param context the context
     *
     * @return the sq lite database
     */
    public static SQLiteDatabase openOrCreateDatabase(@NonNull Context context){
        return context.openOrCreateDatabase(Utils.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    /**
     * Create all tables.
     *
     * @param db the db
     */
    public static void createAllTables(@NonNull SQLiteDatabase db){
        db.execSQL("create table if not exists " +
                " tbl_alarm(alarm_id integer primary key autoincrement, event_private_id text, event_date_time text)");
    }

    /**
     * Add alarm int.
     *
     * @param event_private_id the event private id
     * @param event_dateTime   the event date time
     * @param db               the db
     *
     * @return the int
     */
    public static int addAlarm(@NonNull String event_private_id, String event_dateTime, @NonNull SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(TABLE_AlARM_COL_EVENT_PRIVATE_ID, event_private_id);
        cv.put(TABLE_AlARM_COL_EVENT_DATE_TIME, event_dateTime);

        db.insert(TABLE_AlARM_NAME, TABLE_AlARM_COL_ALARM_ID, cv);

        return alarmIdByEvent(event_private_id, db);
    }

    /**
     * Delete alarm int.
     *
     * @param event_private_id the event private id
     * @param db               the db
     *
     * @return the int
     */
    public static int deleteAlarm(@NonNull String event_private_id, @NonNull SQLiteDatabase db){
        System.out.println(event_private_id);

        int alarm_id = alarmIdByEvent(event_private_id, db);

        db.execSQL("delete from " + TABLE_AlARM_NAME + " where " + TABLE_AlARM_COL_EVENT_PRIVATE_ID
                + " = '" + event_private_id + "'");

        return alarm_id;
    }

    /**
     * Check if alarm set boolean.
     *
     * @param event_private_id the event private id
     * @param db               the db
     *
     * @return the boolean
     */
    public static boolean checkIfAlarmSet(String event_private_id, @NonNull SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        boolean alarmSet = cursor.moveToNext();
        cursor.close();

        return alarmSet;
    }

    /**
     * Alarm id by event int.
     *
     * @param event_private_id the event private id
     * @param db               the db
     *
     * @return the int
     */
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

    /**
     * Generate random color int.
     *
     * @return the int
     */
    @ColorInt
    public static int generateRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    /**
     * Create custom dialog alert dialog.
     *
     * @param dialog the dialog
     *
     * @return the alert dialog
     */
    @NonNull
    @Contract("_ -> param1")
    public static AlertDialog createCustomDialog(@NonNull AlertDialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
        return dialog;
    }

    /**
     * Create custom dialog.
     *
     * @param dialog the dialog
     */
    public static void createCustomDialog(@NonNull Dialog dialog){
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_picker_dialog_background);
    }

    /**
     * Round double.
     *
     * @param value  the value
     * @param places the places
     *
     * @return the double
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Long to time text string.
     *
     * @param before the before
     *
     * @return the string
     */
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

    /**
     * Show toast.
     *
     * @param context the context
     * @param msg     the msg
     */
    public static void showToast(Context context, String msg){
        if (context != null){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets contrast color.
     *
     * @param color the color
     *
     * @return contrast color
     */
    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color);
        double blackContrast = ColorUtils.calculateContrast(Color.BLACK, color);

        return (whiteContrast > blackContrast) ? Color.WHITE : Color.BLACK;
    }

    /**
     * Gets contrast background color.
     *
     * @param color the color
     *
     * @return contrast background color
     */
    @ColorInt
    public static int getContrastBackgroundColor(@ColorInt int color) {
        return (color != Color.WHITE) ? Color.LTGRAY : Color.DKGRAY;
    }

    /**
     * Creates {@link GradientDrawable} from received background color a
     * nd the color opposite to contrast text color for such a background.
     * It will be used as background for {@link RecyclerView} items.
     *
     * @param color Background color of {@link RecyclerView} item.
     *
     * @return customGradientDrawable gradient background
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

    /**
     * Dp to px int.
     *
     * @param dp      the dp
     * @param context the context
     *
     * @return the int
     */
    public static int dpToPx(float dp, @NonNull Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Creates custom {@link AlertDialog} based on provided parameters
     *
     * @param context           Context in which the dialog will be created
     * @param title             Title of the dialog
     * @param message           Message that will be shown in dialog box
     * @param pBText            Text for positive button
     * @param onPBClickListener {@link DialogInterface.OnClickListener} for positive button
     * @param nBText            Text for negative button
     * @param onNBClickListener {@link DialogInterface.OnClickListener} for negative button
     * @param onCancelListener  {@link DialogInterface.OnCancelListener}
     *
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

    /**
     * Create alert dialog alert dialog.
     *
     * @param context           the context
     * @param title             the title
     * @param message           the message
     * @param pBTextInt         the p b text int
     * @param onPBClickListener the on pb click listener
     * @param nBTextInt         the n b text int
     * @param onNBClickListener the on nb click listener
     * @param onCancelListener  the on cancel listener
     *
     * @return the alert dialog
     */
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

    /**
     * Add default text changed listener.
     *
     * @param textInputLayouts the text input layouts
     */
    public static void addDefaultTextChangedListener(@NonNull TextInputLayout... textInputLayouts){
        Arrays.stream(textInputLayouts).forEach(t ->
                Objects.requireNonNull(t.getEditText()).addTextChangedListener(getDefaultTextChangedListener(t)));
    }

    /**
     * Get intent clear top intent.
     *
     * @param intent the intent
     *
     * @return the intent
     */
    public static Intent getIntentClearTop(@NonNull Intent intent){
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    /**
     * Get text string.
     *
     * @param textInputLayout the text input layout
     *
     * @return the string
     */
    @NonNull
    public static String getText(@NonNull TextInputLayout textInputLayout){
        return Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
    }

    /**
     * Set text.
     *
     * @param textInputLayout the text input layout
     * @param text            the text
     */
    public static void setText(@NonNull TextInputLayout textInputLayout, String text){
        Objects.requireNonNull(textInputLayout.getEditText()).setText(text);
    }

    /**
     * Set text.
     *
     * @param textInputLayout the text input layout
     * @param resId           the res id
     */
    public static void setText(@NonNull TextInputLayout textInputLayout, @StringRes int resId){
        Objects.requireNonNull(textInputLayout.getEditText()).setText(resId);
    }
}
