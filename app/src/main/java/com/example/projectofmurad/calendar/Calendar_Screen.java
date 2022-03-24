package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.MainActivity;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Calendar_Screen extends AppCompatActivity implements CalendarAdapter.CalendarOnItemListener,
        AdapterView.OnItemSelectedListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    private ArrayList<LocalDate> daysInMonth;
    private CalendarAdapter calendarAdapter;

    private ConstraintLayout ll_calendar_view;
//    private LinearLayout ll_calendar_view;

    private int positionOfToday = 0;
    private Intent intent_for_previous;

    private LocalDate today;

    private int prev = 0;
    private int next = 1;

    private int dayOfWeek;
    private int length;

    private int old_position = 0;

    private final String[] days = UtilsCalendar.getShortDaysOfWeek();

    private TextView tv_Sunday;
    private TextView tv_Monday;
    private TextView tv_Tuesday;
    private TextView tv_Wednesday;
    private TextView tv_Thursday;
    private TextView tv_Friday;
    private TextView tv_Saturday;

    public static final String action_to_find_today = BuildConfig.APPLICATION_ID + "to find today";

    public static final String action_to_change_previous = BuildConfig.APPLICATION_ID + "action_to_change_previous";
    public static final String action_to_change_next = BuildConfig.APPLICATION_ID + "action_to_change_next";

    BroadcastReceiver broadcastReceiver;

    private Intent gotten_intent;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_screen);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.calendar);

        today = LocalDate.now();

        gotten_intent = getIntent();

        int selected_day = gotten_intent.getIntExtra("day", today.getDayOfMonth());
        int selected_month = gotten_intent.getIntExtra("month", today.getMonthValue());
        int selected_year = gotten_intent.getIntExtra("year", today.getYear());

        selectedDate = LocalDate.of(selected_year, selected_month, selected_day);

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        monthYearText.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Calendar_Screen.this, AlertDialog.THEME_HOLO_LIGHT);
            datePickerDialog.getDatePicker().setBackgroundColor(Color.TRANSPARENT);
            datePickerDialog.updateDate(selected_year, selected_month, selected_day);
            datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
            datePickerDialog.setOnDateSetListener((view1, year, month, day) -> {
                month = month + 1;
                selectedDate = LocalDate.of(year, month, day);
                prev = 0;
                next = 1;
                setMonthView();
            });
            datePickerDialog.show();
        });


/*        LocalDate now = LocalDate.now();
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd LLLL", UtilsCalendar.locale);

        monthYearText.setText(now.format(simpleDateFormat));*/

        ll_calendar_view = findViewById(R.id.ll_calendar_view);

        Toast.makeText(getApplicationContext(), selectedDate.getDayOfWeek().toString() + " is " + selectedDate.getDayOfWeek().getValue(), Toast.LENGTH_SHORT).show();

        Log.d("murad","today is " + UtilsCalendar.DateToTextOnline(today));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch(action) {
                    case action_to_find_today:
                        if (calendarRecyclerView == null){
                            return;
                        }
                        Log.d("murad", "broadcastReceiver for today triggered");
                        int pos = intent.getIntExtra("today", 0);

                        calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                findViewById(R.id.cellDayText).setBackgroundResource(R.drawable.calendar_cell_text_today_background);

                        calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                setBackgroundResource(R.drawable.calendar_cell_selected_background);

                        int textColor = ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(pos).
                                itemView.findViewById(R.id.cellDayText)).getCurrentTextColor();

                        if(textColor == Color.RED || textColor == Color.BLUE){
                            calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                    findViewById(R.id.cellDayText).getBackground().setTint(textColor);

                            ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(pos).itemView.
                                    findViewById(R.id.cellDayText)).setTextColor(Color.WHITE);
                        }

                        old_position = pos;
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

                            int finalColor = Color.GRAY | ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).getCurrentTextColor();

/*                            if(((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).getCurrentTextColor() == Color.RED){

                                finalColor = finalColor | Color.RED;
                            }*/

                            ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).setTextColor(finalColor);

                        }

                        for(int i = length - next; i < length; i++) {
                            int finalColor = Color.GRAY | ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).getCurrentTextColor();

