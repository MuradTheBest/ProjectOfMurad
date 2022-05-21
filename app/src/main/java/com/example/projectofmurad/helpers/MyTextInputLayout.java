package com.example.projectofmurad.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MyTextInputLayout extends TextInputLayout {

    public MyTextInputLayout(@NonNull Context context) {
        super(context);

        Objects.requireNonNull(getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setError(null);
            }
        });
    }

    @NonNull
    public String getText() {
        return Objects.requireNonNull(getEditText()).getText().toString();
    }

    public void setText(String text) {
        Objects.requireNonNull(getEditText()).setText(text);
    }
}
