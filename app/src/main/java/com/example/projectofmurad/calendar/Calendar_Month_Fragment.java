package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    public static final String ARG_SELECTED_DATE_DAY = "selectedDate_day";
    public static final String ARG_SELECTED_DATE_MONTH = "selectedDate_month";
    public static final String ARG_SELECTED_DATE_YEAR = "selectedDate_year";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocalDate selectedDate;
    private int selectedDate_day;
    private int selectedDate_month;
    private int selectedDate_year;

    private int prev = 0;
    private int next = 1;

    int dayOfWeek;
    int lastDayOfPrevMonth;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button btn_previous_month;
    private Button btn_next_month;


    private ArrayList<String> daysInMonth;
    private CalendarAdapter calendarAdapter;

    int position = 0;
    Intent intent_for_previous;

    LocalDate today;
    Thread thread;
    LocalDate passingDate;

    String day;

    BroadcastReceiver broadcastReceiver;

    ViewPager view_pager;
    ViewPager2 view_pager2;

    private Calendar_Month_Fragment fragment;

    public static final String action_to_find_today = BuildConfig.APPLICATION_ID + "to find today";
    public static final String action_to_change_previous = BuildConfig.APPLICATION_ID + "action_to_change_previous";
    public static final String action_to_change_next = BuildConfig.APPLICATION_ID + "action_to_change_next";


    public Calendar_Month_Fragment() {
        // Required empty public constructor
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
        Calendar_Month_Fragment fragment = new Calendar_Month_Fragment();
        /*
           The 1 below is an optimization, being the number of arguments that will
           be added to this bundle.  If you know the number of arguments you will add
           to the bundle it stops additional allocations of the backing map.  If
           unsure, you can construct Bundle without any arguments
        */

        Bundle args = new Bundle();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar__month_, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView_test);
        monthYearText = view.findViewById(R.id.monthYearTV_test);

        btn_previous_month = view.findViewById(R.id.btn_previous_month);
        btn_previous_month.setOnClickListener(v -> previousMonthActionForFragment());

        btn_next_month = view.findViewById(R.id.btn_next_month);
        btn_next_month.setOnClickListener(v -> nextMonthActionForFragment());

        //view_pager = getActivity().findViewById(R.id.view_pager);

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
            selectedDate_day = getArguments().getInt(ARG_SELECTED_DATE_DAY);
            selectedDate_month = getArguments().getInt(ARG_SELECTED_DATE_MONTH);
            selectedDate_year = getArguments().getInt(ARG_SELECTED_DATE_YEAR);

        }

        selectedDate = LocalDate.of(selectedDate_year, selectedDate_month, selectedDate_day);
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
                        calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.cellDayText).setBackgroundResource(R.drawable.calendar_cell_background);
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
                            calendarRecyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.cellDayText).setBackgroundResource(R.drawable.calendar_cell_foreground);
                        }

                        for(int j = length - next; j < length; j++) {
                            calendarRecyclerView.findViewHolderForAdapterPosition(j).itemView.findViewById(R.id.cellDayText).setBackgroundResource(R.drawable.calendar_cell_foreground);
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

                Log.d("murad", position + " " + calendarAdapter.getItemCount());
                Log.d("murad", " recycler view's child count = " + calendarRecyclerView.getChildCount());

                if(selectedDate.getMonth().equals(today.getMonth()) && selectedDate.getYear() == today.getYear()){
                    Log.d("murad",  "selectedDate == LocalDate.now()");
                    for(int i=0; i<42; i++){
                        if(daysInMonth.get(i).equals(day)){
                            Log.d("murad", "i: "+i);
                            position = i;
                        }

                    }
                    Intent i = new Intent(action_to_find_today);
                    i.putExtra("today", position);
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(i);
                }
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent_for_previous);
            }
        });
        thread.start();

    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
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


        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek) {
                daysInMonthArray.add("");
                prev++;
            }
            else if(i > daysInCurrentMonth + dayOfWeek){
                daysInMonthArray.add(String.valueOf(next));
                next++;
            }
            else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        next--;

        Log.d("murad", "prev " + prev);

        for(int j = prev-1; j >= 0; j--, lastDayOfPrevMonth--) {
            daysInMonthArray.set(j, String.valueOf(lastDayOfPrevMonth));
        }

        return daysInMonthArray;
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
        getParentFragmentManager().beginTransaction().add(Calendar_Month_Fragment.class, bundle, "new");

    }

    public void nextMonthActionForFragment() {
        passingDate = selectedDate.plusMonths(1);
        fragment = Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1));

        Bundle bundle = new Bundle();

        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_DAY, passingDate.getDayOfMonth());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_MONTH, passingDate.getMonth().getValue());
        bundle.putInt(Calendar_Month_Fragment.ARG_SELECTED_DATE_YEAR, passingDate.getYear());

        //getParentFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        getParentFragmentManager().beginTransaction().add(R.id.container, fragment);}

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals("")) {

            LocalDate passingDate = selectedDate;

            Log.d("murad","position " + position);
            Log.d("murad","dayOfWeek " + dayOfWeek);
            Log.d("murad","lengthOfMonth " + selectedDate.lengthOfMonth());

            if(position <= dayOfWeek) {
                selectedDate = selectedDate.minusMonths(1);
                prev = 0;
                next = 1;
                setMonthView();
            }
            else if(position > selectedDate.lengthOfMonth() + dayOfWeek){
                selectedDate = selectedDate.plusMonths(1);
                prev = 0;
                next = 1;
                setMonthView();
            }

            String message = dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

            Log.d("murad", "child count after load = " + calendarRecyclerView.getChildCount());

            Dialog d = new Dialog(requireContext());
            d.setContentView(R.layout.day_info);
            d.setCancelable(true);

            TextView tv_date = d.findViewById(R.id.tv_date);
            tv_date.setText(message);
            TextView textview1 = d.findViewById(R.id.textView1);
            TextView textview2 = d.findViewById(R.id.textView2);
            TextView textview3 = d.findViewById(R.id.textView3);
            TextView textview4 = d.findViewById(R.id.textView4);
            FloatingActionButton fab_add_event = d.findViewById(R.id.fab_add_event);
            fab_add_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //createAddEventDialog(dayText, selectedDate);
                    Intent toAddEvent_Screen = new Intent(requireContext(), AddEvent_Screen.class);
                    int month = selectedDate.getMonth().getValue();
                    int year = selectedDate.getYear();

                    toAddEvent_Screen.putExtra("day", dayText);
                    toAddEvent_Screen.putExtra("month", month);
                    toAddEvent_Screen.putExtra("year", year);

                    startActivity(toAddEvent_Screen);
                    d.dismiss();
                }
            });

            d.show();
        }
    }

    public LocalDate getSelectedDate(){
        return selectedDate;
    }
}