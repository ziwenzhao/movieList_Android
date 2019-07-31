package com.example.ziwenzhao.db;

import android.arch.persistence.room.RoomDatabase;

@android.arch.persistence.room.Database(entities = {MoviePersistData.class}, version = 1)

public abstract class Database extends RoomDatabase {
    public  abstract MoviePersistDataDao moviePersistDataDao();
}
