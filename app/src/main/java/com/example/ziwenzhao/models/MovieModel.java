package com.example.ziwenzhao.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class MovieModel {
    private Integer Id;
    private String title;
    private Bitmap bitmap;

    public MovieModel(Integer Id, String title, Bitmap bitmap) {
        this.Id = Id;
        this.title = title;
        this.bitmap = bitmap;
    }

    public Integer getId() {
        return Id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
