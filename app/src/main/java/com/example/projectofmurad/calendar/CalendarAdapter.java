package com.example.projectofmurad.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;
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
    private final CalendarOnItemListener calendarOnItemListener;
    private LayoutInflater inflater;
    private FirebaseDatabase firebase;
    private DatabaseReference eventsDatabase;

    private String name;
//    private int count = 1;

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
                Log.d("murad", getAdapterPosition() + " | " + dayOfMonth.getText().toString() + " | " + Utils.DateToText(daysOfMonth.get(getAdapterPosition())));
                calendarOnItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString(), daysOfMonth.get(getAdapterPosition()));
            }
        }

        public void changeDayTextColor(int color){
            dayOfMonth.setTextColor(color);
        }

    }

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, Context context, CalendarOnItemListener calendarOnItemListener) {
        inflater = LayoutInflater.from(context);
        this.daysOfMonth = daysOfMonth;
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

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
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
/*
        eventsDatabase.child(Utils.DateToTextForFirebase(daysOfMonth.get(position))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = 1;
                for(DataSnapshot data : snapshot.getChildren()){
                    Log.d("murad", "date: " + daysOfMonth.get(holder.getAdapterPosition()).getDayOfMonth());
                    Log.d("murad", "getChildrenCount" + snapshot.getChildrenCount());
                    Log.d("murad", "count = " + count);
                    if(data.exists()){
                        name = data.child("name").getValue().toString();
                        switch(count){
                            case 1:
                                holder.tv_event_1.setText(name);
                                holder.tv_event_1.setVisibility(View.VISIBLE);
                            case 2:
                                holder.tv_event_2.setText(name);
                                holder.tv_event_2.setVisibility(View.VISIBLE);
                            case 3:
                                holder.tv_event_3.setText(name);
                                holder.tv_event_3.setVisibility(View.VISIBLE);
                            case 4:
                                holder.tv_event_4.setText(name);
                                holder.tv_event_4.setVisibility(View.VISIBLE);
                        }
                    }
                    count++;
                }
                Log.d("murad", "count after loop = " + count);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/
        Query query = eventsDatabase.child(Utils.DateToTextForFirebase(daysOfMonth.get(holder.getAdapterPosition()))).orderByChild("timestamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
/*
                for(DataSnapshot data : snapshot.getChildren()){
                    Log.d("murad", "date: " + daysOfMonth.get(holder.getAdapterPosition()).getDayOfMonth());
                    Log.d("murad", "getChildrenCount" + snapshot.getChildrenCount());
                    Log.d("murad", "count = " + count);
                    if(data.exists()){
                        name = data.child("name").getValue().toString();
                        switch(count){
                            case 1:
                                holder.tv_event_1.setText(name);
                                holder.tv_event_1.setVisibility(View.VISIBLE);
                            case 2:
                                holder.tv_event_2.setText(name);
                                holder.tv_event_2.setVisibility(View.VISIBLE);
                            case 3:
                                holder.tv_event_3.setText(name);
                                holder.tv_event_3.setVisibility(View.VISIBLE);
                            case 4:
                                holder.tv_event_4.setText(name);
                                holder.tv_event_4.setVisibility(View.VISIBLE);
                        }
                    }
                    count++;
                }
*/
                Log.d("murad", "---------------------------------------------------------------------------------");
                Log.d("murad", "date: " + daysOfMonth.get(position).getDayOfMonth());
                Log.d("murad", "getChildrenCount" + snapshot.getChildrenCount());
                if(snapshot.hasChildren()){
                    int count = 1;

                    for(DataSnapshot data : snapshot.getChildren()){
                        Log.d("murad", "count = " + count);

                        name = data.child("name").getValue().toString();
                        if(count == 1) {
                            holder.tv_event_1.setText(name);
                            holder.tv_event_1.setVisibility(View.VISIBLE);
                            Log.d("murad", "tv_event_1 set VISIBLE");
                            int height = holder.tv_event_1.getHeight();
                            Log.d("murad", "tv_height "+height);
                        }
                        else if(count == 2) {
                            holder.tv_event_2.setText(name);
                            holder.tv_event_2.setVisibility(View.VISIBLE);
                            Log.d("murad", "tv_event_2 set VISIBLE");
                        }
                        else if(count == 3) {
                            holder.tv_event_3.setText(name);
                            holder.tv_event_3.setVisibility(View.VISIBLE);
                            Log.d("murad", "tv_event_3 set VISIBLE");
                        }
                        else if(count == 4) {
                            holder.tv_event_4.setText(name);
                            holder.tv_event_4.setVisibility(View.VISIBLE);
                            Log.d("murad", "tv_event_4 set VISIBLE");
                        }
                        count++;
                    }
                    Log.d("murad", "count after loop = " + count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("murad", "---------------------------------------------------------------------------------");

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
