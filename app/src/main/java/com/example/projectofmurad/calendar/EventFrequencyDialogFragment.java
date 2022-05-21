package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.Utils;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventFrequencyDialogFragment extends DialogFragment implements
                                                            CompoundButton.OnCheckedChangeListener,
                                                            RadioGroup.OnCheckedChangeListener,
                                                            AdapterView.OnItemClickListener,
                                                            View.OnFocusChangeListener,
                                                            View.OnClickListener {

    public static final String TAG = "ChooseEventFrequencyDialog";

    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";

    public LinearLayout ll_choose_day_of_week;
    public NestedScrollView scrollview_frequency;

    public TextInputEditText et_frequency;
    public TextInputEditText et_times;

    public MaterialTextView tv_event_frequency_info;

    public RadioGroup rg_repeat_for_month;

    public MaterialRadioButton rb_with_day_and_month;
    public MaterialRadioButton rb_with_last_day_and_month;
    public MaterialRadioButton rb_with_dayOfWeek_and_month;
    public MaterialRadioButton rb_with_last_dayOfWeek_and_month;

    public RadioGroup rg_repeat_for_year;

    public MaterialRadioButton rb_with_day_and_year;
    public MaterialRadioButton rb_with_last_day_and_year;
    public MaterialRadioButton rb_with_dayOfWeek_and_year;
    public MaterialRadioButton rb_with_last_dayOfWeek_and_year;

    public RadioGroup rg_duration;

    public MaterialRadioButton rb_never;
    public MaterialRadioButton rb_times;
    public MaterialRadioButton rb_until;

    public TextInputLayout textInputLayout_times;

    public MaterialAutoCompleteTextView autoCompleteTextView_frequencyType;

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

    public boolean last = false;

    public final String[] days = CalendarUtils.getNarrowDaysOfWeek();

    public ArrayList<ToggleButton> toggleButtons;

    public ToggleButton tb_Sunday;
    public ToggleButton tb_Monday;
    public ToggleButton tb_Tuesday;
    public ToggleButton tb_Wednesday;
    public ToggleButton tb_Thursday;
    public ToggleButton tb_Friday;
    public ToggleButton tb_Saturday;

    public LocalDate startDate;

    public Dialog dialog;

    public DatePicker date_picker_end;

    public Button btn_ok;
    public Button btn_cancel;

    EventFrequencyViewModel eventFrequencyViewModel;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    @NonNull
    static EventFrequencyDialogFragment newInstance(@NonNull LocalDate startDate) {
        EventFrequencyDialogFragment fragment = new EventFrequencyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_YEAR, startDate.getYear());
        args.putInt(KEY_MONTH, startDate.getMonthValue());
        args.putInt(KEY_DAY, startDate.getDayOfMonth());
        fragment.setArguments(args);
        return fragment;
    }

    public EventFrequencyDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme);

        if(getArguments() != null) {
            int year = getArguments().getInt(KEY_YEAR);
            int month = getArguments().getInt(KEY_MONTH);
            int day = getArguments().getInt(KEY_DAY);

            this.startDate = LocalDate.of(year, month, day);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_frequency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow;

        eventFrequencyViewModel = new ViewModelProvider(requireActivity()).get(EventFrequencyViewModel.class);

        array_frequencyDayOfWeek = new ArrayList<>();
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);
        array_frequencyDayOfWeek.add(false);

        day = startDate.getDayOfMonth();
        dayOfWeek = startDate.getDayOfWeek().getDisplayName(TextStyle.FULL, CalendarUtils.getLocale());

        dayOfWeekPosition = startDate.getDayOfWeek().getValue()-1;

        weekNumber = CalendarUtils.getWeekNumber(startDate);
        month_name = startDate.getMonth().getDisplayName(TextStyle.FULL, CalendarUtils.getLocale());
        month = startDate.getMonthValue();

        ll_choose_day_of_week = view.findViewById(R.id.ll_choose_day_of_week);
        ll_choose_day_of_week.setVisibility(View.GONE);

        scrollview_frequency = view.findViewById(R.id.scrollview_frequency);

        et_frequency = view.findViewById(R.id.et_frequency);
        et_frequency.setOnFocusChangeListener(this);

        et_times = view.findViewById(R.id.et_times);
        et_times.setOnFocusChangeListener(this);

        initAllToggleButtons();

        tv_event_frequency_info = view.findViewById(R.id.tv_event_frequency_info);

        rg_repeat_for_month = view.findViewById(R.id.rg_repeat_for_month);
        rg_repeat_for_month.setVisibility(View.GONE);
