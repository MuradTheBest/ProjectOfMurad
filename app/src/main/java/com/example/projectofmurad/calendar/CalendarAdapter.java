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
    private  ArrayList<LocalDate> daysOfMonth;
    private  LocalDate selectedDate;
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
                Log.d("murad", getLayoutPosition() + " | " + dayOfMonth.getText().toString() + " | " + Utils_Calendar.DateToTextOnline(daysOfMonth.get(getLayoutPosition())));
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
            if(daysOfMonth.get(position).getDayOfWeek().getValue() == 5 ||
                    daysOfMonth.get(position).getDayOfWeek().getValue() == 6){
                holder.dayOfMonth.setTextColor(Color.RED);
            }
        }

        holder.tv_event_1.setVisibility(View.INVISIBLE);
        holder.tv_event_2.setVisibility(View.INVISIBLE);
        holder.tv_event_3.setVisibility(View.INVISIBLE);
        holder.tv_event_4.setVisibility(View.INVISIBLE);


        firebase = FirebaseDatabase.getInstance();
        eventsDatabase = firebase.getReference("EventsDatabase");

        query = eventsDatabase.child(Utils_Calendar.DateToTextForFirebase(daysOfMonth.get(position))).orderByChild("timestamp");
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
                    int timestamp;

                    int[] ids = new int[]{R.id.tv_event_1, R.id.tv_event_2, R.id.tv_event_3, R.id.tv_event_4};

                    for(DataSnapshot data : snapshot.getChildren()){
                        Log.d("murad", "count = " + count);

                        if (count < ids.length){
                            int id = ids[count];

                            CalendarEventWithTextOnly2FromSuper event = data.getValue(CalendarEventWithTextOnly2FromSuper.class);
                            assert event != null;
                            LocalDate startDate = event.receiveStart_date();
                            Log.d("murad", "Start date of event: " + event.getStart_date());
                            LocalDate endDate = event.receiveEnd_date();
                            Log.d("murad", "End date of event: " + event.getEnd_date());

                            name = data.child("name").getValue().toString();
                            backgroundColor = Integer.parseInt(data.child("color").getValue().toString());

                            int textColor = Color.BLACK;

                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");
                            Log.d("murad", " ");
                            Log.d("murad", "Date at position is " + Utils_Calendar.DateToTextLocal(daysOfMonth.get(position)));
                            Log.d("murad", "Selected date is " + Utils_Calendar.DateToTextLocal(selectedDate));
                            Log.d("murad", " ");
                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");

                            if (!daysOfMonth.get(position).getMonth().equals(selectedDate.getMonth())) {
                                backgroundColor = ColorUtils.blendARGB(backgroundColor, Color.LTGRAY, 0.6f);

/*                            int dayColor = holder.dayOfMonth.getCurrentTextColor();
                            dayColor = ColorUtils.blendARGB(dayColor, Color.LTGRAY, 0.7f);
                            holder.dayOfMonth.setTextColor(dayColor);*/

                                textColor = Color.DKGRAY;
                            }

                            timestamp = Integer.parseInt(data.child("timestamp").getValue().toString());
/*                        if(count == 1) {
                            holder.tv_event_1.setVisibility(View.VISIBLE);
                            holder.tv_event_1.setText(name);
                            holder.tv_event_1.setTextColor(textColor);
                            //holder.tv_event_1 = editTextView(holder.tv_event_1, timestamp);
                            holder.tv_event_1.getBackground().setTint(backgroundColor);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            int left = 0;
                            int top = 0;
                            int right = 0;
                            int bottom = 0;
                            if(selectedDate.isEqual(startDate)){
                                left = 2;
                            }
                            if(selectedDate.isEqual(endDate)){
                                right = 2;
                            }

                            params.setMargins(left,0,right,0);

                            holder.tv_event_1.setLayoutParams(params);

                            Log.d("murad", "tv_event_1 set VISIBLE");
                        }
                        else if(count == 2) {
                            holder.tv_event_2.setVisibility(View.VISIBLE);
                            holder.tv_event_2.setText(name);
                            holder.tv_event_2.setTextColor(textColor);
                            //holder.tv_event_2 = editTextView(holder.tv_event_2, timestamp);
                            holder.tv_event_2.getBackground().setTint(backgroundColor);
                            Log.d("murad", "tv_event_2 set VISIBLE");
                        }
                        else if(count == 3) {
                            holder.tv_event_3.setVisibility(View.VISIBLE);
                            holder.tv_event_3.setText(name);
                            holder.tv_event_3.setTextColor(textColor);
                            //holder.tv_event_3 = editTextView(holder.tv_event_3, timestamp);
                            holder.tv_event_3.getBackground().setTint(backgroundColor);
                            Log.d("murad", "tv_event_3 set VISIBLE");
                        }
                        else if(count == 4) {
                            holder.tv_event_4.setVisibility(View.VISIBLE);
                            holder.tv_event_4.setText(name);
                            holder.tv_event_4.setTextColor(textColor);
                            //holder.tv_event_4 = editTextView(holder.tv_event_4, timestamp);
                            holder.tv_event_4.getBackground().setTint(backgroundColor);
                            Log.d("murad", "tv_event_4 set VISIBLE");
                        }*/

                            ((TextView) holder.itemView.findViewById(id)).setVisibility(View.VISIBLE);
                            ((TextView) holder.itemView.findViewById(id)).setText(name);
                            ((TextView) holder.itemView.findViewById(id)).setTextColor(textColor);
                            //((TextView) holder.itemView.findViewById(id)) = editTextView(((TextView) holder.itemView.findViewById(id)), timestamp);
                            ((TextView) holder.itemView.findViewById(id)).getBackground().setTint(backgroundColor);

/*                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        int left = 0;
                        int top = 0;
                        int right = 0;
                        int bottom = 0;
                        if(selectedDate.isEqual(startDate)){
                            left = 5;
                        }
                        if(selectedDate.isEqual(endDate)){
                            right = 5;
                        }

                        params.setMargins(left,0,right,0);

                        holder.tv_event_1.setLayoutParams(params);*/

                            Log.d("murad", "Event number " + count + " is set VISIBLE");

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


/*
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/

        if(changed){
            this.notifyDataSetChanged();
        }

        Log.d("murad", "---------------------------------------------------------------------------------");

    }

    public TextView editTextView(TextView textView, int timestamp){
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
