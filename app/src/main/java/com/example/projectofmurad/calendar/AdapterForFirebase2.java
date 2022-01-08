package com.example.projectofmurad.calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class AdapterForFirebase2 extends FirebaseRecyclerAdapter<CalendarEventWithTextOnly, AdapterForFirebase2.ViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private LocalDate selectedDate;
    private ObservableSnapshotArray<CalendarEventWithTextOnly> calendarEventArrayList;
    private final OnEventListener onEventListener;
    private FirebaseRecyclerOptions<CalendarEventWithTextOnly> options;
    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    public AdapterForFirebase2(@NonNull FirebaseRecyclerOptions<CalendarEventWithTextOnly> options, LocalDate selectedDate, OnEventListener onEventListener) {
        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.selectedDate = selectedDate;
        this.onEventListener = onEventListener;
    }

    public class ViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_event_name;
        private TextView tv_event_place;
        private TextView tv_event_description;
        private TextView tv_event_start_time;
        private TextView tv_hyphen;
        private TextView tv_event_end_time;

        public ViewHolderForFirebase(@NonNull View itemView, OnEventListener onEventListener) {
            super(itemView);

            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);
            tv_event_start_time = itemView.findViewById(R.id.tv_event_start_time);
            tv_hyphen = itemView.findViewById(R.id.tv_hyphen);
            tv_event_end_time = itemView.findViewById(R.id.tv_event_end_time);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view == itemView){
                onEventListener.onEventClick(getAdapterPosition(), calendarEventArrayList.get(getAdapterPosition()));
                Log.d("murad", "getAdapterPosition " + getAdapterPosition() );
            }
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterForFirebase2.ViewHolderForFirebase holder, int position, @NonNull CalendarEventWithTextOnly model) {
        Log.d("murad", "RECYCLING STARTED");
        String info = "";

        holder.tv_event_name.setText(model.getName());
        Log.d("murad","name: " + model.getName());
        info += model.getName();

        holder.tv_event_place.setText(model.getPlace());
        Log.d("murad","place: " +  model.getPlace());
        info += " | " + model.getPlace();

        holder.tv_event_description.setText(model.getDescription());
        Log.d("murad", "description " + model.getDescription());
        info += " | " + model.getDescription() + "\n";

        if(model.getStart_date().equals(model.getEnd_date())){
            holder.tv_event_start_time.setText(model.getStart_time());
            Log.d("murad","Starting time: " + model.getStart_time());

            holder.tv_event_end_time.setText(model.getEnd_time());
            Log.d("murad","Ending time: " + model.getEnd_time());

        }
        else if(model.getStart_date().equals(Utils.DateToText(selectedDate))){
            holder.tv_event_start_time.setText(model.getStart_time());
            Log.d("murad","Starting time: " + model.getStart_time());

        }
        else if(model.getEnd_date().equals(Utils.DateToText(selectedDate))){
            holder.tv_event_end_time.setText(model.getEnd_time());
            Log.d("murad","Ending time: " + model.getEnd_time());

        }
        else{
            holder.tv_hyphen.setText("All day");
        }
        Log.d("murad", "position"+ holder.getAdapterPosition());

    }



    @NonNull
    @Override
    public AdapterForFirebase2.ViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_info, parent, false);

        return new AdapterForFirebase2.ViewHolderForFirebase(view, onEventListener);
    }

    public interface OnEventListener {
        void onEventClick(int position, CalendarEventWithTextOnly calendarEventWithTextOnly);
    }

    @Override
    public int getItemCount() {
        return calendarEventArrayList.size();
    }
}
