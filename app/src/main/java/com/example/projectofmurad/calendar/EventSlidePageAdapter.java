package com.example.projectofmurad.calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventSlidePageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    private final CalendarEvent next_event;
    private final CalendarEvent last_event;

    public EventSlidePageAdapter(@NonNull Fragment fragment, CalendarEvent next_event, CalendarEvent last_event) {
        super(fragment);

        this.next_event = next_event;
        this.last_event = last_event;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return Event_Info_DialogFragment.newInstance(last_event, false);
            case 1:
                return Event_Info_DialogFragment.newInstance(next_event, false);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
