package com.example.ziwenzhao.db;

import android.arch.persistence.room.RoomDatabase;

import com.example.ziwenzhao.db.MoviePersistData;
import com.example.ziwenzhao.db.MoviePersistDataDao;

@android.arch.persistence.room.Database(entities = {MoviePersistData.class}, version = 1)

public abstract class Database extends RoomDatabase {
    public  abstract MoviePersistDataDao moviePersistDataDao();
}
