package com.example.projectofmurad.tracking;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventsAdapterForFirebase;
import com.example.projectofmurad.helpers.CalendarUtils;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.training.Training;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

public class ChooseEventClickDialog extends AppCompatDialog implements EventsAdapterForFirebase.OnEventClickListener{

    private final LocalDate passingDate;
    private final Context context;

    private RecyclerView rv_events;

    private final Training training;

    private final SaveTrainingDialog.OnAddTrainingListener onAddTrainingListener;

    private LoadingDialog LoadingDialog;

    public ChooseEventClickDialog(@NonNull Context context, LocalDate passingDate, Training training, SaveTrainingDialog.OnAddTrainingListener onAddTrainingListener) {
        super(context);

        this.context = context;
        this.passingDate = passingDate;
        this.training = training;
        this.onAddTrainingListener = onAddTrainingListener;

        getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_dialog);
        setCancelable(true);

        LoadingDialog = new LoadingDialog(getContext());

        TextView tv_day = findViewById(R.id.tv_day);
        tv_day.setVisibility(View.GONE);

        TextView tv_full_date = findViewById(R.id.tv_full_date);
        tv_full_date.setVisibility(View.GONE);

        TextView tv_no_events = findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        FloatingActionButton fab_add_event = findViewById(R.id.fab_add_event);
        fab_add_event.setVisibility(View.GONE);

        Button btn_clear_all = findViewById(R.id.btn_clear_all);
        btn_clear_all.setVisibility(View.GONE);

        rv_events = findViewById(R.id.rv_events);

        Query events = FirebaseUtils.getEventsDatabase().child(CalendarUtils.DateToTextForFirebase(passingDate))
                .orderByChild("start");
        DatabaseReference allEventsDatabase = FirebaseUtils.getAllEventsDatabase();

        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setIndexedQuery(events, allEventsDatabase, CalendarEvent.class)
                .setLifecycleOwner((LifecycleOwner) context)
                .build();

        EventsAdapterForFirebase adapterForFirebase = new EventsAdapterForFirebase(options,
                passingDate, context, this);

        rv_events.setAdapter(adapterForFirebase);
        rv_events.setLayoutManager(new LinearLayoutManagerWrapper(context));

        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rv_events.setVisibility(snapshot.hasChildren() ? View.VISIBLE : View.INVISIBLE);
                tv_no_events.setVisibility(snapshot.hasChildren() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEvent) {
        String selectedEventPrivateId = calendarEvent.getPrivateId();

        LoadingDialog.setMessage("Adding the trainingData to selected event...");
        LoadingDialog.show();

        FirebaseUtils.addTrainingForEvent(selectedEventPrivateId, training).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LoadingDialog.dismiss();
                        if (!task.isSuccessful()){
                            Toast.makeText(context, "Adding the training to this event failed \n" +
                                    "The training will be added to private trainings", Toast.LENGTH_SHORT).show();
                            onAddTrainingListener.onAddTraining(training);
                        }

                        Toast.makeText(context, "The training was added to this event successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

}
