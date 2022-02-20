package com.example.projectofmurad.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.example.projectofmurad.R;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChooseEventFrequencyDialogCustomWithExposedDropdown extends Dialog implements
                                                                CompoundButton.OnCheckedChangeListener,
                                                                AdapterView.OnItemSelectedListener,
                                                                RadioGroup.OnCheckedChangeListener,
                                                                ChipGroup.OnCheckedChangeListener,
                                                                AdapterView.OnItemClickListener,
                                                                View.OnFocusChangeListener,
                                                                View.OnClickListener {

    public Context context;

    public LinearLayout ll_choose_day_of_week;
    public ScrollView scrollview_frequency;

    public TextInputEditText et_frequency;
    public TextInputEditText et_times;

    public TextView tv_event_frequency_info;

    public RadioGroup rg_repeat_for_month;

        public RadioButton rb_with_day_and_month;
        public RadioButton rb_with_last_day_and_month;
        public RadioButton rb_with_dayOfWeek_and_month;
        public RadioButton rb_with_last_dayOfWeek_and_month;

    public RadioGroup rg_repeat_for_year;

        public RadioButton rb_with_day_and_year;
        public RadioButton rb_with_last_day_and_year;
        public RadioButton rb_with_dayOfWeek_and_year;
        public RadioButton rb_with_last_dayOfWeek_and_year;

        /*public RadioButton rb_never;*/

    public RadioGroup rg_duration;

        public RadioButton rb_never;
        public RadioButton rb_times;
        public RadioButton rb_until;


    public TextInputLayout textInputLayout_times;

    public AutoCompleteTextView autoCompleteTextView_frequencyType;

    public ArrayAdapter<String> arrayAdapter;

    public int frequency;

    public int amount;
    public LocalDate frequency_end_date;

    public int day;
    public String dayOfWeek;
    public int dayOfWeekPosition;
    public List<Boolean> array_frequencyDayOfWeek;
    public int weekNumber;
    public String month_name;
    public int month;

    public OnSwitchDialog onSwitchDialog;
    public OnSendMessageListener onSendMessageListener;

    public OnDayFrequencyListener onDayFrequencyListener;
    public OnDayOfWeekFrequencyListener onDayOfWeekFrequencyListener;
    public OnDayAndMonthFrequencyListener onDayAndMonthFrequencyListener;
    public OnDayOfWeekAndMonthFrequencyListener onDayOfWeekAndMonthFrequencyListener;
    public OnDayAndYearFrequencyListener onDayAndYearFrequencyListener;
    public OnDayOfWeekAndYearFrequencyListener onDayOfWeekAndYearFrequencyListener;
    public OnNeverFrequencyListener onNeverFrequencyListener;

    String msg;

    public boolean expanded_choose_day_of_week_layout = false;
    public boolean expanded_rg_month = false;
    public boolean expanded_rg_year = false;

    public boolean isFrequency_by_amount = true;

    public boolean frequency_never = false;

    public boolean frequency_day = false;
    public boolean frequency_dayOfWeek = false;

    public boolean frequency_day_and_month = false;
    public boolean frequency_last_day_and_month = false;

    public boolean frequency_dayOfWeek_and_month = false;
    public boolean frequency_last_dayOfWeek_and_month = false;

    public boolean frequency_day_and_year = false;
    public boolean frequency_last_day_and_year = false;

    public boolean frequency_dayOfWeek_and_year = false;
    public boolean frequency_last_dayOfWeek_and_year = false;

    public final String[] days = Utils_Calendar.getNarrowDaysOfWeek();

    public ArrayList<ToggleButton> toggleButtons;

    public ToggleButton tb_Sunday;
    public ToggleButton tb_Monday;
    public ToggleButton tb_Tuesday;
    public ToggleButton tb_Wednesday;
    public ToggleButton tb_Thursday;
    public ToggleButton tb_Friday;
    public ToggleButton tb_Saturday;

    public LocalDate startDate;
    public int end_year;
    public int end_month;
    public int end_day;

    public Dialog dialog;

    public DatePicker date_picker_end;

    public Button btn_ok;
    public Button btn_cancel;

    ChooseEventFrequencyDialogCustomWithExposedDropdown copy;

    Resources res;

    public ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context, LocalDate startDate) {
        super(context);
        this.context = context;
        this.startDate = startDate;

        Toast.makeText(context, Utils_Calendar.DateToTextLocal(startDate), Toast.LENGTH_SHORT).show();

        this.onSwitchDialog = (OnSwitchDialog) context;
        this.onSendMessageListener = (OnSendMessageListener) context;

        this.onNeverFrequencyListener = (OnNeverFrequencyListener) context;
        this.onDayFrequencyListener = (OnDayFrequencyListener) context;
        this.onDayOfWeekFrequencyListener = (OnDayOfWeekFrequencyListener) context;
        this.onDayAndMonthFrequencyListener = (OnDayAndMonthFrequencyListener) context;
        this.onDayOfWeekAndMonthFrequencyListener = (OnDayOfWeekAndMonthFrequencyListener) context;
        this.onDayAndYearFrequencyListener = (OnDayAndYearFrequencyListener) context;
        this.onDayOfWeekAndYearFrequencyListener = (OnDayOfWeekAndYearFrequencyListener) context;

    }

    public ChooseEventFrequencyDialogCustomWithExposedDropdown(@NonNull Context context) {

//        super(context, android.R.style.ThemeOverlay_Material_ActionBar);
        super(context, R.style.FullScreenDialogTheme);
        this.context = context;


        this.onSwitchDialog = (OnSwitchDialog) context;
        this.onSendMessageListener = (OnSendMessageListener) context;

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
/*        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.choose_event_frequency_dialogcustom_with_exposed_dropdown_scroll_view);
        setCancelable(true);

        res = getContext().getResources();

        array_frequencyDayOfWeek = new ArrayList<>();
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);

        day = startDate.getDayOfMonth();
        dayOfWeek = startDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);

        dayOfWeekPosition = startDate.getDayOfWeek().getValue()-1;

        Log.d("murad", "dayOfWeek is " + dayOfWeek + " at position " + dayOfWeekPosition);

        weekNumber = Utils_Calendar.getWeekNumber(startDate);
        month_name = startDate.getMonth().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);
        month = startDate.getMonthValue();

        ll_choose_day_of_week = this.findViewById(R.id.choose_day_of_week_layout);
        ll_choose_day_of_week.setVisibility(View.GONE);

        scrollview_frequency = this.findViewById(R.id.scrollview_frequency);

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

        tv_event_frequency_info = this.findViewById(R.id.tv_event_frequency_info);

        rg_repeat_for_month = this.findViewById(R.id.rg_repeat_for_month);
        rg_repeat_for_month.setVisibility(View.GONE);
        rg_repeat_for_month.setOnCheckedChangeListener(this);

            rb_with_day_and_month = this.findViewById(R.id.rb_with_day_and_month);
            rb_with_day_and_month.setText(res.getString(R.string.on_d_day, day));
            rb_with_day_and_month.setOnCheckedChangeListener(this);

            rb_with_last_day_and_month = this.findViewById(R.id.rb_with_last_day_and_month);
            rb_with_last_day_and_month.setText(R.string.on_the_last_day);
            rb_with_last_day_and_month.setOnCheckedChangeListener(this);

            rb_with_dayOfWeek_and_month = this.findViewById(R.id.rb_with_dayOfWeek_and_month);
            rb_with_dayOfWeek_and_month.setText(res.getString(R.string.on_s_d, dayOfWeek, weekNumber));
            rb_with_dayOfWeek_and_month.setOnCheckedChangeListener(this);

            rb_with_last_dayOfWeek_and_month = this.findViewById(R.id.rb_with_last_dayOfWeek_and_month);
            rb_with_last_dayOfWeek_and_month.setText(res.getString(R.string.on_the_last_s, dayOfWeek));
            rb_with_last_dayOfWeek_and_month.setOnCheckedChangeListener(this);


        rg_repeat_for_year = this.findViewById(R.id.rg_repeat_for_year);
        rg_repeat_for_year.setVisibility(View.GONE);
        rg_repeat_for_year.setOnCheckedChangeListener(this);

            rb_with_day_and_year = this.findViewById(R.id.rb_with_day_and_year);
            rb_with_day_and_year.setText(res.getString(R.string.on_d_of_s, day, month_name));
//            rb_with_day_and_year.setText("On " + day + " of " + month_name);
            rb_with_day_and_year.setOnCheckedChangeListener(this);

            rb_with_last_day_and_year = this.findViewById(R.id.rb_with_last_day_and_year);
            rb_with_last_day_and_year.setText(res.getString(R.string.on_the_last_day_of_s, month_name));
//            rb_with_last_day_and_year.setText("On the last day of " + month_name);
            rb_with_last_day_and_year.setOnCheckedChangeListener(this);

            rb_with_dayOfWeek_and_year = this.findViewById(R.id.rb_with_dayOfWeek_and_year);
            rb_with_dayOfWeek_and_year.setText(res.getString(R.string.on_s_d_of_s, dayOfWeek, weekNumber, month_name));
//            rb_with_dayOfWeek_and_year.setText("On " + weekNumber + " " + dayOfWeek + " of " + month_name);
            rb_with_dayOfWeek_and_year.setOnCheckedChangeListener(this);

            rb_with_last_dayOfWeek_and_year = this.findViewById(R.id.rb_with_last_dayOfWeek_and_year);
            rb_with_last_dayOfWeek_and_year.setText(res.getString(R.string.on_the_last_s_of_s, dayOfWeek, month_name));
//            rb_with_last_dayOfWeek_and_year.setText("On the last " + dayOfWeek + " of " + month_name);
            rb_with_last_dayOfWeek_and_year.setOnCheckedChangeListener(this);

        rg_duration = this.findViewById(R.id.rg_duration);
        rg_duration.setOnCheckedChangeListener(this);

            rb_never = this.findViewById(R.id.rb_never);
            //rb_never.setOnCheckedChangeListener(this);

            rb_times = this.findViewById(R.id.rb_times);
            //rb_times.setOnCheckedChangeListener(this);

            rb_until = this.findViewById(R.id.rb_until);
            //rb_until.setOnCheckedChangeListener(this);

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

/*        String[] frequencyTypesArrayPlural = new String[4];
        frequencyTypesArrayPlural[0] = "days";
        frequencyTypesArrayPlural[1] = "weeks";
        frequencyTypesArrayPlural[2] = "months";
        frequencyTypesArrayPlural[3] = "years";*/

        String[] frequencyTypesArrayPlural = res.getStringArray(R.array.frequencyTypes);

        arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, frequencyTypesArrayPlural);

        autoCompleteTextView_frequencyType = this.findViewById(R.id.autoCompleteTextView_frequencyType);
