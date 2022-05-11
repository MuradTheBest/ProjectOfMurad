package com.example.projectofmurad.graphs;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.calendar.AlarmDialog;
import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.calendar.UtilsCalendar;
import com.example.projectofmurad.helpers.LinearLayoutManagerWrapper;
import com.example.projectofmurad.helpers.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.example.projectofmurad.training.Training;
import com.example.projectofmurad.training.TrainingAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/** FirebaseRecyclerAdapter is a class provided by
   FirebaseUI. it provides functions to bind, adapt and show
   database contents in a Recycler View */
public class EventsAndTrainingsAdapterForFirebase extends FirebaseRecyclerAdapter<CalendarEvent, EventsAndTrainingsAdapterForFirebase.EventAndTrainingsViewHolderForFirebase> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private final OnEventClickListener onEventClickListener;
    private final Context context;
    private String selected_UID;

    private final SQLiteDatabase db;

    public EventsAndTrainingsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, String selected_UID,
                                                @NonNull Context context, OnEventClickListener onEventClickListener) {

        super(options);
        this.onEventClickListener = onEventClickListener;
        this.context = context;
        this.selected_UID = selected_UID;
        this.db = context.openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public class EventAndTrainingsViewHolderForFirebase extends RecyclerView.ViewHolder implements
            View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        public ConstraintLayout constraintLayout;

        public SwitchCompat switch_alarm;

        public TextView tv_event_name;
        public TextView tv_event_place;
        public TextView tv_event_description;

        public LinearLayout expanded_layout;
        public TextView tv_event_start_date_time;
        public TextView tv_event_end_date_time;

        public CheckBox checkbox_all_attendances;

        private RecyclerView rv_trainings;

        public EventAndTrainingsViewHolderForFirebase(@NonNull View itemView, OnEventClickListener onEventClickListener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            switch_alarm = itemView.findViewById(R.id.switch_alarm);
            switch_alarm.setOnClickListener(this);

            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);

            rv_trainings = itemView.findViewById(R.id.rv_trainings);

            expanded_layout = itemView.findViewById(R.id.expanded_layout);
            tv_event_start_date_time = itemView.findViewById(R.id.tv_event_start_date_time);
            tv_event_end_date_time = itemView.findViewById(R.id.tv_event_end_date_time);

            checkbox_all_attendances = itemView.findViewById(R.id.checkbox__all_attendances);
            checkbox_all_attendances.setOnCheckedChangeListener(this);

//            itemView.setOnClickListener(this);
            itemView.setOnClickListener(v -> onEventClickListener.onEventClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition())));
        }

        @Override
        public void onClick(View view) {
            /*if (view == iv_attendance){
                Intent intent = new Intent(context, Event_Attendance_Screen.class);
                String event_private_id = getItem(getBindingAdapterPosition()).getPrivateId();
                intent.putExtra("event_private_id", event_private_id);

                context.startActivity(intent);
            }*/
            if(view == itemView){
                /*expanded = !expanded;
                if (selectedDate != null){
                    onEventExpand(getBindingAdapterPosition(), expanded);
                }*/

            }
            else if (view == switch_alarm){

                CalendarEvent event = getItem(getAbsoluteAdapterPosition());

                if (switch_alarm.isChecked()){

                    Log.d("murad", "Alarm added");

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
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView == checkbox_all_attendances){
                if (getAbsoluteAdapterPosition() > -1 && getAbsoluteAdapterPosition() < getItemCount()) {
                    Log.d(Utils.LOG_TAG, "getAbsoluteAdapterPosition() = " + getAbsoluteAdapterPosition());
                    FirebaseUtils.getAttendanceDatabase().child(getItem(getAbsoluteAdapterPosition()).getPrivateId())
                            .child(FirebaseUtils.getCurrentUID()).child("attend").setValue(isChecked);
                    Toast.makeText(context, "Attendance " + (isChecked ? "approved" : "disapproved"), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull EventAndTrainingsViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
        Log.d("murad", "RECYCLING STARTED");

        int textColor = Utils.getContrastColor(model.getColor());

        GradientDrawable gd = Utils.getGradientBackground(model.getColor());

        holder.constraintLayout.setBackground(gd);

        holder.tv_event_name.setText(model.getName());
        Log.d("murad","name: " + model.getName());

        holder.tv_event_place.setText(model.getPlace());
        Log.d("murad","place: " +  model.getPlace());

        holder.tv_event_description.setText(model.getDescription());
        Log.d("murad", "description " + model.getDescription());

        Log.d("murad", "position = " + position);

        Resources res = context.getResources();

        holder.tv_event_start_date_time.setText(String.format(res.getString(R.string.starting_time_s_s),
                UtilsCalendar.OnlineTextToLocal(model.getStartDate()), model.getStartTime()));

        holder.tv_event_end_date_time.setText(String.format(res.getString(R.string.ending_time_s_s),
                UtilsCalendar.OnlineTextToLocal(model.getEndDate()), model.getEndTime()));

        holder.itemView.getBackground().setTint(model.getColor());

        String event_private_id = model.getPrivateId();

        if (selected_UID != null){
            holder.checkbox_all_attendances.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkbox_all_attendances.setVisibility(View.GONE);
            selected_UID = FirebaseUtils.getCurrentUID();
        }

        if (selected_UID != null){
            DatabaseReference ref = FirebaseUtils.getAttendanceDatabase().child(event_private_id).child(selected_UID).child("attend");

            final boolean[] attend = {false};

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        attend[0] = snapshot.getValue(boolean.class);
                    }
                    holder.checkbox_all_attendances.setChecked(attend[0]);
                    holder.checkbox_all_attendances.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            /*ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().exists()){
                        attend[0] = task.getResult().getValue(boolean.class);
                    }
                    holder.checkbox_all_attendances.setChecked(attend[0]);
                    holder.checkbox_all_attendances.setVisibility(View.GONE);
                    if (selected_UID == null){
                    }
                }
            });*/
        }

        boolean alarmSet = false;

        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        while (cursor.moveToNext()){
            alarmSet = true;
        }

        cursor.close();
        holder.switch_alarm.setChecked(alarmSet);

        holder.itemView.setTag(model.getPrivateId());

        FirebaseUtils.getCurrentUserTrainingsForEvent(model.getPrivateId()).observe(
                (LifecycleOwner) context, new Observer<ArrayList<Training>>() {
                    @Override
                    public void onChanged(ArrayList<Training> trainings) {

                        Log.d(Utils.LOG_TAG, trainings.toString());

                        TrainingAdapter trainingAdapter = new TrainingAdapter(context, model.getColor(), trainings);
                        holder.rv_trainings.setAdapter(trainingAdapter);
                        holder.rv_trainings.setLayoutManager(new LinearLayoutManagerWrapper(context));
                    }
                });

        holder.tv_event_name.setTextColor(textColor);
        holder.tv_event_place.setTextColor(textColor);
        holder.tv_event_description.setTextColor(textColor);
        holder.tv_event_start_date_time.setTextColor(textColor);
        holder.tv_event_end_date_time.setTextColor(textColor);

    }

    @NonNull
    @Override
    public EventAndTrainingsViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event_and_trainings, parent, false);

        return new EventAndTrainingsViewHolderForFirebase(view, onEventClickListener);
    }

    public interface OnEventClickListener {
        void onEventClick(int position, CalendarEvent calendarEvent);
    }

    public interface OnEventChooseListener {
        void onEventChoose(int oldPosition, int newPosition, String eventPrivateId);
    }

    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
