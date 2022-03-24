package com.example.projectofmurad.calendar;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
import com.example.projectofmurad.notifications.AlarmManagerForToday;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

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

    private LocalDate selectedDate;
    private ObservableSnapshotArray<CalendarEvent> calendarEventArrayList;
    private final OnEventClickListener onEventClickListener;
    private OnEventExpandListener onEventExpandListener;
    private FirebaseRecyclerOptions<CalendarEvent> options;
    private Context context;
    private String selected_UID;

    private final SQLiteDatabase db;

    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, LocalDate selectedDate,
                                    @NonNull Context context, OnEventClickListener onEventClickListener, OnEventExpandListener onEventExpandListener) {

        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.selectedDate = selectedDate;
        this.onEventClickListener = onEventClickListener;
        this.onEventExpandListener = onEventExpandListener;
        this.context = context;
        this.db = context.openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, String selected_UID,
                                    @NonNull Context context, OnEventClickListener onEventClickListener) {

        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.onEventClickListener = onEventClickListener;
        this.context = context;
        this.selected_UID = selected_UID;
        this.db = context.openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
    }

    private int alarm_hour = 2;
    private int alarm_minute = 0;

    public class EventViewHolderForFirebase extends RecyclerView.ViewHolder implements
            View.OnClickListener, OnEventExpandListener, CompoundButton.OnCheckedChangeListener {

        public ConstraintLayout constraintLayout;

        public SwitchCompat switch_alarm;

        public TextView tv_event_name;
        public TextView tv_event_place;
        public TextView tv_event_description;

        public LinearLayout wrapped_layout;
        public TextView tv_event_start_time;
        public TextView tv_hyphen;
        public TextView tv_event_end_time;

        public LinearLayout expanded_layout;
        public TextView tv_event_start_date_time;
        public TextView tv_event_end_date_time;

        public CheckBox checkbox_all_attendances;

        public boolean expanded = false;

        public EventViewHolderForFirebase(@NonNull View itemView, OnEventClickListener onEventClickListener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            switch_alarm = itemView.findViewById(R.id.switch_alarm);
            switch_alarm.setOnClickListener(this);

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

                    AlarmDialog alarmDialog = new AlarmDialog(context, event, switch_alarm, alarm_hour, alarm_minute);

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

        public void animateConstraintLayout(ConstraintLayout constraintLayout, @NonNull ConstraintSet set,
                                            long duration, int direction) {
            AutoTransition trans = new AutoTransition();
            trans.setDuration(duration);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            //trans.setInterpolator(new DecelerateInterpolator());
            //trans.setInterpolator(new FastOutSlowInInterpolator());

            Slide slide = new Slide(direction);
//            slide.setInterpolator(new AccelerateDecelerateInterpolator());

            slide.setDuration(500);

            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(300);
            changeBounds.setInterpolator(new AccelerateDecelerateInterpolator());

            TransitionManager.beginDelayedTransition(constraintLayout, changeBounds);

            set.applyTo(constraintLayout);
        }

        @Override
        public void onEventExpand(int position, boolean expanded) {
            if(expanded){
                wrapped_layout.setVisibility(View.GONE);
                expanded_layout.setVisibility(View.VISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.linearLayout, ConstraintSet.BOTTOM);
                //constraintSet.applyTo(constraintLayout);

                animateConstraintLayout(constraintLayout, constraintSet, 300, Gravity.TOP);
            }
            else {
                expanded_layout.setVisibility(View.GONE);
                wrapped_layout.setVisibility(View.VISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, R.id.tv_event_description, ConstraintSet.BOTTOM);
                //constraintSet.applyTo(constraintLayout);

                animateConstraintLayout(constraintLayout, constraintSet, 300, Gravity.BOTTOM);
            }

            /*expanded_layout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            wrapped_layout.setVisibility(!expanded ? View.VISIBLE : View.GONE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.tv_event_place, ConstraintSet.TOP, expanded ? R.id.linearLayout : R.id.tv_event_description, ConstraintSet.BOTTOM);
            animateConstraintLayout(constraintLayout, constraintSet, 300, expanded ? Gravity.TOP : Gravity.BOTTOM);*/

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView == checkbox_all_attendances){
                FirebaseUtils.attendanceDatabase.child(getItem(getBindingAdapterPosition()).getPrivateId())
                        .child(FirebaseUtils.getCurrentUID()).setValue(isChecked);
                Toast.makeText(context, "Attendance " + (isChecked ? "approved" : "disapproved"), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
        Log.d("murad", "RECYCLING STARTED");
        String info = "";

//        holder.iv_circle.getDrawable().setTint(model.getColor());

        holder.tv_event_name.setText(model.getName());
        Log.d("murad","name: " + model.getName());
        info += model.getName();

        holder.tv_event_place.setText(model.getPlace());
        Log.d("murad","place: " +  model.getPlace());
        info += " | " + model.getPlace();

        holder.tv_event_description.setText(model.getDescription());
        Log.d("murad", "description " + model.getDescription());
        info += " | " + model.getDescription() + "\n";


        if (selectedDate != null){
            holder.wrapped_layout.setVisibility(View.VISIBLE);
            holder.expanded_layout.setVisibility(View.GONE);
        }
        else {
            holder.wrapped_layout.setVisibility(View.GONE);
            holder.expanded_layout.setVisibility(View.VISIBLE);
        }


        if (selectedDate != null){
            if(model.getStartDate().equals(model.getEndDate())){
                holder.tv_event_start_time.setText(model.getStartTime());
                Log.d("murad","Starting time: " + model.getStartTime());

                holder.tv_event_end_time.setText(model.getEndTime());
                Log.d("murad","Ending time: " + model.getEndTime());

            }
            else if(model.getStartDate().equals(UtilsCalendar.DateToTextOnline(selectedDate))){
                holder.tv_event_start_time.setText(model.getStartTime());
                Log.d("murad","Starting time: " + model.getStartTime());

            }
            else if(model.getEndDate().equals(UtilsCalendar.DateToTextOnline(selectedDate))){
                holder.tv_event_end_time.setText(model.getEndTime());
                Log.d("murad","Ending time: " + model.getEndTime());

            }
            else{
                holder.tv_hyphen.setText(R.string.all_day);
            }

            if(model.getTimestamp() == 0){
                holder.tv_event_start_time.setText("");
                holder.tv_hyphen.setText(R.string.all_day);
                holder.tv_event_end_time.setText("");
            }
        }


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

        DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(selected_UID);

        final boolean[] attend = {false};
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
        });

        boolean alarmSet = false;

        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        while (cursor.moveToNext()){
            alarmSet = true;
            holder.switch_alarm.setMaxEms(10);
        }

        cursor.close();
        holder.switch_alarm.setChecked(alarmSet);

        holder.itemView.setTag(model.getPrivateId());
    }

    @NonNull
    @Override
    public EventViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_info_expanded_with_card_view, parent, false);

        return new EventViewHolderForFirebase(view, onEventClickListener);
    }

    public interface OnEventClickListener {
        void onEventClick(int position, CalendarEvent calendarEvent);
    }

    public interface OnEventExpandListener {
        void onEventExpand(int position, boolean expanded);
    }

    @Override
    public int getItemCount() {
        return calendarEventArrayList.size();
    }
}