/*
        autoCompleteTextView_frequencyType.addTextChangedListener(new TextWatcher() {
            String text = autoCompleteTextView_frequencyType.getText().toString();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int anchor = autoCompleteTextView_frequencyType.getDropDownAnchor();
                if(s.equals("1")){
                    arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, frequencyTypesArray);
                }
                else {
                    arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, frequencyTypesArrayPlural);
                }
//                new ArrayAdapter<>(context, R.layout.dropdown_item, (text.equals("1")) ? frequencyTypesArray : frequencyTypesArrayPlural);
                autoCompleteTextView_frequencyType.setAdapter(arrayAdapter);
                autoCompleteTextView_frequencyType.setDropDownAnchor(anchor);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/

        autoCompleteTextView_frequencyType.setAdapter(arrayAdapter);
        autoCompleteTextView_frequencyType.setSelected(true);
        autoCompleteTextView_frequencyType.setText(frequencyTypesArrayPlural[0], false);

        frequency_day = true;

        autoCompleteTextView_frequencyType.setOnItemClickListener(this);
//        autoCompleteTextView_frequencyType.setListSelection(2);
//        autoCompleteTextView_frequencyType.setSelection(2);
//        autoCompleteTextView_frequencyType.extendSelection(2);

        textInputLayout_times = this.findViewById(R.id.textInputLayout_times);

        date_picker_end = this.findViewById(R.id.date_picker_end);
        date_picker_end.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                frequency_end_date = LocalDate.of(year, month, day);
                String date_text = Utils_Calendar.DateToTextLocal(frequency_end_date);

                end_year = year;
                end_month = month;
                end_day = day;

                rb_until.setText(res.getString(R.string.until___, date_text));
            }
        });
        date_picker_end.setMinDate(Calendar.getInstance().getTimeInMillis());
        date_picker_end.setFirstDayOfWeek(Calendar.SUNDAY);
        date_picker_end.setVisibility(View.GONE);

        rb_until.setText(R.string.until);

        btn_ok = this.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        btn_cancel = this.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        printAllIds();
    }

    private void initAllToggleButtons(){
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

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void printAllIds(){
        Log.d("murad", "rb_with_day_and_month is " + rb_with_day_and_month.getId());
        Log.d("murad", "rb_with_dayOfWeek_and_month is " + rb_with_dayOfWeek_and_month.getId());
        Log.d("murad", "rb_with_day_and_year is " + rb_with_day_and_year.getId());
        Log.d("murad", "rb_with_dayOfWeek_and_year is " + rb_with_dayOfWeek_and_year.getId());
        Log.d("murad", "rb_never is " + rb_never.getId());
        Log.d("murad", "rb_times is " + rb_times.getId());
        Log.d("murad", "rb_until is " + rb_until.getId());
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

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

//        TransitionManager.beginDelayedTransition(viewGroup, trans);
        TransitionManager.beginDelayedTransition(viewGroup, changeBounds);


    }

    // method onCheckedChanged for RadioGroup

    private boolean never_is_checked = true;

    public String frequency_text = "";
    public String repeat_text = "";

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        frequency_text = "";
        repeat_text = "";
        String event_frequency_info;

        String text = ((RadioButton) getWindow().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

        Log.d("murad", "*****************************************************************************************************");
        Log.d("murad", "rb_never is set to to " + rb_never.isChecked() + " |  frequency_never = " + frequency_never);
        Log.d("murad", "rb_times is set to to " + rb_times.isChecked() + " |  isFrequency_by_amount = " + isFrequency_by_amount);
        Log.d("murad", "rb_until is set to to " + rb_until.isChecked() + " |  frequency_by_end = " + !isFrequency_by_amount);
        Log.d("murad", "*****************************************************************************************************");

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_times /*|| frequency_never*/){

            Log.d("murad", "rb_times is " + rb_times.isChecked());
            Log.d("murad", "rb_until is " + rb_until.isChecked());

            textInputLayout_times.setVisibility(View.VISIBLE);
            date_picker_end.setVisibility(View.GONE);
//            rb_until.setText("Until");

            animate(radioGroup);
            animate(date_picker_end);

            isFrequency_by_amount = true;
            frequency_never = false;
        }
        else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_until){

            rb_times.setChecked(false);
            Log.d("murad", "rb_until is checked = " + rb_until.isChecked());
            Log.d("murad", "rb_times is checked = " + rb_times.isChecked());

            textInputLayout_times.setVisibility(View.GONE);
            date_picker_end.setVisibility(View.VISIBLE);
            date_picker_end.updateDate(end_year, end_month-1, end_day);

            animate(radioGroup);
            animate(date_picker_end);

            isFrequency_by_amount = false;
            frequency_never = false;

            scrollview_frequency.post(new Runnable() {
                @Override
                public void run() {
                    scrollview_frequency.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        if (radioGroup.getId() == rg_repeat_for_month.getId() || (radioGroup.getId() == rg_repeat_for_year.getId())){
            if(rb_never.isChecked()){
                rb_never.setChecked(false);
                frequency_never = false;
                Toast.makeText(getContext(), "rb_never is set to false", Toast.LENGTH_SHORT).show();
            }
            frequency_text = ((RadioButton) getWindow().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
            frequency_text = frequency_text.substring(0, 1).toLowerCase() + frequency_text.substring(1);
        }

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_never){

            never_is_checked = true;

            textInputLayout_times.setVisibility(View.GONE);
            date_picker_end.setVisibility(View.GONE);
            animate(date_picker_end);

            animate(radioGroup);

            isFrequency_by_amount = false;

            frequency_never = true;

            frequency_day = false;
            frequency_dayOfWeek = false;
            frequency_day_and_month = false;
            frequency_dayOfWeek_and_month = false;
            frequency_day_and_year = false;
            frequency_dayOfWeek_and_year = false;

            /*rb_with_day_and_month.setChecked(false);
            rb_with_dayOfWeek_and_month.setChecked(false);
            rb_with_day_and_year.setChecked(false);
            rb_with_dayOfWeek_and_year.setChecked(false);*/

            ll_choose_day_of_week.setVisibility(View.GONE);
            animate(ll_choose_day_of_week);

            rg_repeat_for_month.setVisibility(View.GONE);
            animate(rg_repeat_for_month);
        }

    }

    // method onCheckedChanged for ToggleButtons and RadioButtons
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        frequency_dayOfWeek = false;

/*        if (compoundButton != rb_never && compoundButton != rb_until && compoundButton != rb_times){
            if(rb_never.isChecked()){
                rb_never.setChecked(false);
                frequency_never = false;
                Toast.makeText(getContext(), "rb_never is set to false", Toast.LENGTH_SHORT).show();
            }
        }*/
/*
        if (compoundButton == rb_never && !isChecked){
            rb_times.setChecked(true);
        }*/

        int pos=0;

        for(int i = 0; i < toggleButtons.size(); i++) {
            if(toggleButtons.get(i) == compoundButton){
                pos = i;
                array_frequencyDayOfWeek.set(i, isChecked);
                frequency_dayOfWeek = true;
            }
        }
        if (frequency_dayOfWeek){
            StringBuilder days_of_week = new StringBuilder();
            for(int i = 0; i < array_frequencyDayOfWeek.size(); i++) {
                if(array_frequencyDayOfWeek.get(i)){
                    days_of_week.append(DayOfWeek.of(i + 1).getDisplayName(TextStyle.FULL,
                            Utils_Calendar.locale)).append(", ");
                }
            }
            days_of_week.deleteCharAt(days_of_week.length()-1);
            frequency_text = res.getString(R.string.on) + days_of_week;
        }

        Log.d("murad", toggleButtons.get(pos).getText().toString() + " is checked = " + isChecked);
        /*else if(compoundButton == rb_never){
            msg = "The event never repeats";
        }*/

        frequency_never = rb_never.isChecked();

        isFrequency_by_amount = rb_times.isChecked();
        isFrequency_by_amount = !rb_until.isChecked();


        frequency_day_and_month = rb_with_day_and_month.isChecked();
        frequency_last_day_and_month = rb_with_last_day_and_month.isChecked();

        frequency_dayOfWeek_and_month = rb_with_dayOfWeek_and_month.isChecked();
        frequency_last_dayOfWeek_and_month = rb_with_last_dayOfWeek_and_month.isChecked();

        frequency_day_and_year = rb_with_day_and_year.isChecked();
        frequency_last_day_and_year = rb_with_last_day_and_year.isChecked();

        frequency_dayOfWeek_and_year = rb_with_dayOfWeek_and_year.isChecked();
        frequency_last_dayOfWeek_and_year = rb_with_last_dayOfWeek_and_year.isChecked();

        if(frequency_last_day_and_month){
            frequency_day_and_month = true;
        }
        else if(frequency_last_dayOfWeek_and_month){
            frequency_dayOfWeek_and_month = true;
        }
        else if(frequency_last_day_and_year){
            frequency_day_and_year = true;
        }
        else if(frequency_last_dayOfWeek_and_year){
            frequency_dayOfWeek_and_year = true;
        }

//        frequency_never = rb_never.isChecked();

        Log.d("murad", "-------------------------------------------------------------------");

/*        Log.d("murad", "day checked: " + frequency_day);
        Log.d("murad", "dayOfWeek is checked: " + frequency_dayOfWeek);
        Log.d("murad", "day_and_month is checked: " + rb_with_day_and_month.isChecked());
        Log.d("murad", "dayOfWeek_and_month is checked: " + rb_with_dayOfWeek_and_month.isChecked());
        Log.d("murad", "day_and_year is checked: " + rb_with_day_and_year.isChecked());
        Log.d("murad", "dayOfWeek_and_year is checked: " + rb_with_dayOfWeek_and_year.isChecked());*/

        Log.d("murad", "isFrequency_by_amount is set to " + isFrequency_by_amount);

        Log.d("murad", "day is set to " + frequency_day);
        Log.d("murad", "dayOfWeek is set to " + frequency_dayOfWeek);

        Log.d("murad", "day_and_month is set to " + frequency_day_and_month);
        Log.d("murad", "last_day_and_month is set to " + frequency_last_day_and_month);

        Log.d("murad", "dayOfWeek_and_month is set to " + frequency_dayOfWeek_and_month);
        Log.d("murad", "last_dayOfWeek_and_month is set to " + frequency_last_dayOfWeek_and_month);

        Log.d("murad", "day_and_year is set to " + frequency_day_and_year);
        Log.d("murad", "last_day_and_year is set to " + frequency_last_day_and_year);

        Log.d("murad", "dayOfWeek_and_year is set to " + frequency_dayOfWeek_and_year);
        Log.d("murad", "last_dayOfWeek_and_year is set to " + frequency_last_dayOfWeek_and_year);

        Log.d("murad", "                                           ");
        Log.d("murad", "rb_never is set to " + rb_never.isChecked());
        Log.d("murad", "rb_times is set to " + rb_times.isChecked());
        Log.d("murad", "rb_until is set to " + rb_until.isChecked());


        Log.d("murad", "-------------------------------------------------------------------");
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
        frequency_text = "";
        repeat_text = "";

        frequency_day = false;

        frequency_day_and_month = false;
        frequency_last_day_and_month = false;

        frequency_dayOfWeek_and_month = false;
        frequency_last_dayOfWeek_and_month = false;

        frequency_day_and_year = false;
        frequency_last_day_and_year = false;

        frequency_dayOfWeek_and_year = false;
        frequency_last_dayOfWeek_and_year = false;

        frequency_never = false;
        rb_never.setChecked(false);
//        frequency_never = false;

        // checks expansion
        if(expanded_choose_day_of_week_layout){
            ll_choose_day_of_week.setVisibility(View.GONE);
            animate(ll_choose_day_of_week);
            expanded_choose_day_of_week_layout = false;
        }

        if(expanded_rg_month){
            rg_repeat_for_month.setVisibility(View.GONE);
            animate(rg_repeat_for_month);
            expanded_rg_month = false;
        }

        if(expanded_rg_year){
            rg_repeat_for_year.setVisibility(View.GONE);
            animate(rg_repeat_for_year);
            expanded_rg_year = false;
        }

        String item = adapterView.getItemAtPosition(i).toString();

        // manages content after clicking on dropdown menu item
        if(item.equals("day") || item.equals(res.getString(R.string.days))){
            Toast.makeText(context, "DAY", Toast.LENGTH_SHORT).show();
            frequency_day = true;
        }
        else if(item.equals("week") || item.equals(res.getString(R.string.weeks))){
            expanded_choose_day_of_week_layout = true;
            frequency_dayOfWeek = true;

            ll_choose_day_of_week.setVisibility(View.VISIBLE);
            animate(ll_choose_day_of_week);

            toggleButtons.get(dayOfWeekPosition).setChecked(true);

        }
        else if(item.equals("month") || item.equals(res.getString(R.string.months))){
            expanded_rg_month = true;

            rg_repeat_for_month.setVisibility(View.VISIBLE);

            if (day != startDate.lengthOfMonth()) {
                rb_with_last_day_and_month.setVisibility(View.GONE);
            }
            else {
/*                if(day == 30 || day == 31){
                }
                else{
                    rb_with_day_and_month.setVisibility(View.GONE);
                }*/
            }

            if(startDate.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(dayOfWeekPosition+1))).equals(startDate)){

                if (weekNumber != 4) {
                    rb_with_dayOfWeek_and_month.setVisibility(View.GONE);
                }
            }
            else{
                rb_with_last_dayOfWeek_and_month.setVisibility(View.GONE);
            }

            animate(rg_repeat_for_month);

        }
        else if(item.equals("year") || item.equals(res.getString(R.string.years))){
            expanded_rg_year = true;

            rg_repeat_for_year.setVisibility(View.VISIBLE);

            if (day != startDate.lengthOfMonth()) {
                rb_with_last_day_and_year.setVisibility(View.GONE);
            }
            else {

/*                if(day == 30){

                }
                else{
                    rb_with_day_and_year.setVisibility(View.GONE);
                }*/
            }

            if(startDate.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(dayOfWeekPosition+1))).equals(startDate)){

                if (weekNumber != 4) {
                    rb_with_dayOfWeek_and_year.setVisibility(View.GONE);
                }
            }
            else{
                rb_with_last_dayOfWeek_and_year.setVisibility(View.GONE);
            }

            animate(rg_repeat_for_year);

        }

        animate(scrollview_frequency);

        Log.d("murad", "getItemAtPosition(i) = " + item);

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

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {

        if(group.getCheckedChipId() == R.id.rb_never){
            never_is_checked = true;

            textInputLayout_times.setVisibility(View.GONE);
            date_picker_end.setVisibility(View.GONE);
            animate(date_picker_end);

            animate(group);

            isFrequency_by_amount = false;

            frequency_never = true;

            frequency_day = false;
            frequency_dayOfWeek = false;
            frequency_day_and_month = false;
            frequency_dayOfWeek_and_month = false;
            frequency_day_and_year = false;
            frequency_dayOfWeek_and_year = false;

            /*rb_with_day_and_month.setChecked(false);
            rb_with_dayOfWeek_and_month.setChecked(false);
            rb_with_day_and_year.setChecked(false);
            rb_with_dayOfWeek_and_year.setChecked(false);*/

            ll_choose_day_of_week.setVisibility(View.GONE);
            animate(ll_choose_day_of_week);

            rg_repeat_for_month.setVisibility(View.GONE);
            animate(rg_repeat_for_month);
        }
        else if(group.getCheckedChipId() == R.id.rb_times /*|| frequency_never*/){
            Log.d("murad", "rb_times is " + rb_times.isChecked());
            Log.d("murad", "rb_until is " + rb_until.isChecked());

            textInputLayout_times.setVisibility(View.VISIBLE);
            date_picker_end.setVisibility(View.GONE);
            rb_until.setText("Until");

            animate(group);
            animate(date_picker_end);

            isFrequency_by_amount = true;
            frequency_never = false;
        }
        else if(group.getCheckedChipId() == R.id.rb_until){
            rb_times.setChecked(false);
            Log.d("murad", "rb_until is checked = " + rb_until.isChecked());
            Log.d("murad", "rb_times is checked = " + rb_times.isChecked());

            textInputLayout_times.setVisibility(View.GONE);
            date_picker_end.setVisibility(View.VISIBLE);
            date_picker_end.updateDate(end_year, end_month-1, end_day);

            animate(group);
            animate(date_picker_end);

            isFrequency_by_amount = false;
            frequency_never = false;

            scrollview_frequency.post(new Runnable() {
                @Override
                public void run() {
                    scrollview_frequency.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        Log.d("murad", "*****************************************************************************************************");
        Log.d("murad", "rb_never is set to to " + rb_never.isChecked() + " |  frequency_never = " + frequency_never);
        Log.d("murad", "rb_times is set to to " + rb_times.isChecked() + " |  isFrequency_by_amount = " + isFrequency_by_amount);
        Log.d("murad", "rb_until is set to to " + rb_until.isChecked() + " |  frequency_by_end = " + !isFrequency_by_amount);
        Log.d("murad", "*****************************************************************************************************");


        if (group.getId() == rg_repeat_for_month.getId() || (group.getId() == rg_repeat_for_year.getId())){
            if(rb_never.isChecked()){
                rb_never.setChecked(false);
                frequency_never = false;
                Toast.makeText(getContext(), "rb_never is set to false", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnSwitchDialog {
        void switchDialog(ChooseEventFrequencyDialogCustomWithExposedDropdown copy);
    }

    public interface OnSendMessageListener {
        void setOnMessage(String msg);
    }

    public interface OnDayFrequencyListener {
        void setOnDayFrequency(int selected_frequency, int selected_amount);
        void setOnDayFrequency(int selected_frequency, LocalDate selected_end);
    }

    public interface OnDayOfWeekFrequencyListener {
        void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency,
                                     int selected_amount);
        void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency,
                                     LocalDate selected_end);
    }

    public interface OnDayAndMonthFrequencyListener {
        void setOnDayAndMonthFrequency(int selected_day, int selected_frequency, int selected_amount, boolean isLast);
        void setOnDayAndMonthFrequency(int selected_day, int selected_frequency, LocalDate selected_end, boolean isLast);
    }

    public interface OnDayOfWeekAndMonthFrequencyListener {
        void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                             int selected_frequency, int selected_amount, boolean isLast);
        void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                             int selected_frequency, LocalDate selected_end, boolean isLast);
    }

    public interface OnDayAndYearFrequencyListener {
        void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                      int selected_frequency, int selected_amount, boolean isLast);
        void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                      int selected_frequency, LocalDate selected_end, boolean isLast);
    }

    public interface OnDayOfWeekAndYearFrequencyListener {
        void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                            int selected_frequency, int selected_amount, boolean isLast);
        void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                            int selected_frequency, LocalDate selected_end, boolean isLast);
    }

    public interface OnNeverFrequencyListener {
        void setOnNeverFrequency();
    }

    public void setStartDateForRepeatInitial(@NonNull LocalDate startDate){
        this.startDate = startDate;

        this.end_year = startDate.getYear();
        this.end_month = startDate.getMonthValue();
        this.end_day = startDate.getDayOfMonth();

    }

    public void setStartDateForRepeat(LocalDate startDate,
                                      int selected_frequency,
                                      int selected_amount,
                                      LocalDate selected_end,
                                      List<Boolean> selected_array_frequencyDayOfWeek,
                                      boolean selected_frequency_day,
                                      boolean selected_frequency_dayOfWeek,
                                      boolean selected_frequency_day_and_month,
                                      boolean selected_frequency_dayOfWeek_and_month,
                                      boolean selected_frequency_day_and_year,
                                      boolean selected_frequency_dayOfWeek_and_year){

        startDate = startDate;

        end_year = startDate.getYear();
        end_month = startDate.getMonthValue();
        end_day = startDate.getDayOfMonth();

        day = startDate.getDayOfMonth();
        dayOfWeek = startDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);

        dayOfWeekPosition = startDate.getDayOfWeek().getValue()-1;

        Log.d("murad", "dayOfWeek is " + dayOfWeek + " at position " + dayOfWeekPosition);

        weekNumber = Utils_Calendar.getWeekNumber(startDate);
        month_name = startDate.getMonth().getDisplayName(TextStyle.FULL, Utils_Calendar.locale);
        month = startDate.getMonthValue();

        frequency = selected_frequency;
        amount = selected_amount;

        if(startDate.isAfter(selected_end)){
            selected_end = startDate;
        }
        else{
            frequency_end_date = selected_end;
        }

        array_frequencyDayOfWeek = selected_array_frequencyDayOfWeek;

        frequency_day = selected_frequency_day;
        frequency_dayOfWeek = selected_frequency_dayOfWeek;
        frequency_day_and_month = selected_frequency_day_and_month;
        frequency_dayOfWeek_and_month = selected_frequency_dayOfWeek_and_month;
        frequency_day_and_year = selected_frequency_day_and_year;
        frequency_dayOfWeek_and_year = selected_frequency_dayOfWeek_and_year;

    }

    @Override
    public void onClick(View view) {
        boolean toDismiss = true;

        if(view == btn_ok){

            frequency = Integer.parseInt(et_frequency.getText().toString());
            amount = Integer.parseInt(et_times.getText().toString());

            if(isFrequency_by_amount){
                if(amount == 1){
                    repeat_text = res.getString(R.string.one_time);
                }
                else{
                    repeat_text = res.getString(R.string._d_times, amount);
                }
            }
            else {
                repeat_text = rb_until.getText().toString();
                repeat_text = repeat_text.substring(0, 1).toLowerCase() + repeat_text.substring(1);
            }

            String event_frequency_info = res.getString(R.string.this_event_will_repeat_every_d_s_s_s,
                    frequency,
                    autoCompleteTextView_frequencyType.getText().toString(),
                    frequency_text,
                    repeat_text);

            msg = event_frequency_info;

            if (msg.contains("  ")){
                msg = msg.replace("  ", " ");
            }

            if (frequency_never){
                msg = res.getString(R.string.does_not_repeat);
            }

            onSendMessageListener.setOnMessage(msg);

            if(expanded_rg_month){

                if(frequency_day_and_month && isFrequency_by_amount){
                    Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + true);
                    onDayAndMonthFrequencyListener.setOnDayAndMonthFrequency(day, frequency, amount, frequency_last_day_and_month);
                }
                else if(frequency_day_and_month){
                    Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + false);
                    onDayAndMonthFrequencyListener.setOnDayAndMonthFrequency(day, frequency, frequency_end_date, frequency_last_day_and_month);
                }
                else if(frequency_dayOfWeek_and_month && isFrequency_by_amount){
                    Log.d("murad", "frequency_dayOfWeek_and_month is " + true + "\n isFrequency_by_amount is " + true);
                    onDayOfWeekAndMonthFrequencyListener.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, amount, frequency_last_dayOfWeek_and_month);
                }
                else if(frequency_dayOfWeek_and_month){
                    Log.d("murad", "frequency_dayOfWeek_and_month is  " + true + "\n isFrequency_by_amount is " + false);
                    onDayOfWeekAndMonthFrequencyListener.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, frequency_end_date, frequency_last_dayOfWeek_and_month);
                }

            }
            else if (expanded_rg_year){

                if(frequency_day_and_year && isFrequency_by_amount){
                    Log.d("murad", "frequency_day_and_year is " + true + "\n isFrequency_by_amount is " + true);
                    onDayAndYearFrequencyListener.setOnDayAndYearFrequency(day, month, frequency, amount, frequency_last_day_and_year);
                }
                else if(frequency_day_and_year){
                    Log.d("murad", "frequency_day_and_year is  " + true + "\n isFrequency_by_amount is " + false);
                    onDayAndYearFrequencyListener.setOnDayAndYearFrequency(day, month, frequency, frequency_end_date, frequency_last_day_and_year);
                }
                else if(frequency_dayOfWeek_and_year && isFrequency_by_amount){
                    Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + true);
                    onDayOfWeekAndYearFrequencyListener.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, amount, frequency_last_dayOfWeek_and_year);
                }
                else if(frequency_dayOfWeek_and_year){
                    Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + false);
                    onDayOfWeekAndYearFrequencyListener.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, frequency_end_date, frequency_last_dayOfWeek_and_year);
                }

            }
            else if(expanded_choose_day_of_week_layout){

                if(frequency_dayOfWeek && isFrequency_by_amount){
                    Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + true);
                    onDayOfWeekFrequencyListener.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, amount);
                }
                else if(frequency_dayOfWeek){
                    Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + false);
                    onDayOfWeekFrequencyListener.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, frequency_end_date);
                }

            }
            else {

                if(frequency_day && isFrequency_by_amount){
                    Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + true);
                    onDayFrequencyListener.setOnDayFrequency(frequency, amount);
                }
                else if(frequency_day){
                    Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + false);
                    onDayFrequencyListener.setOnDayFrequency(frequency, frequency_end_date);
                }
                else if (frequency_never){
                    onNeverFrequencyListener.setOnNeverFrequency();
                }

            }


            /*if(frequency_day && isFrequency_by_amount){
                Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + true);
                onDayFrequencyListener.setOnDayFrequency(frequency, amount);
            }
            else if(frequency_day){
                Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + false);
                onDayFrequencyListener.setOnDayFrequency(frequency, frequency_end_date);
            }
            else if(frequency_dayOfWeek && isFrequency_by_amount){
                Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + true);
                onDayOfWeekFrequencyListener.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, amount);
            }
            else if(frequency_dayOfWeek){
                Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + false);
                onDayOfWeekFrequencyListener.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, frequency_end_date);
            }
            else if(frequency_day_and_month && isFrequency_by_amount){
                Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + true);
                onDayAndMonthFrequencyListener.setOnDayAndMonthFrequency(day, frequency, amount);
            }
            else if(frequency_day_and_month){
                Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + false);
                onDayAndMonthFrequencyListener.setOnDayAndMonthFrequency(day, frequency, frequency_end_date);
            }
            else if(frequency_dayOfWeek_and_month && isFrequency_by_amount){
                Log.d("murad", "frequency_dayOfWeek_and_month is " + true + "\n isFrequency_by_amount is " + true);
                onDayOfWeekAndMonthFrequencyListener.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, amount);
            }
            else if(frequency_dayOfWeek_and_month){
                Log.d("murad", "frequency_dayOfWeek_and_month is  " + true + "\n isFrequency_by_amount is " + false);
                onDayOfWeekAndMonthFrequencyListener.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, frequency_end_date);
            }
            else if(frequency_day_and_year && isFrequency_by_amount){
                Log.d("murad", "frequency_day_and_year is " + true + "\n isFrequency_by_amount is " + true);
                onDayAndYearFrequencyListener.setOnDayAndYearFrequency(day, month, frequency, amount);
            }
            else if(frequency_day_and_year){
                Log.d("murad", "frequency_day_and_year is  " + true + "\n isFrequency_by_amount is " + false);
                onDayAndYearFrequencyListener.setOnDayAndYearFrequency(day, month, frequency, frequency_end_date);
            }
            else if(frequency_dayOfWeek_and_year && isFrequency_by_amount){
                Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + true);
                onDayOfWeekAndYearFrequencyListener.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, amount);
            }
            else if(frequency_dayOfWeek_and_year){
                Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + false);
                onDayOfWeekAndYearFrequencyListener.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, frequency_end_date);
            }
            else if(frequency_never){
                Log.d("murad", "frequency_never is " + true);
                onNeverFrequencyListener.setOnNeverFrequency();
            }
            else {
                toDismiss = false;
                Toast.makeText(context, "Please select frequency type for the event", Toast.LENGTH_SHORT).show();
            }*/

            if(toDismiss){
//                copy = this;
                Toast.makeText(context, "Copy saved", Toast.LENGTH_SHORT).show();
                hide();

//            cancel();
            }

        }
        else if(view == btn_cancel){
            cancel();
        }


    }

    @Override
    public void dismiss() {
        super.dismiss();


        Log.d("murad", "isFrequency_by_amount is set to " + isFrequency_by_amount);
        Log.d("murad", "frequency_never is set to " + frequency_never);
        Log.d("murad", "frequency_day is set to " + frequency_day);
        Log.d("murad", "frequency_dayOfWeek is set to " + frequency_dayOfWeek);
        Log.d("murad", "frequency_day_and_month is set to " + frequency_day_and_month);
        Log.d("murad", "frequency_dayOfWeek_and_month is set to " + frequency_dayOfWeek_and_month);
        Log.d("murad", "frequency_day_and_year is set to " + frequency_day_and_year);
        Log.d("murad", "frequency_dayOfWeek_and_year is set to " + frequency_dayOfWeek_and_year);
    }

