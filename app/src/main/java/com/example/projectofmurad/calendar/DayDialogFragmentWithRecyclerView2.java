package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.RecyclerViewSwipeDecorator;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class DayDialogFragmentWithRecyclerView2 extends Dialog implements
        EventsAdapterForFirebase.OnEventListener, Calendar_Screen.OnEventShowListener,
        EventsAdapterForFirebase.OnEventExpandListener{

    private LocalDate passingDate;
    private Context context;

    private ArrayList<CalendarEvent> calendarEventArrayList;

    private FloatingActionButton fab_add_event;
    private Button btn_clear_all;

    private RecyclerView rv_events;
    private EventsAdapterForFirebase adapterForFirebase;

    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    private FirebaseRecyclerOptions<CalendarEvent> options;

    private Animation scale;

    public static final String ACTION_TO_SHOW_EVENT = BuildConfig.APPLICATION_ID + "to show event";

    public DayDialogFragmentWithRecyclerView2(@NonNull Context context, LocalDate passingDate) {
        super(context);
        this.passingDate = passingDate;
        this.context = context;

        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
    }

    public DayDialogFragmentWithRecyclerView2(@NonNull Context context, LocalDate passingDate, int themeResId) {
        super(context, themeResId);
        this.passingDate = passingDate;
        this.context = context;

//        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
    }

    /*public DayDialogFragment(@NonNull Context context, LocalDate passingDate, int themeResId) {
        super(context, themeResId);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.day_dialog_with_recyclerview);
        this.setCancelable(true);

        String day = String.valueOf(passingDate.getDayOfMonth());
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy", UtilsCalendar.locale));

        TextView tv_day = this.findViewById(R.id.tv_day);
        tv_day.setText(day);

        TextView tv_full_date = this.findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        rv_events = this.findViewById(R.id.rv_events);

        TextView tv_no_events = this.findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = FirebaseUtils.eventsDatabase;

        calendarEventArrayList = new ArrayList<>();

        eventsDatabase = eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(passingDate));
        Query query = eventsDatabase.orderByChild("start");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Log.d("snapshot", "===============================================================");
                    Log.d("snapshot", data.getKey());
                    Log.d("snapshot", "=====================================================================");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(query, CalendarEvent.class)
//                .setIndexedQuery(, , CalendarEvent.class)
                .setLifecycleOwner((LifecycleOwner) this.getOwnerActivity())
                .build();


        adapterForFirebase = new EventsAdapterForFirebase(options, passingDate, context, this, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);
        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());
        rv_events.setLayoutManager(new LinearLayoutManager(context));
        rv_events.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // if the recycler view is scrolled
                // above hide the FAB
                if (dy > 3 && fab_add_event.isShown()) {
                    fab_add_event.hide();
                }

                // if the recycler view is
                // scrolled above show the FAB
                if (dy < -3 && !fab_add_event.isShown()) {
                    fab_add_event.show();
                }

                // of the recycler view is at the first
                // item always show the FAB
                if (!recyclerView.canScrollVertically(-1)) {
                    fab_add_event.show();
                }
            }
        });

        eventsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    rv_events.setVisibility(View.VISIBLE);
                    Log.d("murad", "Visibility set to " + rv_events.getVisibility());
                }
                else {
                    rv_events.setVisibility(View.INVISIBLE);
                    Log.d("murad", "Visibility set to " + rv_events.getVisibility());
                    tv_no_events.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fab_add_event = this.findViewById(R.id.fab_add_event);
        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createAddEventDialog(dayText, selectedDate);
                Intent toAddEvent_Screen = new Intent(context, Edit_Event_Screen.class);

                DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
                Log.d("murad","passingDate " + passingDate.format(simpleDateFormat));

                int day = passingDate.getDayOfMonth();
                int month = passingDate.getMonthValue();
                int year = passingDate.getYear();

                toAddEvent_Screen.putExtra("day", day);
                toAddEvent_Screen.putExtra("month", month);
                toAddEvent_Screen.putExtra("year", year);

                int cx = (int) (fab_add_event.getWidth()/2 + fab_add_event.getX());
                int cy = (int) (fab_add_event.getHeight()/2 + fab_add_event.getY());

                toAddEvent_Screen.putExtra("cx", cx);
                toAddEvent_Screen.putExtra("cy", cy);

                context.startActivity(toAddEvent_Screen);
//                dismiss();
            }
        });

        btn_clear_all = this.findViewById(R.id.btn_clear_all);
        btn_clear_all.setOnClickListener(v -> {
            FirebaseUtils.eventsDatabase
                    .child(UtilsCalendar.DateToTextForFirebase(passingDate)).setValue(null);

            rv_events.setVisibility(View.INVISIBLE);
            Log.d("murad", "Visibility set to " + rv_events.getVisibility());
            tv_no_events.setVisibility(View.VISIBLE);
        });


//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_events);

        scale = AnimationUtils.loadAnimation(getContext(), R.anim.scale);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {
                String action = intent.getAction();
                int position = 0;
/*
                if (ACTION_TO_SHOW_EVENT.equals(action)) {
                    Log.d("murad", "broadcastReceiver for today triggered");
                    String event_private_id = intent.getStringExtra("event_private_id");
//                        View view = rv_events.findViewWithTag(event_private_id);
//                        view.getBackground().setTint(Color.BLACK);
//
//                        int position = rv_events.getChildViewHolder(view).getAbsoluteAdapterPosition();

                    Log.d("murad", "event_private_id is " + event_private_id);


                    */
/*for (int i = 0; i < options.getSnapshots().size(); i++) {
                        if (options.getSnapshots().get(i).getPrivateId().equals(
                                event_private_id)) {
                            Log.d("murad", options.getSnapshots().get(i).toString());
                            position = i;
                            break;
                        }
                    }*//*

//        rv_events.getChildViewHolder(view);
//        rv_events.getChildViewHolder(view).itemView.getBackground().setTint(Color.WHITE);
                    */
/*rv_events.smoothScrollToPosition(position);
                    *//*
*/
/*rv_events.findViewHolderForAdapterPosition(
                            position).itemView.getBackground().setTint(Color.WHITE);*//*
*/
/*

                    rv_events.findViewHolderForAdapterPosition(
                            position).itemView.startAnimation(scale);
                    rv_events.startLayoutAnimation();*//*


                    int pos = rv_events.getChildViewHolder(rv_events.findViewWithTag(event_private_id)).getAbsoluteAdapterPosition();
                    rv_events.smoothScrollToPosition(pos);
                    Log.d("murad", "notification event_private_id is " + event_private_id);
                    Log.d("murad", "notification position is " + pos);
                    new Handler().postDelayed(() ->
                            rv_events.findViewWithTag(event_private_id).getBackground().setTint(Color.TRANSPARENT), 300);

                }
*/
            }
        };

        // registering the specialized custom BroadcastReceiver in LocalBroadcastManager to receive custom broadcast.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TO_SHOW_EVENT);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);

    }

    RecyclerView.ItemAnimator itemAnimator = new SimpleItemAnimator() {
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX,
                                   int toY) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder,
                                     RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop,
                                     int toLeft, int toTop) {
            return false;
        }

        @Override
        public void runPendingAnimations() {

        }

        @Override
        public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    };


