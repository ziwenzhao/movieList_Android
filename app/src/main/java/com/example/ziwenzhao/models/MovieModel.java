package com.example.ziwenzhao.models;

import java.io.InputStream;

public class MovieModel {
    private Integer Id;
    private String title;
    private InputStream inputStream;

    public MovieModel(Integer Id, String title, InputStream inputStream) {
        this.Id = Id;
        this.title = title;
        this.inputStream = inputStream;
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
