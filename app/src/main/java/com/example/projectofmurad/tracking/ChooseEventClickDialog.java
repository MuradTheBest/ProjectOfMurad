package com.example.projectofmurad.tracking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventsAdapterForFirebase;
import com.example.projectofmurad.calendar.UtilsCalendar;
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

public class ChooseEventClickDialog extends Dialog implements EventsAdapterForFirebase.OnEventClickListener{

    private final LocalDate passingDate;
    private final Context context;

    private RecyclerView rv_events;

    private final Training training;

    private final SaveTrainingDialog.OnAddTrainingListener onAddTrainingListener;

    private ProgressDialog progressDialog;

    public ChooseEventClickDialog(@NonNull Context context, LocalDate passingDate, Training training, SaveTrainingDialog.OnAddTrainingListener onAddTrainingListener) {
        super(context);

        this.context = context;
        this.passingDate = passingDate;
        this.training = training;
        this.onAddTrainingListener = onAddTrainingListener;

        this.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id
        this.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);

//        View view = LayoutInflater.from(getContext()).inflate(R.layout.day_dialog_with_recyclerview,null, false);
//        setView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.day_dialog);
        setCancelable(true);

        progressDialog = new ProgressDialog(getContext());
        Utils.createCustomDialog(progressDialog);

        TextView tv_day = findViewById(R.id.tv_day);
        tv_day.setVisibility(View.GONE);

        TextView tv_full_date = this.findViewById(R.id.tv_full_date);
        tv_full_date.setVisibility(View.GONE);

        TextView tv_no_events = this.findViewById(R.id.tv_no_events);
        tv_no_events.setVisibility(View.GONE);

        FloatingActionButton fab_add_event = this.findViewById(R.id.fab_add_event);
        fab_add_event.setVisibility(View.GONE);

        Button btn_clear_all = this.findViewById(R.id.btn_clear_all);
        btn_clear_all.setVisibility(View.GONE);

        rv_events = this.findViewById(R.id.rv_events);

        DatabaseReference eventsDatabase = FirebaseUtils.getEventsDatabase();

        eventsDatabase = eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(passingDate));
//        Query query = eventsDatabase.orderByChild("start");
        Query query = eventsDatabase;

        //                .setIndexedQuery(, , CalendarEvent.class)
        FirebaseRecyclerOptions<CalendarEvent> options = new FirebaseRecyclerOptions.Builder<CalendarEvent>()
                .setQuery(query, CalendarEvent.class)
//                .setIndexedQuery(, , CalendarEvent.class)
                .setLifecycleOwner((LifecycleOwner) context)
                .build();


        EventsAdapterForFirebase adapterForFirebase = new EventsAdapterForFirebase(options,
                passingDate, context, this);
        Log.d("murad", "passingDate is " + UtilsCalendar.DateToTextOnline(passingDate));
        Log.d("murad", "adapterForFirebase.getItemCount() = " + adapterForFirebase.getItemCount());
        Log.d("murad", "options.getItemCount() = " + options.getSnapshots().size());

        rv_events.setAdapter(adapterForFirebase);
        Log.d("murad", "rv_events.getChildCount() = " + rv_events.getChildCount());
        rv_events.setLayoutManager(new LinearLayoutManagerWrapper(context));


        Log.d("murad", "events database is at " + eventsDatabase.getKey());

        eventsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    rv_events.setVisibility(View.VISIBLE);
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()){
                        Log.d("murad", "event " + eventSnapshot.getKey());
                    }

                }
                else {
                    rv_events.setVisibility(View.INVISIBLE);
                    tv_no_events.setVisibility(View.VISIBLE);
                }

                Log.d("murad", "Visibility set to " + (rv_events.getVisibility() == View.VISIBLE ? "visible" : (rv_events.getVisibility() == View.INVISIBLE) ? "invisible" : "gone"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/*
        setButton(DialogInterface.BUTTON_POSITIVE, "Select", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedEventPrivateId.isEmpty()){

                }
                else {
                    dismiss();
                    FirebaseUtils.addTrainingForEvent(selectedEventPrivateId, trainingData).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(context, "Adding the trainingData to this event failed \n" +
                                                "The trainingData will be added to private trainings", Toast.LENGTH_SHORT).show();
//                            new MyRepository(getOwnerActivity().getApplication()).insert(trainingData);
                                        onAddTrainingListener.onAddTraining(trainingData);

                                    }

                                    Toast.makeText(context, "The trainingData was added to this event successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
*/
    }



    @Override
    public void onEventClick(int position, @NonNull CalendarEvent calendarEvent) {
        String selectedEventPrivateId = calendarEvent.getPrivateId();

        progressDialog.setMessage("Adding the trainingData to selected event...");
        progressDialog.show();

        FirebaseUtils.addTrainingForEvent(selectedEventPrivateId, training).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        dismiss();
                        if (!task.isSuccessful()){
                            Toast.makeText(context, "Adding the training to this event failed \n" +
                                    "The training will be added to private trainings", Toast.LENGTH_SHORT).show();
                            onAddTrainingListener.onAddTraining(training);
                        }

                        Toast.makeText(context, "The training was added to this event successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