/*
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab_add_event.getX() + (fab_add_event.getWidth()/2));
        int cy = (int) (fab_add_event.getY())+ fab_add_event.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }
*/

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    protected void onStart() {
        super.onStart();
        adapterForFirebase.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    protected void onStop() {
        super.onStop();
        adapterForFirebase.stopListening();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        adapterForFirebase.stopListening();
    }

    Vibrator vibrator;
    VibrationEffect vibrationEffect;

    boolean at_the_time = false;
    boolean before_5_minutes = false;
    boolean before_15_minutes = false;
    boolean before_30_minutes = false;
    boolean before_1_hour = false;
    boolean custom = false;


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END | ItemTouchHelper.START) {
        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
//            return super.getSwipeThreshold(viewHolder);
            return 0.5f;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        String text = "";
        String attendance = "";
        String alarm = "";

        @SuppressLint("MissingPermission")
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAbsoluteAdapterPosition();

            vibrator.vibrate(vibrationEffect);

            switch (direction) {
                case ItemTouchHelper.START:

                    /*if (!text.isEmpty()){
                        long before = 0;
                        *//*switch (text){
                            case "5 minutes before":
                                Log.d("murad", "swipe text is " + text);
                                before = 5 * 60 * 1000;
                                break;
                            case "15 minutes before":
                                Log.d("murad", "swipe text is " + text);
                                before = 15 * 60 * 1000;
                                break;
                            case "30 minutes before":
                                Log.d("murad", "swipe text is " + text);
                                before = 30 * 60 * 1000;
                                break;
                            case "1 hour before":
                                Log.d("murad", "swipe text is " + text);
                                before = 60 * 60 * 1000;
                                break;
                            case "Custom":
                                Log.d("murad", "swipe text is " + text);
                                ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.setChecked(true);
                                break;
                        }*//*

                        if (before_5_minutes) {
                            Log.d("murad", "swipe text is " + text);
                            before = 5 * 60 * 1000;
                        }
                        else if (before_15_minutes) {
                            Log.d("murad", "swipe text is " + text);
                            before = 15 * 60 * 1000;
                        }
                        else if (before_30_minutes) {
                            Log.d("murad", "swipe text is " + text);
                            before = 30 * 60 * 1000;
                        }
                        else if (before_1_hour) {
                            Log.d("murad", "swipe text is " + text);
                            before = 60 * 60 * 1000;
                        }
                        else if (custom) {
                            Log.d("murad", "swipe text is " + text);
                            ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.setChecked(true);
                        }


                        CalendarEvent event = options.getSnapshots().get(viewHolder.getAbsoluteAdapterPosition());

                        AlarmManagerForToday.addAlarm(context, event, before);
                    }
                    else
                        ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.toggle();
*/

                    /*long before = 0;

                    if (before_5_minutes) {
                        Log.d("murad", "swipe text is " + text);
                        before = 5 * 60 * 1000;
                        CalendarEvent event = options.getSnapshots().get(viewHolder.getAbsoluteAdapterPosition());

                        AlarmManagerForToday.addAlarm(context, event, before);
                    }
                    else if (before_15_minutes) {
                        Log.d("murad", "swipe text is " + text);
                        before = 15 * 60 * 1000;
                        CalendarEvent event = options.getSnapshots().get(viewHolder.getAbsoluteAdapterPosition());

                        AlarmManagerForToday.addAlarm(context, event, before);
                    }
                    else if (before_30_minutes) {
                        Log.d("murad", "swipe text is " + text);
                        before = 30 * 60 * 1000;
                        CalendarEvent event = options.getSnapshots().get(viewHolder.getAbsoluteAdapterPosition());

                        AlarmManagerForToday.addAlarm(context, event, before);
                    }
                    else if (before_1_hour) {
                        Log.d("murad", "swipe text is " + text);
                        before = 60 * 60 * 1000;
                        CalendarEvent event = options.getSnapshots().get(viewHolder.getAbsoluteAdapterPosition());

                        AlarmManagerForToday.addAlarm(context, event, before);
                    }
                    else if (custom) {
                        Log.d("murad", "swipe text is " + text);
                        ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.setChecked(true);
                    }
                    else {
                        ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.toggle();
                    }*/

                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.toggle();

                    Log.d("murad", "swipe ******************************************************");
                    Log.d("murad", "swipe  switch_alarm is " + ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.isChecked());
                    Log.d("murad", "swipe ******************************************************");
                    Snackbar.make(context, rv_events, alarm, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.toggle();
                                }
                            }).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                    break;
                case ItemTouchHelper.END :

                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).checkbox_all_attendances.toggle();
                    Log.d("murad", "swipe ***********************************************************");
                    Log.d("murad", "swipe checkbox_all_attendances is " + ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).checkbox_all_attendances.isChecked());
                    Log.d("murad", "swipe ***********************************************************");
                    Snackbar.make(context, rv_events, attendance, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).checkbox_all_attendances.toggle();
                                }
                            }).setGestureInsetBottomIgnored(true).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();

                    break;

            }
