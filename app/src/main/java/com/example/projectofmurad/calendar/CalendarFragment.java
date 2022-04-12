package com.example.projectofmurad.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.notifications.FCMSend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements CalendarAdapter.CalendarOnItemListener{

    public static final String SELECTED_DATE_DAY = "selected_date_day";
    public static final String SELECTED_DATE_MONTH = "selected_date_month";
    public static final String SELECTED_DATE_YEAR = "selected_date_year";

    public static final String EVENT_TO_SHOW_PRIVATE_ID = "selected_date_year";

    public static final String ACTION_MOVE_TO_CALENDAR_FRAGMENT = "action_move_to_calendar_fragment";

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate = LocalDate.now();

    private String event_private_id;

    private ArrayList<LocalDate> daysInMonth;
    private CalendarAdapter calendarAdapter;

    private ConstraintLayout ll_calendar_view;

    private LocalDate today;

    private Button btn_auto_event;

    private Button btn_previous_month;
    private Button btn_next_month;

    private int prev = 0;
    private int next = 1;

    private int dayOfWeek;
    private int length;

    private final String[] days = UtilsCalendar.getShortDaysOfWeek();

    private TextView tv_Sunday;
    private TextView tv_Monday;
    private TextView tv_Tuesday;
    private TextView tv_Wednesday;
    private TextView tv_Thursday;
    private TextView tv_Friday;
    private TextView tv_Saturday;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectedDate Selected date.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static CalendarFragment newInstance(@NonNull LocalDate selectedDate) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
        args.putInt(SELECTED_DATE_MONTH, selectedDate.getMonthValue());
        args.putInt(SELECTED_DATE_YEAR, selectedDate.getYear());

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static CalendarFragment newInstance(@NonNull LocalDate selectedDate, String event_private_id) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTED_DATE_DAY, selectedDate.getDayOfMonth());
        args.putInt(SELECTED_DATE_MONTH, selectedDate.getMonthValue());
        args.putInt(SELECTED_DATE_YEAR, selectedDate.getYear());

        args.putString(EVENT_TO_SHOW_PRIVATE_ID, event_private_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int day = getArguments().getInt(SELECTED_DATE_DAY, LocalDate.now().getDayOfMonth());
            int month = getArguments().getInt(SELECTED_DATE_MONTH, LocalDate.now().getMonthValue());
            int year = getArguments().getInt(SELECTED_DATE_YEAR, LocalDate.now().getYear());

            selectedDate = LocalDate.of(year, month, day);
            if (selectedDate == null){
                Log.d(Utils.LOG_TAG, "selectedDate is null");
            }


            event_private_id = getArguments().getString(EVENT_TO_SHOW_PRIVATE_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    private MainViewModel mainViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        today = LocalDate.now();

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);

        monthYearText = view.findViewById(R.id.monthYearTV);
        monthYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        AlertDialog.THEME_HOLO_LIGHT);

                datePickerDialog.getDatePicker().setBackgroundColor(Color.TRANSPARENT);
                datePickerDialog.updateDate(selectedDate.getYear(), selectedDate.getMonthValue(),
                        selectedDate.getDayOfMonth());

                datePickerDialog.getDatePicker().findViewById(
                        getResources().getIdentifier("day", "id",
                                "android")).setVisibility(View.GONE);

                datePickerDialog.setOnDateSetListener((v, year, month, day) -> {
                    month = month + 1;
                    selectedDate = LocalDate.of(year, month, day);
                    prev = 0;
                    next = 1;
                    setMonthView();
                });

                datePickerDialog.show();
            }
        });


/*        LocalDate now = LocalDate.now();
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd LLLL", UtilsCalendar.locale);

        monthYearText.setText(now.format(simpleDateFormat));*/

        ll_calendar_view = view.findViewById(R.id.ll_calendar_view);

        Log.d("murad","today is " + UtilsCalendar.DateToTextOnline(today));

        btn_auto_event = view.findViewById(R.id.btn_auto_event);
        btn_auto_event.setOnClickListener(this::sendAlarm);

        btn_previous_month = view.findViewById(R.id.btn_previous_month);
        btn_previous_month.setOnClickListener(this::previousMonthAction);

        btn_next_month = view.findViewById(R.id.btn_next_month);
        btn_next_month.setOnClickListener(this::nextMonthAction);

        String day = "" + selectedDate.getDayOfMonth();
        Toast.makeText(requireContext(), day, Toast.LENGTH_SHORT).show();
        initAllDaysOfWeek(view);


        mainViewModel.getEventDate().observe(getViewLifecycleOwner(), new Observer<LocalDate>() {
            @Override
            public void onChanged(LocalDate localDate) {
                selectedDate = localDate;

                Log.d(Utils.LOG_TAG, "mainViewModel selectedDate changed!");
                Log.d(Utils.LOG_TAG, "mainViewModel selectedDate is " + UtilsCalendar.DateToTextOnline(selectedDate));

                prev = 0;
                next = 1;
                direction = 0;

                setMonthView();
            }
        });

        mainViewModel.getEventPrivateId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String eventPrivateId) {
                event_private_id = eventPrivateId;

                Log.d(Utils.LOG_TAG, "mainViewModel event_private_id changed!");
                Log.d(Utils.LOG_TAG, "mainViewModel event_private_id is " + event_private_id);

                showEvent(event_private_id);
            }
        });

