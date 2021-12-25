package com.example.projectofmurad.calendar;

import android.content.Context;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.util.ArrayList;

public class DayAdapter extends BaseAdapter {
    ArrayList<CalendarEvent> eventArrayList;
    Context context;

    public DayAdapter(ArrayList<CalendarEvent> eventArrayList, Context context){
        this.eventArrayList = eventArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public CalendarEvent getItem(int position) {
        return eventArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        CalendarEvent event = eventArrayList.get(position);
        convertview = LayoutInflater.from(context).inflate(R.layout.event_info, null);

        TextView tv_event_name = convertview.findViewById(R.id.tv_event_name);
        TextView tv_event_place = convertview.findViewById(R.id.tv_event_place);
        TextView tv_event_description = convertview.findViewById(R.id.tv_event_description);
        TextView tv_event_start_time = convertview.findViewById(R.id.tv_event_start_time);
        TextView tv_event_end_time = convertview.findViewById(R.id.tv_event_end_time);

        tv_event_name.setText(event.getName());
        tv_event_place.setText(event.getPlace());
        tv_event_description.setText(event.getDescription());
        tv_event_start_time.setText(Utils.getDefaultTime(event.getStart_time()));
        tv_event_end_time.setText(Utils.getDefaultTime(event.getEnd_time()));

        return convertview;
    }
}
