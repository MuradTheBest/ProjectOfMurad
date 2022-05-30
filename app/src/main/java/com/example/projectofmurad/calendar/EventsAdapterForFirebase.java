package com.example.projectofmurad.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class EventsAdapterForFirebase extends FirebaseRecyclerAdapter<CalendarEvent, EventsAdapterForFirebase.EventViewHolderForFirebase> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    protected LocalDate selectedDate;
    protected final OnEventClickListener onEventClickListener;
    protected final Context context;
    protected String selected_UID;
    protected final SQLiteDatabase db;

    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, LocalDate selectedDate,
                                    @NonNull Context context, OnEventClickListener onEventClickListener) {

        super(options);
        this.onEventClickListener = onEventClickListener;
        this.context = context;
        this.selectedDate = selectedDate;
        this.db = Utils.openOrCreateDatabase(context);
    }

    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, String selected_UID,
                                    @NonNull Context context, OnEventClickListener onEventClickListener) {

        super(options);
        this.onEventClickListener = onEventClickListener;
        this.context = context;
        this.selected_UID = selected_UID;
        this.db = Utils.openOrCreateDatabase(context);
    }

    public class EventViewHolderForFirebase extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ConstraintLayout constraintLayout;

        public SwitchMaterial switch_alarm;

        public MaterialTextView tv_event_name;
        public MaterialTextView tv_event_place;
        public MaterialTextView tv_event_description;

        public MaterialTextView tv_event_start_date_time;
        public MaterialTextView tv_hyphen;
        public MaterialTextView tv_event_end_date_time;

        public MaterialCheckBox cb_all_attendances;

        public EventViewHolderForFirebase(@NonNull View itemView, OnEventClickListener onEventClickListener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            switch_alarm = itemView.findViewById(R.id.switch_alarm);
            switch_alarm.setOnClickListener(this);

            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);

            tv_event_start_date_time = itemView.findViewById(R.id.tv_event_start_date_time);
            tv_hyphen = itemView.findViewById(R.id.tv_hyphen);
            tv_event_end_date_time = itemView.findViewById(R.id.tv_event_end_date_time);

            cb_all_attendances = itemView.findViewById(R.id.checkbox__all_attendances);
            cb_all_attendances.setOnClickListener(this);

            itemView.setOnClickListener(v -> onEventClickListener.onEventClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition())));
        }

        @Override
        public void onClick(View view) {
            CalendarEvent event = getItem(getAbsoluteAdapterPosition());

            if (view == switch_alarm){

                if (switch_alarm.isChecked()){
                    int alarm_hour = 2;
                    int alarm_minute = 0;
                    AlarmDialog alarmDialog = new AlarmDialog(context, event, switch_alarm,
                            alarm_hour, alarm_minute);

                    alarmDialog.show();
                }
                else {
                    Log.d("murad", "Alarm deleted");
                    Toast.makeText(context, "Alarm deleted", Toast.LENGTH_SHORT).show();
//                    Utils.deleteAlarm(event_private_id, event_date, event, db, context);
                    AlarmManagerForToday.cancelAlarm(context, event);
                }
            }
            else if(view == cb_all_attendances){
                FirebaseUtils.getCurrentUserTrackingRef(event.getPrivateId())
                        .child("attend").setValue(cb_all_attendances.isChecked());

                Toast.makeText(context, "Attendance " +
                        (cb_all_attendances.isChecked() ? "approved" : "disapproved"), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
        int textColor = Utils.getContrastColor(model.getColor());

        holder.switch_alarm.setTextColor(textColor);
        holder.tv_event_name.setTextColor(textColor);
        holder.tv_event_place.setTextColor(textColor);
        holder.tv_event_description.setTextColor(textColor);
        holder.tv_event_start_date_time.setTextColor(textColor);
        holder.tv_hyphen.setTextColor(textColor);
        holder.tv_event_end_date_time.setTextColor(textColor);
        holder.cb_all_attendances.setTextColor(textColor);

        GradientDrawable gd = Utils.getGradientBackground(model.getColor());
        holder.constraintLayout.setBackground(gd);

        holder.tv_event_name.setText(model.getName());
        holder.tv_event_place.setText(model.getPlace());
        holder.tv_event_description.setText(model.getDescription());

        if (selectedDate != null) {
            if(model.getStartDate().equals(model.getEndDate())){
                holder.tv_event_start_date_time.setText(model.getStartTime());
                holder.tv_event_end_date_time.setText(model.getEndTime());
            }
            else if(model.receiveStartDate().equals(selectedDate)){
                holder.tv_event_start_date_time.setText(model.getStartTime());
            }
            else if(model.receiveEndDate().equals(selectedDate)){
                holder.tv_event_end_date_time.setText(model.getEndTime());
            }
            else{
                holder.tv_hyphen.setText(R.string.all_day);
            }
        }

        if(model.getStartDate().equals(model.getEndDate())){
            holder.tv_event_start_date_time.setText(model.getStartTime());
            holder.tv_event_end_date_time.setText(model.getEndTime());
        }
        else if(model.receiveStartDate().equals(selectedDate)){
            holder.tv_event_start_date_time.setText(model.getStartTime());
        }
        else if(model.receiveEndDate().equals(selectedDate)){
            holder.tv_event_end_date_time.setText(model.getEndTime());
        }
        else{
            holder.tv_hyphen.setText(R.string.all_day);
        }

        if (model.isAllDay()){
            holder.tv_hyphen.setText(R.string.all_day);
        }

        if (selected_UID != null){
            DatabaseReference ref = FirebaseUtils.getAttendanceDatabase().child(model.getPrivateId())
                    .child(selected_UID).child("attend");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.cb_all_attendances.setChecked(snapshot.exists() && snapshot.getValue(boolean.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (selected_UID != null){
            holder.cb_all_attendances.setVisibility(View.VISIBLE);
        }
        else {
            holder.cb_all_attendances.setVisibility(View.GONE);
        }

        holder.switch_alarm.setChecked(Utils.checkIfAlarmSet(model.getPrivateId(), db));    }

    @NonNull
    @Override
    public EventViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event_info, parent, false);

        return new EventViewHolderForFirebase(view, onEventClickListener);
    }

    public interface OnEventClickListener {
        void onEventClick(int position, CalendarEvent calendarEvent);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }
}
