package com.example.projectofmurad.training;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TrainingDao {

    @Query("SELECT * FROM trainings")
    List<Training> getAll();

    @Query("SELECT * FROM trainings WHERE id IN (:trainingIds)")
    List<Training> loadAllByIds(int[] trainingIds);

    @Query("SELECT * FROM trainings WHERE privateId = :privateId")
    List<Training> loadAllByPrivateId(String privateId);

    @Query("SELECT * FROM trainings WHERE day = :day")
    List<Training> loadAllByDay(int day);

    @Query("SELECT * FROM trainings WHERE day = :day AND month = :month")
    List<Training> loadAllByDayAndMonth(int day, int month);

    @Query("SELECT * FROM trainings WHERE month = :month")
    List<Training> loadAllByMonth(int month);

    @Query("SELECT * FROM trainings WHERE month = :month AND year = :year")
    List<Training> loadAllByMonthAndYear(int month, int year);

    @Query("SELECT * FROM trainings WHERE day = :year")
    List<Training> loadAllByYear(int year);

    @Query("SELECT * FROM trainings WHERE day = :day AND month = :month AND year = :year")
    List<Training> loadAllByDayAndMonthAndYear(int day, int month, int year);

    @Insert
    void insertAll(Training... trainings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Training training);

    @Delete
    void delete(Training training);

    @Query("DELETE FROM trainings WHERE privateId = :privateId")
    void delete(String privateId);
}
