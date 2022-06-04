package com.example.projectofmurad.tracking;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventsAdapterForFirebase;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.LoadingDialog;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.example.projectofmurad.training.Training;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.LocalDate;

public class ChooseEventClickDialog extends AppCompatDialog implements EventsAdapterForFirebase.OnEventClickListener {

    private final LocalDate passingDate;
    private final Context context;

    private RecyclerView rv_events;
    private EventsAdapterForFirebase adapterForFirebase;

    private final Training training;

    private LoadingDialog loadingDialog;

    public ChooseEventClickDialog(@NonNull Context context, LocalDate passingDate, Training training) {
        super(context);

        this.context = context;
        this.passingDate = passingDate;
        this.training = training;

        Utils.createCustomDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_dialog);
        setCancelable(false);

        loadingDialog = new LoadingDialog(getContext());

        TextView tv_day = findViewById(R.id.tv_day);
        tv_day.setVisibility(View.GONE);

        TextView tv_full_date = findViewById(R.id.tv_full_date);
        tv_full_date.setVisibility(View.GONE);

        TextView tv_no_events = findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        MaterialButton btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.VISIBLE);

        btn_cancel.setOnClickListener(v -> {
            SaveTrainingDialog saveTrainingDialog = new SaveTrainingDialog(context, training);
            dismiss();
            saveTrainingDialog.show();
        });

        rv_events = findViewById(R.id.rv_events);

        Query events = FirebaseUtils.getEventsForDateRef(passingDate).orderByChild(CalendarEvent.KEY_EVENT_START);
        DatabaseReference allEventsDatabase = FirebaseUtils.getAllEventsDatabase();

        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setIndexedQuery(events, allEventsDatabase, CalendarEvent.class)
                .setLifecycleOwner((LifecycleOwner) context)
                .build();

        adapterForFirebase = new EventsAdapterForFirebase(options, passingDate, getContext(), this);
        rv_events.setAdapter(adapterForFirebase);

        LinearLayoutManagerWrapper layoutManager = new LinearLayoutManagerWrapper(getContext());
        layoutManager.setOnLayoutCompleteListener(
                () -> new Handler().postDelayed(() -> {
                    boolean isShowingItems = adapterForFirebase.getItemCount() > 0;
                    rv_events.setVisibility(isShowingItems ? View.VISIBLE : View.INVISIBLE);
                    tv_no_events.setVisibility(isShowingItems ? View.GONE : View.VISIBLE);
                }, 500));

        rv_events.setLayoutManager(layoutManager);
    }

    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEvent) {
        loadingDialog.setMessage("Adding the training to selected event...");
        loadingDialog.show();

        FirebaseUtils.addTrainingForEvent(calendarEvent.getPrivateId(), training).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismiss();
                        if (!task.isSuccessful()){
                            Toast.makeText(context, "Adding the training to this event failed \n" +
                                    "The training will be added to private trainings", Toast.LENGTH_SHORT).show();
                            FirebaseUtils.getCurrentUserPrivateTrainingsRef().child(training.getPrivateId()).setValue(training);
                        }

                        Toast.makeText(context, "The training was added to this event successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterForFirebase.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterForFirebase.stopListening();
    }
}
