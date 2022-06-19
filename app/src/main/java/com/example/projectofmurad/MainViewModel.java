package com.example.projectofmurad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.calendar.CalendarEvent;

import java.time.LocalDate;

/**
 * The type Main view model.
 */
public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<CalendarEvent> last_event;
    private final MutableLiveData<CalendarEvent> next_event;

    private MutableLiveData<LocalDate> eventDate;
    private MutableLiveData<String> eventPrivateId;

    private final MutableLiveData<Integer> ready;

    /**
     * Instantiates a new Main view model.
     *
     * @param application the application
     */
    public MainViewModel(@NonNull Application application) {
        super(application);

        last_event = new MutableLiveData<>();
        next_event = new MutableLiveData<>();

        eventDate = new MutableLiveData<>(LocalDate.now());
        eventPrivateId = new MutableLiveData<>();

        ready = new MutableLiveData<>(0);
    }

    /**
     * Gets last event.
     *
     * @return the last event
     */
    public MutableLiveData<CalendarEvent> getLastEvent() {
        return last_event;
    }

    /**
     * Sets last event.
     *
     * @param last_event the last event
     */
    public void setLastEvent(CalendarEvent last_event) {
        this.last_event.setValue(last_event);
    }

    /**
     * Gets next event.
     *
     * @return the next event
     */
    public MutableLiveData<CalendarEvent> getNextEvent() {
        return next_event;
    }

    /**
     * Sets nex event.
     *
     * @param next_event the next event
     */
    public void setNexEvent(CalendarEvent next_event) {
        this.next_event.setValue(next_event);
    }

    /**
     * Gets event date.
     *
     * @return the event date
     */
    public MutableLiveData<LocalDate> getEventDate() {
        return eventDate;
    }

    /**
     * Sets event date.
     *
     * @param eventDate the event date
     */
    public void setEventDate(LocalDate eventDate) {
        this.eventDate.setValue(eventDate);
    }

    /**
     * Reset event date.
     */
    public void resetEventDate() {
        this.eventDate = new MutableLiveData<>();
    }

    /**
     * Gets event private id.
     *
     * @return the event private id
     */
    public MutableLiveData<String> getEventPrivateId() {
        return eventPrivateId;
    }

    /**
     * Sets event private id.
     *
     * @param eventPrivateId the event private id
     */
    public void setEventPrivateId(String eventPrivateId) {
        this.eventPrivateId.setValue(eventPrivateId);
    }

    /**
     * Reset event private id.
     */
    public void resetEventPrivateId() {
        this.eventPrivateId = new MutableLiveData<>();
    }

    /**
     * Gets ready.
     *
     * @return the ready
     */
    public MutableLiveData<Integer> getReady() {
        return ready;
    }

    /**
     * Reset ready.
     */
    public void resetReady() {
        this.ready.setValue(0);
    }

    /**
     * Add ready.
     */
    public void addReady() {
        this.ready.setValue(this.ready.getValue()+1);
    }
}
