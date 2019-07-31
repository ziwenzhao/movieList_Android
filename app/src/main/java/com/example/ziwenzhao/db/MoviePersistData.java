package com.example.ziwenzhao.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity
public class MoviePersistData {
    @PrimaryKey
    public int Id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] imageBytes;
}
