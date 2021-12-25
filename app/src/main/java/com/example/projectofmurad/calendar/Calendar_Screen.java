package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Calendar_Screen extends AppCompatActivity implements CalendarAdapter.OnItemListener, AdapterView.OnItemSelectedListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    private ArrayList<LocalDate> daysInMonth;
    private CalendarAdapter calendarAdapter;

    RecyclerView rv_events;
    CalendarEventAdapter calendarEventAdapter;

    ListView lv_events;
    DayAdapter adapter;

    int positionOfToday = 0;
    Intent intent_for_previous;

    LocalDate today;
    Thread thread;

    EditText et_name;
    EditText et_place;
    EditText et_description;

    int prev = 0;
    int next = 1;

    int dayOfWeek;
    int lastDayOfPrevMonth;
    int length;

    int old_position = 0;

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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Calendar");

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

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        day = ""+selectedDate.getDayOfMonth();
        Toast.makeText(getApplicationContext(), day, Toast.LENGTH_SHORT).show();
        setMonthView();

        Utils.printHashMap(Utils.map);
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

                Log.d("murad", positionOfToday + " " + calendarAdapter.getItemCount());
                Log.d("murad", " recycler view's child count = " + calendarRecyclerView.getChildCount());

                if(selectedDate.getMonth().equals(today.getMonth()) && selectedDate.getYear() == today.getYear()){
                    Log.d("murad",  "selectedDate == LocalDate.now()");
                    for(int i=0; i<length; i++){
                        if(String.valueOf(daysInMonth.get(i).getDayOfMonth()).equals(day)){
                            Log.d("murad", "i: "+i);
                            positionOfToday = i;
                        }

                    }
                    Intent i = new Intent(action_to_find_today);
                    i.putExtra("today", positionOfToday);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent_for_previous);
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
        Dialog d = new Dialog(this);
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

            adapter = new DayAdapter(calendarEventArrayList, Calendar_Screen.this, passingDate);
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
                Intent toAddEvent_Screen = new Intent(getApplicationContext(), AddEvent_Screen.class);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int selectedId = item.getItemId();
        Intent intent = new Intent();

        if(selectedId == R.id.app_bar_search){

        }
        if(selectedId == R.id.calendar_app_bar_today){
            selectedDate = today;
            prev=0;
            next=1;
            setMonthView();
        }

        return true;
    }
}








