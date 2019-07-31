package com.example.ziwenzhao.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoviePersistDataDao {
    @Query("Select * from MoviePersistData")
    List<MoviePersistData> getAll();

    @Insert
    void insertAll(List<MoviePersistData> moviePersistDataList);

    @Delete
    void delete(MoviePersistData moviePersistData);

    @Query("Delete from MoviePersistData")
    void deleteAll();

}
