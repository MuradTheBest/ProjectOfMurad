package com.example.projectofmurad;

import com.example.projectofmurad.training.Training;

import java.util.HashMap;
import java.util.List;

public class SuperUserTraining {

    private HashMap<String, List<Training>> trainings;

    public SuperUserTraining() {}

    public SuperUserTraining(HashMap<String, List<Training>> trainings) {
        this.trainings = trainings;
    }

    public HashMap<String, List<Training>> getTrainings() {
        return trainings;
    }

    public void setTrainings(HashMap<String, List<Training>> trainings) {
        this.trainings = trainings;
    }
}
