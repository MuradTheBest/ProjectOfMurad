package com.example.projectofmurad.training;

import android.app.Application;

import androidx.lifecycle.LiveData;

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
//        MyDatabase.databaseWriteExecutor.execute(() -> mTrainingDao.insert(trainingData));
        appExecutors.diskIO().execute(() -> mTrainingDao.insert(training));
    }


    public List<Training> getAll(){
        return mTrainingDao.getAll();
    }

    public List<Training> loadAllByIds(int[] trainingIds) {
         return mTrainingDao.loadAllByIds(trainingIds);
    }

    public List<Training> loadAllByPrivateId(String privateId) {
         return mTrainingDao.loadAllByPrivateId(privateId);
    }

    public List<Training> loadAllByDay(int day) {
         return mTrainingDao.loadAllByDay(day);
    }

    public List<Training> loadAllByDayAndMonth(int day, int month) {
         return mTrainingDao.loadAllByDayAndMonth(day, month);

    }

    public List<Training> loadAllByMonth(int month) {
         return mTrainingDao.loadAllByMonth(month);

    }

    public List<Training> loadAllByMonthAndYear(int month, int year) {
         return mTrainingDao.loadAllByMonthAndYear(month, year);
    }

    public List<Training> loadAllByYear(int year) {
         return mTrainingDao.loadAllByYear(year);
    }

    public List<Training> loadAllByDayAndMonthAndYear(int day, int month, int year) {
         return mTrainingDao.loadAllByDayAndMonthAndYear(day, month, year);
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
