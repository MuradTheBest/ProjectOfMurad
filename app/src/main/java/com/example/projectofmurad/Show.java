package com.example.projectofmurad;

import androidx.annotation.NonNull;

public enum Show {
    All(2),
    Madrich (1),
    NoOne(0);

    private int value;

    Show(int value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return (value == 2) ? "everyone" : (value == 1) ? "madrichs only" : "no one";
    }

    public final int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
