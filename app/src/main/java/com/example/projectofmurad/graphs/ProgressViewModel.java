package com.example.projectofmurad.graphs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.projectofmurad.training.MyRepository;
import com.example.projectofmurad.training.Training;

import java.util.List;

public class ProgressViewModel extends AndroidViewModel {

    private final MyRepository myRepository;

    public ProgressViewModel(@NonNull Application application) {
        super(application);

        myRepository = new MyRepository(application);
    }

    public LiveData<List<Training>> getPrivateTrainings(){
        return myRepository.getAll();
    }
}
