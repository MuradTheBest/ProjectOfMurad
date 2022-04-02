package com.example.projectofmurad.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventFrequencyViewModel extends AndroidViewModel {

    public MutableLiveData<String> frequencyType;

    public MutableLiveData<Integer> frequency;

    public MutableLiveData<Integer> amount;
    public MutableLiveData<LocalDate> frequency_end_date;

    public MutableLiveData<Integer> day;
    public MutableLiveData<String> dayOfWeek;
    public MutableLiveData<Integer> dayOfWeekPosition;
    public MutableLiveData<List<Boolean>> array_frequencyDayOfWeek;
    public MutableLiveData<Integer> weekNumber;
    public MutableLiveData<String> month_name;
    public MutableLiveData<Integer> month;

    public EventFrequencyViewModel(@NonNull Application application) {
        super(application);
    }

    public void clearFrequencyData(){
        frequencyType.setValue(Edit_Event_Screen.DAY_BY_END);

        this.frequency.setValue(1);
        this.amount.setValue(1);
        this.frequency_end_date.setValue(null);
        this.day.setValue(1);
        this.dayOfWeekPosition.setValue(0);
        this.array_frequencyDayOfWeek.setValue(new ArrayList<>());
        this.weekNumber.setValue(0);
        this.month.setValue(0);
    }
}
