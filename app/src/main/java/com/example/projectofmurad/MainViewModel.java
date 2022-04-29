package com.example.projectofmurad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.calendar.CalendarEvent;
import com.example.projectofmurad.tracking.Location;
import com.example.projectofmurad.training.MyRepository;
import com.example.projectofmurad.training.Training;

import java.time.LocalDate;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static MutableLiveData<Boolean> toSwipeFragments = new MutableLiveData<>();

    private static MutableLiveData<Boolean> toSwipeViewModelForTrainings = new MutableLiveData<>();

    private final MutableLiveData<CalendarEvent> last_event;
    private final MutableLiveData<CalendarEvent> next_event;

    private MutableLiveData<LocalDate> eventDate;
    private MutableLiveData<String> eventPrivateId;

    private MutableLiveData<String> filePath;

    private MutableLiveData<Location> location;
    private MutableLiveData<List<Location>> locations;

    private final MyRepository myRepository;

    private final MutableLiveData<Integer> ready;

    private final MutableLiveData<Boolean> scrollUp;

    private final MutableLiveData<Integer> selectedTab;


    public MainViewModel(@NonNull Application application) {
        super(application);

        last_event = new MutableLiveData<>();
        next_event = new MutableLiveData<>();

        eventDate = new MutableLiveData<>(LocalDate.now());
        eventPrivateId = new MutableLiveData<>();

        location = new MutableLiveData<>();
        locations = new MutableLiveData<>();

        filePath = new MutableLiveData<>();

        myRepository = new MyRepository(application);

        ready = new MutableLiveData<>(0);
        scrollUp = new MutableLiveData<>();
        selectedTab = new MutableLiveData<>(1);
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

    public static void setToSwipeViewModelForTrainings(boolean toSwipeViewModelForTrainings) {
        MainViewModel.toSwipeViewModelForTrainings.setValue(toSwipeViewModelForTrainings);
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

    public MutableLiveData<Location> getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location.setValue(location);
    }

    public void resetLocation() {
        this.location = new MutableLiveData<>();
    }

    public MutableLiveData<List<Location>> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations.setValue(locations);
    }

    public void resetLocations() {
        this.locations = new MutableLiveData<>();
    }

    public void addPrivateTraining(Training training){
        myRepository.insert(training);
    }

    public MutableLiveData<String> getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.setValue(filePath);
    }

    public void resetFilePath() {
        this.filePath = new MutableLiveData<>();
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

    public MutableLiveData<Boolean> getScrollUp() {
        return scrollUp;
    }

    public void setScrollUp(boolean scrollUp) {
        this.scrollUp.setValue(scrollUp);
    }

    public MutableLiveData<Integer> getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int selectedTab){
        this.selectedTab.setValue(selectedTab);
    }
}