//        rg_repeat_for_month.setOnCheckedChangeListener(this);

        rb_with_day_and_month = view.findViewById(R.id.rb_with_day_and_month);
        rb_with_day_and_month.setText(getString(R.string.on_d_day, day));
        rb_with_day_and_month.setOnCheckedChangeListener(this);

        rb_with_last_day_and_month = view.findViewById(R.id.rb_with_last_day_and_month);
        rb_with_last_day_and_month.setText(R.string.on_the_last_day);
        rb_with_last_day_and_month.setOnCheckedChangeListener(this);

        rb_with_dayOfWeek_and_month = view.findViewById(R.id.rb_with_dayOfWeek_and_month);
        rb_with_dayOfWeek_and_month.setText(getString(R.string.on_s_d, dayOfWeek, weekNumber));
        rb_with_dayOfWeek_and_month.setOnCheckedChangeListener(this);

        rb_with_last_dayOfWeek_and_month = view.findViewById(R.id.rb_with_last_dayOfWeek_and_month);
        rb_with_last_dayOfWeek_and_month.setText(getString(R.string.on_the_last_s, dayOfWeek));
        rb_with_last_dayOfWeek_and_month.setOnCheckedChangeListener(this);


        rg_repeat_for_year = view.findViewById(R.id.rg_repeat_for_year);
        rg_repeat_for_year.setVisibility(View.GONE);
