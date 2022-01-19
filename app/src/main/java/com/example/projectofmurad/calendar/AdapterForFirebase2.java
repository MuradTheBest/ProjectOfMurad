package com.example.projectofmurad.calendar;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;

import java.time.LocalDate;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class AdapterForFirebase2 extends FirebaseRecyclerAdapter<CalendarEventWithTextOnly2FromSuper, AdapterForFirebase2.ViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private LocalDate selectedDate;
    private ObservableSnapshotArray<CalendarEventWithTextOnly2FromSuper> calendarEventArrayList;
    private final OnEventListener onEventListener;
    private FirebaseRecyclerOptions<CalendarEventWithTextOnly2FromSuper> options;

    public AdapterForFirebase2(@NonNull FirebaseRecyclerOptions<CalendarEventWithTextOnly2FromSuper> options, LocalDate selectedDate, OnEventListener onEventListener) {
        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.selectedDate = selectedDate;
        this.onEventListener = onEventListener;
    }

    public class ViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterForFirebase2.OnExpandedListener {
        private ConstraintLayout constraintLayout;

        private ImageView iv_circle;
        private ImageView iv_edit;
        private ImageView iv_attendance;

        private TextView tv_event_name;
        private TextView tv_event_place;
        private TextView tv_event_description;

        private LinearLayout wrapped_layout;
        private TextView tv_event_start_time;
        private TextView tv_hyphen;
        private TextView tv_event_end_time;

        private LinearLayout expanded_layout;
        private TextView tv_event_start_date_time;
        private TextView tv_event_end_date_time;

        private boolean expanded = false;

        public ViewHolderForFirebase(@NonNull View itemView, OnEventListener onEventListener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_circle = itemView.findViewById(R.id.iv_circle);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            iv_attendance = itemView.findViewById(R.id.iv_attendance);

            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);

            wrapped_layout = itemView.findViewById(R.id.wrapped_layout);
            tv_event_start_time = itemView.findViewById(R.id.tv_event_start_time);
            tv_hyphen = itemView.findViewById(R.id.tv_hyphen);
            tv_event_end_time = itemView.findViewById(R.id.tv_event_end_time);

            expanded_layout = itemView.findViewById(R.id.expanded_layout);
            tv_event_start_date_time = itemView.findViewById(R.id.tv_event_start_date_time);
            tv_event_end_date_time = itemView.findViewById(R.id.tv_event_end_date_time);

            iv_edit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view == iv_edit){
                onEventListener.onEventClick(getAdapterPosition(), calendarEventArrayList.get(getAdapterPosition()));
                Log.d("murad", "getAdapterPosition " + getAdapterPosition() );
            }
            if(view == itemView){
                expanded = !expanded;
                onExpandClick(getAdapterPosition(), expanded);
                /*if(expanded){
                    wrapped_layout.setVisibility(View.GONE);
                    expanded_layout.setVisibility(View.VISIBLE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.linearLayout, ConstraintSet.BOTTOM);
                    //constraintSet.applyTo(constraintLayout);

                    animateConstraintLayout(constraintLayout, constraintSet, 300);
                }
                else {
                    expanded_layout.setVisibility(View.GONE);
                    wrapped_layout.setVisibility(View.VISIBLE);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.tv_event_description, ConstraintSet.BOTTOM);
                    //constraintSet.applyTo(constraintLayout);

                    animateConstraintLayout(constraintLayout, constraintSet, 300);
                }*/

            }
        }

        public void animateConstraintLayout(ConstraintLayout constraintLayout, ConstraintSet set, long duration) {
            AutoTransition trans = new AutoTransition();
            trans.setDuration(duration);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            //trans.setInterpolator(new DecelerateInterpolator());
            //trans.setInterpolator(new FastOutSlowInInterpolator());

            TransitionManager.beginDelayedTransition(constraintLayout, trans);
            set.applyTo(constraintLayout);
        }

        @Override
        public void onExpandClick(int position, boolean expanded) {
            if(expanded){
                wrapped_layout.setVisibility(View.GONE);
                expanded_layout.setVisibility(View.VISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.linearLayout, ConstraintSet.BOTTOM);
                //constraintSet.applyTo(constraintLayout);

                animateConstraintLayout(constraintLayout, constraintSet, 300);
            }
            else {
                expanded_layout.setVisibility(View.GONE);
                wrapped_layout.setVisibility(View.VISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.tv_event_description, ConstraintSet.BOTTOM);
                //constraintSet.applyTo(constraintLayout);

                animateConstraintLayout(constraintLayout, constraintSet, 300);
            }
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterForFirebase2.ViewHolderForFirebase holder, int position, @NonNull CalendarEventWithTextOnly2FromSuper model) {
        Log.d("murad", "RECYCLING STARTED");
        String info = "";

        holder.iv_circle.getDrawable().setTint(model.getColor());

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
        else if(model.getStart_date().equals(Utils_Calendar.DateToTextOnline(selectedDate))){
            holder.tv_event_start_time.setText(model.getStart_time());
            Log.d("murad","Starting time: " + model.getStart_time());

        }
        else if(model.getEnd_date().equals(Utils_Calendar.DateToTextOnline(selectedDate))){
            holder.tv_event_end_time.setText(model.getEnd_time());
            Log.d("murad","Ending time: " + model.getEnd_time());

        }
        else{
            holder.tv_hyphen.setText("All day");
        }
        Log.d("murad", "position = " + position);

        holder.expanded_layout.setVisibility(View.GONE);
        holder.tv_event_start_date_time.setText("Starting time: " + Utils_Calendar.OnlineTextToLocal(model.getStart_date()) + ", " + model.getStart_time());
        holder.tv_event_end_date_time.setText("Ending time: " + Utils_Calendar.OnlineTextToLocal(model.getEnd_date()) + ", " + model.getEnd_time());
    }



    @NonNull
    @Override
    public AdapterForFirebase2.ViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_info_expanded_with_card_view, parent, false);

        return new AdapterForFirebase2.ViewHolderForFirebase(view, onEventListener);
    }

    public interface OnEventListener {
        void onEventClick(int position, CalendarEventWithTextOnly2FromSuper calendarEventWithTextOnly);
    }

    public interface OnExpandedListener {
        void onExpandClick(int position, boolean expanded);
    }

    @Override
    public int getItemCount() {
        return calendarEventArrayList.size();
    }
}
