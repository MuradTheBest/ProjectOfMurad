package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.ConfigurationCompat;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;


public class ChooseEventFrequencyDialogCustomWithExposedDropdown extends Dialog implements CompoundButton.OnCheckedChangeListener,
                                                                        RadioGroup.OnCheckedChangeListener {

    private RadioGroup radioGroup;

        private RadioButton rb_never;
        private RadioButton rb_day;
        private RadioButton rb_dayOfWeek;
        private RadioButton rb_month;
        private RadioButton rb_year;

    ConstraintLayout constraintLayout_frequency;
    LinearLayout choose_day_of_week_layout;

    private GetFrequencyListener listener;

    private int frequency;

    private boolean frequencyDay;
    private boolean frequencyWeekNumber;
    private boolean[] frequencyDayOfWeek;
    private boolean frequencyMonth;
    private boolean frequencyYear;


    boolean expanded = false;

    private final String[] days = Utils.getShortDayOfWeek();

    private ToggleButton tb_Sunday;
    private ToggleButton tb_Monday;
    private ToggleButton tb_Tuesday;
    private ToggleButton tb_Wednesday;
    private ToggleButton tb_Thursday;
    private ToggleButton tb_Friday;
    private ToggleButton tb_Saturday;

    private Intent intent;

    public ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context) {
        super(context);
        listener = (GetFrequencyListener) context;
    }

    public ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_event_frequency_dialogcustom);
        setCancelable(true);

        frequencyDayOfWeek = new boolean[7];

        constraintLayout_frequency = this.findViewById(R.id.constraintLayout_frequency);

        choose_day_of_week_layout = this.findViewById(R.id.choose_day_of_week_layout);

        for(String s : days) {
            Log.d("murad", s);
        }

        tb_Sunday = this.findViewById(R.id.tb_Sunday);
        addText(tb_Sunday, 6);

        tb_Monday = this.findViewById(R.id.tb_Monday);
        addText(tb_Monday, 0);

        tb_Tuesday = this.findViewById(R.id.tb_Tuesday);
        addText(tb_Tuesday, 1);

        tb_Wednesday = this.findViewById(R.id.tb_Wednesday);
        addText(tb_Wednesday, 2);

        tb_Thursday = this.findViewById(R.id.tb_Thursday);
        addText(tb_Thursday, 3);

        tb_Friday = this.findViewById(R.id.tb_Friday);
        addText(tb_Friday, 4);

        tb_Saturday = this.findViewById(R.id.tb_Saturday);
        addText(tb_Saturday, 5);

        tb_Sunday.setOnCheckedChangeListener(this);
        tb_Monday.setOnCheckedChangeListener(this);
        tb_Tuesday.setOnCheckedChangeListener(this);
        tb_Wednesday.setOnCheckedChangeListener(this);
        tb_Thursday.setOnCheckedChangeListener(this);
        tb_Friday.setOnCheckedChangeListener(this);
        tb_Saturday.setOnCheckedChangeListener(this);

        choose_day_of_week_layout.setVisibility(View.GONE);

        radioGroup = this.findViewById(R.id.radioGroup);

        Log.d("murad", "COUNTRY " + Utils.locale.getCountry());
        Log.d("murad", "LANGUAGE " + Utils.locale.getLanguage());

        ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);

        Log.d("murad", "COUNTRY " + ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).getCountry());
        Log.d("murad", "LANGUAGE " + ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).getLanguage());


        radioGroup.setOnCheckedChangeListener(this);

        rb_never = this.findViewById(R.id.rb_never);
        rb_never.setOnCheckedChangeListener(this);

        rb_day = this.findViewById(R.id.rb_day);
        rb_day.setOnCheckedChangeListener(this);

        rb_dayOfWeek = this.findViewById(R.id.rb_dayOfWeek);
        rb_dayOfWeek.setOnCheckedChangeListener(this);

        rb_month = this.findViewById(R.id.rb_month);
        rb_month.setOnCheckedChangeListener(this);

        rb_year = this.findViewById(R.id.rb_year);
        rb_year.setOnCheckedChangeListener(this);



    }

    public void addText(ToggleButton toggleButton, int i){
        toggleButton.setText(days[i]);
        toggleButton.setTextOn(days[i]);
        toggleButton.setTextOff(days[i]);
        toggleButton.setChecked(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(expanded){

            choose_day_of_week_layout.setVisibility(View.GONE);

            AutoTransition trans = new AutoTransition();
            trans.setDuration(300);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            //trans.setInterpolator(new DecelerateInterpolator());
            //trans.setInterpolator(new FastOutSlowInInterpolator());

            TransitionManager.beginDelayedTransition(constraintLayout_frequency, trans);
        }

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_dayOfWeek){
            expanded = true;
            choose_day_of_week_layout.setVisibility(View.VISIBLE);

            AutoTransition trans = new AutoTransition();
            trans.setDuration(300);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            //trans.setInterpolator(new DecelerateInterpolator());
            //trans.setInterpolator(new FastOutSlowInInterpolator());



            TransitionManager.beginDelayedTransition(constraintLayout_frequency, trans);
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(tb_Sunday.equals(compoundButton)) {
            frequencyDayOfWeek[0] = b;
        }
        else if(tb_Monday.equals(compoundButton)) {
            frequencyDayOfWeek[1] = b;
        }
        else if(tb_Tuesday.equals(compoundButton)) {
            frequencyDayOfWeek[2] = b;
        }
        else if(tb_Wednesday.equals(compoundButton)) {
            frequencyDayOfWeek[3] = b;
        }
        else if(tb_Thursday.equals(compoundButton)) {
            frequencyDayOfWeek[4] = b;
        }
        else if(tb_Friday.equals(compoundButton)) {
            frequencyDayOfWeek[5] = b;
        }
        else if(tb_Saturday.equals(compoundButton)) {
            frequencyDayOfWeek[6] = b;
        }


    }

    public interface GetFrequencyListener {
        void getFrequency(int frequency, boolean frequencyDay, boolean[] frequencyDayOfWeek,
                          boolean frequencyMonth, boolean frequencyYear);
    }

    @Override
    public void dismiss() {
        listener.getFrequency(frequency, frequencyDay, frequencyDayOfWeek,
                frequencyMonth, frequencyYear);

        super.dismiss();
    }

}
