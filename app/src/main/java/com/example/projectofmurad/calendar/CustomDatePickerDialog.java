package com.example.projectofmurad.calendar;

import android.app.DatePickerDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomDatePickerDialog extends DatePickerDialog {
    public CustomDatePickerDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public CustomDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public CustomDatePickerDialog(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }


}
