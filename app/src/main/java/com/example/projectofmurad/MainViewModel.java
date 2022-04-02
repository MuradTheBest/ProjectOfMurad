package com.example.projectofmurad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.calendar.CalendarEvent;

public class MainViewModel extends AndroidViewModel {

    private static MutableLiveData<Boolean> toSwipeFragments = new MutableLiveData<>();

    private static MutableLiveData<Boolean> toSwipeViewModelForTrainings = new MutableLiveData<>();

    private MutableLiveData<CalendarEvent> last_event;
    private MutableLiveData<CalendarEvent> next_event;

    public MainViewModel(@NonNull Application application) {
        super(application);

        last_event = new MutableLiveData<>();
        next_event = new MutableLiveData<>();

    }

    public static MutableLiveData<Boolean> getToSwipeFragments() {
        return toSwipeFragments;
    }

    public static void setToSwipeFragments(
            MutableLiveData<Boolean> toSwipeFragments) {
        MainViewModel.toSwipeFragments = toSwipeFragments;
    }

    public static MutableLiveData<Boolean> getToSwipeViewModelForTrainings() {
        return toSwipeViewModelForTrainings;
    }

    public static void setToSwipeViewModelForTrainings(
            MutableLiveData<Boolean> toSwipeViewModelForTrainings) {
        MainViewModel.toSwipeViewModelForTrainings = toSwipeViewModelForTrainings;
    }

    public MutableLiveData<CalendarEvent> getLastEvent() {
        return last_event;
    }

    public void setLastEvent(CalendarEvent last_event) {
        this.last_event.setValue(last_event);
    }

    public MutableLiveData<CalendarEvent> getNextEvent() {
        return next_event;
    }

    public void setNexEvent(CalendarEvent next_event) {
        this.next_event.setValue(next_event);
    }
}