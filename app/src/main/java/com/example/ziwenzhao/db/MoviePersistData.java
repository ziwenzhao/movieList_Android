package com.example.ziwenzhao.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;


@Entity
public class MoviePersistData {

    @PrimaryKey
    private int Id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] imageBytes;

    public MoviePersistData(int Id, String title, byte[] imageBytes) {
        this.Id = Id;
        this.title = title;
        this.imageBytes = imageBytes;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImageBytes() {
        return  imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
