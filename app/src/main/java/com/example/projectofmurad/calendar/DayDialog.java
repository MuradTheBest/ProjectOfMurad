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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.RecyclerViewSwipeDecorator;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The type Day dialog.
 */
public class DayDialog extends AppCompatDialog implements EventsAdapter.OnEventClickListener{

    /**
     * The Passing date.
     */
    public final LocalDate passingDate;

    private FloatingActionButton fab_add_event;

    private RecyclerView rv_events;
    private EventsAdapter eventsAdapter;

    /**
     * The constant ACTION_TO_SHOW_EVENT.
     */
    public static final String ACTION_TO_SHOW_EVENT = Utils.APPLICATION_ID + "to show event";

    private final String eventPrivateId;

    private final Context context;

    /**
     * Instantiates a new Day dialog.
     *
     * @param context        the context
     * @param passingDate    the passing date
     * @param eventPrivateId the event private id
     */
    public DayDialog(@NonNull Context context, LocalDate passingDate, String eventPrivateId) {
        super(context);
        this.context = context;
        this.passingDate = passingDate;

        Utils.createCustomDialog(this);

        this.eventPrivateId = eventPrivateId;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
        }
    }

    /**
     * Gets passing date.
     *
     * @return the passing date
     */
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

        TextView tv_day = findViewById(R.id.tv_day);
        tv_day.setText(day);

        TextView tv_full_date = findViewById(R.id.tv_full_date);
        tv_full_date.setText(full_date);

        rv_events = findViewById(R.id.rv_events);

        TextView tv_no_events = findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        Query eventsDatabase = FirebaseUtils.getEventsForDateRef(passingDate).orderByValue();

        DatabaseReference allEventsDatabase = FirebaseUtils.getAllEventsDatabase();

        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setIndexedQuery(eventsDatabase, allEventsDatabase, CalendarEvent.class)
                .build();

        eventsAdapter = new EventsAdapter(options, passingDate, context, this);
        rv_events.setAdapter(eventsAdapter);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(context);
        layoutManager.setOnLayoutCompleteListener(() -> {
            rv_events.setVisibility(eventsAdapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
            tv_no_events.setVisibility(eventsAdapter.getItemCount() > 0 ? View.INVISIBLE : View.VISIBLE);
            showEvent(eventPrivateId);
        });

        rv_events.setLayoutManager(layoutManager);

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

        fab_add_event = findViewById(R.id.fab_add_event);
        fab_add_event.setVisibility(View.VISIBLE);
        fab_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddEvent_Screen = new Intent(context, AddOrEditEventScreen.class);

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

        FirebaseUtils.isMadrich().observe((LifecycleOwner) context,
                isMadrich -> fab_add_event.setVisibility(isMadrich ? View.VISIBLE : View.GONE));

        itemTouchHelper.attachToRecyclerView(rv_events);
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    protected void onStart() {
        super.onStart();
        eventsAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    protected void onStop() {
        super.onStop();
        eventsAdapter.stopListening();
    }

    /**
     * Show event.
     *
     * @param eventPrivateId the event private id
     */
    public void showEvent(String eventPrivateId){

        Log.d(Utils.LOG_TAG, "mainViewModel event_private_id in DayDialog is " + eventPrivateId);
        if (eventPrivateId == null){
            return;
        }

        int position = -1;

        int i = 0;
        for (CalendarEvent calendarEvent : eventsAdapter.getSnapshots()) {
            if (calendarEvent.getPrivateId().equals(eventPrivateId)){
                position = i;
                Log.d(Utils.LOG_TAG, "mainViewModel position = " + position);
                break;
            }
            i++;
        }

        if (rv_events.findViewHolderForAdapterPosition(position) != null && position > -1) {
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

    private final Vibrator vibrator;
    private VibrationEffect vibrationEffect;

    /**
     * The Simple callback.
     */
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END | ItemTouchHelper.START) {
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

            final EventsAdapter.EventViewHolder eventViewHolder
                    = (EventsAdapter.EventViewHolder) viewHolder;

            vibrator.vibrate(vibrationEffect);

            switch (direction) {
                case ItemTouchHelper.START:

                    eventViewHolder.switch_alarm.performClick();
                    Snackbar.make(context, rv_events, alarm, Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> eventViewHolder.switch_alarm.performClick())
                            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

                    break;
                case ItemTouchHelper.END :

                    eventViewHolder.cb_all_attendances.performClick();
                    Snackbar.make(context, rv_events, attendance, Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> eventViewHolder.cb_all_attendances.performClick())
                            /*.setGestureInsetBottomIgnored(true)*/
                            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();

                    break;
            }

            itemTouchHelper.startSwipe(viewHolder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            final EventsAdapter.EventViewHolder eventViewHolder
                    = (EventsAdapter.EventViewHolder) viewHolder;

            boolean approved = eventViewHolder.cb_all_attendances.isChecked();
            boolean alarmSet = eventViewHolder.switch_alarm.isChecked();

            alarm = alarmSet ? "Alarm cancelling" : "Alarm setting";
            attendance = approved ? "Attendance disapproving" : "Attendance approving";

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, alarmSet ? R.color.alarm_off : R.color.colorAccent))
                    .addSwipeLeftActionIcon(alarmSet ? R.drawable.ic_baseline_notifications_off_40 : R.drawable.ic_baseline_notifications_active_40)
                    .addSwipeLeftLabel(alarm)
                    .setSwipeLeftLabelColor(ContextCompat.getColor(context, android.R.color.white))
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .addSwipeRightBackgroundColor(approved ? Color.RED : Color.GREEN)
                    .addSwipeRightActionIcon(approved ? R.drawable.ic_baseline_assignment_late_40 : R.drawable.ic_baseline_assignment_turned_in_40)
                    .addSwipeRightLabel(attendance)
                    .setSwipeRightLabelColor(ContextCompat.getColor(context, android.R.color.white))
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 12)
                    .setSwipeRightLabelTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setActionIconTint(ContextCompat.getColor(context, android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }
    };

    /**
     * The Item touch helper.
     */
    final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent event) {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();

        if (fm.findFragmentByTag(EventInfoDialogFragment.TAG) == null){
            EventInfoDialogFragment event_info_dialogFragment = EventInfoDialogFragment.newInstance(event, true);
            event_info_dialogFragment.show(fm, EventInfoDialogFragment.TAG);
        }
    }
}