//        rg_repeat_for_year.setOnCheckedChangeListener(this);

        rb_with_day_and_year = view.findViewById(R.id.rb_with_day_and_year);
        rb_with_day_and_year.setText(getString(R.string.on_d_of_s, day, month_name));
        rb_with_day_and_year.setOnCheckedChangeListener(this);

        rb_with_last_day_and_year = view.findViewById(R.id.rb_with_last_day_and_year);
        rb_with_last_day_and_year.setText(getString(R.string.on_the_last_day_of_s, month_name));
        rb_with_last_day_and_year.setOnCheckedChangeListener(this);

        rb_with_dayOfWeek_and_year = view.findViewById(R.id.rb_with_dayOfWeek_and_year);
        rb_with_dayOfWeek_and_year.setText(getString(R.string.on_s_d_of_s, dayOfWeek, weekNumber, month_name));
        rb_with_dayOfWeek_and_year.setOnCheckedChangeListener(this);

        rb_with_last_dayOfWeek_and_year = view.findViewById(R.id.rb_with_last_dayOfWeek_and_year);
        rb_with_last_dayOfWeek_and_year.setText(getString(R.string.on_the_last_s_of_s, dayOfWeek, month_name));
        rb_with_last_dayOfWeek_and_year.setOnCheckedChangeListener(this);

        rg_duration = view.findViewById(R.id.rg_duration);
        rg_duration.setOnCheckedChangeListener(this);

        rb_never = view.findViewById(R.id.rb_never);
        //rb_never.setOnCheckedChangeListener(this);

        rb_times = view.findViewById(R.id.rb_times);
        //rb_times.setOnCheckedChangeListener(this);

        rb_until = view.findViewById(R.id.rb_until);
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

        String[] frequencyTypesArrayPlural = getResources().getStringArray(R.array.frequencyTypes);

        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, frequencyTypesArrayPlural);

        autoCompleteTextView_frequencyType = view.findViewById(R.id.autoCompleteTextView_frequencyType);

        autoCompleteTextView_frequencyType.setAdapter(arrayAdapter);
        autoCompleteTextView_frequencyType.setSelected(true);
        autoCompleteTextView_frequencyType.setText(frequencyTypesArrayPlural[0], false);

        autoCompleteTextView_frequencyType.setOnItemClickListener(this);

        textInputLayout_times = view.findViewById(R.id.textInputLayout_times);

        date_picker_end = view.findViewById(R.id.date_picker_end);
        date_picker_end.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                frequency_end_date = LocalDate.of(year, month, day);
                String date_text = CalendarUtils.DateToTextLocal(frequency_end_date);

                frequency_end_date = LocalDate.of(year, month, day);

                rb_until.setText(getString(R.string.until___, date_text));
            }
        });

        date_picker_end.setMinDate(Calendar.getInstance().getTimeInMillis());
        date_picker_end.setFirstDayOfWeek(Calendar.SUNDAY);
        date_picker_end.setVisibility(View.GONE);

        rb_until.setText(R.string.until);

        btn_ok = view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    private void initAllToggleButtons(){
        tb_Sunday = getView().findViewById(R.id.tb_Sunday);
        addText(tb_Sunday, 6);

        tb_Monday = getView().findViewById(R.id.tb_Monday);
        addText(tb_Monday, 0);

        tb_Tuesday = getView().findViewById(R.id.tb_Tuesday);
        addText(tb_Tuesday, 1);

        tb_Wednesday = getView().findViewById(R.id.tb_Wednesday);
        addText(tb_Wednesday, 2);

        tb_Thursday = getView().findViewById(R.id.tb_Thursday);
        addText(tb_Thursday, 3);

        tb_Friday = getView().findViewById(R.id.tb_Friday);
        addText(tb_Friday, 4);

        tb_Saturday = getView().findViewById(R.id.tb_Saturday);
        addText(tb_Saturday, 5);

        toggleButtons = new ArrayList<>();

        toggleButtons.add(tb_Monday);
        toggleButtons.add(tb_Tuesday);
        toggleButtons.add(tb_Wednesday);
        toggleButtons.add(tb_Thursday);
        toggleButtons.add(tb_Friday);
        toggleButtons.add(tb_Saturday);
        toggleButtons.add(tb_Sunday);

        toggleButtons.forEach(tb -> tb.setOnCheckedChangeListener(this));
    }

    public void addText(@NonNull ToggleButton toggleButton, int i){
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
    @Override
    public void onCheckedChanged(@NonNull RadioGroup radioGroup, int i) {
        last = false;

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_times){
            textInputLayout_times.setVisibility(View.VISIBLE);
            date_picker_end.setVisibility(View.GONE);

            animate(radioGroup);
            animate(date_picker_end);
        }
        else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_until){

            textInputLayout_times.setVisibility(View.GONE);
            date_picker_end.setVisibility(View.VISIBLE);
            date_picker_end.updateDate(date_picker_end.getYear(),
                    frequency_end_date.getMonthValue()-1, frequency_end_date.getDayOfMonth());

            animate(radioGroup);
            animate(date_picker_end);

            scrollview_frequency.post(() -> scrollview_frequency.fullScroll(ScrollView.FOCUS_DOWN));
        }

        int checkedId = rg_repeat_for_month.getCheckedRadioButtonId();

        Log.d(Utils.LOG_TAG, "checkedId = " + checkedId);

        rg_repeat_for_month.clearCheck();
        rg_repeat_for_month.check(checkedId);

        checkedId = rg_repeat_for_year.getCheckedRadioButtonId();

        Log.d(Utils.LOG_TAG, "checkedId = " + checkedId);

        rg_repeat_for_year.clearCheck();
        rg_repeat_for_year.check(checkedId);
    }

    // method onCheckedChanged for ToggleButtons and RadioButtons
    @Override
    public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked) {

        Log.d(Utils.LOG_TAG, "compoundButton "  + compoundButton.getId() + " checked");

        for(int i = 0; i < toggleButtons.size(); i++) {
            if(toggleButtons.get(i) == compoundButton) {
                array_frequencyDayOfWeek.set(i, isChecked);
                if (rb_times.isChecked()) {
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_AMOUNT);
                }
                else if (rb_until.isChecked()) {
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_END);
                }
            }
        }

        Log.d(Utils.LOG_TAG, "--------------------------------------------------------------");
        if (rb_times.isChecked()){
            switch (compoundButton.getId()){
                case R.id.rb_with_day_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT.toString());
                    break;
                case R.id.rb_with_last_day_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT.toString() + true);
                    break;
                case R.id.rb_with_dayOfWeek_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT.toString());
                    break;
                case R.id.rb_with_last_dayOfWeek_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT.toString() + true);
                    break;
                case R.id.rb_with_day_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT.toString());
                    break;
                case R.id.rb_with_last_day_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT.toString() + true);
                    break;
                case R.id.rb_with_dayOfWeek_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT.toString());
                    break;
                case R.id.rb_with_last_dayOfWeek_and_year :
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT.toString() + true);
                    break;
            }
        }
        else if (rb_until.isChecked()){
            switch (compoundButton.getId()){
                case R.id.rb_with_day_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END.toString());
                    break;
                case R.id.rb_with_last_day_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END.toString() + true);
                    break;
                case R.id.rb_with_dayOfWeek_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END.toString());
                    break;
                case R.id.rb_with_last_dayOfWeek_and_month:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END.toString() + true);
                    break;
                case R.id.rb_with_day_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END.toString());
                    break;
                case R.id.rb_with_last_day_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END.toString() + true);
                    break;
                case R.id.rb_with_dayOfWeek_and_year:
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END);
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END.toString());
                    break;
                case R.id.rb_with_last_dayOfWeek_and_year :
                    eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END);
                    last = true;
                    Log.d(Utils.LOG_TAG, CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END.toString() + true);
                    break;
            }
        }
        Log.d(Utils.LOG_TAG, "--------------------------------------------------------------");
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> adapterView, View view, int i, long l) {
        rb_never.setChecked(false);
        last = false;

        eventFrequencyViewModel.clearFrequencyType();

        ll_choose_day_of_week.setVisibility(View.GONE);
        rg_repeat_for_month.setVisibility(View.GONE);
        rg_repeat_for_year.setVisibility(View.GONE);

        animate(ll_choose_day_of_week);
        animate(rg_repeat_for_month);
        animate(rg_repeat_for_year);

        String item = adapterView.getItemAtPosition(i).toString();

        // manages content after clicking on dropdown menu item
        if(item.equals("day") || item.equals(getString(R.string.days))){
            if (rb_times.isChecked()){
                eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_AMOUNT);
            }
            else if (rb_until.isChecked()){
                eventFrequencyViewModel.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
            }
        }
        else if(item.equals("week") || item.equals(getString(R.string.weeks))){

            ll_choose_day_of_week.setVisibility(View.VISIBLE);
            animate(ll_choose_day_of_week);

            toggleButtons.get(dayOfWeekPosition).setChecked(true);

        }
        else if(item.equals("month") || item.equals(getString(R.string.months))){

            rg_repeat_for_month.setVisibility(View.VISIBLE);

            if (day != startDate.lengthOfMonth()) {
                rb_with_last_day_and_month.setVisibility(View.GONE);
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
        else if(item.equals("year") || item.equals(getString(R.string.years))){

            rg_repeat_for_year.setVisibility(View.VISIBLE);

            if (day != startDate.lengthOfMonth()) {
                rb_with_last_day_and_year.setVisibility(View.GONE);
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
            View new_focus = getDialog().getWindow().getCurrentFocus();
            /*Log.d("murad", "new_focus is " + new_focus.getClass().getName());*/

            // now assign the system service to InputMethodManager
            if(new_focus != et_times || new_focus != et_frequency){
                InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    @Override
    public void onClick(View view) {
        if(view == btn_ok){

            frequency = Integer.parseInt(et_frequency.getText().toString());
            amount = Integer.parseInt(et_times.getText().toString());

            switch (eventFrequencyViewModel.frequencyType.getValue()){
                case DAY_BY_AMOUNT: {
                    Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayFrequency(frequency, amount);
                    break;
                }
                case DAY_OF_WEEK_BY_AMOUNT: {
                    Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, amount);
                    break;
                }
                case DAY_AND_MONTH_BY_AMOUNT: {
                    Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayAndMonthFrequency(day, frequency, amount, last);
                    break;
                }
                case DAY_OF_WEEK_AND_MONTH_BY_AMOUNT: {
                    Log.d("murad", "frequency_dayOfWeek_and_month is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, amount, last);
                    break;
                }
                case DAY_AND_YEAR_BY_AMOUNT: {
                    Log.d("murad", "frequency_day_and_year is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayAndYearFrequency(day, month, frequency, amount, last);
                    break;
                }
                case DAY_OF_WEEK_AND_YEAR_BY_AMOUNT: {
                    Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + true);
                    eventFrequencyViewModel.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, amount, last);
                    break;
                }
                case DAY_BY_END: {
                    Log.d("murad", "frequency_day is " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayFrequency(frequency, frequency_end_date);
                    break;
                }
                case DAY_OF_WEEK_BY_END: {
                    Log.d("murad", "frequencyDayOfWeek is " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayOfWeekFrequency(array_frequencyDayOfWeek, frequency, frequency_end_date);
                    break;
                }
                case DAY_AND_MONTH_BY_END: {
                    Log.d("murad", "frequency_day_and_month is " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayAndMonthFrequency(day, frequency, frequency_end_date, last);
                    break;
                }
                case DAY_OF_WEEK_AND_MONTH_BY_END: {
                    Log.d("murad", "frequency_dayOfWeek_and_month is  " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayOfWeekAndMonthFrequency(weekNumber, dayOfWeekPosition, frequency, frequency_end_date, last);
                    break;
                }
                case DAY_AND_YEAR_BY_END: {
                    Log.d("murad", "frequency_day_and_year is  " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayAndYearFrequency(day, month, frequency, frequency_end_date, last);
                    break;
                }
                case DAY_OF_WEEK_AND_YEAR_BY_END: {
                    Log.d("murad", "frequency_dayOfWeek_and_year is " + true + "\n isFrequency_by_amount is " + false);
                    eventFrequencyViewModel.setOnDayOfWeekAndYearFrequency(weekNumber, dayOfWeekPosition, month, frequency, frequency_end_date, last);
                    break;
                }
                default:
                    eventFrequencyViewModel.setOnNeverFrequency();
                    break;
            }

            dismiss();

        }
        else if(view == btn_cancel){
            dismiss();
        }

    }
}
