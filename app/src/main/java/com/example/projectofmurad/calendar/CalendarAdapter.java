package com.example.projectofmurad.calendar;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private  ArrayList<LocalDate> daysOfMonth;
    private final OnItemListener onItemListener;
    private LayoutInflater inflater;

    class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dayOfMonth;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                onItemListener.onItemClick(getAdapterPosition(), dayOfMonth.getText().toString(), daysOfMonth.get(getAdapterPosition()));
            }
        }

        public void changeDayTextColor(int color){
            dayOfMonth.setTextColor(color);
        }
    }

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, Context context, OnItemListener onItemListener) {
        inflater = LayoutInflater.from(context);
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(" " + daysOfMonth.get(position).getDayOfMonth() + " ");
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText, LocalDate selectedDate);
    }

    public interface OnColorListener {
        void OnColor(int position);
    }
}
