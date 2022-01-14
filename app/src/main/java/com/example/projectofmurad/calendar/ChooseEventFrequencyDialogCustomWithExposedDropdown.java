package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils_Calendar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;


public class ChooseEventFrequencyDialogCustomWithExposedDropdown extends Dialog implements CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener {

    private Context context;

    private LinearLayout ll_choose_day_of_week;

    private TextInputEditText et_frequency;
    private TextInputEditText et_times;

    private RadioGroup rg_repeat;

        private RadioButton rb_with_day_and_month;
        private RadioButton rb_with_dayOfWeek_and_month;

        private RadioButton rb_with_day_and_year;
        private RadioButton rb_with_dayOfWeek_and_year;

        private RadioButton rb_never;

    private RadioGroup rg_duration;

        private RadioButton rb_times;
        private RadioButton rb_until;


    private TextInputLayout textInputLayout_times;

    private AutoCompleteTextView autoCompleteTextView_frequencyType;

    private ArrayAdapter<String> arrayAdapter;

    private int day;
    private String dayOfWeek;
    private int dayOfWeekPosition;
    private int weekNumber;
    private String month_name;
    private int month;

    private OnDayFrequencyListener onDayFrequencyListener;
    private OnDayOfWeekFrequencyListener onDayOfWeekFrequencyListener;
    private OnDayAndMonthFrequencyListener onDayAndMonthFrequencyListener;
    private OnDayOfWeekAndMonthFrequencyListener onDayOfWeekAndMonthFrequencyListener;
    private OnDayAndYearFrequencyListener onDayAndYearFrequencyListener;
    private OnDayOfWeekAndYearFrequencyListener onDayOfWeekAndYearFrequencyListener;
    private OnNeverFrequencyListener onNeverFrequencyListener;

    private int frequency;
    private int amount;

/*    private boolean frequencyDay;
    private boolean frequencyWeekNumber;
    private boolean frequencyMonth;
    private boolean frequencyYear;*/
    private boolean[] array_frequencyDayOfWeek;

    String msg;

    private boolean expanded_choose_day_of_week_layout = false;
    private boolean expanded_rg_month = false;
    private boolean expanded_rg_year = false;

    private boolean frequency_day = false;
    private boolean frequencyDayOfWeek = false;
    private boolean frequency_day_and_month = false;
    private boolean frequency_dayOfWeek_and_month = false;
    private boolean frequency_day_and_year = false;
    private boolean frequency_dayOfWeek_and_year = false;
    private boolean frequency_never = false;

    private final String[] days = Utils_Calendar.getShortDayOfWeek();

    private ArrayList<ToggleButton> toggleButtons;

    private ToggleButton tb_Sunday;
    private ToggleButton tb_Monday;
    private ToggleButton tb_Tuesday;
    private ToggleButton tb_Wednesday;
    private ToggleButton tb_Thursday;
    private ToggleButton tb_Friday;
    private ToggleButton tb_Saturday;

    private LocalDate startDate;

    private Dialog dialog;

