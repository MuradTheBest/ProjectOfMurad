package com.example.projectofmurad.calendar;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.R;
import com.example.projectofmurad.utils.CalendarUtils;
import com.example.projectofmurad.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Event frequency view model.
 */
public class EventFrequencyViewModel extends AndroidViewModel {

    /**
     * The Frequency type.
     */
    public final MutableLiveData<CalendarEvent.FrequencyType> frequencyType;
    /**
     * The Frequency.
     */
    public final MutableLiveData<Integer> frequency;
    /**
     * The Amount.
     */
    public final MutableLiveData<Integer> amount;
    /**
     * The Msg.
     */
    public final MutableLiveData<String> msg;
    /**
     * The End.
     */
    public final MutableLiveData<LocalDate> end;
    /**
     * The Last.
     */
    public final MutableLiveData<Boolean> last;
    /**
     * The Day.
     */
    public final MutableLiveData<Integer> day;
    /**
     * The Day of week position.
     */
    public final MutableLiveData<Integer> dayOfWeekPosition;
    /**
     * The Days of week.
     */
    public final MutableLiveData<List<Boolean>> daysOfWeek;
    /**
     * The Week number.
     */
    public final MutableLiveData<Integer> weekNumber;
    /**
     * The Month.
     */
    public final MutableLiveData<Integer> month;

    /**
     * Instantiates a new Event frequency view model.
     *
     * @param application the application
     */
    public EventFrequencyViewModel(@NonNull Application application) {
        super(application);

        this.frequencyType = new MutableLiveData<>();
        this.frequency = new MutableLiveData<>();
        this.amount = new MutableLiveData<>();
        this.msg = new MutableLiveData<>();
        this.end = new MutableLiveData<>();
        this.last = new MutableLiveData<>();
        this.day = new MutableLiveData<>();
        this.dayOfWeekPosition = new MutableLiveData<>();
        this.daysOfWeek = new MutableLiveData<>();
        this.weekNumber = new MutableLiveData<>();
        this.month = new MutableLiveData<>();
    }

    /**
     * Clear frequency data.
     */
    public void clearFrequencyData() {
        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
        this.frequency.setValue(1);
        this.amount.setValue(0);
        this.msg.setValue("");
        this.end.setValue(LocalDate.now());
        this.last.setValue(false);
        this.day.setValue(0);
        this.dayOfWeekPosition.setValue(0);
        this.daysOfWeek.setValue(new ArrayList<>());
        this.weekNumber.setValue(0);
        this.month.setValue(0);
    }

    /**
     * Sets on day frequency.
     *
     * @param selected_frequency the selected frequency
     * @param selected_amount    the selected amount
     */
    public void setOnDayFrequency(int selected_frequency, int selected_amount) {

        Log.d(Utils.EVENT_TAG, "setOnDayFrequency");
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.msg.setValue("Every " + selected_frequency + " days, " + selected_amount + " times");
    }

    /**
     * Sets on day frequency.
     *
     * @param selected_frequency the selected frequency
     * @param selected_end       the selected end
     */
    public void setOnDayFrequency(int selected_frequency, LocalDate selected_end) {

        Log.d(Utils.EVENT_TAG, "setOnDayFrequency");
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);

        this.msg.setValue("Every " + selected_frequency + " days until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    /**
     * Sets on day of week frequency.
     *
     * @param selected_array_frequencyDayOfWeek the selected array frequency day of week
     * @param selected_frequency                the selected frequency
     * @param selected_amount                   the selected amount
     */
    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency, int selected_amount) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekFrequency");

        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.daysOfWeek.setValue(selected_array_frequencyDayOfWeek);

