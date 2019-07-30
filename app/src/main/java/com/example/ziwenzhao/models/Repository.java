package com.example.ziwenzhao.models;

import android.graphics.Bitmap;

import com.example.ziwenzhao.Utils.ImageSize;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;

public interface Repository {

    Observable<List<MovieHttpResult>> getMovieJSONLocal();

    Observable<List<MovieHttpResult>> getMovieJSONRemote();

    Observable<Bitmap> getMovieImageLocal();

    Observable<Bitmap> getMovieImageRemote(ImageSize size, String path);

    Observable<List<Bitmap>> getMultipleMovieImageRemote(ImageSize size, List<String> paths);

}