    public ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context, LocalDate startDate) {
        super(context);
        this.context = context;
        this.startDate = startDate;
        Toast.makeText(context, Utils_Calendar.DateToText(startDate), Toast.LENGTH_SHORT).show();

        this.onNeverFrequencyListener = (OnNeverFrequencyListener) context;
        this.onDayFrequencyListener = (OnDayFrequencyListener) context;
        this.onDayOfWeekFrequencyListener = (OnDayOfWeekFrequencyListener) context;
        this.onDayAndMonthFrequencyListener = (OnDayAndMonthFrequencyListener) context;
        this.onDayOfWeekAndMonthFrequencyListener = (OnDayOfWeekAndMonthFrequencyListener) context;
        this.onDayAndYearFrequencyListener = (OnDayAndYearFrequencyListener) context;
        this.onDayOfWeekAndYearFrequencyListener = (OnDayOfWeekAndYearFrequencyListener) context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_event_frequency_dialogcustom_with_exposed_dropdown);
        setCancelable(true);

        array_frequencyDayOfWeek = new boolean[7];

        day = startDate.getDayOfMonth();
        dayOfWeek = startDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);

        dayOfWeekPosition = startDate.getDayOfWeek().getValue()-1;

        Log.d("murad", "dayOfWeek is " + dayOfWeek + " at position " + dayOfWeekPosition);

        weekNumber = Utils_Calendar.getWeekNumber(startDate);
        month_name = startDate.getMonth().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);
        month = startDate.getMonthValue();

        ll_choose_day_of_week = this.findViewById(R.id.choose_day_of_week_layout);
        ll_choose_day_of_week.setVisibility(View.GONE);

        et_frequency = this.findViewById(R.id.et_frequency);
        et_frequency.setOnFocusChangeListener(this);

        et_times = this.findViewById(R.id.et_times);
        et_times.setOnFocusChangeListener(this);

        for(String s : days) {
            Log.d("murad", s);
        }

        /*tb_Sunday = this.findViewById(R.id.tb_Sunday);
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

        toggleButtons = new ArrayList<>();

        toggleButtons.add(tb_Monday);
        toggleButtons.add(tb_Tuesday);
        toggleButtons.add(tb_Wednesday);
        toggleButtons.add(tb_Thursday);
        toggleButtons.add(tb_Friday);
        toggleButtons.add(tb_Saturday);
        toggleButtons.add(tb_Sunday);

        for(ToggleButton tb : toggleButtons) {
            tb.setOnCheckedChangeListener(this);
        }*/

        initAllToggleButtons();

        rg_repeat = this.findViewById(R.id.rg_repeat);

        rg_repeat.setOnCheckedChangeListener(this);
        rg_repeat.setVisibility(View.GONE);

            rb_with_day_and_month = this.findViewById(R.id.rb_with_day_and_month);
            rb_with_day_and_month.setOnCheckedChangeListener(this);

            rb_with_dayOfWeek_and_month = this.findViewById(R.id.rb_with_dayOfWeek_and_month);
            rb_with_dayOfWeek_and_month.setOnCheckedChangeListener(this);

            rb_with_day_and_year = this.findViewById(R.id.rb_with_day_and_year);
            rb_with_day_and_year.setOnCheckedChangeListener(this);

            rb_with_dayOfWeek_and_year = this.findViewById(R.id.rb_with_dayOfWeek_and_year);
            rb_with_dayOfWeek_and_year.setOnCheckedChangeListener(this);

            rb_never = this.findViewById(R.id.rb_never);
            rb_never.setOnCheckedChangeListener(this);

        rg_duration = this.findViewById(R.id.rg_duration);
        rg_duration.setOnCheckedChangeListener(this);

            rb_times = this.findViewById(R.id.rb_times);
            rb_times.setOnCheckedChangeListener(this);
            rb_until = this.findViewById(R.id.rb_until);
            rb_until.setOnCheckedChangeListener(this);

