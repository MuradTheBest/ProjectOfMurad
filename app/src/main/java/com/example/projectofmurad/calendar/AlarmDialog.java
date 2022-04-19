package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;

public class AlarmDialog extends Dialog {

    private final Context context;

    private CalendarEvent event;

    private int alarm_hour;
    private int alarm_minute;

    private final SwitchCompat switch_alarm;

    private TimePickerDialog timePickerDialog;

    private OnAutoAlarmSetListener onAutoAlarmSetListener;

    public AlarmDialog(@NonNull Context context, CalendarEvent event, SwitchCompat switch_alarm, int alarm_hour, int alarm_minute) {
        super(context);

        this.context = context;
        this.event = event;
        this.alarm_hour = alarm_hour;
        this.alarm_minute = alarm_minute;

        this.switch_alarm = switch_alarm;

        setCancelable(true);

        Utils.createCustomDialog(this);

/*        getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id

        getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);*/
    }

    public AlarmDialog(@NonNull Context context, SwitchCompat switch_alarm, OnAutoAlarmSetListener onAutoAlarmSetListener) {
        super(context);

        this.context = context;
        this.switch_alarm = switch_alarm;
        this.onAutoAlarmSetListener = onAutoAlarmSetListener;

        setCancelable(true);

        Utils.createCustomDialog(this);
/*
        getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id

        getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_dialog_layout);

        timePickerDialog = new TimePickerDialog(context/*, AlertDialog.THEME_HOLO_LIGHT*/,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        alarm_hour = hourOfDay;
                        alarm_minute = minute;

                        switch_alarm.setChecked(true);

                        Log.d("murad", "switch_alarm is " + " not " + " null");
                        Log.d("murad", "switch_alarm.isChecked() is " + switch_alarm.isChecked());

                        long before = (alarm_hour*60L + alarm_minute)*60*1000;

                        Log.d(Utils.LOG_TAG, String.valueOf(before));

                        if (onAutoAlarmSetListener == null){
                            AlarmManagerForToday.addAlarm(getContext(), event, before);
                            //new Handler().postDelayed(AlarmDialog.this::dismiss,300);
                        }
                        else {
                            onAutoAlarmSetListener.onAutoAlarmSet(before, Utils.longToTimeText(before));
                        }

                        timePickerDialog.dismiss();
                    }
                }, alarm_hour, alarm_minute, true);

        Utils.createCustomDialog(timePickerDialog);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> switch_alarm.setChecked(false));

        RadioGroup rg_alarm = findViewById(R.id.rg_alarm);
        rg_alarm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch_alarm.setChecked(true);
                long before = 0;
                String toast = "Alarm was set for timeData of beginning of the event";

                int delay = 300;

                toast = onAutoAlarmSetListener == null ? "Alarm " : "Alarms will automatically be set ";

                switch (checkedId) {
                    case R.id.rb_at_time:
                        before = 0;
                        toast += "Alarm was set for time of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_5_mins_before:
                        before = 5 * 60 * 1000;
                        toast += "Alarm was set for 5 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_15_mins_before:
                        before = 15 * 60 * 1000;
                        toast += "Alarm was set for 15 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_30_mins_before:
                        before = 30 * 60 * 1000;
                        toast += "Alarm was set for 30 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_1_hour_before:
                        before = 60 * 60 * 1000;
                        toast += "Alarm was set for 1 hour before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_custom:
                        delay = 0;
                        switch_alarm.setChecked(false);
                        timePickerDialog.show();
                }

                if (onAutoAlarmSetListener == null){
                    AlarmManagerForToday.addAlarm(AlarmDialog.this.getContext(), event, before);
                }
                else {
                    onAutoAlarmSetListener.onAutoAlarmSet(before, Utils.longToTimeText(before));
                }

                new Handler().postDelayed(AlarmDialog.this::dismiss, delay);
            }
        });

        setOnCancelListener(dialog -> switch_alarm.setChecked(false));
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        hide();
    }

    public interface OnAutoAlarmSetListener{
        void onAutoAlarmSet(long before, String prior);
    }
}
