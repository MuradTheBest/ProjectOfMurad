package com.example.projectofmurad.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectofmurad.R;
import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

public class FragmentAdapter extends RecyclerView.Adapter<FragmentAdapter.CalendarViewHolder> {
    private LinkedList<RecyclerView> recyclerViewLinkedList;
    private LayoutInflater inflater;

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView rv_month;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_month = itemView.findViewById(R.id.rv_month);
        }

    }

    public FragmentAdapter(LinkedList<RecyclerView> recyclerViewLinkedList, Context context) {
        inflater = LayoutInflater.from(context);
        this.recyclerViewLinkedList = recyclerViewLinkedList;
    }

    /*public void movedToPrevious(LocalDate centralDate){
        this.recyclerViewLinkedList.removeLast();

        this.recyclerViewLinkedList.addFirst();

        //this.notifyItemRemoved(4);
        this.notifyDataSetChanged();

        this.recyclerViewLinkedList.addFirst(Utils.createCalendar_Month_Fragment(centralDate.minusMonths(2)));
        Log.d("view_pager2", "month added " + this.fragments.get(0).getSelectedDate().getMonth().toString());
        Log.d("view_pager2", "new size = " + this.fragments.size());
        //this.notifyItemInserted(0);
        this.notifyDataSetChanged();

        Log.d("view_pager2", "movedToPrevious");
        Log.d("view_pager2", "position = 0   -> " + Utils.getDefaultDate(this.fragments.get(0).getSelectedDate()));
        Log.d("view_pager2", "position = 1   -> " + Utils.getDefaultDate(this.fragments.get(1).getSelectedDate()));
        Log.d("view_pager2", "position = 2   -> " + Utils.getDefaultDate(this.fragments.get(2).getSelectedDate()));
        Log.d("view_pager2", "position = 3   -> " + Utils.getDefaultDate(this.fragments.get(3).getSelectedDate()));
        Log.d("view_pager2", "position = 4   -> " + Utils.getDefaultDate(this.fragments.get(4).getSelectedDate()));
        Log.d("view_pager2", "---------------------------------------------------------------------------------------------------");



    }

    public void movedToNext(LocalDate centralDate){
        Log.d("view_pager2", "month removed " + this.fragments.removeFirst().getSelectedDate().getMonth().toString());
        Log.d("view_pager2", "new size = " + this.fragments.size());
        //this.notifyItemRemoved(0);
        this.notifyDataSetChanged();
        this.fragments.addLast(Utils.createCalendar_Month_Fragment(centralDate.plusMonths(2)));
        Log.d("view_pager2", "month added " + this.fragments.get(4).getSelectedDate().getMonth().toString());
        Log.d("view_pager2", "new size = " + this.fragments.size());
        //this.notifyItemInserted(4);
        this.notifyDataSetChanged();

        Log.d("view_pager2", "movedToNext");
        Log.d("view_pager2", "position = 0   -> " + Utils.getDefaultDate(this.fragments.get(0).getSelectedDate()));
        Log.d("view_pager2", "position = 1   -> " + Utils.getDefaultDate(this.fragments.get(1).getSelectedDate()));
        Log.d("view_pager2", "position = 2   -> " + Utils.getDefaultDate(this.fragments.get(2).getSelectedDate()));
        Log.d("view_pager2", "position = 3   -> " + Utils.getDefaultDate(this.fragments.get(3).getSelectedDate()));
        Log.d("view_pager2", "position = 4   -> " + Utils.getDefaultDate(this.fragments.get(4).getSelectedDate()));
        Log.d("view_pager2", "---------------------------------------------------------------------------------------------------");
    }*/

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_fragment_list, parent, false);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return recyclerViewLinkedList.size();
    }

}