/*        ArrayList<String> frequencyTypes = new ArrayList<>();
        frequencyTypes.add("day");
        frequencyTypes.add("week");
        frequencyTypes.add("month");
        frequencyTypes.add("year");*/

        String[] frequencyTypesArray = new String[4];
        frequencyTypesArray[0] = "day";
        frequencyTypesArray[1] = "week";
        frequencyTypesArray[2] = "month";
        frequencyTypesArray[3] = "year";

        arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, frequencyTypesArray);

        autoCompleteTextView_frequencyType = this.findViewById(R.id.autoCompleteTextView_frequencyType);

        autoCompleteTextView_frequencyType.setAdapter(arrayAdapter);
        autoCompleteTextView_frequencyType.setText(frequencyTypesArray[0], false);
        frequency_day = true;

        autoCompleteTextView_frequencyType.setOnItemClickListener(this);

        textInputLayout_times = this.findViewById(R.id.textInputLayout_times);
    }

    public void initAllToggleButtons(){
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

        toggleButtons = new ArrayList<>();

        toggleButtons.add(tb_Monday);
        toggleButtons.add(tb_Tuesday);
        toggleButtons.add(tb_Wednesday);
        toggleButtons.add(tb_Thursday);
        toggleButtons.add(tb_Friday);
        toggleButtons.add(tb_Saturday);
        toggleButtons.add(tb_Sunday);

        for(ToggleButton tb : toggleButtons) {
            tb.setOnCheckedChangeListener(this);
        }
    }

    public void addText(ToggleButton toggleButton, int i){
        toggleButton.setText(days[i]);
        toggleButton.setTextOn(days[i]);
        toggleButton.setTextOff(days[i]);
        toggleButton.setChecked(false);
    }

    public void animate(ViewGroup viewGroup){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        TransitionManager.beginDelayedTransition(viewGroup, trans);
    }

    // method onCheckedChanged for RadioGroup
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_times){
            Log.d("murad", "rb_times is " + rb_times.isChecked());
            Log.d("murad", "rb_until is " + rb_until.isChecked());
            textInputLayout_times.setVisibility(View.VISIBLE);
            animate(radioGroup);
        }
        else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_until){
            Log.d("murad", "rb_until is " + rb_until.isChecked());
            Log.d("murad", "rb_times is " + rb_times.isChecked());
            textInputLayout_times.setVisibility(View.GONE);
            animate(radioGroup);
        }
    }

    // method onCheckedChanged for ToggleButtons and RadioButtons
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        frequencyDayOfWeek = false;

        int pos=0;
        for(int i = 0; i < toggleButtons.size(); i++) {
            if(toggleButtons.get(i) == compoundButton){
                pos = i;
                array_frequencyDayOfWeek[i] = isChecked;
                frequencyDayOfWeek = true;
            }
        }

        Log.d("murad", toggleButtons.get(pos).getText().toString() + " is checked = " + isChecked);

        if(compoundButton == rb_with_day_and_month){
            msg = "Every month on " + day + " day";
        }
        else if(compoundButton == rb_with_dayOfWeek_and_month){
            msg = "Every month on " + weekNumber + " " + dayOfWeek;
        }
        else if(compoundButton == rb_with_day_and_year){
            msg = "Every year on " + day + " of " + month_name;
        }
        else if(compoundButton == rb_with_dayOfWeek_and_year){
            msg = "Every " + month_name + " on " + weekNumber + " " + dayOfWeek;

        }
        else if(compoundButton == rb_never){
            msg = "The event never repeats";
        }


        frequency_day_and_month = rb_with_day_and_month.isChecked();
        frequency_dayOfWeek_and_month = rb_with_dayOfWeek_and_month.isChecked();
        frequency_day_and_year = rb_with_day_and_year.isChecked();
        frequency_dayOfWeek_and_year = rb_with_dayOfWeek_and_year.isChecked();
        frequency_never = rb_never.isChecked();


        Log.d("murad", "day checked: " + frequency_day);
        Log.d("murad", "dayOfWeek is checked: " + frequencyDayOfWeek);
        Log.d("murad", "day_and_month is checked: " + rb_with_day_and_month.isChecked());
        Log.d("murad", "dayOfWeek_and_month is checked: " + rb_with_dayOfWeek_and_month.isChecked());
        Log.d("murad", "day_and_year is checked: " + rb_with_day_and_year.isChecked());
        Log.d("murad", "dayOfWeek_and_year is checked: " + rb_with_dayOfWeek_and_year.isChecked());
        Log.d("murad", "never is checked: " + rb_never.isChecked());

    }

    //ToDo what is onItemSelected???
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getSelectedItem().equals("day")){
            Toast.makeText(context, "DAY", Toast.LENGTH_SHORT).show();
        }

        Log.d("murad", "getSelectedItem = " + adapterView.getSelectedItem());

        Toast.makeText(context, adapterView.getItemAtPosition(i).getClass().getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        frequency_day = false;

        // checks expansion
        if(expanded_choose_day_of_week_layout){
            ll_choose_day_of_week.setVisibility(View.GONE);
            animate(ll_choose_day_of_week);
        }

        if(expanded_rg_month){
            rg_repeat.setVisibility(View.GONE);
            animate(rg_repeat);
        }

        if(expanded_rg_year){
            rg_repeat.setVisibility(View.GONE);
            animate(rg_repeat);
        }

        // manages content after clicking on dropdown menu item
        if(adapterView.getItemAtPosition(i).equals("day")){
            Toast.makeText(context, "DAY", Toast.LENGTH_SHORT).show();
            frequency_day = true;
        }
        else if(adapterView.getItemAtPosition(i).equals("week")){
            expanded_choose_day_of_week_layout = true;

            ll_choose_day_of_week.setVisibility(View.VISIBLE);
            animate(ll_choose_day_of_week);

            toggleButtons.get(dayOfWeekPosition).setChecked(true);

        }
        else if(adapterView.getItemAtPosition(i).equals("month")){
            expanded_rg_month = true;

            //Todo fix this
            /*if(rg_repeat.getVisibility() == View.GONE){
                rg_repeat.setVisibility(View.VISIBLE);
                animate(rg_repeat);
            }*/

            rg_repeat.setVisibility(View.VISIBLE);
            rb_with_day_and_year.setVisibility(View.GONE);
            rb_with_dayOfWeek_and_year.setVisibility(View.GONE);

            rb_with_day_and_month.setVisibility(View.VISIBLE);
            rb_with_dayOfWeek_and_month.setVisibility(View.VISIBLE);
            rb_never.setVisibility(View.VISIBLE);

            animate(rg_repeat);

            rb_with_day_and_month.setText("Every month on " + day + " day");
            rb_with_dayOfWeek_and_month.setText("Every month on " + weekNumber + " " + dayOfWeek);
        }
        else if(adapterView.getItemAtPosition(i).equals("year")){
            expanded_rg_year = true;

            //Todo fix this
            /*if(rg_repeat.getVisibility() == View.GONE){
                rg_repeat.setVisibility(View.VISIBLE);
                animate(rg_repeat);
            }*/

            rg_repeat.setVisibility(View.VISIBLE);

            rb_with_day_and_month.setVisibility(View.GONE);
            rb_with_dayOfWeek_and_month.setVisibility(View.GONE);

            rb_with_day_and_year.setVisibility(View.VISIBLE);
            rb_with_dayOfWeek_and_year.setVisibility(View.VISIBLE);
            rb_never.setVisibility(View.VISIBLE);

            animate(rg_repeat);

            rb_with_day_and_year.setText("Every year on " + day + " of " + month_name);
            rb_with_dayOfWeek_and_year.setText("Every " + month_name + " on " + weekNumber + " " + dayOfWeek);

        }

        Log.d("murad", "getItemAtPosition(i) = " + adapterView.getItemAtPosition(i));

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        String text = ((TextInputEditText) view).getText().toString();
        if(!b){
            if(text.equals("0") || text.isEmpty() || text.equals("00")){
                ((TextInputEditText) view).setText("1");
            }
            else if(text.startsWith("0")){
                text = text.substring(1);
                ((TextInputEditText) view).setText(text);
            }

            //ToDo fix problem with editText fields
            View new_focus = ((Dialog) this).getWindow().getCurrentFocus();
            /*Log.d("murad", "new_focus is " + new_focus.getClass().getName());*/

            // now assign the system service to InputMethodManager
            if(new_focus != et_times || new_focus != et_frequency){
                InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    public interface OnDayFrequencyListener {
        void setOnDayFrequency(int frequency, int amount);
    }

    public interface OnDayOfWeekFrequencyListener {
        void setOnDayOfWeekFrequency(boolean[] frequencyDayOfWeek, int frequency, int amount);
    }

    public interface OnDayAndMonthFrequencyListener {
        void setOnDayAndMonthFrequency(int day, int frequency, int amount);
    }

    public interface OnDayOfWeekAndMonthFrequencyListener {
        void setOnDayOfWeekAndMonthFrequency(int weekNumber, int dayOfWeek, int frequency, int amount);
    }

    public interface OnDayAndYearFrequencyListener {
        void setOnDayAndYearFrequency(int day, int month, int frequency, int amount);
    }

    public interface OnDayOfWeekAndYearFrequencyListener {
        void setOnDayOfWeekAndYearFrequency(int weekNumber, int dayOfWeek, int month, int frequency, int amount);
    }

    public interface OnNeverFrequencyListener {
        void setOnNeverFrequency();
    }

    @Override
    public void dismiss() {
        boolean toDismiss = true;

        frequency = Integer.parseInt(et_frequency.getText().toString());
        amount = Integer.parseInt(et_times.getText().toString());

        if(frequency_day){
            onDayFrequencyListener.setOnDayFrequency(frequency, amount);
        }
        else if(frequencyDayOfWeek){
            onDayOfWeekFrequencyListener.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, amount);
        }
        else if(frequency_day_and_month){
            onDayAndMonthFrequencyListener.setOnDayAndMonthFrequency(day, frequency, amount);
        }
        else if(frequency_dayOfWeek_and_month){
            onDayOfWeekAndMonthFrequencyListener.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, amount);
        }
        else if(frequency_day_and_year){
            onDayAndYearFrequencyListener.setOnDayAndYearFrequency(day, month, frequency, amount);
        }
        else if(frequency_dayOfWeek_and_year){
            onDayOfWeekAndYearFrequencyListener.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, amount);
        }
        else if(frequency_never){
            onNeverFrequencyListener.setOnNeverFrequency();
        }
        else {
            toDismiss = false;
            Toast.makeText(context, "Please select frequency for the event", Toast.LENGTH_SHORT).show();
        }

        if(toDismiss){
            super.dismiss();
        }

    }

}
