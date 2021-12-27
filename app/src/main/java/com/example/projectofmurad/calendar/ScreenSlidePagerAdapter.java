package com.example.projectofmurad.calendar;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectofmurad.Utils;

import java.time.LocalDate;
import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    LocalDate selectedDate = LocalDate.now();
    FragmentActivity fragmentActivity;
    ArrayList<Calendar_Month_Fragment> fragmentArrayList = new ArrayList<>();


    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, LocalDate selectedDate) {
        super((FragmentActivity) fragmentActivity);

        this.fragmentActivity = fragmentActivity;

        this.fragmentArrayList.add(Utils.createCalendar_Month_Fragment(LocalDate.now().minusMonths(2)));
        this.fragmentArrayList.add(Utils.createCalendar_Month_Fragment(LocalDate.now().minusMonths(1)));
        this.fragmentArrayList.add(Utils.createCalendar_Month_Fragment(LocalDate.now()));
        this.fragmentArrayList.add(Utils.createCalendar_Month_Fragment(LocalDate.now().plusMonths(1)));
        this.fragmentArrayList.add(Utils.createCalendar_Month_Fragment(LocalDate.now().plusMonths(2)));
        //this.selectedDate = this.fragmentArrayList.get(2).getSelectedDate();
    }

    public ScreenSlidePagerAdapter(@NonNull Calendar_Month_Fragment fragment) {
        super(fragment);
    }

    public ScreenSlidePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Calendar_Month_Fragment createFragment(int position) {
        LocalDate newDate;
        ScreenSlidePagerAdapter adapter;
        switch(position){
            case 0:
                /*newDate = selectedDate.minusMonths(2);
                adapter = new ScreenSlidePagerAdapter(fragmentActivity, newDate);
                Calendar_Test_Screen.viewPager2.setAdapter(adapter);*/
                return fragmentArrayList.get(0);
            case 1:
                /*newDate = selectedDate.minusMonths(1);
                adapter = new ScreenSlidePagerAdapter(fragmentActivity, newDate);
                Calendar_Test_Screen.viewPager2.setAdapter(adapter);*/
                Calendar_Month_Fragment fragment = fragmentArrayList.get(1);
                ArrayList<Calendar_Month_Fragment> tmp = Utils.move(fragmentArrayList, -1);
                Log.d("murad", Utils.getDefaultDate(selectedDate));
                tmp.add(0, Utils.createCalendar_Month_Fragment(selectedDate.minusMonths(2)));
                this.fragmentArrayList = tmp;
                return fragment;

            case 2:
                return fragmentArrayList.get(2);
            case 3:
                /*newDate = selectedDate.plusMonths(1);
                adapter = new ScreenSlidePagerAdapter(fragmentActivity, newDate);
                Calendar_Test_Screen.viewPager2.setAdapter(adapter);*/
                Calendar_Month_Fragment fragment2 = fragmentArrayList.get(3);
                ArrayList<Calendar_Month_Fragment> tmp2 = Utils.move(fragmentArrayList, 1);
                tmp2.add(4, Utils.createCalendar_Month_Fragment(selectedDate.plusMonths(2)));
                this.fragmentArrayList = tmp2;
                return fragment2;
            case 4:
                /*newDate = selectedDate.plusMonths(2);
                adapter = new ScreenSlidePagerAdapter(fragmentActivity, newDate);
                Calendar_Test_Screen.viewPager2.setAdapter(adapter);*/
                return fragmentArrayList.get(4);
            default:
                return null;

        }

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}