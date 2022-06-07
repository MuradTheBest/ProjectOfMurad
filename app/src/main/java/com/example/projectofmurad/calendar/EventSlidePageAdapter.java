package com.example.projectofmurad.calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * The type Event slide page adapter.
 */
public class EventSlidePageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;

    private final CalendarEvent next_event;
    private final CalendarEvent last_event;

    /**
     * Instantiates a new Event slide page adapter.
     *
     * @param fragment   the fragment
     * @param next_event the next event
     * @param last_event the last event
     */
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
                return (last_event == null)
                        ? ThereIsNoEventFragment.newInstance("There is no last event that happened")
                        : EventInfoDialogFragment.newInstance(last_event, false);
            case 1:
                return (next_event == null)
                        ? ThereIsNoEventFragment.newInstance("There is no next event that will happen")
                        : EventInfoDialogFragment.newInstance(next_event, false);
            default:
                return ThereIsNoEventFragment.newInstance("There is no event");
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
