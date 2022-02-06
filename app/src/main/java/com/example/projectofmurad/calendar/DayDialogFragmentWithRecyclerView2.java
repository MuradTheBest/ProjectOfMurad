package com.example.projectofmurad.calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DayDialogFragmentWithRecyclerView2 extends Dialog implements EventsAdapterForFirebase.OnEventListener {
    private LocalDate passingDate;
    private Context context;

    private ArrayList<CalendarEventWithTextOnly2FromSuper> calendarEventArrayList;

    private FloatingActionButton fab_add_event;

    private RecyclerView rv_events;
    private EventsAdapterForFirebase adapterForFirebase;

    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    public DayDialogFragmentWithRecyclerView2(@NonNull Context context, LocalDate passingDate) {
        super(context);
        this.passingDate = passingDate;
        this.context = context;

        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
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
        String full_date = passingDate.format(DateTimeFormatter.ofPattern("E, MMMM yyyy", Utils_Calendar.locale));

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

        eventsDatabase = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(passingDate));
        Query query = eventsDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<CalendarEventWithTextOnly2FromSuper> options
                = new FirebaseRecyclerOptions.Builder<CalendarEventWithTextOnly2FromSuper>()
                .setQuery(query, CalendarEventWithTextOnly2FromSuper.class)
                .setLifecycleOwner((LifecycleOwner) this.getOwnerActivity())
                .build();


        adapterForFirebase = new EventsAdapterForFirebase(options, passingDate, context, this);
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);
        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());
        rv_events.setLayoutManager(new LinearLayoutManager(context));

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
                Intent toAddEvent_Screen = new Intent(context, Add_Event_Screen.class);

                DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
                Log.d("murad","passingDate " + passingDate.format(simpleDateFormat));

                int day = passingDate.getDayOfMonth();
                int month = passingDate.getMonth().getValue();
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

    @Override
    public void dismiss() {
        super.dismiss();
        adapterForFirebase.stopListening();
    }

    @Override
    public void onEventClick(int position, CalendarEventWithTextOnly2FromSuper calendarEventWithTextOnly) {
        this.dismiss();
        String chain_key = calendarEventWithTextOnly.getEvent_chain_id();
        String private_key = calendarEventWithTextOnly.getEvent_private_id();

        int timestamp = calendarEventWithTextOnly.getTimestamp();
        String name = calendarEventWithTextOnly.getName();
        String description = calendarEventWithTextOnly. getDescription();
        String place = calendarEventWithTextOnly.getPlace();
        int color = calendarEventWithTextOnly.getColor();
        String start_date = calendarEventWithTextOnly.getStart_date();
        String start_time = calendarEventWithTextOnly.getStart_time();
        String end_date = calendarEventWithTextOnly.getEnd_date();
        String end_time = calendarEventWithTextOnly.getEnd_time();

        Intent intent = new Intent(context, Edit_Event_Screen.class);

        intent.putExtra("event_chain_key", chain_key);
        intent.putExtra("event_private_key", private_key);

        intent.putExtra("event_name", name);
        intent.putExtra("event_description", description);
        intent.putExtra("event_place", place);
        intent.putExtra("event_color", color);
        intent.putExtra("event_start_date", start_date);
        intent.putExtra("event_start_time", start_time);
        intent.putExtra("event_end_date", end_date);
        intent.putExtra("event_end_time", end_time);

        getContext().startActivity(intent);
    }
}
