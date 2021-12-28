package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendar_Month_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar_Month_Fragment extends Fragment implements CalendarAdapter.OnItemListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String ARG_SELECTED_DATE_DAY = "selectedDate_day";
    public static final String ARG_SELECTED_DATE_MONTH = "selectedDate_month";
    public static final String ARG_SELECTED_DATE_YEAR = "selectedDate_year";

    private LocalDate selectedDate;
    private int selectedDate_current_day;
    private int selectedDate_current_month;
    private int selectedDate_current_year;

    private int prev = 0;
    private int next = 1;

    int dayOfWeek;
    int lastDayOfPrevMonth;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button btn_previous_month;
    private Button btn_next_month;


    private CalendarAdapter calendarAdapter;

    int position = 0;
    Intent intent_for_previous;

    LocalDate today;
    Thread thread;
    LocalDate passingDate;

    String day;


    private ArrayList<LocalDate> daysInMonth;


    RecyclerView rv_events;
    CalendarEventAdapter calendarEventAdapter;

    ListView lv_events;
    DayAdapter adapter;

    int positionOfToday = 0;

    EditText et_name;
    EditText et_place;
    EditText et_description;

    int length;

    int old_position = 0;

    TextView textView5;


    BroadcastReceiver broadcastReceiver;

    ViewPager view_pager;
    ViewPager2 view_pager2;

    private Calendar_Month_Fragment fragment;

    public static final String action_to_find_today = BuildConfig.APPLICATION_ID + "to find today";
    public static final String action_to_change_previous = BuildConfig.APPLICATION_ID + "action_to_change_previous";
    public static final String action_to_change_next = BuildConfig.APPLICATION_ID + "action_to_change_next";


    public Calendar_Month_Fragment(int selectedDate_day, int selectedDate_month, int selectedDate_year) {
        // Required empty public constructor
        Log.d("view_pager2", "constructor initialization");
        if(getArguments() != null) {
            selectedDate_current_day = getArguments().getInt(ARG_SELECTED_DATE_DAY);
            selectedDate_current_month = getArguments().getInt(ARG_SELECTED_DATE_MONTH);
            selectedDate_current_year = getArguments().getInt(ARG_SELECTED_DATE_YEAR);
        }
        this.selectedDate = LocalDate.of(selectedDate_year, selectedDate_month, selectedDate_day);
        Log.d("view_pager2", Utils.getDefaultDate(selectedDate));
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment Calendar_Month_Fragment.
     */

    // TODO: Rename and change types and number of parameters
    public static Calendar_Month_Fragment newInstance(int selectedDate_day, int selectedDate_month, int selectedDate_year) {
        Calendar_Month_Fragment fragment = new Calendar_Month_Fragment(selectedDate_day, selectedDate_month, selectedDate_year);
        /*
           The 1 below is an optimization, being the number of arguments that will
           be added to this bundle.  If you know the number of arguments you will add
           to the bundle it stops additional allocations of the backing map.  If
           unsure, you can construct Bundle without any arguments
        */

        Bundle args = new Bundle();
        Log.d("view_pager2", "fragment's newInstance created");
        /*
          This stores the argument as an argument in the bundle.
          Note that even if the 'name' parameter is NULL then this will work, so you should consider
          at this point if the parameter is mandatory and if so check for NULL and throw an appropriate error if so
        */
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_SELECTED_DATE_DAY, selectedDate_day);
        args.putInt(ARG_SELECTED_DATE_MONTH, selectedDate_month);
        args.putInt(ARG_SELECTED_DATE_YEAR, selectedDate_year);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("view_pager2", "fragment created");
        if(getArguments() != null) {
            selectedDate_current_day = getArguments().getInt(ARG_SELECTED_DATE_DAY);
            selectedDate_current_month = getArguments().getInt(ARG_SELECTED_DATE_MONTH);
            selectedDate_current_year = getArguments().getInt(ARG_SELECTED_DATE_YEAR);
        }

        this.selectedDate = LocalDate.of(selectedDate_current_year, selectedDate_current_month, selectedDate_current_day);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar__month_, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView_test);
        monthYearText = view.findViewById(R.id.monthYearTV_test);

        btn_previous_month = view.findViewById(R.id.btn_previous_month);
        btn_previous_month.setOnClickListener(v -> previousMonthActionForFragment());

        btn_next_month = view.findViewById(R.id.btn_next_month);
        btn_next_month.setOnClickListener(v -> nextMonthActionForFragment());

        view_pager2 = getActivity().findViewById(R.id.view_pager2);

        selectedDate = LocalDate.of(selectedDate_current_year, selectedDate_current_month, selectedDate_current_day);

        Log.d("murad",  "onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(requireContext(), "OnViewCreated", Toast.LENGTH_SHORT).show();
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView_test);
        monthYearText = view.findViewById(R.id.monthYearTV_test);


        Log.d("murad",  "onViewCreated");

        if(getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            selectedDate_current_day = getArguments().getInt(ARG_SELECTED_DATE_DAY);
            selectedDate_current_month = getArguments().getInt(ARG_SELECTED_DATE_MONTH);
            selectedDate_current_year = getArguments().getInt(ARG_SELECTED_DATE_YEAR);

        }

        selectedDate = LocalDate.of(selectedDate_current_year, selectedDate_current_month, selectedDate_current_day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        Log.d("murad", selectedDate.format(formatter));
        today = LocalDate.now();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch(action) {
                    case action_to_find_today:
                        Log.d("murad", "broadcastReceiver for today triggered");
                        int pos = intent.getIntExtra("today", 0);
                        calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                findViewById(R.id.cellDayText).setBackgroundResource(R.drawable.calendar_cell_text_today_background);

                        calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                setBackgroundResource(R.drawable.calendar_cell_selected_background);
                        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent_for_previous);
                        break;
                    case action_to_change_previous:
                        Log.d("murad", "broadcastReceiver for previous triggered");
                        int previous = intent.getIntExtra("previous", 0);
                        int next = intent.getIntExtra("next", 0);

                        Log.d("murad", "previous " + previous);
                        Log.d("murad", "next " + next);

                        int length = calendarRecyclerView.getChildCount();

                        for(int i = 0; i < previous; i++) {
                            calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText).
                                    setBackgroundResource(R.drawable.calendar_cell_text_not_current_month_background);
                        }

                        for(int j = length - next; j < length; j++) {
                            calendarRecyclerView.findViewHolderForAdapterPosition(j).
                                    itemView.findViewById(R.id.cellDayText).
                                    setBackgroundResource(R.drawable.calendar_cell_text_not_current_month_background);
                        }
                        break;
                }
            }
        };

        // registering the specialized custom BroadcastReceiver in LocalBroadcastManager to receive custom broadcast.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action_to_find_today);
        intentFilter.addAction(action_to_change_previous);
        intentFilter.addAction(action_to_change_next);

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter);

        day = ""+selectedDate.getDayOfMonth();
        Toast.makeText(requireContext(), day, Toast.LENGTH_SHORT).show();
        setMonthView();
    }

    private void setMonthView() {

        monthYearText.setText(monthYearFromDate(selectedDate));
        daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, requireContext(), this);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));

        intent_for_previous = new Intent(action_to_change_previous);
        intent_for_previous.putExtra("previous", prev);
        intent_for_previous.putExtra("next", next);

        Log.d("murad", "prev " + prev);
        Log.d("murad", "next " + next);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //int i=0;
                while(calendarRecyclerView.getChildCount() < daysInMonth.size()){}

                Log.d("murad", positionOfToday + " " + calendarAdapter.getItemCount());
                Log.d("murad", " recycler view's child count = " + calendarRecyclerView.getChildCount());

                if(selectedDate.getMonth().equals(today.getMonth()) && selectedDate.getYear() == today.getYear()){
                    Log.d("murad",  "selectedDate == LocalDate.now()");
                    for(int i=0; i < length; i++){
                        if(String.valueOf(daysInMonth.get(i).getDayOfMonth()).equals(day)){
                            Log.d("murad", "i: "+i);
                            positionOfToday = i;
                        }

                    }
                    Intent i = new Intent(action_to_find_today);
                    i.putExtra("today", positionOfToday);
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i);
                }
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent_for_previous);
            }
        });
        thread.start();

    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        LocalDate previousMonthDate = date.minusMonths(1);
        YearMonth previousYearMonth = YearMonth.from(previousMonthDate);

        int daysInCurrentMonth = yearMonth.lengthOfMonth();
        int daysInPrevMonth = previousYearMonth.lengthOfMonth();

        LocalDate firstOfCurrentMonth = date.withDayOfMonth(1);
        LocalDate lastOfPrevMonth = previousMonthDate.withDayOfMonth(daysInPrevMonth);

        dayOfWeek = firstOfCurrentMonth.getDayOfWeek().getValue();

        lastDayOfPrevMonth = lastOfPrevMonth.getDayOfMonth();

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
    private void cleanupCalendar(int prevDays, int nextDays, ArrayList<LocalDate> daysInMonthArray) {
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
            next -=7;
        }
        length = daysInMonthArray.size();
        Log.d("murad", "new length " + length);
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        Log.d("murad", date.format(formatter));
        return date.format(formatter);
    }

    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        prev=0;
        next=1;
        setMonthView();
    }

    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        prev=0;
        next=1;
        setMonthView();
    }

    public void previousMonthActionForFragment() {
        passingDate = selectedDate.minusMonths(1);
        fragment = Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(1));

        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, passingDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, passingDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, passingDate.getYear());

        //getParentFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        //getParentFragmentManager().beginTransaction().add(Calendar_Month_Fragment.class, bundle, "new");

    }

    public void nextMonthActionForFragment() {
        passingDate = selectedDate.plusMonths(1);
        fragment = Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1));

        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, passingDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, passingDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, passingDate.getYear());

        //getParentFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        //getParentFragmentManager().beginTransaction().add(R.id.container, fragment);
    }


    @Override
    public void onItemClick(int position, String dayText, LocalDate passingDate) {
        if(!dayText.equals("")) {

            Log.d("murad","position " + position);
            Log.d("murad","dayOfWeek " + dayOfWeek);
            Log.d("murad","lengthOfMonth " + passingDate.lengthOfMonth());

            /*if(position < dayOfWeek) {
                LocalDate previousMonth = selectedDate.minusMonths(1);
                selectedDate = previousMonth;
                int firstDayOfWeekOfPreviousMonth = previousMonth.withDayOfMonth(1).getDayOfWeek().getValue();
                if(firstDayOfWeekOfPreviousMonth == 7){
                    firstDayOfWeekOfPreviousMonth -= 7;
                }
                position = previousMonth.lengthOfMonth() + firstDayOfWeekOfPreviousMonth - 1;
                prev = 0;
                next = 1;
                setMonthView();
            }
            else if(position >= selectedDate.lengthOfMonth() + dayOfWeek){
                LocalDate nextMonth = selectedDate.plusMonths(1);
                selectedDate = nextMonth;
                int firstDayOfWeekOfNextMonth = nextMonth.withDayOfMonth(1).getDayOfWeek().getValue();
                if(firstDayOfWeekOfNextMonth == 7){
                    firstDayOfWeekOfNextMonth -= 7;
                }
                position = nextMonth.lengthOfMonth() + firstDayOfWeekOfNextMonth - position + 2;
                prev = 0;
                next = 1;
                setMonthView();
            }*/

            YearMonth yearMonthOfSelectedDate = YearMonth.of(selectedDate.getYear(), selectedDate.getMonth());
            YearMonth yearMonthOfPassingDate = YearMonth.of(passingDate.getYear(), passingDate.getMonth());

            if(!yearMonthOfPassingDate.equals(yearMonthOfSelectedDate)){
                selectedDate = passingDate;
                prev = 0;
                next = 1;
                setMonthView();
            }
            else {
                calendarRecyclerView.findViewHolderForAdapterPosition(old_position).
                        itemView.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);

                calendarRecyclerView.findViewHolderForAdapterPosition(positionOfToday).
                        itemView.setBackgroundResource(R.drawable.calendar_cell_unclicked_background);

                old_position = position;

            /*if(passingDate == today){
                calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundResource(R.drawable.calendar_cell_today_and_selected_background);
            }
            else {
                if(selectedDate.getMonth().equals(today.getMonth()) && selectedDate.getYear() == today.getYear()){
                    calendarRecyclerView.findViewHolderForAdapterPosition(positionOfToday).itemView.setBackgroundResource(R.drawable.calendar_cell_text_today_background);
                }
            }*/

                calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.
                        setBackgroundResource(R.drawable.calendar_cell_selected_background);
            }

            //String message = dayText + " " + monthYearFromDate(selectedDate);
            //Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Log.d("murad", "child count after load = " + calendarRecyclerView.getChildCount());

            createDayDialog(passingDate);

        }
    }

    public void createDayDialog(LocalDate passingDate){
        Dialog d = new Dialog(requireContext());
        d.setContentView(R.layout.day_dialog);
        d.setCancelable(true);

        String day = String.valueOf(passingDate.getDayOfMonth());
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy"));

        TextView tv_day = d.findViewById(R.id.tv_day);
        tv_day.setText(day);

        TextView tv_full_date = d.findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        TextView tv_no_events = d.findViewById(R.id.tv_no_events);
        //rv_events = d.findViewById(R.id.rv_events);
        lv_events = d.findViewById(R.id.lv_events);

        if(Utils.map.containsKey(passingDate)){
            tv_no_events.setVisibility(View.INVISIBLE);

            ArrayList<CalendarEvent> calendarEventArrayList = Utils.map.get(passingDate);
            Log.d("murad", "empty calendarEventArrayList " + calendarEventArrayList.isEmpty());
            /*calendarEventAdapter = new CalendarEventAdapter(calendarEventArrayList, getApplicationContext());
            rv_events.setAdapter(calendarEventAdapter);*/

            adapter = new DayAdapter(calendarEventArrayList, requireContext(), passingDate);
            lv_events.setAdapter(adapter);
            lv_events.setVisibility(View.VISIBLE);

            Log.d("murad", "containsKey");
        }
        else{
            //rv_events.setVisibility(View.INVISIBLE);
            lv_events.setVisibility(View.INVISIBLE);
            tv_no_events.setVisibility(View.VISIBLE);
            Log.d("murad", "not containsKey");
        }

        if(Utils.map.isEmpty()){
            Log.d("murad", "isEmpty");
        }

        FloatingActionButton fab_add_event = d.findViewById(R.id.fab_add_event);
        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createAddEventDialog(dayText, selectedDate);
                Intent toAddEvent_Screen = new Intent(requireContext(), AddEvent_Screen.class);

                DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
                Log.d("murad","passingDate " + passingDate.format(simpleDateFormat));

                int day = passingDate.getDayOfMonth();
                int month = passingDate.getMonth().getValue();
                int year = passingDate.getYear();
                toAddEvent_Screen.putExtra("day", day);
                toAddEvent_Screen.putExtra("month", month);
                toAddEvent_Screen.putExtra("year", year);


                startActivity(toAddEvent_Screen);
                d.dismiss();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                d.show();
            }
        }, 100);
    }

    public LocalDate getSelectedDate(){
        return this.selectedDate;
    }


}