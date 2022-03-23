package com.example.projectofmurad.tracking;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyRepository {

    private TrainingDao mTrainingDao;
    private LiveData<List<Training>> mAllTrainings;

    private AppExecutors appExecutors;

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
    void insert(Training training) {
//        MyDatabase.databaseWriteExecutor.execute(() -> mTrainingDao.insert(training));
        appExecutors.diskIO().execute(() -> mTrainingDao.insert(training));
    }


    List<Training> getAll(){
        return mTrainingDao.getAll();
    }

    List<Training> loadAllByIds(int[] trainingIds) {
         return mTrainingDao.loadAllByIds(trainingIds);
    }

    List<Training> loadAllByPrivateId(String privateId) {
         return mTrainingDao.loadAllByPrivateId(privateId);
    }

    List<Training> loadAllByDay(int day) {
         return mTrainingDao.loadAllByDay(day);
    }

    List<Training> loadAllByDayAndMonth(int day, int month) {
         return mTrainingDao.loadAllByDayAndMonth(day, month);

    }

    List<Training> loadAllByMonth(int month) {
         return mTrainingDao.loadAllByMonth(month);

    }

    List<Training> loadAllByMonthAndYear(int month, int year) {
         return mTrainingDao.loadAllByMonthAndYear(month, year);
    }

    List<Training> loadAllByYear(int year) {
         return mTrainingDao.loadAllByYear(year);
    }

    List<Training> loadAllByDayAndMonthAndYear(int day, int month, int year) {
         return mTrainingDao.loadAllByDayAndMonthAndYear(day, month, year);
    }

    void insertAll(Training... trainings) {
        appExecutors.diskIO().execute(() -> mTrainingDao.insertAll(trainings));
    }

    void delete(Training training) {
         appExecutors.diskIO().execute(() -> mTrainingDao.delete(training));
    }

    void delete(String privateId) {
        appExecutors.diskIO().execute(() -> mTrainingDao.delete(privateId));
    }
}