/*
    @Override
    public void hide() {
        super.hide();
        copy = this;

         Log.d("murad", "isFrequency_by_amount is set to " + isFrequency_by_amount);
         Log.d("murad", "frequency_never is set to " + frequency_never);
         Log.d("murad", "frequency_day is set to " + frequency_day);
         Log.d("murad", "frequency_dayOfWeek is set to " + frequency_dayOfWeek);
         Log.d("murad", "frequency_day_and_month is set to " + frequency_day_and_month);
         Log.d("murad", "frequency_dayOfWeek_and_month is set to " + frequency_dayOfWeek_and_month);
         Log.d("murad", "frequency_day_and_year is set to " + frequency_day_and_year);
         Log.d("murad", "frequency_dayOfWeek_and_year is set to " + frequency_dayOfWeek_and_year);

    }
*/

    @Override
    public void cancel() {
//        super.cancel();
        /*            frequency_never = true;
            onNeverFrequencyListener.setOnNeverFrequency();*/
        /*cancel();*/

        /*if (copy == null){
            onSwitchDialog.switchDialog(copy);
            Toast.makeText(getContext(), "Copy Dialog is null", Toast.LENGTH_SHORT).show();
        }
        else{
            onSwitchDialog.switchDialog(copy);
            Toast.makeText(getContext(), "Copy Dialog is not null", Toast.LENGTH_SHORT).show();
        }

        if(this.equals(copy)){
            Toast.makeText(getContext(), "Dialogs are equal!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Dialogs are not equal!", Toast.LENGTH_SHORT).show();
        }
*/
        dismiss();
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //getting Root View that gets focus
        View rootView =((ViewGroup)getWindow().findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideKeyboard(ChooseEventFrequencyDialogCustomWithExposedDropdown.this);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();


        //getting Root View that gets focus
        View rootView = ((ViewGroup)ChooseEventFrequencyDialogCustomWithExposedDropdown.this.findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(ChooseEventFrequencyDialogCustomWithExposedDropdown.this);
                }
            }
        });

    }

    public static void hideKeyboard(Dialog d) {
        InputMethodManager inputMethodManager = (InputMethodManager) d.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow( d.getCurrentFocus().getWindowToken(), 0);
    }
}
