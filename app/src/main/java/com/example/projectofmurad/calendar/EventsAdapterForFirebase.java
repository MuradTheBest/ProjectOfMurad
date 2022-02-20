package com.example.projectofmurad.calendar;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.AlarmManagerForToday;
import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
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
    private final OnEventListener onEventListener;
    private FirebaseRecyclerOptions<CalendarEvent> options;
    private Context context;
    private String selected_UID;

    private SQLiteDatabase db;


    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, LocalDate selectedDate,
                                    @NonNull Context context, OnEventListener onEventListener) {

        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.selectedDate = selectedDate;
        this.onEventListener = onEventListener;
        this.context = context;
        this.db = context.openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public EventsAdapterForFirebase(@NonNull FirebaseRecyclerOptions<CalendarEvent> options, String selected_UID,
                                    @NonNull Context context, OnEventListener onEventListener) {

        super(options);
        this.calendarEventArrayList = options.getSnapshots();
        this.onEventListener = onEventListener;
        this.context = context;
        this.selected_UID = selected_UID;
        this.db = context.openOrCreateDatabase(Utils.DATABASE_NAME, MODE_PRIVATE, null);
    }

    public class EventViewHolderForFirebase extends RecyclerView.ViewHolder implements
            View.OnClickListener, OnExpandedListener, CompoundButton.OnCheckedChangeListener,
            RadioGroup.OnCheckedChangeListener {

        private ConstraintLayout constraintLayout;

        private ImageView iv_circle;
        private ImageView iv_edit;
        private ImageView iv_attendance;

        private SwitchCompat switch_alarm;

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

        private CheckBox checkbox__all_attendances;

        private boolean expanded = false;

        public EventViewHolderForFirebase(@NonNull View itemView, OnEventListener onEventListener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayout);

            iv_circle = itemView.findViewById(R.id.iv_circle);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            iv_attendance = itemView.findViewById(R.id.iv_attendance);
            iv_attendance.setOnClickListener(this);

            switch_alarm = itemView.findViewById(R.id.switch_alarm);
            switch_alarm.setOnCheckedChangeListener(this);

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

            checkbox__all_attendances = itemView.findViewById(R.id.checkbox__all_attendances);

            iv_edit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view == iv_edit){
                onEventListener.onEventClick(getBindingAdapterPosition(), getItem(getBindingAdapterPosition()));
                Log.d("murad", "getAdapterPosition " + getBindingAdapterPosition() );
            }
            if (view == iv_attendance){
                Intent intent = new Intent(context, Event_Attendance_Screen.class);
                String event_private_id = getItem(getBindingAdapterPosition()).getEvent_private_id();
                intent.putExtra("event_private_id", event_private_id);

                context.startActivity(intent);
            }
            if(view == itemView){
                expanded = !expanded;
                onExpandClick(getBindingAdapterPosition(), expanded);
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
        public void onExpandClick(int position, boolean expanded) {
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
            if (buttonView == switch_alarm && switch_alarm.getMaxEms() == 0){
                String event_private_id = calendarEventArrayList.get(getBindingAdapterPosition()).getEvent_private_id();
                String event_date = calendarEventArrayList.get(getBindingAdapterPosition()).getStart_date();

                CalendarEvent event = getItem(getBindingAdapterPosition());
//                FirebaseUtils.eventsDatabase.child(event_private_id).child("alarm_UIDs").child(FirebaseUtils.getCurrentUID()).setValue(isChecked);
                if (isChecked){
                    Log.d("murad", "Alarm added");
//                    Utils.addAlarm(event_private_id, event_date, event, db, context);

                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.alarm_dialog_layout);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_background);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimationWindow; //style id


                    RadioGroup rg_alarm = dialog.findViewById(R.id.rg_alarm);
                    rg_alarm.setOnCheckedChangeListener((group, checkedId) -> {
                        long before = 0;
                        String toast = "Alarm was set for time of beginning of the event";

                        switch (checkedId){
                            case R.id.rb_5_mins_before:
                                before = 5 * 60 * 1000;
                                toast = "Alarm was set for 5 minutes before beginning of the event";
                                break;
                            case R.id.rb_15_mins_before:
                                before = 15 * 60 * 1000;
                                toast = "Alarm was set for 15 minutes before beginning of the event";
                                break;
                            case R.id.rb_30_mins_before:
                                before = 30 * 60 * 1000;
                                toast = "Alarm was set for 30 minutes before beginning of the event";
                                break;
                            case R.id.rb_1_hour_before:
                                before = 60 * 60 * 1000;
                                toast = "Alarm was set for 1 hour before beginning of the event";
                                break;
                        }
                        AlarmManagerForToday.addAlarm(context, event, before);
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(dialog::dismiss,500);

                    });

                    dialog.show();
                }
                else {
                    Log.d("murad", "Alarm deleted");
//                    Utils.deleteAlarm(event_private_id, event_date, event, db, context);
                    AlarmManagerForToday.cancelAlarm(context, event);
                }

            }
            switch_alarm.setMaxEms(0);
        }

        @Override
        public void onCheckedChanged(@NonNull RadioGroup group, int checkedId) {

            long before = 0;

            switch (checkedId){
                case R.id.rb_at_time:
                    before = 0;
                    break;
                case R.id.rb_5_mins_before:
                    before = 5 * 60 * 1000;
                    break;
                case R.id.rb_15_mins_before:
                    before = 15 * 60 * 1000;
                    break;
                case R.id.rb_30_mins_before:
                    before = 30 * 60 * 1000;
                    break;
                case R.id.rb_1_hour_before:
                    before = 60 * 60 * 1000;
                    break;
            }

            AlarmManagerForToday.addAlarm(context, getItem(getBindingAdapterPosition()), before);

        }
    }

    @Override
    protected void onBindViewHolder(@NonNull EventViewHolderForFirebase holder, int position, @NonNull CalendarEvent model) {
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


        if (selectedDate != null){

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
                holder.tv_hyphen.setText(R.string.all_day);
            }
        }

        Log.d("murad", "position = " + position);

        Resources res = context.getResources();

        if (selectedDate != null){
            holder.expanded_layout.setVisibility(View.GONE);
        }
        else {
            holder.expanded_layout.setVisibility(View.VISIBLE);
        }

        holder.tv_event_start_date_time.setText(String.format(res.getString(R.string.starting_time_s_s),
                Utils_Calendar.OnlineTextToLocal(model.getStart_date()), model.getStart_time()));

        holder.tv_event_end_date_time.setText(String.format(res.getString(R.string.ending_time_s_s),
                Utils_Calendar.OnlineTextToLocal(model.getEnd_date()), model.getEnd_time()));

        holder.itemView.getBackground().setTint(model.getColor());

        String event_private_id = model.getEvent_private_id();

        if (selected_UID != null){
            holder.checkbox__all_attendances.setVisibility(View.VISIBLE);

            DatabaseReference ref = FirebaseUtils.attendanceDatabase.child(event_private_id).child(selected_UID);

            final boolean[] attend = {false};
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().exists()){
                        attend[0] = task.getResult().getValue(boolean.class);
                    }
                    holder.checkbox__all_attendances.setChecked(attend[0]);
                }
            });
        }

        boolean alarmSet = false;

        Cursor cursor = db.rawQuery("select * from tbl_alarm where "
                + Utils.TABLE_AlARM_COL_EVENT_PRIVATE_ID + " = '" + event_private_id + "'",  null);

        while (cursor.moveToNext()){
            alarmSet = true;
            holder.switch_alarm.setMaxEms(10);
        }

        cursor.close();

        holder.switch_alarm.setChecked(alarmSet);

        holder.itemView.setTag(model.getEvent_private_id());
    }

    @NonNull
    @Override
    public EventViewHolderForFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_info_expanded_with_card_view, parent, false);

        return new EventViewHolderForFirebase(view, onEventListener);
    }

    public interface svb extends CompoundButton.OnCheckedChangeListener{

    }

    public interface OnEventListener {
        void onEventClick(int position, CalendarEvent calendarEventWithTextOnly);
    }

    public interface OnExpandedListener {
        void onExpandClick(int position, boolean expanded);
    }

    @Override
    public int getItemCount() {
        return calendarEventArrayList.size();
    }
}
