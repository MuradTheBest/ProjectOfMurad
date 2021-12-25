package com.example.projectofmurad.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.util.ArrayList;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.CalendarEventViewHolder> {
    private ArrayList<CalendarEvent> eventArrayList;
    private OnItemListener onItemListener;
    private LayoutInflater inflater;

    class CalendarEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_event_name;
        private TextView tv_event_place;
        private TextView tv_event_description;
        private TextView tv_event_start_time;
        private TextView tv_event_end_time;


        public CalendarEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_event_place = itemView.findViewById(R.id.tv_event_place);
            tv_event_description = itemView.findViewById(R.id.tv_event_description);
            tv_event_start_time = itemView.findViewById(R.id.tv_event_start_time);
            tv_event_end_time = itemView.findViewById(R.id.tv_event_end_time);
        }

        @Override
        public void onClick(View view) {

        }

    }

    public CalendarEventAdapter(ArrayList<CalendarEvent> eventArrayList, Context context) {
        inflater = LayoutInflater.from(context);
        this.eventArrayList = eventArrayList;

    }

    @NonNull
    @Override
    public CalendarEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.event_info, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventViewHolder holder, int position) {
        CalendarEvent event = eventArrayList.get(position);
        Log.d("murad", event.toString());

        holder.tv_event_name.setText(event.getName());
        holder.tv_event_place.setText(event.getPlace());
        holder.tv_event_description.setText(event.getDescription());
        holder.tv_event_start_time.setText(Utils.getDefaultTime(event.getStart_time()));
        holder.tv_event_end_time.setText(Utils.getDefaultTime(event.getEnd_time()));

    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }

}
