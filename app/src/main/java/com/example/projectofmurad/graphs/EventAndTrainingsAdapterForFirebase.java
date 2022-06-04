package com.example.projectofmurad.graphs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.EventsAdapterForFirebase;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.training.Training;
import com.example.projectofmurad.training.TrainingAdapterForFirebase;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class EventAndTrainingsAdapterForFirebase extends EventsAdapterForFirebase {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     * @param onEventClickListener
     */

    public EventAndTrainingsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options,
                                               @NonNull Context context, OnEventClickListener onEventClickListener) {

        super(options, (String) null, context, onEventClickListener);
    }

    public class EventAndTrainingsViewHolderForFirebase extends EventViewHolderForFirebase {

        private final RecyclerView rv_trainings;

        public EventAndTrainingsViewHolderForFirebase(@NonNull View itemView) {
            super(itemView);

            rv_trainings = itemView.findViewById(R.id.rv_trainings);
            itemView.setOnClickListener(null);
            itemView.setOnLongClickListener(v -> {
                onEventClickListener.onEventClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition()));
                return false;
            });
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
        super.onBindViewHolder(holder, position, model);

        EventAndTrainingsViewHolderForFirebase holderForFirebase = (EventAndTrainingsViewHolderForFirebase) holder;

        holderForFirebase.tv_event_start_date_time.setText(String.format(context.getString(R.string.starting_time_s_s),
                model.getStartDate(), model.getStartTime()));

        holderForFirebase.tv_event_end_date_time.setText(String.format(context.getString(R.string.ending_time_s_s),
                model.getEndDate(), model.getEndTime()));

        Query trainings = FirebaseUtils.getCurrentUserTrainingsRefForEvent(model.getPrivateId()).orderByChild(Training.KEY_TRAINING_START);

        FirebaseRecyclerOptions<Training> options = new FirebaseRecyclerOptions.Builder<Training>()
                .setQuery(trainings, Training.class)
                .setLifecycleOwner((LifecycleOwner) context)
                .build();

        TrainingAdapterForFirebase trainingAdapterForFirebase
                = new TrainingAdapterForFirebase(options, holder.itemView.getContext(), model.getColor());

        holderForFirebase.rv_trainings.setAdapter(trainingAdapterForFirebase);
        holderForFirebase.rv_trainings.setLayoutManager(new LinearLayoutManagerWrapper(holder.itemView.getContext()));
    }

    @NonNull
    @Override
    public EventAndTrainingsViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event_and_trainings, parent, false);

        return new EventAndTrainingsViewHolderForFirebase(view);
    }
}
