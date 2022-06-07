package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;

import com.example.projectofmurad.R;
import com.example.projectofmurad.utils.Utils;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AlarmDialog extends AppCompatDialog {

    private final Context context;

    private final CalendarEvent event;

    private final SwitchMaterial switch_alarm;

    private TimePickerDialog timePickerDialog;

    public AlarmDialog(Context context, CalendarEvent event, SwitchMaterial switch_alarm) {
        super(context);

        this.context = context;
        this.event = event;
        this.switch_alarm = switch_alarm;

        setCancelable(true);
        Utils.createCustomDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_dialog_layout);

        timePickerDialog = new TimePickerDialog(context/*, AlertDialog.THEME_HOLO_LIGHT*/,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        switch_alarm.setChecked(true);

                        Log.d("murad", "switch_alarm is " + " not " + " null");
                        Log.d("murad", "switch_alarm.isChecked() is " + switch_alarm.isChecked());

                        long before = (hourOfDay*60L + minute)*60*1000;

                        Log.d(Utils.LOG_TAG, String.valueOf(before));

                        MyAlarmManager.addAlarm(getContext(), event, before);

                        timePickerDialog.dismiss();
                    }
                }, 2, 0, true);

        Utils.createCustomDialog(timePickerDialog);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> switch_alarm.setChecked(false));

        RadioGroup rg_alarm = findViewById(R.id.rg_alarm);
        rg_alarm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch_alarm.setChecked(true);
                long before = 0;
                String toast;

                int delay = 300;

                switch (checkedId) {
                    case R.id.rb_at_time:
                        toast = "Alarm was set for time of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_5_mins_before:
                        before = 5 * 60 * 1000;
                        toast = "Alarm was set for 5 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_15_mins_before:
                        before = 15 * 60 * 1000;
                        toast = "Alarm was set for 15 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_30_mins_before:
                        before = 30 * 60 * 1000;
                        toast = "Alarm was set for 30 minutes before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_1_hour_before:
                        before = 60 * 60 * 1000;
                        toast = "Alarm was set for 1 hour before beginning of the event";
                        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rb_custom:
                        delay = 0;
                        switch_alarm.setChecked(false);
                        timePickerDialog.show();
                }

                MyAlarmManager.addAlarm(getContext(), event, before);

                new Handler().postDelayed(AlarmDialog.this::dismiss, delay);
            }
        });

        setOnCancelListener(dialog -> switch_alarm.setChecked(false));
    }
}
