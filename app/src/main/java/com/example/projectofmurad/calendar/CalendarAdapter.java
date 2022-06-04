package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.utils.FirebaseUtils;
import com.example.projectofmurad.helpers.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<LocalDate> daysOfMonth;
    private final LocalDate selectedDate;
    private final OnCalendarCellClickListener onCalendarCellClickListener;
    private final int rows;

    private int oldPosition;

    public CalendarAdapter(@NonNull ArrayList<LocalDate> daysOfMonth,
                           LocalDate selectedDate,
                           OnCalendarCellClickListener onCalendarCellClickListener) {

        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.onCalendarCellClickListener = onCalendarCellClickListener;
        this.rows = daysOfMonth.size()/7;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = parent.getHeight()/rows;

        return new CalendarViewHolder(view);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        private final TextView dayOfMonth;

        private final TextView tv_event_1;
        private final TextView tv_event_2;
        private final TextView tv_event_3;
        private final TextView tv_event_4;
        private final TextView tv_event_5;

        private final TextView tv_more;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfMonth = itemView.findViewById(R.id.cellDayText);

            tv_event_1 = itemView.findViewById(R.id.tv_event_1);
            tv_event_2 = itemView.findViewById(R.id.tv_event_2);
            tv_event_3 = itemView.findViewById(R.id.tv_event_3);
            tv_event_4 = itemView.findViewById(R.id.tv_event_4);
            tv_event_5 = itemView.findViewById(R.id.tv_event_5);

            tv_more = itemView.findViewById(R.id.tv_more);

            itemView.setOnClickListener(v -> {
                onCalendarCellClickListener.onCalendarCellClick(getAbsoluteAdapterPosition(), oldPosition,
                        daysOfMonth.get(getBindingAdapterPosition()));
                oldPosition = getBindingAdapterPosition();
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalDate date = daysOfMonth.get(position);

        holder.dayOfMonth.setText(" " + date.getDayOfMonth() + " ");

        if(date.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            holder.dayOfMonth.setTextColor(Color.BLUE);
        }
        if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            holder.dayOfMonth.setTextColor(Color.RED);
        }
        if (date.equals(LocalDate.now())){

            holder.dayOfMonth.setBackgroundResource(R.drawable.calendar_cell_text_today_background);
            holder.itemView.setBackgroundResource(R.drawable.calendar_cell_selected_background);

            oldPosition = position;

            int textColor = holder.dayOfMonth.getCurrentTextColor();

            if(textColor == Color.RED || textColor == Color.BLUE){
                holder.dayOfMonth.getBackground().setTint(textColor);
                holder.dayOfMonth.setTextColor(Color.WHITE);
            }
        }
        if (date.getMonthValue() != selectedDate.getMonthValue()){
            holder.itemView.setAlpha(0.7f);
            holder.dayOfMonth.setAlpha(0.7f);
        }

        holder.tv_event_1.setVisibility(View.GONE);
        holder.tv_event_2.setVisibility(View.GONE);
        holder.tv_event_3.setVisibility(View.GONE);
        holder.tv_event_4.setVisibility(View.GONE);
        holder.tv_event_5.setVisibility(View.GONE);
        holder.tv_more.setVisibility(View.GONE);

        TextView[] tv_events = new TextView[5];
        tv_events[0] = holder.tv_event_1;
        tv_events[1] = holder.tv_event_2;
        tv_events[2] = holder.tv_event_3;
        tv_events[3] = holder.tv_event_4;
        tv_events[4] = holder.tv_event_5;

        Query query = FirebaseUtils.getEventsForDateRef(date).orderByChild(CalendarEvent.KEY_EVENT_START);

        DatabaseReference allEventsDatabase = FirebaseUtils.getAllEventsDatabase();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.hasChildren()){
                    return;
                }

                int allEventsCount = (int) snapshot.getChildrenCount();

                int shownEventCount = 0;

                for(DataSnapshot data : snapshot.getChildren()){

                    if (shownEventCount < tv_events.length){
                        int finalShownEventCount = shownEventCount;

                        allEventsDatabase.child(data.getKey()).get().addOnSuccessListener(
                                new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()){
                                            return;
                                        }

                                        CalendarEvent event = dataSnapshot.getValue(CalendarEvent.class);

                                        TextView textView = tv_events[finalShownEventCount];

                                        String name = event.getName();
                                        int backgroundColor = event.getColor();

                                        int textColor = Utils.getContrastColor(backgroundColor);

                                        textView.setVisibility(View.VISIBLE);
                                        textView.setText(name);
                                        textView.setTextColor(textColor);
                                        textView.getBackground().setTint(backgroundColor);

                                        Log.d("murad", "Event number " + finalShownEventCount + " is set VISIBLE");
                                    }
                        });

                    }
                    else {
                        break;
                    }
                    shownEventCount++;
                }
                Log.d("murad", "shownEventCount after loop = " + shownEventCount);

                int hiddenEventsCount = allEventsCount - shownEventCount;

                if (hiddenEventsCount > 0){
                    holder.tv_more.setVisibility(View.VISIBLE);
                    if (holder.tv_event_5.getVisibility() == View.VISIBLE){
                        holder.tv_event_5.setVisibility(View.GONE);
                        hiddenEventsCount++;
                    }

                    holder.tv_more.setText("+" + hiddenEventsCount + " ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("murad", "---------------------------------------------------------------------------------");

    }

    public interface OnCalendarCellClickListener {
        void onCalendarCellClick(int position, int oldPosition, LocalDate selectedDate);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
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
