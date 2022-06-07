package com.example.projectofmurad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.calendar.CalendarEvent;

import java.time.LocalDate;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<CalendarEvent> last_event;
    private final MutableLiveData<CalendarEvent> next_event;

    private MutableLiveData<LocalDate> eventDate;
    private MutableLiveData<String> eventPrivateId;

    private final MutableLiveData<Integer> ready;

    public MainViewModel(@NonNull Application application) {
        super(application);

        last_event = new MutableLiveData<>();
        next_event = new MutableLiveData<>();

        eventDate = new MutableLiveData<>(LocalDate.now());
        eventPrivateId = new MutableLiveData<>();

        ready = new MutableLiveData<>(0);
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

    public MutableLiveData<LocalDate> getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate.setValue(eventDate);
    }

    public void resetEventDate() {
        this.eventDate = new MutableLiveData<>();
    }

    public MutableLiveData<String> getEventPrivateId() {
        return eventPrivateId;
    }

    public void setEventPrivateId(String eventPrivateId) {
        this.eventPrivateId.setValue(eventPrivateId);
    }

    public void resetEventPrivateId() {
        this.eventPrivateId = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getReady() {
        return ready;
    }

    public void setReady(int ready) {
        this.ready.setValue(ready);
    }

    public void resetReady() {
        this.ready.setValue(0);
    }

    public void addReady() {
        this.ready.setValue(this.ready.getValue()+1);
    }
}
