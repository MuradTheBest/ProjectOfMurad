package com.example.projectofmurad.calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;

public class SmartAdapter extends FragmentStateAdapter {
    LocalDate selectedDate;
    ArrayList<Calendar_Month_Fragment> fragmentArrayList;

    public SmartAdapter(@NonNull FragmentActivity fragmentActivity, LocalDate selectedDate) {
        super(fragmentActivity);
        this.selectedDate = selectedDate;
    }

    public SmartAdapter(@NonNull Calendar_Month_Fragment fragment) {
        super(fragment);
    }

    public SmartAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Calendar_Month_Fragment> fragmentArrayList) {
        super(fragmentManager, lifecycle);
        this.fragmentArrayList = fragmentArrayList;
    }

    @Override
    public Calendar_Month_Fragment createFragment(int position) {
        /*switch(position){
            case 0:
                return Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(2));
            case 1:
                return Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(1));
            case 2:
                return Utils.createCalendar_Month_Fragment(selectedDate);
            case 3:
                return Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(1));
            case 4:
                return Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(2));
            default:
                return null;

        }*/

        return fragmentArrayList.get(position);
    }

    public void add(int position, Calendar_Month_Fragment fragment) {
        fragmentArrayList.add(position, fragment);
        notifyItemInserted(position);
    }

    public void replace(int position, Calendar_Month_Fragment fragment) {
        fragmentArrayList.set(position, fragment);
        notifyItemChanged(position);
    }

    public void remove(int position) {
        fragmentArrayList.remove(position);
        notifyItemRemoved(position);
    }


    public Calendar_Month_Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}