//            rv_events.getAdapter().notifyItemChanged(position);
            itemTouchHelper.startSwipe(viewHolder);

        }

        float last_Dx = 0;

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            boolean approved = ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).checkbox_all_attendances.isChecked();
            Log.d("murad", "swipe approved is " + approved);
            boolean alarmSet = ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.isChecked();
            Log.d("murad", "swipe alarmSet is " + alarmSet);

            float xColor = dX;
            int swipeLeftBackgroundColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
//            swipeLeftBackgroundColor = ColorUtils.blendARGB(swipeLeftBackgroundColor, Color.WHITE, 1-dX/1000);
            swipeLeftBackgroundColor = ColorUtils.blendARGB(swipeLeftBackgroundColor, Color.WHITE, -dX/1000);

//            text = "";

            if (dX < 0 && !alarmSet){
                if (-dX != 0){
                    dX += -50;
                }
            /*if (-dX < 100){
                text = "At time of the event";
            }
            else if (-dX > 100 && -dX < 200){
                text = "5 minutes before";
            }
            else if (-dX > 200 && -dX < 300){
                text = "15 minutes before";
            }
            else if (-dX > 300 && -dX < 400){
                text = "30 minutes before";
            }
            else if (-dX > 400 && -dX < 500){
                text = "1 hour before";
            }
            else if (-dX > 500){
                text = "custom";
            }*/
                if (-last_Dx < -dX){
                    resetAllBooleans();
                }
                if (-dX < 100){
                    text = "At the time of event";
                    at_the_time = true;
                    Log.d("murad", "swipe text label is " + text);
                }
                else if (-dX > 100 && -dX < 330){
                    text = "5 minutes"/* + " before"*/;
                    before_5_minutes = true;
                    Log.d("murad", "swipe text label is " + text);
                }
                else if (-dX > 330 && -dX < 390){
                    text = "15 minutes"/* + " before"*/;
                    before_15_minutes = true;
                    Log.d("murad", "swipe text label is " + text);
                }
                else if (-dX > 390 && -dX < 450){
                    before_30_minutes = true;
                    text = "30 minutes"/* + " before"*/;
                    Log.d("murad", "swipe text label is " + text);
                }
                else if (-dX > 450 && -dX < 510){
                    before_1_hour = true;
                    text = "1 hour"/* + " before"*/;
                    Log.d("murad", "swipe text label is " + text);
                }
                else if (-dX > 510){
                    custom = true;
                    text = "Custom";
                    Log.d("murad", "swipe text label is " + text);
                }

                Log.d("murad","*****************************swipe*********************************");
                Log.d("murad","swipe at_the_time is " + at_the_time);
                Log.d("murad","swipe before_5_minutes is " + before_5_minutes);
                Log.d("murad","swipe before_15_minutes is " + before_15_minutes);
                Log.d("murad","swipe before_30_minutes is " + before_30_minutes);
                Log.d("murad","swipe before_1_hour is " + before_1_hour);
                Log.d("murad", "swipe custom is " + custom);


                int x = (int) -dX/2;
                if (x > 0 && x < 255){
//                swipeLeftBackgroundColor = ColorUtils.setAlphaComponent(swipeLeftBackgroundColor, x);
                }
            }

            alarm = alarmSet ? "Alarm cancelling" : text;
            attendance = approved ? "Attendance disapproving" : "Attendance approving";


            Log.d("murad", "swipe dX = " + dX);
            Log.d("murad", "swipe last_Dx " + last_Dx);
            Log.d("murad","*********************************swipe*****************************");
            last_Dx = dX;

            new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, (EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder, dX, dY, actionState, isCurrentlyActive)
