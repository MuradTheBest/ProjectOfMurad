package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.helpers.FirebaseUtils;
import com.example.projectofmurad.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<LocalDate> daysOfMonth;
    private final LocalDate selectedDate;
    private final OnCalendarCellClickListener onCalendarCellClickListener;
    private final LayoutInflater inflater;
    private final int rows;

    private String name;

    private int oldPosition;

    public CalendarAdapter(@NonNull ArrayList<LocalDate> daysOfMonth, Context context,
                           OnCalendarCellClickListener onCalendarCellClickListener,
                           LocalDate selectedDate) {

        inflater = LayoutInflater.from(context);
        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.onCalendarCellClickListener = onCalendarCellClickListener;
        this.rows = daysOfMonth.size()/7;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = parent.getHeight()/rows;

        return new CalendarViewHolder(view);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                Log.d("murad", getAbsoluteAdapterPosition() + " | " + dayOfMonth.getText().toString() + " | " + UtilsCalendar.DateToTextOnline(daysOfMonth.get(getAbsoluteAdapterPosition())));
                onCalendarCellClickListener.onCalendarCellClick(getAbsoluteAdapterPosition(), oldPosition, daysOfMonth.get(getAbsoluteAdapterPosition()));
                oldPosition = getAbsoluteAdapterPosition();
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.dayOfMonth.setText(" " + daysOfMonth.get(position).getDayOfMonth() + " ");

        if(position > -1){
            if(daysOfMonth.get(position).getDayOfWeek().getValue() == 5) {
                holder.dayOfMonth.setTextColor(Color.BLUE);
            }
            if (daysOfMonth.get(position).getDayOfWeek().getValue() == 6) {
                holder.dayOfMonth.setTextColor(Color.RED);
            }
            if (daysOfMonth.get(position).equals(LocalDate.now())){

                holder.dayOfMonth.setBackgroundResource(R.drawable.calendar_cell_text_today_background);
                holder.itemView.setBackgroundResource(R.drawable.calendar_cell_selected_background);

                oldPosition = position;

                int textColor = holder.dayOfMonth.getCurrentTextColor();

                if(textColor == Color.RED || textColor == Color.BLUE){
                    holder.dayOfMonth.getBackground().setTint(textColor);
                    holder.dayOfMonth.setTextColor(Color.WHITE);
                }
            }
            if (daysOfMonth.get(position).getMonthValue() != selectedDate.getMonthValue()){
                int finalColor = Color.GRAY | holder.dayOfMonth.getCurrentTextColor();

                holder.dayOfMonth.setTextColor(finalColor);
            }
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

        DatabaseReference eventsDatabase = FirebaseUtils.getEventsDatabase();

        Query query = eventsDatabase.child(UtilsCalendar.DateToTextForFirebase(daysOfMonth.get(position))).orderByChild("start");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("murad", "---------------------------------------------------------------------------------");
                Log.d("murad", "date: " + daysOfMonth.get(position).getDayOfMonth());

                Log.d("murad", "getChildrenCount" + snapshot.getChildrenCount());

                if(snapshot.hasChildren()){

                    int allEventsCount = (int) snapshot.getChildrenCount();

                    int shownEventCount = 0;
                    int backgroundColor;

                    for(DataSnapshot data : snapshot.getChildren()){
                        Log.d("murad", "shownEventCount = " + shownEventCount);

                        if (shownEventCount < tv_events.length){
                            CalendarEvent event = data.getValue(CalendarEvent.class);

                            Log.d("murad", "event " + event);

                            TextView textView = tv_events[shownEventCount];

                            name = data.child("name").getValue().toString();
                            backgroundColor = Integer.parseInt(data.child("color").getValue().toString());

                            int textColor = Color.BLACK;

                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");
                            Log.d("murad", " ");
                            Log.d("murad", "Date at position is " + UtilsCalendar.DateToTextLocal(daysOfMonth.get(position)));
                            Log.d("murad", "Selected date is " + UtilsCalendar.DateToTextLocal(selectedDate));
                            Log.d("murad", " ");
                            Log.d("murad", "----------------------------------------------date-------------------------------------------------------");

                            //adding gray contrast for events from non-selected months
                            if (daysOfMonth.get(position).getMonthValue() != selectedDate.getMonthValue()) {
                                backgroundColor = ColorUtils.blendARGB(backgroundColor, Color.LTGRAY, 0.6f);
                                textColor = Color.DKGRAY;
                            }

                            textView.setVisibility(View.VISIBLE);
                            textView.setText(name);
                            textView.setTextColor(textColor);
                            textView.getBackground().setTint(backgroundColor);

                            Log.d("murad", "Event number " + shownEventCount + " is set VISIBLE");
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

    public interface OnCalendarCellClickListener {
        void onCalendarCellClick(int position, int oldPosition, LocalDate selectedDate);
    }
}
