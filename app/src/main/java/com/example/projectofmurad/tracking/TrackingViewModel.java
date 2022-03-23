package com.example.projectofmurad.tracking;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackingViewModel extends AndroidViewModel {

    public static MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> isNewTraining = new MutableLiveData<>(true);

    public static MutableLiveData<List<LatLng>> locations = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<List<LatLng>> stops = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<Long> time = new MutableLiveData<>(0L);
    public static MutableLiveData<Long> totalTime = new MutableLiveData<>(0L);

    public static MutableLiveData<Double> avgSpeed = new MutableLiveData<>(0D);
    public static MutableLiveData<HashMap<String, Double>> speeds = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<Double> maxSpeed = new MutableLiveData<>(0D);

    public static MutableLiveData<String> avgPace = new MutableLiveData<>("--'--" + '"' + "/km");
    public static MutableLiveData<HashMap<String, Float>> paces = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<String> maxPace = new MutableLiveData<>("--'--" + '"' + "/km");

    public static MutableLiveData<Double> totalDistance = new MutableLiveData<>(0D);

    public static MutableLiveData<Training> training = new MutableLiveData<>();


    public static MutableLiveData<Integer> activity_transition_enter = new MutableLiveData<>(0);
    public static MutableLiveData<Integer> activity_transition_exit = new MutableLiveData<>(0);

    public static void clearData(){
        isRunning.setValue(false);
        isNewTraining.setValue(true);
        locations = new MutableLiveData<>();
        stops = new MutableLiveData<>();

        time.setValue(0L);
        totalTime.setValue(0L);

        avgSpeed.setValue(0D);
        speeds.setValue(new HashMap<>());
        maxSpeed.setValue(0D);

        avgPace.setValue("");
        paces.setValue(new HashMap<>());
        maxPace.setValue("");

        totalDistance.setValue(0D);
    }

    public TrackingViewModel(@NonNull Application application) {
        super(application);
    }

}
