package com.example.ziwenzhao.service;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.Repository;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class MovieRepository implements Repository{
    private List<MovieHttpResult> movieList;
    private MovieJSONApiService movieJSONApiService;
    private MovieImageApiService movieImageApiService;
    private long timeStamp;
    private static final long EXPIRE_DURATION = 60 * 1000;

    public MovieRepository(MovieJSONApiService movieJSONApiService, MovieImageApiService movieImageApiService) {
        this.movieJSONApiService = movieJSONApiService;
        this.movieImageApiService = movieImageApiService;
        movieList = new ArrayList<>();
    }

    @Override
    public Observable<List<MovieHttpResult>> getMovieJSONLocal() {
        return null;
    }

    @Override
    public Observable<List<MovieHttpResult>> getMovieJSONRemote() {
        return this.movieJSONApiService.getMoives().map(new Function<HttpResponse, List<MovieHttpResult>>() {
            @Override
            public List<MovieHttpResult> apply(HttpResponse httpResponse) throws Exception {
                return httpResponse.getResults();
            }
        }).doOnNext(new Consumer<List<MovieHttpResult>>() {
            @Override
            public void accept(List<MovieHttpResult> movieHttpResults) throws Exception {
                movieList = movieHttpResults;
            }
        });
    }

    @Override
    public Observable<InputStream> getMovieImageLocal() {
        return null;
    }

    @Override
    public Observable<InputStream> getMovieImageRemote(ImageSize imageSize, String path) {
        return movieImageApiService.getMovieImageByPath(imageSize.toString(), path).map(new Function<ResponseBody, InputStream>() {
            @Override
            public InputStream apply(ResponseBody responseBody) throws Exception {
                return responseBody.byteStream();
            }
        });
    }

    @Override
    public Observable<List<InputStream>> getMultipleMovieImageRemote(ImageSize imageSize, List<String> paths) {

        List<Observable<ResponseBody>> tasks = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++) {
            tasks.add(movieImageApiService.getMovieImageByPath(imageSize.toString(), paths.get(i)));
        }

        return Observable.zip(tasks, new Function<Object[], List<InputStream>>() {
            @Override
            public List<InputStream> apply(Object[] objects) throws Exception {
                List<InputStream> list = new ArrayList<>();
                for (Object object: objects) {
                    list.add(((ResponseBody) object).byteStream());
                }
                return list;
            }
        });
    }

    private boolean isStale() {
        return System.currentTimeMillis() - timeStamp > EXPIRE_DURATION;
    }
}