//        setMonthView();
    }

    private void showEvent(String event_private_id){
        if (event_private_id != null){
            new Handler().postDelayed(() -> createDayDialog(selectedDate, event_private_id), 500);
            mainViewModel.setEventPrivateId(null);
        }
    }

    public boolean action;

    public void sendAlarm(View view) {

        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setName("Sample Event");
        calendarEvent.setPlace("Sample Place");
        calendarEvent.setDescription("Sample Description");
        calendarEvent.setColor(Utils.generateRandomColor());
        calendarEvent.setPrivateId("sample_event_private_id");
        calendarEvent.setChainId("sample_event_private_id");
        calendarEvent.updateStart_date(LocalDate.now());
        calendarEvent.updateStart_time(LocalTime.now().plusMinutes(1));
        calendarEvent.updateEnd_date(LocalDate.now());
        calendarEvent.updateEnd_time(LocalTime.now().plusMinutes(1).plusHours(1));
        Log.d("murad", calendarEvent.toString());

        AlarmManagerForToday.addAlarm(requireContext(), calendarEvent, 0);
        FirebaseUtils.eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(calendarEvent.receiveStart_date()))
                .child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        FirebaseUtils.allEventsDatabase.child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        FCMSend.sendNotificationsToAllUsersWithTopic(requireContext(), calendarEvent, Utils.ADD_EVENT_NOTIFICATION_CODE);

    }

    private void initAllDaysOfWeek(@NonNull View view){
        tv_Sunday = view.findViewById(R.id.tv_Sunday);
        tv_Sunday.setText(days[6]);

        tv_Monday = view.findViewById(R.id.tv_Monday);
        tv_Monday.setText(days[0]);

        tv_Tuesday = view.findViewById(R.id.tv_Tuesday);
        tv_Tuesday.setText(days[1]);

        tv_Wednesday = view.findViewById(R.id.tv_Wednesday);
        tv_Wednesday.setText(days[2]);

        tv_Thursday = view.findViewById(R.id.tv_Thursday);
        tv_Thursday.setText(days[3]);

        tv_Friday = view.findViewById(R.id.tv_Friday);
        tv_Friday.setText(days[4]);

        tv_Saturday = view.findViewById(R.id.tv_Saturday);
        tv_Saturday.setText(days[5]);
    }

    int direction = 1;

    public void animate(ViewGroup viewGroup){
        AutoTransition trans = new AutoTransition();
        trans.setDuration(100);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        //trans.setInterpolator(new DecelerateInterpolator());
        //trans.setInterpolator(new FastOutSlowInInterpolator());

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());


        if (direction == 0) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }
        else if (direction == 1) {
            AutoTransition slide = new AutoTransition();
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);

        }
        else if (direction == 2) {
            Slide slide = new Slide(Gravity.END);
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }

