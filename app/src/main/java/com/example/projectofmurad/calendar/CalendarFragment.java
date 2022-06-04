package com.example.projectofmurad.calendar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.MainViewModel;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.MyGridLayoutManager;
import com.example.projectofmurad.helpers.utils.CalendarUtils;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.notifications.FCMSend;
import com.example.projectofmurad.notifications.MyAlarmManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements CalendarAdapter.OnCalendarCellClickListener {

    public static final String SELECTED_DATE_DAY = "selected_date_day";
    public static final String SELECTED_DATE_MONTH = "selected_date_month";
    public static final String SELECTED_DATE_YEAR = "selected_date_year";

    public static final String ACTION_MOVE_TO_CALENDAR_FRAGMENT = "action_move_to_calendar_fragment";

    private TextView tv_date;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate = LocalDate.now();

    private LocalDate previousDate = selectedDate;

    private ConstraintLayout ll_calendar_view;

    private int prev = 0;
    private int next = 1;

    private MainViewModel mainViewModel;

    private final String[] days = CalendarUtils.getShortDaysOfWeek();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int day = getArguments().getInt(SELECTED_DATE_DAY, LocalDate.now().getDayOfMonth());
            int month = getArguments().getInt(SELECTED_DATE_MONTH, LocalDate.now().getMonthValue());
            int year = getArguments().getInt(SELECTED_DATE_YEAR, LocalDate.now().getYear());

            selectedDate = LocalDate.of(year, month, day);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        LocalDate today = LocalDate.now();

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);

        tv_date = view.findViewById(R.id.tv_date);
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        android.app.AlertDialog.THEME_HOLO_LIGHT);

                datePickerDialog.getDatePicker().setBackgroundColor(Color.TRANSPARENT);
                datePickerDialog.updateDate(selectedDate.getYear(), selectedDate.getMonthValue(), selectedDate.getDayOfMonth());

                datePickerDialog.getDatePicker().findViewById(
                        getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                datePickerDialog.setOnDateSetListener((v, year, month, day) -> setMonthView(LocalDate.of(year, month+1, day)));

                datePickerDialog.show();
            }
        });

        ll_calendar_view = view.findViewById(R.id.ll_calendar_view);

        Log.d("murad","today is " + CalendarUtils.DateToTextOnline(today));

        MaterialButton btn_auto_event = view.findViewById(R.id.btn_auto_event);
        btn_auto_event.setOnClickListener(v -> sendAlarm());

        MaterialButton btn_previous_month = view.findViewById(R.id.btn_previous_month);
        btn_previous_month.setOnClickListener(v -> setMonthView(selectedDate.minusMonths(1)));

        MaterialButton btn_next_month = view.findViewById(R.id.btn_next_month);
        btn_next_month.setOnClickListener(v -> setMonthView(selectedDate.plusMonths(1)));

        initAllDaysOfWeek();

        mainViewModel.getEventDate().observe(getViewLifecycleOwner(), this::setMonthView);

        MaterialToolbar materialToolbar = view.findViewById(R.id.materialToolbar);
        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.calendar_app_bar_today) {
                setMonthView(LocalDate.now());
            }
            return false;
        });
    }

    private void showEvent(String event_private_id){
        if (event_private_id != null){
            new Handler().postDelayed(() -> createDayDialog(selectedDate, event_private_id), 500);
            mainViewModel.resetEventPrivateId();
            Log.d(Utils.LOG_TAG, "event_private_id is NOT null");
        }
    }

    public void sendAlarm() {
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setName("Sample Event");
        calendarEvent.setPlace("Sample Place");
        calendarEvent.setDescription("Sample Description");
        calendarEvent.setColor(Utils.generateRandomColor());
        calendarEvent.setPrivateId("sample_event_private_id");
        calendarEvent.setChainId("sample_event_private_id");
        calendarEvent.updateStartDate(LocalDate.now());
        calendarEvent.updateStartTime(LocalTime.now().plusMinutes(1));
        calendarEvent.updateEndDate(LocalDate.now());
        calendarEvent.updateEndTime(LocalTime.now().plusMinutes(1).plusHours(1));
        Log.d("murad", calendarEvent.toString());

        MyAlarmManager.addAlarm(requireContext(), calendarEvent, 0);
        FirebaseUtils.getEventsForDateRef(calendarEvent.receiveStartDate())
                .child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        FirebaseUtils.getAllEventsDatabase().child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        FCMSend.sendNotificationsToAllUsersWithTopic(requireContext(), calendarEvent, Utils.ADD_EVENT_NOTIFICATION_CODE);

    }

    private void initAllDaysOfWeek(){
        TextView tv_Sunday = requireView().findViewById(R.id.tv_Sunday);
        tv_Sunday.setText(days[6]);

        TextView tv_Monday = requireView().findViewById(R.id.tv_Monday);
        tv_Monday.setText(days[0]);

        TextView tv_Tuesday = requireView().findViewById(R.id.tv_Tuesday);
        tv_Tuesday.setText(days[1]);

        TextView tv_Wednesday = requireView().findViewById(R.id.tv_Wednesday);
        tv_Wednesday.setText(days[2]);

        TextView tv_Thursday = requireView().findViewById(R.id.tv_Thursday);
        tv_Thursday.setText(days[3]);

        TextView tv_Friday = requireView().findViewById(R.id.tv_Friday);
        tv_Friday.setText(days[4]);

        TextView tv_Saturday = requireView().findViewById(R.id.tv_Saturday);
        tv_Saturday.setText(days[5]);
    }

    public void animate(ViewGroup viewGroup) {

        if (previousDate.getMonthValue() == selectedDate.getMonthValue()) {
            AutoTransition slide = new AutoTransition();
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }
        else if (previousDate.getMonthValue() > selectedDate.getMonthValue()) {
            Slide slide = new Slide(Gravity.START);
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }
        else if (previousDate.getMonthValue() < selectedDate.getMonthValue()) {
            Slide slide = new Slide(Gravity.END);
            slide.setDuration(100);
            TransitionManager.beginDelayedTransition(viewGroup, slide);
        }

        previousDate = selectedDate;
    }

    private void setMonthView(LocalDate newSelectedDate) {
        prev = 0;
        next = 1;
        selectedDate = newSelectedDate;

        tv_date.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, this);
        calendarRecyclerView.setAdapter(calendarAdapter);

        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(requireContext(), 7);

        gridLayoutManager.setOnLayoutCompleteListener(
                () -> mainViewModel.getEventPrivateId().observe(getViewLifecycleOwner(), CalendarFragment.this::showEvent));

        calendarRecyclerView.setLayoutManager(gridLayoutManager);
        calendarRecyclerView.setHasFixedSize(true);

        animate(ll_calendar_view);

    }

    @NonNull
    private ArrayList<LocalDate> daysInMonthArray(@NonNull LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        LocalDate previousMonthDate = date.minusMonths(1);

        int daysInCurrentMonth = date.lengthOfMonth();
        int daysInPrevMonth = date.minusMonths(1).lengthOfMonth();

        LocalDate firstOfCurrentMonth = date.withDayOfMonth(1);
        LocalDate lastOfPrevMonth = previousMonthDate.withDayOfMonth(daysInPrevMonth);

        int dayOfWeek = firstOfCurrentMonth.getDayOfWeek().getValue();

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
     * Cleans up days from previous or next month in current month view
     * if amount any of them is bigger than 7.
     * <p>
     * @param prevDays <b>days in current month view from previous month</b>
     * @param nextDays <b>days in current month view from next month</b>
     * @param daysInMonthArray <b>days in current month</b>
     * <p>
     */
    private void cleanupCalendar(int prevDays, int nextDays, @NonNull ArrayList<LocalDate> daysInMonthArray) {
        int length = daysInMonthArray.size();
        Log.d("murad", "old length " + length);
        if(prevDays == 7){
            for(int i = 0; i < 7; i++){
                daysInMonthArray.remove(0).getDayOfMonth();
            }
            prev = 0;
        }

        length = daysInMonthArray.size();

        if(nextDays >= 7){
            for(int i = length-1; i >= length-7; i--){
                daysInMonthArray.remove(i);
            }
            next -= 7;
        }
    }

    private String monthYearFromDate(@NonNull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", CalendarUtils.getLocale());
        return date.format(formatter);
    }

    @Override
    public void onCalendarCellClick(int position, int oldPosition, @NonNull LocalDate passingDate) {
        Log.d("murad","position " + position);
        Log.d("murad","lengthOfMonth " + passingDate.lengthOfMonth());

        int duration = 0;

        Log.d("murad", "selectedDate " + CalendarUtils.DateToTextOnline(selectedDate) + ", passingDate " + CalendarUtils.DateToTextOnline(passingDate));
        if(selectedDate.getMonthValue() != passingDate.getMonthValue()){
            setMonthView(passingDate);
            duration = 300;
        }
        else {
            calendarRecyclerView.findViewHolderForAdapterPosition(oldPosition).
                    itemView.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);

            calendarRecyclerView.findViewHolderForAdapterPosition(position)
                    .itemView.setBackgroundResource(R.drawable.calendar_cell_selected_background);

            calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.bringToFront();
        }

        new Handler().postDelayed(() -> createDayDialog(passingDate, null), duration);
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
}