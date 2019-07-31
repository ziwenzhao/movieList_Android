package com.example.ziwenzhao.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

@Dao
public interface MoviePersistDataDao {
    @Insert
    void insertAll(MoviePersistData ...moviePersistData);

    @Delete
    void delete(MoviePersistData moviePersistData);

    @Delete
    void deleteAll(MoviePersistData ...moviePersistData);
}
