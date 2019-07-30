package com.example.ziwenzhao.models;

import com.example.ziwenzhao.Utils.ImageSize;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;

public interface Repository {

    Observable<List<MovieHttpResult>> getMovieJSONLocal();

    Observable<List<MovieHttpResult>> getMovieJSONRemote();

    Observable<InputStream> getMovieImageLocal();

    Observable<InputStream> getMovieImageRemote(ImageSize size, String path);

    Observable<List<InputStream>> getMultipleMovieImageRemote(ImageSize size, List<String> paths);

}
