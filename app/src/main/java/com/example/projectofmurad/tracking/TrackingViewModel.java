package com.example.projectofmurad.tracking;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.projectofmurad.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackingViewModel extends AndroidViewModel {

    public static MutableLiveData<Boolean> isRunning = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> isNewTraining = new MutableLiveData<>(true);

    public static MutableLiveData<List<LatLng>> locationsData = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<List<LatLng>> stops = new MutableLiveData<>(new ArrayList<>());

    public static MutableLiveData<Long> timeData = new MutableLiveData<>(0L);
    public static MutableLiveData<Long> totalTimeData = new MutableLiveData<>(0L);

    public static MutableLiveData<Double> avgSpeedData = new MutableLiveData<>(0D);
    public static MutableLiveData<HashMap<String, Double>> speedsData = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<Double> maxSpeedData = new MutableLiveData<>(0D);

    public static MutableLiveData<String> avgPaceData = new MutableLiveData<>("--'--" + '"' + "/km");
    public static MutableLiveData<HashMap<String, Float>> pacesData = new MutableLiveData<>(new HashMap<>());
    public static MutableLiveData<String> maxPaceData = new MutableLiveData<>("--'--" + '"' + "/km");

    public static MutableLiveData<Double> totalDistance = new MutableLiveData<>(0D);

    public static MutableLiveData<Training> training = new MutableLiveData<>();


    public static MutableLiveData<Integer> activity_transition_enter = new MutableLiveData<>(0);
    public static MutableLiveData<Integer> activity_transition_exit = new MutableLiveData<>(0);

    public static MutableLiveData<String> trainingType = new MutableLiveData<>(null);
    public static MutableLiveData<String> eventPrivateId = new MutableLiveData<>(null);

    public static final String PRIVATE_TRAINING = Utils.APPLICATION_ID + "private_training";
    public static final String GROUP_TRAINING = Utils.APPLICATION_ID + "group_training";

    public static void clearData(){
        isRunning.setValue(false);
        isNewTraining.setValue(true);
        locationsData = new MutableLiveData<>();
        stops = new MutableLiveData<>();

        timeData.setValue(0L);
        totalTimeData.setValue(0L);

        avgSpeedData.setValue(0D);
        speedsData.setValue(new HashMap<>());
        maxSpeedData.setValue(0D);

        avgPaceData.setValue("");
        pacesData.setValue(new HashMap<>());
        maxPaceData.setValue("");

        totalDistance.setValue(0D);
    }

    public TrackingViewModel(@NonNull Application application) {
        super(application);
    }

}
