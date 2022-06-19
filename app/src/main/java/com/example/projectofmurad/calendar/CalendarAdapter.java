package com.example.projectofmurad.calendar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.utils.FirebaseUtils;
import com.example.projectofmurad.utils.Utils;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseIndexArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The type Calendar adapter.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<LocalDate> daysOfMonth;
    private final LocalDate selectedDate;
    private final OnCalendarCellClickListener onCalendarCellClickListener;
    private final int rows;

    private int oldPosition;

    /**
     * Instantiates a new Calendar adapter.
     *
     * @param daysOfMonth                 the days of month
     * @param selectedDate                the selected date
     * @param onCalendarCellClickListener the on calendar cell click listener
     */
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

    /**
     * The type Calendar view holder.
     */
    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_day_of_month;

        private final TextView tv_event_1;
        private final TextView tv_event_2;
        private final TextView tv_event_3;
        private final TextView tv_event_4;
        private final TextView tv_event_5;

        private final TextView tv_more;

        /**
         * Instantiates a new Calendar view holder.
         *
         * @param itemView the item view
         */
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_day_of_month = itemView.findViewById(R.id.cellDayText);

            tv_event_1 = itemView.findViewById(R.id.tv_event_1);
            tv_event_2 = itemView.findViewById(R.id.tv_event_2);
            tv_event_3 = itemView.findViewById(R.id.tv_event_3);
            tv_event_4 = itemView.findViewById(R.id.tv_event_4);
            tv_event_5 = itemView.findViewById(R.id.tv_event_5);

            tv_more = itemView.findViewById(R.id.tv_more);

            itemView.setOnClickListener(v -> {
                onCalendarCellClickListener.onCalendarCellClick(getBindingAdapterPosition(), oldPosition,
                        daysOfMonth.get(getBindingAdapterPosition()));
                oldPosition = getBindingAdapterPosition();
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalDate date = daysOfMonth.get(position);

        holder.tv_day_of_month.setText(" " + date.getDayOfMonth() + " ");

        if(date.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            holder.tv_day_of_month.setTextColor(Color.BLUE);
        }
        if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            holder.tv_day_of_month.setTextColor(Color.RED);
        }
        if (date.equals(LocalDate.now())){

            holder.tv_day_of_month.setBackgroundResource(R.drawable.calendar_cell_text_today_background);
            holder.itemView.setBackgroundResource(R.drawable.calendar_cell_selected_background);

            oldPosition = position;

            int textColor = holder.tv_day_of_month.getCurrentTextColor();

            if(textColor == Color.RED || textColor == Color.BLUE){
                holder.tv_day_of_month.getBackground().setTint(textColor);
                holder.tv_day_of_month.setTextColor(Color.WHITE);
            }
        }
        if (date.getMonthValue() != selectedDate.getMonthValue()){
            holder.itemView.setAlpha(0.7f);
            holder.tv_day_of_month.setAlpha(0.7f);
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

        FirebaseIndexArray<CalendarEvent> firebaseIndexArray
                = new FirebaseIndexArray<>(query, allEventsDatabase, new ClassSnapshotParser<>(CalendarEvent.class));

        firebaseIndexArray.addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type,
                                       @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {}

            @Override
            public void onDataChanged() {
                for (int i = 0; i < firebaseIndexArray.size() && i < tv_events.length; i++) {
                    CalendarEvent event = firebaseIndexArray.get(i);

                    TextView textView = tv_events[i];

                    String name = event.getName();
                    int backgroundColor = event.getColor();
                    int textColor = Utils.getContrastColor(backgroundColor);

                    textView.setVisibility(View.VISIBLE);
                    textView.setText(name);
                    textView.setTextColor(textColor);
                    textView.getBackground().setTint(backgroundColor);
                }

                int hiddenEventsCount = firebaseIndexArray.size() - 5;

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
            public void onError(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * The interface On calendar cell click listener.
     */
    public interface OnCalendarCellClickListener {
        /**
         * On calendar cell click.
         *
         * @param position     the position
         * @param oldPosition  the old position
         * @param selectedDate the selected date
         */
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
