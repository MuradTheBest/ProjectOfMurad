package com.example.projectofmurad;

import androidx.annotation.NonNull;

public enum Show {
    ALL(2),
    MADRICH(1),
    NO_ONE(0);

    private int value;

    Show(int value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return (value == 2) ? "everyone" : (value == 1) ? "madrichs only" : "no one";
    }

    @NonNull
    public String print(){
        return toString().substring(0, 1).toUpperCase() + toString().substring(1).toLowerCase();
    }

    public final int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
