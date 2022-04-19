package com.example.projectofmurad.training;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MyRepository {

    private final TrainingDao mTrainingDao;
    private LiveData<List<Training>> mAllTrainings;

    private final AppExecutors appExecutors;

    // Note that in order to unit test the MyRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public MyRepository(Application application) {
        MyDatabase db = MyDatabase.getDatabase(application);
        mTrainingDao = db.trainingDao();
        appExecutors = new AppExecutors();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Training training) {
        appExecutors.diskIO().execute(() -> mTrainingDao.insert(training));
    }


    public MutableLiveData<List<Training>> getAll(){
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.getAll()));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByIds(int[] trainingIds) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByIds(trainingIds)));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByPrivateId(String privateId) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByPrivateId(privateId)));
         return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByDay(int day) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByDay(day)));
         return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByDayAndMonth(int day, int month) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByDayAndMonth(day, month)));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByMonth(int month) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByMonth(month)));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByMonthAndYear(int month, int year) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByMonthAndYear(month, year)));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByYear(int year) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByYear(year)));
        return trainings;
    }

    public MutableLiveData<List<Training>> loadAllByDayAndMonthAndYear(int day, int month, int year) {
        MutableLiveData<List<Training>> trainings = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> trainings.postValue(mTrainingDao.loadAllByDayAndMonthAndYear(day, month, year)));
        return trainings;
    }

    public void insertAll(Training... trainings) {
        appExecutors.diskIO().execute(() -> mTrainingDao.insertAll(trainings));
    }

    public void delete(Training training) {
         appExecutors.diskIO().execute(() -> mTrainingDao.delete(training));
    }

    public void delete(String privateId) {
        appExecutors.diskIO().execute(() -> mTrainingDao.delete(privateId));
    }
}
