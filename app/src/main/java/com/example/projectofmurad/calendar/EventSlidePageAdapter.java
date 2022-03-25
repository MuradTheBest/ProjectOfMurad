package com.example.projectofmurad.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectofmurad.MainActivity;

import org.jetbrains.annotations.NotNull;

public class EventSlidePageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    private CalendarEvent next_event;
    private CalendarEvent last_event;

    public EventSlidePageAdapter(MainActivity mainActivity) {
        super(mainActivity);
    }

    public EventSlidePageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public EventSlidePageAdapter(@NonNull Fragment fragment, CalendarEvent next_event, CalendarEvent last_event) {
        super(fragment);

        this.next_event = next_event;
        this.last_event = last_event;
    }

    public EventSlidePageAdapter(@NonNull FragmentManager fragmentManager,
                                 @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){

            case 0:
                Event_Info_DialogFragment event_info_fragment = new Event_Info_DialogFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(UtilsCalendar.KEY_EVENT, next_event);
                bundle.putBoolean(Event_Info_DialogFragment.ARG_IS_SHOWS_DIALOG, false);

                event_info_fragment.setArguments(bundle);

                return event_info_fragment;
            case 1:
                event_info_fragment = new Event_Info_DialogFragment();

                bundle = new Bundle();
                bundle.putSerializable(UtilsCalendar.KEY_EVENT, last_event);
                bundle.putBoolean(Event_Info_DialogFragment.ARG_IS_SHOWS_DIALOG, false);

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
