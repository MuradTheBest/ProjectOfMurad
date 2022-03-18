package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.FirebaseUtils;
import com.example.projectofmurad.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private ArrayList<LocalDate> daysOfMonth;
    private LocalDate selectedDate;
    private final CalendarOnItemListener calendarOnItemListener;
    private LayoutInflater inflater;
    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    private String name;
    boolean changed = false;

    class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dayOfMonth;
        private TextView tv_event_1;
        private TextView tv_event_2;
        private TextView tv_event_3;
        private TextView tv_event_4;

        public CalendarViewHolder(@NonNull View itemView, CalendarOnItemListener calendarOnItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            tv_event_1 = itemView.findViewById(R.id.tv_event_1);
            tv_event_2 = itemView.findViewById(R.id.tv_event_2);
            tv_event_3 = itemView.findViewById(R.id.tv_event_3);
            tv_event_4 = itemView.findViewById(R.id.tv_event_4);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                Log.d("murad", getLayoutPosition() + " | " + dayOfMonth.getText().toString() + " | " + UtilsCalendar.DateToTextOnline(daysOfMonth.get(getLayoutPosition())));
                calendarOnItemListener.onItemClick(getLayoutPosition(), dayOfMonth.getText().toString(), daysOfMonth.get(getLayoutPosition()));
            }
        }

        public void changeDayTextColor(int color){
            dayOfMonth.setTextColor(color);
        }

    }

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, Context context,
                           CalendarOnItemListener calendarOnItemListener,
                           LocalDate selectedDate) {

        inflater = LayoutInflater.from(context);
        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.calendarOnItemListener = calendarOnItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarViewHolder(view, calendarOnItemListener);
    }

    Query query;

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.dayOfMonth.setText(" " + daysOfMonth.get(position).getDayOfMonth() + " ");
/*
        Log.d("murad ", "length"+getItemCount());
        Log.d("murad ", "adapterPosition"+position);*/

        if(position > 0){
            if(daysOfMonth.get(position).getDayOfWeek().getValue() == 5) {
                holder.dayOfMonth.setTextColor(Color.BLUE);
            }
            if (daysOfMonth.get(position).getDayOfWeek().getValue() == 6) {
                holder.dayOfMonth.setTextColor(Color.RED);
            }
        }

        holder.tv_event_1.setVisibility(View.INVISIBLE);
        holder.tv_event_2.setVisibility(View.INVISIBLE);
        holder.tv_event_3.setVisibility(View.INVISIBLE);
        holder.tv_event_4.setVisibility(View.INVISIBLE);

        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = FirebaseUtils.eventsDatabase;

//        query = eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(daysOfMonth.get(position))).orderByChild("timestamp");
        query = eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(daysOfMonth.get(position))).orderByChild("start");
        query.keepSynced(true);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("murad", "---------------------------------------------------------------------------------");
                Log.d("murad", "date: " + daysOfMonth.get(position).getDayOfMonth());

                Log.d("murad", "getChildrenCount" + snapshot.getChildrenCount());

                if(snapshot.hasChildren()){
                    int count = 0;
                    int backgroundColor = Color.GREEN;

                    int[] ids = new int[]{R.id.tv_event_1, R.id.tv_event_2, R.id.tv_event_3, R.id.tv_event_4};

                    for(DataSnapshot data : snapshot.getChildren()){
                        Log.d("murad", "count = " + count);

                        if (count < ids.length){
                            CalendarEvent event = data.getValue(CalendarEvent.class);
                            /*assert event != null;
                            count = event.getPosition();*/

                            int id = ids[count];


                            data.getRef().child("position").setValue(count);

                            //                            LocalDate startDate = event.receiveStart_date();
                            Log.d("murad", "Start date of event: " + event.getStartDate());
//                            LocalDate endDate = event.receiveEnd_date();
                            Log.d("murad", "End date of event: " + event.getEndDate());

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            int left = 0;
                            int right = 0;



                            params.setMargins(left,0,right,0);

                            Log.d("murad", "event " + event);

                            /*if(!event.getStartDate().equals(event.getEndDate())){
                                data.getRef().child("position").setValue(count);
                            }

                            if(selectedDate.isEqual(event.receiveStart_date())){
                                left = 5;
                            }
                            if(selectedDate.isEqual(event.receiveEnd_date())){
                                right = 5;
                            }*/

                            params.setMargins(left, 0, right, 0);

                            holder.itemView.findViewById(id).setLayoutParams(params);

                            name = data.child("name").getValue().toString();
                            backgroundColor = Integer.parseInt(data.child("color").getValue().toString());

                            int textColor = Color.BLACK;

                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");
                            Log.d("murad", " ");
                            Log.d("murad", "Date at position is " + UtilsCalendar.DateToTextLocal(daysOfMonth.get(position)));
                            Log.d("murad", "Selected date is " + UtilsCalendar.DateToTextLocal(selectedDate));
                            Log.d("murad", " ");
                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");

                            if (daysOfMonth.get(position).getMonthValue() != selectedDate.getMonthValue()) {
                                backgroundColor = ColorUtils.blendARGB(backgroundColor, Color.LTGRAY, 0.6f);

/*                            int dayColor = holder.dayOfMonth.getCurrentTextColor();
                            dayColor = ColorUtils.blendARGB(dayColor, Color.LTGRAY, 0.7f);
                            holder.dayOfMonth.setTextColor(dayColor);*/

                                textColor = Color.DKGRAY;
                            }


                            ((TextView) holder.itemView.findViewById(id)).setVisibility(View.VISIBLE);
                            ((TextView) holder.itemView.findViewById(id)).setText(name);
                            ((TextView) holder.itemView.findViewById(id)).setTextColor(textColor);
                            //((TextView) holder.itemView.findViewById(id)) = editTextView(((TextView) holder.itemView.findViewById(id)), timestamp);
                            ((TextView) holder.itemView.findViewById(id)).getBackground().setTint(backgroundColor);

                            Log.d("murad", "Event number " + count + " is set VISIBLE");

                        }
                        else {
                            break;
                        }
                        count++;
                    }
                    Log.d("murad", "count after loop = " + count);
                }

                changed = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(changed){
//            this.notifyDataSetChanged();
        }

        Log.d("murad", "---------------------------------------------------------------------------------");

    }

    public TextView editTextView(@NonNull TextView textView, int timestamp){
        textView.setText(name);
        if(timestamp == 0){
            textView.setBackgroundResource(R.drawable.calendar_cell_text_has_events_background);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,0);
            textView.setLayoutParams(params);

        }
        int height = textView.getHeight();
        Log.d("murad", "tv_height "+height);
        return textView;
    }



    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface CalendarOnItemListener {
        void onItemClick(int position, String dayText, LocalDate selectedDate);
    }

    public interface OnTextViewListener {
        void onTextView(int position, String dayText, LocalDate selectedDate);
    }

    public interface OnColorListener {
        void OnColor(int position);
    }
}
