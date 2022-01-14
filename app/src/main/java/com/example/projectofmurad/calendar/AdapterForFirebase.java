package com.example.projectofmurad.calendar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils_Calendar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.time.LocalDate;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class AdapterForFirebase extends FirebaseRecyclerAdapter<CalendarEvent, AdapterForFirebase.ViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private LocalDate selectedDate;

    public AdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, LocalDate selectedDate) {
        super(options);
        this.selectedDate = selectedDate;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterForFirebase.ViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
        Log.d("murad", "RECYCLING STARTED");

        holder.tv_event_name.setText(model.getName());
        Log.d("murad","name: " + model.getName());

        holder.tv_event_place.setText(model.getPlace());
        Log.d("murad","place: " +  model.getPlace());

        holder.tv_event_description.setText(model.getDescription());
        Log.d("murad", "description " + model.getDescription());

        if(model.getStart_date().equals(model.getEnd_date())){
            holder.tv_event_start_time.setText(Utils_Calendar.TimeToText(model.getStart_time()));
            Log.d("murad","Starting time: " +  Utils_Calendar.TimeToText(model.getStart_time()));

            holder.tv_event_end_time.setText(Utils_Calendar.TimeToText(model.getEnd_time()));
            Log.d("murad","Ending time: " +  Utils_Calendar.TimeToText(model.getEnd_time()));

        }
        else if(model.getStart_date().equals(selectedDate)){
            holder.tv_event_start_time.setText(Utils_Calendar.TimeToText(model.getStart_time()));
            Log.d("murad","Starting date: " +  Utils_Calendar.TimeToText(model.getStart_time()));

        }
        else if(model.getEnd_date().equals(selectedDate)){
            holder.tv_event_end_time.setText(Utils_Calendar.TimeToText(model.getEnd_time()));
            Log.d("murad","Ending date: " +  Utils_Calendar.TimeToText(model.getEnd_time()));

        }
        else{
            holder.tv_hyphen.setText("All day");
        }
    }

    @NonNull
    @Override
    public AdapterForFirebase.ViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_info, parent, false);

        return new AdapterForFirebase.ViewHolderForFirebase(view);
    }

    public class ViewHolderForFirebase extends RecyclerView.ViewHolder {
        private TextView tv_event_name;
        private TextView tv_event_place;
        private TextView tv_event_description;
        private TextView tv_event_start_time;
        private TextView tv_hyphen;
        private TextView tv_event_end_time;

        public ViewHolderForFirebase(@NonNull View itemView) {
            super(itemView);

            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);
            tv_event_start_time = itemView.findViewById(R.id.tv_event_start_time);
            tv_hyphen = itemView.findViewById(R.id.tv_hyphen);
            tv_event_end_time = itemView.findViewById(R.id.tv_event_end_time);


        }
    }


}
