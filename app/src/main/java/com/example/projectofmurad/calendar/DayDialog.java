package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.BuildConfig;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.RecyclerViewSwipeDecorator;
import com.example.projectofmurad.helpers.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DayDialog extends AppCompatDialog implements EventsAdapterForFirebase.OnEventClickListener{

    public final LocalDate passingDate;
    private final Context context;

    private FloatingActionButton fab_add_event;
    private Button btn_clear_all;

    private RecyclerView rv_events;
    private EventsAdapterForFirebase adapterForFirebase;

    private FirebaseRecyclerOptions<CalendarEvent> options;

    public static final String ACTION_TO_SHOW_EVENT = BuildConfig.APPLICATION_ID + "to show event";

    private final FragmentManager fm;

    private final String event_private_id;

    public DayDialog(@NonNull Context context, LocalDate passingDate, String event_private_id) {
        super(context);
        this.passingDate = passingDate;

        Log.d(Utils.LOG_TAG, "passingDate is " + CalendarUtils.DateToTextOnline(passingDate));

        this.context = context;

        getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);

        this.fm = ((FragmentActivity) context).getSupportFragmentManager();

        this.event_private_id = event_private_id;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
        }
    }

    public LocalDate getPassingDate() {
        return passingDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_dialog);
        setCancelable(true);

        String day = String.valueOf(passingDate.getDayOfMonth());
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy", CalendarUtils.getLocale()));

        MaterialTextView tv_day = findViewById(R.id.tv_day);
        tv_day.setText(day);

        MaterialTextView tv_full_date = findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        rv_events = findViewById(R.id.rv_events);

        MaterialTextView tv_no_events = findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        Query eventsDatabase = FirebaseUtils.getEventsDatabase().child(CalendarUtils.DateToTextForFirebase(passingDate))
                .orderByValue();

        DatabaseReference allEventsDatabase = FirebaseUtils.getAllEventsDatabase();

        options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setIndexedQuery(eventsDatabase, allEventsDatabase, CalendarEvent.class)
                .setLifecycleOwner((LifecycleOwner) context)
                .build();

        adapterForFirebase = new EventsAdapterForFirebase(options, passingDate, context, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);

        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(getContext());
        layoutManager.setOnLayoutCompleteListener(() -> showEvent(event_private_id));

        rv_events.setLayoutManager(new LinearLayoutManagerWrapper(getContext())
                .addOnLayoutCompleteListener(() -> showEvent(event_private_id)));

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
                if(snapshot.exists()){
                    rv_events.setVisibility(View.VISIBLE);
                }
                else {
                    rv_events.setVisibility(View.INVISIBLE);
                    tv_no_events.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab_add_event = findViewById(R.id.fab_add_event);
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

        btn_clear_all = findViewById(R.id.btn_clear_all);
        btn_clear_all.setOnClickListener(v -> {
            FirebaseUtils.getEventsDatabase().child(CalendarUtils.DateToTextForFirebase(passingDate)).setValue(null);

            rv_events.setVisibility(View.INVISIBLE);
            Log.d("murad", "Visibility set to " + rv_events.getVisibility());
            tv_no_events.setVisibility(View.VISIBLE);
        });

        itemTouchHelper.attachToRecyclerView(rv_events);
    }

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

    public void showEvent(String event_private_id){

        Log.d(Utils.LOG_TAG, "mainViewModel event_private_id in DayDialog is " + event_private_id);
        if (event_private_id == null){
            return;
        }

        int position = -1;

        int i = 0;
        for (CalendarEvent calendarEvent : options.getSnapshots()){
            if (calendarEvent.getPrivateId().equals(event_private_id)){
                position = i;
                Log.d(Utils.LOG_TAG, "mainViewModel position = " + position);
            }
            i++;
        }

        if (rv_events.findViewHolderForAdapterPosition(position) != null && position > -1){
            View event = rv_events.findViewHolderForAdapterPosition(position).itemView;

            Log.d(Utils.LOG_TAG, "mainViewModel pressing event");

            Runnable unpressRunnable = () -> event.setPressed(false);

            Runnable pressRunnable = () -> {
                event.setPressed(true);
                event.postOnAnimationDelayed(unpressRunnable, 1000);
            };

            new Handler().postDelayed(pressRunnable, 500);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        adapterForFirebase.stopListening();
    }

    private final Vibrator vibrator;
    private VibrationEffect vibrationEffect;

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

        String attendance = "";
        String alarm = "";

        @SuppressLint("MissingPermission")
        
        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            vibrator.vibrate(vibrationEffect);

            switch (direction) {
                case ItemTouchHelper.START:

                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.performClick();

                    Log.d("murad", "swipe ******************************************************");
                    Log.d("murad", "swipe  switch_alarm is " + ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.isChecked());
                    Log.d("murad", "swipe ******************************************************");
                    Snackbar.make(context, rv_events, alarm, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.performClick();
                                }
                            })
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
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
                            })
                            /*.setGestureInsetBottomIgnored(true)*/
                            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();

                    break;
            }

            itemTouchHelper.startSwipe(viewHolder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            boolean approved = ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).checkbox_all_attendances.isChecked();
            Log.d("murad", "swipe approved is " + approved);
            boolean alarmSet = ((EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder).switch_alarm.isChecked();
            Log.d("murad", "swipe alarmSet is " + alarmSet);

            alarm = alarmSet ? "Alarm cancelling" : "Alarm setting";
            attendance = approved ? "Attendance disapproving" : "Attendance approving";

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, (EventsAdapterForFirebase.EventViewHolderForFirebase) viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), alarmSet ? R.color.alarm_off : R.color.colorAccent))
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
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent event) {
        if (fm.findFragmentByTag(EventInfoDialogFragment.TAG) == null){
            EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(event, true);
            event_info_dialogFragment.show(fm, EventInfoDialogFragment.TAG);
        }
    }
}
