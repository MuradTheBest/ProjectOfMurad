package com.example.projectofmurad;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    public static MutableLiveData<Boolean> toSwipeFragments = new MutableLiveData<>();

    public static MutableLiveData<Boolean> toSwipeViewModelForTrainings = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }
}
