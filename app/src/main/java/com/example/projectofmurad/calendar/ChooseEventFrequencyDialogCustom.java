package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.projectofmurad.R;

import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class ChooseEventFrequencyDialogCustom extends Dialog {
    private MaterialDayPicker materialDayPicker;
    private RadioGroup radioGroup;

    private int frequency;

    private String frequencyDay;
    private String[] frequencyDayOfWeek;
    private String frequencyMonth;
    private String frequencyYear;

    public ChooseEventFrequencyDialogCustom(@NonNull Context context) {
        super(context);
    }

    public ChooseEventFrequencyDialogCustom(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChooseEventFrequencyDialogCustom(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_event_frequency_dialogcustom);
        setCancelable(true);

        ConstraintLayout constraintLayout_frequency = this.findViewById(R.id.constraintLayout_frequency);

        materialDayPicker = this.findViewById(R.id.materialDayPicker);
        materialDayPicker.clearSelection();
        materialDayPicker.selectDay(MaterialDayPicker.Weekday.THURSDAY);
        materialDayPicker.setFirstDayOfWeek(MaterialDayPicker.Weekday.SUNDAY);
        materialDayPicker.setSelectionMode(SingleSelectionMode.create());
        List<MaterialDayPicker.Weekday> weekdays = materialDayPicker.getSelectedDays();
        for(MaterialDayPicker.Weekday w : weekdays){
            Log.d("murad","weekday " + w.name());
        }
//        materialDayPicker.setVisibility(View.GONE);

        radioGroup = this.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            boolean expanded = false;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(expanded){

                    materialDayPicker.setVisibility(View.GONE);

                    AutoTransition trans = new AutoTransition();
                    trans.setDuration(500);
                    trans.setInterpolator(new AccelerateDecelerateInterpolator());
                    //trans.setInterpolator(new DecelerateInterpolator());
                    //trans.setInterpolator(new FastOutSlowInInterpolator());

                    TransitionManager.beginDelayedTransition(constraintLayout_frequency, trans);
                }

                if(radioGroup.getCheckedRadioButtonId() == R.id.rb_dayOfWeek){
                    expanded = true;
                    materialDayPicker.setVisibility(View.VISIBLE);

                    AutoTransition trans = new AutoTransition();
                    trans.setDuration(500);
                    trans.setInterpolator(new AccelerateDecelerateInterpolator());
                    //trans.setInterpolator(new DecelerateInterpolator());
                    //trans.setInterpolator(new FastOutSlowInInterpolator());

                    TransitionManager.beginDelayedTransition(constraintLayout_frequency, trans);

                }

            }
        });


    }


}