        String days_of_week = "";
        for(int i = 0; i < selected_array_frequencyDayOfWeek.size(); i++) {
            if(selected_array_frequencyDayOfWeek.get(i)){
                days_of_week += ", " + DayOfWeek.of(i+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale());
            }
        }
        days_of_week = days_of_week.replaceFirst(", ", "");
        this.msg.setValue("Every " + selected_frequency + " weeks on " + days_of_week + " " + selected_amount + " times");
    }

    /**
     * Sets on day of week frequency.
     *
     * @param selected_array_frequencyDayOfWeek the selected array frequency day of week
     * @param selected_frequency                the selected frequency
     * @param selected_end                      the selected end
     */
    public void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency, LocalDate selected_end) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekFrequency");

        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);

        this.daysOfWeek.setValue(selected_array_frequencyDayOfWeek);

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

        this.msg.setValue("Every " + selected_frequency + " weeks on " + days_of_week + " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    /**
     * Sets on day and month frequency.
     *
     * @param selected_day       the selected day
     * @param selected_frequency the selected frequency
     * @param selected_amount    the selected amount
     * @param isLast             the is last
     */
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, int selected_amount, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayAndMonthFrequency");
        clearFrequencyData();

        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.day.setValue(selected_day);

        this.msg.setValue("Every " + selected_frequency + " months on " + selected_day + ", " + selected_amount + " times");
    }

    /**
     * Sets on day and month frequency.
     *
     * @param selected_day       the selected day
     * @param selected_frequency the selected frequency
     * @param selected_end       the selected end
     * @param isLast             the is last
     */
    public void setOnDayAndMonthFrequency(int selected_day,
                                          int selected_frequency, LocalDate selected_end, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayAndMonthFrequency");

        clearFrequencyData();
        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_MONTH_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);
        this.day.setValue(selected_day);

        this.msg.setValue("Every " + selected_frequency + " months on " +
                selected_day + " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    /**
     * Sets on day of week and month frequency.
     *
     * @param selected_weekNumber        the selected week number
     * @param selected_dayOfWeekPosition the selected day of week position
     * @param selected_frequency         the selected frequency
     * @param selected_amount            the selected amount
     * @param isLast                     the is last
     */
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, int selected_amount, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekAndMonthFrequency");

        clearFrequencyData();
        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        this.weekNumber.setValue(selected_weekNumber);

        this.msg.setValue("Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition +1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    /**
     * Sets on day of week and month frequency.
     *
     * @param selected_weekNumber        the selected week number
     * @param selected_dayOfWeekPosition the selected day of week position
     * @param selected_frequency         the selected frequency
     * @param selected_end               the selected end
     * @param isLast                     the is last
     */
    public void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                                int selected_frequency, LocalDate selected_end, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekAndMonthFrequency");

        clearFrequencyData();
        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_MONTH_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);

        this.dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        this.weekNumber.setValue(selected_weekNumber);

        this.msg.setValue("Every " + selected_frequency + " months on " + selected_weekNumber + " " +
                DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    /**
     * Sets on day and year frequency.
     *
     * @param selected_day       the selected day
     * @param selected_month     the selected month
     * @param selected_frequency the selected frequency
     * @param selected_amount    the selected amount
     * @param isLast             the is last
     */
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, int selected_amount, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayAndYearFrequency");

        clearFrequencyData();
        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.day.setValue(selected_day);
        this.month.setValue(selected_month);

        this.msg.setValue("Every " + selected_frequency + " years on " + selected_day + " of " +
                Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    /**
     * Sets on day and year frequency.
     *
     * @param selected_day       the selected day
     * @param selected_month     the selected month
     * @param selected_frequency the selected frequency
     * @param selected_end       the selected end
     * @param isLast             the is last
     */
    public void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                         int selected_frequency, LocalDate selected_end, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayAndYearFrequency");

        clearFrequencyData();
        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_AND_YEAR_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);
        this.day.setValue(selected_day);

        this.month.setValue(selected_month);

       this.msg.setValue(getString(R.string.day_and_year_text,
                selected_frequency,
                selected_day,
                Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()),
                CalendarUtils.DateToTextLocal(selected_end)));
    }

    /**
     * Sets on day of week and year frequency.
     *
     * @param selected_weekNumber        the selected week number
     * @param selected_dayOfWeekPosition the selected day of week position
     * @param selected_month             the selected month
     * @param selected_frequency         the selected frequency
     * @param selected_amount            the selected amount
     * @param isLast                     the is last
     */
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, int selected_amount, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekAndYearFrequency");
        clearFrequencyData();

        this.last.setValue(isLast);


        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_AMOUNT);

        this.frequency.setValue(selected_frequency);
        this.amount.setValue(selected_amount);

        this.dayOfWeekPosition.setValue(selected_dayOfWeekPosition);
        this.weekNumber.setValue(selected_weekNumber);
        this.month.setValue(selected_month);

        this.msg.setValue("Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                ", " + selected_amount + " times");
    }

    /**
     * Sets on day of week and year frequency.
     *
     * @param selected_weekNumber        the selected week number
     * @param selected_dayOfWeekPosition the selected day of week position
     * @param selected_month             the selected month
     * @param selected_frequency         the selected frequency
     * @param selected_end               the selected end
     * @param isLast                     the is last
     */
    public void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                               int selected_frequency, LocalDate selected_end, boolean isLast) {

        Log.d(Utils.EVENT_TAG, "setOnDayOfWeekAndYearFrequency" );
        clearFrequencyData();

        this.last.setValue(isLast);

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_OF_WEEK_AND_YEAR_BY_END);

        this.frequency.setValue(selected_frequency);
        this.end.setValue(selected_end);

        this.dayOfWeekPosition.setValue( selected_dayOfWeekPosition);
        this.weekNumber.setValue(selected_weekNumber);
        this.month.setValue(selected_month);

        this.msg.setValue("Every " + selected_frequency + " years on " + selected_weekNumber + " " + DayOfWeek.of(selected_dayOfWeekPosition+1).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " of " + Month.of(selected_month).getDisplayName(TextStyle.FULL, CalendarUtils.getLocale()) +
                " until " + CalendarUtils.DateToTextLocal(selected_end));
    }

    /**
     * Sets on never frequency.
     */
    public void setOnNeverFrequency() {
        clearFrequencyData();

        this.frequencyType.setValue(CalendarEvent.FrequencyType.DAY_BY_END);
        this.frequency.setValue(1);
    }

    /**
     * Gets string.
     *
     * @param resId the res id
     *
     * @return the string
     */
    public final String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    /**
     * Gets string.
     *
     * @param resId      the res id
     * @param formatArgs the format args
     *
     * @return the string
     */
    public final String getString(@StringRes int resId, Object... formatArgs) {
        return getApplication().getString(resId, formatArgs);
    }
}
