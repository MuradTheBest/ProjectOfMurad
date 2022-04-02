package com.example.projectofmurad.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class EventSlidePageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    private CalendarEvent next_event;
    private CalendarEvent last_event;

    public EventSlidePageAdapter(@NonNull Fragment fragment, CalendarEvent next_event, CalendarEvent last_event) {
        super(fragment);

        this.next_event = next_event;
        this.last_event = last_event;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){

            case 0:
                Event_Info_DialogFragment event_info_fragment = new Event_Info_DialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean(Event_Info_DialogFragment.ARG_IS_SHOWS_DIALOG, false);
                bundle.putSerializable(UtilsCalendar.KEY_EVENT, last_event);

                event_info_fragment.setArguments(bundle);

                return event_info_fragment;
            case 1:
                event_info_fragment = new Event_Info_DialogFragment();

                bundle = new Bundle();
                bundle.putBoolean(Event_Info_DialogFragment.ARG_IS_SHOWS_DIALOG, false);
                bundle.putSerializable(UtilsCalendar.KEY_EVENT, next_event);

                event_info_fragment.setArguments(bundle);

                return event_info_fragment;
            default:
                return null;
        }
    }



    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