//        TransitionManager.beginDelayedTransition(viewGroup, trans);


    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, requireContext(), this, selectedDate);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));



        animate(ll_calendar_view);

        Log.d("murad", "prev " + prev);
        Log.d("murad", "next " + next);

    }

    @NonNull
    private ArrayList<LocalDate> daysInMonthArray(@NonNull LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        LocalDate previousMonthDate = date.minusMonths(1);

        int daysInCurrentMonth = date.lengthOfMonth();
        int daysInPrevMonth = date.minusMonths(1).lengthOfMonth();

        LocalDate firstOfCurrentMonth = date.withDayOfMonth(1);
        LocalDate lastOfPrevMonth = previousMonthDate.withDayOfMonth(daysInPrevMonth);

        dayOfWeek = firstOfCurrentMonth.getDayOfWeek().getValue();

        int lastDayOfPrevMonth = lastOfPrevMonth.getDayOfMonth();

        Log.d("murad", "last day is " + lastDayOfPrevMonth);
        Log.d("murad", "daysInCurrentMonth " + daysInCurrentMonth);
        Log.d("murad", "dayOfWeek " + firstOfCurrentMonth.getDayOfWeek().toString());


        int current = 1;

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek) {
                daysInMonthArray.add(date);
                prev++;
            }
            else if(i > daysInCurrentMonth + dayOfWeek){
                //daysInMonthArray.add(String.valueOf(next));
                daysInMonthArray.add(date.plusMonths(1).withDayOfMonth(next));
                next++;
            }
            else {
                //daysInMonthArray.add(String.valueOf(i - dayOfWeek));
                daysInMonthArray.add(date.withDayOfMonth(current));
                current++;
            }
        }
        next--;

        Log.d("murad", "prev " + prev);
        Log.d("murad", "next " + next);

        for(int j = prev-1; j >= 0; j--, lastDayOfPrevMonth--) {
            //daysInMonthArray.set(j, String.valueOf(lastDayOfPrevMonth));
            daysInMonthArray.set(j, date.minusMonths(1).withDayOfMonth(lastDayOfPrevMonth));
        }

        cleanupCalendar(prev, next, daysInMonthArray);

        return daysInMonthArray;
    }

    /**
     * Method cleans up days from previous or next month in current month view
     * if amount any of them is bigger than 7.
     * <p>
     * @param prevDays <b>days in current month view from previous month</b>
     * @param nextDays <b>days in current month view from next month</b>
     * @param daysInMonthArray <b>days in current month</b>
     * <p>
     * @return ArrayList<LocalDate> for current month view
     */
    private void cleanupCalendar(int prevDays, int nextDays, @NonNull ArrayList<LocalDate> daysInMonthArray) {
        length = daysInMonthArray.size();
        Log.d("murad", "old length " + length);
        if(prevDays == 7){
            for(int i=0; i < 7; i++){
                daysInMonthArray.remove(0).getDayOfMonth();
            }
            prev = 0;
        }

        length = daysInMonthArray.size();
        Log.d("murad", "length after cleaning prev " + length);

        if(nextDays >= 7){
            for(int i=length-1; i >= length-7; i--){
                daysInMonthArray.remove(i);
            }
            next -= 7;
        }
        length = daysInMonthArray.size();
        Log.d("murad", "new length " + length);
    }

    private String monthYearFromDate(@NonNull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", UtilsCalendar.locale);
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        prev = 0;
        next = 1;
        direction = 0;
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        prev = 0;
        next = 1;
        direction = 2;
        setMonthView();
    }

    @Override
    public void onItemClick(int position, int oldPosition, @NonNull String dayText, LocalDate passingDate) {
        if(!dayText.equals("")) {

            Log.d("murad","position " + position);
            Log.d("murad","dayOfWeek " + dayOfWeek);
            Log.d("murad","lengthOfMonth " + passingDate.lengthOfMonth());

            int duration = 0;

            Log.d("murad", "selectedDate " + UtilsCalendar.DateToTextOnline(selectedDate) + ", passingDate " + UtilsCalendar.DateToTextOnline(passingDate));
            if(selectedDate.getMonthValue() != passingDate.getMonthValue()){

                selectedDate = passingDate;
                prev = 0;
                next = 1;
                setMonthView();

                duration = 300;
            }
            else {
                calendarRecyclerView.findViewHolderForAdapterPosition(oldPosition).
                        itemView.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);

                calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.
                        setBackgroundResource(R.drawable.calendar_cell_selected_background);

                calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.
                        bringToFront();
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> createDayDialog(passingDate, null), duration);

        }
    }

    private DayDialog dayDialogFragment;

    public void createDayDialog(LocalDate passingDate, String event_private_id){

        if (dayDialogFragment == null || !dayDialogFragment.isShowing()){

            dayDialogFragment = new DayDialog(requireContext(), passingDate, event_private_id);
            dayDialogFragment.show();
        }

        if (dayDialogFragment != null){
            if (!dayDialogFragment.getPassingDate().equals(passingDate)){
                dayDialogFragment.dismiss();

                dayDialogFragment = new DayDialog(requireContext(), passingDate, event_private_id);
                dayDialogFragment.show();
            }
            else {
                dayDialogFragment.showEvent(event_private_id);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dayDialogFragment != null) {
            dayDialogFragment.dismiss();
            dayDialogFragment = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        int selectedId = item.getItemId();

        switch (selectedId) {
            case R.id.app_bar_search:

                break;
            case R.id.calendar_app_bar_today:
                selectedDate = today;
                prev = 0;
                next = 1;
                setMonthView();
                break;
        }

        return true;
    }
}