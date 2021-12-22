package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar_Screen extends AppCompatActivity implements CalendarAdapter.OnItemListener, AdapterView.OnItemSelectedListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    private ArrayList<String> daysInMonth;
    private CalendarAdapter calendarAdapter;

    int position = 0;
    Intent intent_for_previous;


    LocalDate today;
    Thread thread;

    EditText et_name;
    EditText et_place;
    EditText et_description;

    int prev = 0;
    int next = 1;
    TextView textView5;
    String day;

    public static final String action_to_find_today = BuildConfig.APPLICATION_ID + "to find today";

    public static final String action_to_change_previous = BuildConfig.APPLICATION_ID + "action_to_change_previous";
    public static final String action_to_change_next = BuildConfig.APPLICATION_ID + "action_to_change_next";

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_screen);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        selectedDate = LocalDate.now();
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

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        day = ""+selectedDate.getDayOfMonth();
        Toast.makeText(getApplicationContext(), day, Toast.LENGTH_SHORT).show();
        setMonthView();

    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, this, this);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 7));

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
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent_for_previous);
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

        int dayOfWeek = firstOfCurrentMonth.getDayOfWeek().getValue();
        int lastDayOfPrevMonth = lastOfPrevMonth.getDayOfMonth();

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
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        prev=0;
        next=1;
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        prev=0;
        next=1;
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals("")) {
            String message = dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Log.d("murad", "child count after load = " + calendarRecyclerView.getChildCount());

            Dialog d = new Dialog(this);
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
                    Intent toAddEvent_Screen = new Intent(getApplicationContext(), AddEvent_Screen.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}