/*                            if(((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).getCurrentTextColor() == Color.RED){

                                finalColor = finalColor | Color.RED;
                            }*/

                            ((TextView) calendarRecyclerView.findViewHolderForAdapterPosition(i).
                                    itemView.findViewById(R.id.cellDayText)).setTextColor(finalColor);


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

        String day = "" + selectedDate.getDayOfMonth();
        Toast.makeText(getApplicationContext(), day, Toast.LENGTH_SHORT).show();
        initAllDaysOfWeek();
        setMonthView();

        if (gotten_intent.getAction() != null && gotten_intent.getAction().equals(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT)){

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.cancel();
            action = gotten_intent.getBooleanExtra("action", false);
            String event_private_id = gotten_intent.getStringExtra("event_private_id");
            if (action){
                Toast.makeText(this, "Opening from notification", Toast.LENGTH_SHORT).show();
//            onItemClick(positionOfToday, "" + today.getDayOfMonth(), today);
                Log.d("murad", "===============================================");
                Log.d("murad", "event_private_id = " + event_private_id);
                Log.d("murad", "===============================================");

                long start = gotten_intent.getLongExtra("start_time", Calendar.getInstance().getTimeInMillis());
                LocalDate goTo = CalendarEvent.getDate(start);
                Log.d("murad", "start is " + UtilsCalendar.DateToTextLocal(CalendarEvent.getDate(start)));
                selectedDate = goTo;
                prev = 0;
                next = 1;
                direction = 0;
                setMonthView();
                new Handler().postDelayed(() -> createDayDialog(goTo, event_private_id), 500);

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        if (extras != null){
            action = extras.getBoolean("action", false);
            String event_private_id = extras.getString("event_private_id");
            if (action){
                Toast.makeText(this, "Opening from notification", Toast.LENGTH_SHORT).show();
//            onItemClick(positionOfToday, "" + today.getDayOfMonth(), today);
                Log.d("murad", "===============================================");
                Log.d("murad", "event_private_id = " + event_private_id);
                Log.d("murad", "===============================================");
                new Handler().postDelayed(() -> createDayDialog(today, event_private_id), 500);

            }
        }
    }

    public boolean action;

    public void sendAlarm(View view) {
        /*FirebaseUtils.allEventsDatabase.orderByChild("time").limitToLast(1).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()){
                            if (data.exists()){
                                CalendarEvent event = data.getValue(CalendarEvent.class);
                                event.updateStart_date(LocalDate.now());
                                event.updateStart_time(LocalTime.now().plusMinutes(1));
                                event.setColor(Color.RED);

                                Log.d("murad", event.toString());

                                AlarmManagerForToday.addAlarm(Calendar_Screen.this, event, 0);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

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

        AlarmManagerForToday.addAlarm(Calendar_Screen.this, calendarEvent, 0);
        FirebaseUtils.eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(calendarEvent.receiveStart_date()))
                .child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        FirebaseUtils.allEventsDatabase.child(calendarEvent.getPrivateId()).setValue(calendarEvent);

        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.set(atomicInteger.get()+1);
        Log.d("murad", "atomicInteger = " + atomicInteger);
    }

    public interface OnEventShowListener{
        void onEventShow(String event_private_id);
    }

    private void initAllDaysOfWeek(){
        tv_Sunday = findViewById(R.id.tv_Sunday);
        tv_Sunday.setText(days[6]);

        tv_Monday = findViewById(R.id.tv_Monday);
        tv_Monday.setText(days[0]);

        tv_Tuesday = findViewById(R.id.tv_Tuesday);
        tv_Tuesday.setText(days[1]);

        tv_Wednesday = findViewById(R.id.tv_Wednesday);
        tv_Wednesday.setText(days[2]);

        tv_Thursday = findViewById(R.id.tv_Thursday);
        tv_Thursday.setText(days[3]);

        tv_Friday = findViewById(R.id.tv_Friday);
        tv_Friday.setText(days[4]);

        tv_Saturday = findViewById(R.id.tv_Saturday);
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

        calendarAdapter = new CalendarAdapter(daysInMonth, this, this, selectedDate);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 7));

        animate(ll_calendar_view);

        intent_for_previous = new Intent(action_to_change_previous);
        intent_for_previous.putExtra("previous", prev);
        intent_for_previous.putExtra("next", next);

        Log.d("murad", "prev " + prev);
        Log.d("murad", "next " + next);

        //int i=0;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //int i=0;
                while (calendarRecyclerView.getChildCount() < daysInMonth.size()) {}

                Log.d("murad", positionOfToday + " " + calendarAdapter.getItemCount());
                Log.d("murad",
                        " recycler view's child count = " + calendarRecyclerView.getChildCount());

                if (selectedDate.getMonthValue() == today.getMonthValue()) {
                    Log.d("murad", "selectedDate == LocalDate.now()");
                    boolean stop = false;
                    for (int i = 0; i < length && !stop; i++) {
                        if (daysInMonth.get(i).equals(today)) {
                            Log.d("murad", "i: " + i);
                            positionOfToday = i;
                            stop = true;
                        }

                    }
                    Intent i = new Intent(action_to_find_today);
                    i.putExtra("today", positionOfToday);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                        intent_for_previous);
            }
        });
        thread.start();

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onItemClick(int position, @NonNull String dayText, LocalDate passingDate) {
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
                calendarRecyclerView.findViewHolderForAdapterPosition(old_position).
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

                calendarRecyclerView.findViewHolderForAdapterPosition(position).itemView.
                        bringToFront();
            }

            //String message = dayText + " " + monthYearFromDate(selectedDate);
            //Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            Log.d("murad", "child count after load = " + calendarRecyclerView.getChildCount());

            Handler handler = new Handler();
            handler.postDelayed(() -> createDayDialog(passingDate, null), duration);

        }
    }

    public OnEventShowListener onEventShowListener;

    private DayDialogFragmentWithRecyclerView2 dayDialogFragment;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createDayDialog(LocalDate passingDate, String event_private_id){
//        DayDialogFragmentWithRecyclerView2 dayDialogFragment = new DayDialogFragmentWithRecyclerView2(Calendar_Screen.this, passingDate, R.style.RoundedCornersDialog);
        if (dayDialogFragment != null && dayDialogFragment.isShowing()){
            dayDialogFragment.dismiss();
        }

        dayDialogFragment = new DayDialogFragmentWithRecyclerView2(Calendar_Screen.this, passingDate);
        dayDialogFragment.show();

        if (event_private_id != null){
            Log.d("murad", "event_private_id is not null");
            /*onEventShowListener = (OnEventShowListener) dayDialogFragment.getOwnerActivity();
            onEventShowListener.onEventShow(event_private_id);*/
            Intent i = new Intent(DayDialogFragmentWithRecyclerView2.ACTION_TO_SHOW_EVENT);
            i.putExtra("event_private_id", event_private_id);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dayDialogFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                dayDialogFragment.dismiss();
            }
            dayDialogFragment = null;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Calendar_Screen.this, MainActivity.class));
    }
}








