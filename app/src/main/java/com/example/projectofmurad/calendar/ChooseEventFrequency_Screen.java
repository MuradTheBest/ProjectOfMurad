package com.example.projectofmurad.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.projectofmurad.R;

import java.time.LocalDate;
import java.util.List;

public class ChooseEventFrequency_Screen extends AppCompatActivity {

    private OnDayFrequencyListener onDayFrequencyListener;
    private OnDayOfWeekFrequencyListener onDayOfWeekFrequencyListener;
    private OnDayAndMonthFrequencyListener onDayAndMonthFrequencyListener;
    private OnDayOfWeekAndMonthFrequencyListener onDayOfWeekAndMonthFrequencyListener;
    private OnDayAndYearFrequencyListener onDayAndYearFrequencyListener;
    private OnDayOfWeekAndYearFrequencyListener onDayOfWeekAndYearFrequencyListener;
    private OnNeverFrequencyListener onNeverFrequencyListener;

    private int start_day;
    private int start_month;
    private int start_year;

    private LocalDate startDate;

    private Intent gotten_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event_frequency_screen);

        getSupportActionBar().setTitle("Choose frequency of event");

        gotten_intent = getIntent();

        start_day = gotten_intent.getIntExtra("day", 0);
        start_month = gotten_intent.getIntExtra("month", 0);
        start_year = gotten_intent.getIntExtra("year", 0);

        startDate = LocalDate.of(start_year, start_month, start_day);

    }

    public interface OnDayFrequencyListener {
        void setOnDayFrequency(int selected_frequency, int selected_amount);
        void setOnDayFrequency(int selected_frequency, LocalDate selected_end);
    }

    public interface OnDayOfWeekFrequencyListener {
        void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency,
                                     int selected_amount);
        void setOnDayOfWeekFrequency(List<Boolean> selected_array_frequencyDayOfWeek, int selected_frequency,
                                     LocalDate selected_end);
    }

    public interface OnDayAndMonthFrequencyListener {
        void setOnDayAndMonthFrequency(int selected_day, int selected_frequency, int selected_amount);
        void setOnDayAndMonthFrequency(int selected_day, int selected_frequency, LocalDate selected_end);
    }

    public interface OnDayOfWeekAndMonthFrequencyListener {
        void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                             int selected_frequency, int selected_amount);
        void setOnDayOfWeekAndMonthFrequency(int selected_weekNumber, int selected_dayOfWeekPosition,
                                             int selected_frequency, LocalDate selected_end);
    }

    public interface OnDayAndYearFrequencyListener {
        void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                      int selected_frequency, int selected_amount);
        void setOnDayAndYearFrequency(int selected_day, int selected_month,
                                      int selected_frequency, LocalDate selected_end);
    }

    public interface OnDayOfWeekAndYearFrequencyListener {
        void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                            int selected_frequency, int selected_amount);
        void setOnDayOfWeekAndYearFrequency(int selected_weekNumber, int selected_dayOfWeekPosition, int selected_month,
                                            int selected_frequency, LocalDate selected_end);
    }

    public interface OnNeverFrequencyListener {
        void setOnNeverFrequency();

    }
}