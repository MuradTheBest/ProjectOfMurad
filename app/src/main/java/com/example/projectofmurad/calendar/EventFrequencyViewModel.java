package com.example.projectofmurad.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.R;
import com.example.projectofmurad.helpers.CalendarUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;

public class EventFrequencyViewModel extends AndroidViewModel {

    public MutableLiveData<CalendarEvent.FrequencyType> frequencyType;
    public MutableLiveData<Integer> frequency;
    public MutableLiveData<Integer> amount;
    public MutableLiveData<Boolean> row_event;
    public MutableLiveData<String> msg;
    public MutableLiveData<LocalDate> end;
    public MutableLiveData<Boolean> last;
    public MutableLiveData<Integer> day;
    public MutableLiveData<Integer> dayOfWeekPosition;
    public MutableLiveData<List<Boolean>> array_frequencyDayOfWeek;
    public MutableLiveData<Integer> weekNumber;
    public MutableLiveData<Integer> month;

    public EventFrequencyViewModel(@NonNull Application application) {
        super(application);

        this.frequencyType = new MutableLiveData<>();
        this.frequency = new MutableLiveData<>();
        this.amount = new MutableLiveData<>();
        this.row_event = new MutableLiveData<>();
        this.msg = new MutableLiveData<>();
        this.end = new MutableLiveData<>();
        this.last = new MutableLiveData<>();
        this.day = new MutableLiveData<>();
        this.dayOfWeekPosition = new MutableLiveData<>();
        this.array_frequencyDayOfWeek = new MutableLiveData<>();
        this.weekNumber = new MutableLiveData<>();
        this.month = new MutableLiveData<>();
    }

    public void clearFrequencyType() {
        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
        frequency.setValue(1);
    }

    public void clearFrequencyData() {
        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
        this.frequency.setValue(1);
        this.amount = new MutableLiveData<>();
        this.row_event = new MutableLiveData<>();
        this.msg = new MutableLiveData<>();
        this.end = new MutableLiveData<>();
        this.last = new MutableLiveData<>();
        this.day = new MutableLiveData<>();
        this.dayOfWeekPosition = new MutableLiveData<>();
        this.array_frequencyDayOfWeek = new MutableLiveData<>();
        this.weekNumber = new MutableLiveData<>();
        this.month = new MutableLiveData<>();
    }

    public void setOnDayFrequency(int selected_frequency, int selected_amount) {
        this.row_event.setValue(false);
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.msg.postValue("Every " + selected_frequency + " days, " + selected_amount + " times");
    }

    public void setOnDayFrequency(int selected_frequency, LocalDate selected_end) {
        this.row_event.setValue(false);
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);

        this.msg.postValue("Every " + selected_frequency + " days until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek,
                                        int selected_frequency, int selected_amount) {
        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_AMOUNT);

        frequency.setValue(selected_frequency);
        amount.setValue(selected_amount);

        array_frequencyDayOfWeek.setValue(selected_array_frequencyDayOfWeek);

        String days_of_week = "";
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week += DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) + " ,";
            }
        }
        msg.postValue("Every " + selected_frequency + " weeks on " + days_of_week + " " + selected_amount + " times");
    }

    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek,
                                        int selected_frequency, LocalDate selected_end) {
        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_END);

        frequency.setValue(selected_frequency);
        end.setValue(selected_end);

        array_frequencyDayOfWeek.setValue(selected_array_frequencyDayOfWeek);

/*        String days_of_week = "";
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week += DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale() + " ,";
            }
        }*/

        StringBuilder days_of_week = new StringBuilder();
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week.append(DayOfWeek.of(i + 1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale())).append(" ,");
            }
        }

        msg.postValue("Every " + selected_frequency + " weeks on " + days_of_week + " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, int selected_amount, boolean isLast) {
        this.last.setValue(isLast);

        this.row_event.setValue(false);
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT);

        frequency.setValue(selected_frequency);
        amount.setValue(selected_amount);

        day.setValue(selected_day);

        msg.postValue("Every " + selected_frequency + " months on " + selected_day + ", " + selected_amount + " times");
    }

    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, LocalDate selected_end, boolean isLast) {
        this.last.setValue(isLast);

        this.row_event.setValue(false);
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);
        this.day.setValue(selected_day);

        this.msg.postValue("Every " + selected_frequency + " months on " +
                selected_day + " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, int selected_amount, boolean isLast) {
        last.setValue(isLast);

        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);

        frequency.setValue(selected_frequency);
        amount.setValue(selected_amount);

        dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        weekNumber.setValue(selected_weekNumber);

        msg.postValue("Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, LocalDate selected_end, boolean isLast) {
        last.setValue(isLast);

        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END);

        frequency.setValue(selected_frequency);
        end.setValue(selected_end);

        dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        weekNumber.setValue(selected_weekNumber);

        msg.postValue("Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, int selected_amount, boolean isLast) {
        last.setValue(isLast);

        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT);

        frequency.setValue(selected_frequency);
        amount.setValue(selected_amount);

        day.setValue(selected_day);
        month.setValue(selected_month);

        msg.postValue("Every " + selected_frequency + " years on " + selected_day + " of " +
                Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, LocalDate selected_end, boolean isLast) {
        last.setValue(isLast);
        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END);

        frequency.setValue(selected_frequency);
        end.setValue(selected_end);
        day.setValue(selected_day);

        month.setValue(selected_month);

       msg.postValue(getString(R.string.day_and_year_text,
                selected_frequency,
                selected_day,
                Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()),
                CalendarUtils.DateToTextLocal(selected_end)));
    }

    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, int selected_amount, boolean isLast) {
        last.setValue(isLast);

        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);

        frequency.setValue(selected_frequency);
        amount.setValue(selected_amount);

        dayOfWeekPosition.setValue(selected_dayOfWeekPosition);
        weekNumber.setValue(selected_weekNumber);
        month.setValue(selected_month);

        msg.postValue("Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, LocalDate selected_end, boolean isLast) {
        last.setValue(isLast);

        row_event.setValue(false);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END);

        frequency.setValue(selected_frequency);
        end.setValue(selected_end);

        dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        weekNumber.setValue(selected_weekNumber);
        month.setValue(selected_month);

        msg.postValue("Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    public void setOnNeverFrequency() {
        row_event.setValue(true);
        clearFrequencyData();

        frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
        frequency.setValue(1);
    }

    public final String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getApplication().getString(resId, formatArgs);
    }
}