//                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), alarmSet ? R.color.alarm_off : R.color.colorAccent))
                    .addSwipeLeftBackgroundColor(alarmSet ? ContextCompat.getColor(getContext(), R.color.alarm_off) : swipeLeftBackgroundColor)
                    .addSwipeLeftActionIcon(alarmSet ? R.drawable.ic_baseline_notifications_off_40 : R.drawable.ic_baseline_notifications_active_40)
                    .addSwipeLeftLabel(alarm)
                    .setSwipeLeftLabelColor(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .addSwipeRightBackgroundColor(approved ? Color.RED : Color.GREEN)
                    .addSwipeRightActionIcon(approved ? R.drawable.ic_baseline_assignment_late_40 : R.drawable.ic_baseline_assignment_turned_in_40)
                    .addSwipeRightLabel(attendance)
                    .setSwipeRightLabelColor(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeRightLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType,
                                         float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }
    };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

    public void resetAllBooleans(){
        at_the_time = false;
        before_5_minutes = false;
        before_15_minutes = false;
        before_30_minutes = false;
        before_1_hour = false;
        custom = false;
    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent event) {
//        this.dismiss();

        Intent intent = new Intent(context, Edit_Event_Screen.class);
        intent.putExtra("event", event);

        getContext().startActivity(intent);
    }

    @Override
    public void onEventShow(String event_private_id) {
        View view = rv_events.findViewWithTag(event_private_id);
        view.getBackground().setTint(Color.BLACK);

        int position = rv_events.getChildViewHolder(view).getAbsoluteAdapterPosition();

        Log.d("murad", "event_private_id is " + event_private_id);


        for (int i = 0; i < options.getSnapshots().size(); i++) {
            if(options.getSnapshots().get(i).getPrivateId().equals(event_private_id)){
                Log.d("murad", options.getSnapshots().get(i).toString());
                position = i;
                break;
            }
        }


//        rv_events.getChildViewHolder(view);
//        rv_events.getChildViewHolder(view).itemView.getBackground().setTint(Color.WHITE);
        rv_events.scrollToPosition(position);
        rv_events.findViewHolderForAdapterPosition(position).itemView.getBackground().setTint(Color.WHITE);

    }

    @Override
    public void onEventExpand(int position, boolean expanded) {

    }

}
