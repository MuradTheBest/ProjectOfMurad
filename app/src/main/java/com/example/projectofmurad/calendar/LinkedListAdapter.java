package com.example.projectofmurad.calendar;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class LinkedListAdapter extends FragmentStateAdapter {
    LocalDate selectedDate;
    LinkedList<Calendar_Month_Fragment> fragments;

    public LinkedListAdapter(@NonNull FragmentActivity fragmentActivity, LocalDate selectedDate) {
        super(fragmentActivity);
        this.selectedDate = LocalDate.now();
    }

    public LinkedListAdapter(@NonNull Calendar_Month_Fragment fragment) {
        super(fragment);
    }

    public LinkedListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, LinkedList<Calendar_Month_Fragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Calendar_Month_Fragment createFragment(int position) {
        switch(position){
            case 0:
                return this.fragments.get(0);
            case 1:
                return this.fragments.get(1);
            case 2:
                return this.fragments.get(2);
            case 3:
                return this.fragments.get(3);
            case 4:
                return this.fragments.get(4);
            default:
                return null;

        }

        /*Queue<Calendar_Month_Fragment> tmp = this.fragments;
        Log.d("view_pager2", "size " + tmp.size());
        int i=0;
        while(this.fragments != null){
            Calendar_Month_Fragment fragment = this.fragments.remove();
            Log.d("view_pager2", "size " + this.fragments.size());
            //Log.d("view_pager2", Utils.getDefaultDate(fragment.getSelectedDate()));
            if(i == position){
                return fragment;
            }
            i++;
            Log.d("view_pager2", "i = " + i);
        }
        this.fragments.addAll(tmp);

        return null;*/
    }

    public void add(Calendar_Month_Fragment fragment) {
        this.fragments.add(fragment);
        this.notifyDataSetChanged();
    }

    /*public void replace(int position, Calendar_Month_Fragment fragment) {
        fragments.set(position, fragment);
        notifyDataSetChanged();
    }*/

    public void remove() {
        this.fragments.remove();
        this.notifyDataSetChanged();
    }

    public void movedToPrevious(LocalDate centralDate){

        /*Queue<Calendar_Month_Fragment> tmp = fragments;
        this.fragments.clear();
        this.notifyItemRangeRemoved(0, 5);
        this.fragments.add(Utils.createCalendar_Month_Fragment(centralDate.minusMonths(2)));
        this.notifyItemInserted(0);
        this.fragments.addAll(tmp);
        this.notifyItemRangeInserted(1, 5);
        this.fragments.remove(createFragment(5));
        this.notifyItemRemoved(5);*/
        Log.d("view_pager2", "month removed " + this.fragments.removeLast().getSelectedDate().getMonth().toString());
        Log.d("view_pager2", "new size = " + this.fragments.size());
        //this.notifyItemRemoved(4);
        this.notifyDataSetChanged();

        this.fragments.addFirst(Utils.createCalendar_Month_Fragment(centralDate.minusMonths(2)));
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
/*
        this.remove();
        this.notifyItemRemoved(0);
        this.add(Utils.createCalendar_Month_Fragment(centralDate.plusMonths(1)));
        this.notifyItemInserted(4);*/

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
    }

    /*public Calendar_Month_Fragment getItem(int position) {
        return fragments.get(position);
    }*/

    @Override
    public int getItemCount() {
        return 5;
    }


}