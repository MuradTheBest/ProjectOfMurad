package com.example.projectofmurad.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectofmurad.R;
import com.example.projectofmurad.notifications.AlarmManagerForToday;

public class AlarmDialog extends Dialog {

    private boolean gotChecked;

//    private SetOnCancelAlarmListener setOnCancelAlarmListener;

    private CalendarEvent event;

    private int position;

    private int alarm_hour;
    private int alarm_minute;

    TimePickerDialog timePickerDialog;

    public boolean isGotChecked(){
        return gotChecked;
    }

    public void setGotChecked(boolean gotChecked){
        this.gotChecked = gotChecked;
    }

    public AlarmDialog(@NonNull Context context, CalendarEvent event, int position, int alarm_hour, int alarm_minute) {
        super(context);
//        this.setOnCancelAlarmListener = (SetOnCancelAlarmListener) context;
        this.gotChecked = false;
        this.event = event;

        this.alarm_hour = alarm_hour;
        this.alarm_minute = alarm_minute;

        this.setCancelable(true);

        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id

        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
    }

    public AlarmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.alarm_dialog_layout);

        gotChecked = false;

        timePickerDialog = new TimePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        gotChecked = true;

                        alarm_hour = hourOfDay;
                        alarm_minute = minute;

                        long time = (hourOfDay*60L + minute)*60*1000;

                        AlarmManagerForToday.addAlarm(getContext(), event, time);

                        new Handler().postDelayed(AlarmDialog.this::dismiss,300);
                    }
                }, alarm_hour, alarm_minute, true);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotChecked = true;
            }
        });

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotChecked = false;
            }
        });

        /*timePickerDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                *//*if (gotChecked){
                    AlarmDialog.this.cancel();
                }
                else {
                    AlarmDialog newDialog = new AlarmDialog(getContext(), event, 0, alarm_hour, alarm_minute);
                    newDialog.show();
                }*//*

                AlarmDialog.this.cancel();
            }
        });

        timePickerDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (gotChecked){
                    AlarmDialog.this.cancel();
                }
                else {
                    AlarmDialog newDialog = new AlarmDialog(getContext(), event, 0, alarm_hour, alarm_minute);
                    newDialog.show();
                }
            }
        });*/

        RadioGroup rg_alarm = this.findViewById(R.id.rg_alarm);
        rg_alarm.setOnCheckedChangeListener((group, checkedId) -> {
            gotChecked = true;
            long before = 0;
            String toast = "Alarm was set for time of beginning of the event";

            switch (checkedId){
                case R.id.rb_at_time:
                    before = 0;
                    toast = "Alarm was set for time of beginning of the event";
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
                    gotChecked = false;
                    timePickerDialog.show();
            }
            AlarmManagerForToday.addAlarm(getContext(), event, before);

            new Handler().postDelayed(AlarmDialog.this::dismiss,300);

        });

/*        RadioGroup rg_alarm = this.findViewById(R.id.rg_alarm);
        rg_alarm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                gotChecked = true;
                long before = 0;
                String toast = "Alarm was set for time of beginning of the event";

                switch (checkedId) {
                    case R.id.rb_5_mins_before:
                        before = 5 * 60 * 1000;
                        toast = "Alarm was set for 5 minutes before beginning of the event";
                        break;
                    case R.id.rb_15_mins_before:
                        before = 15 * 60 * 1000;
                        toast = "Alarm was set for 15 minutes before beginning of the event";
                        break;
                    case R.id.rb_30_mins_before:
                        before = 30 * 60 * 1000;
                        toast = "Alarm was set for 30 minutes before beginning of the event";
                        break;
                    case R.id.rb_1_hour_before:
                        before = 60 * 60 * 1000;
                        toast = "Alarm was set for 1 hour before beginning of the event";
                        break;
                }
                AlarmManagerForToday.addAlarm(getContext(), event, before);
                Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(AlarmDialog.this::dismiss, 300);


            }
        });*/

    }

    @Override
    public void dismiss() {
//        super.dismiss();

        hide();
    }

    /*@Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);

        if (!gotChecked){
            setOnCancelAlarmListener.onCancelAlarm(position);
        }
        this.dismiss();
    }

    public interface SetOnCancelAlarmListener {
        void onCancelAlarm(int position);
    }*/
}
