package com.example.ziwenzhao.service;

import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.Repository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MovieRepository implements Repository{
    private List<MovieHttpResult> movieList;
    private MovieApiService movieApiService;
    private long timeStamp;
    private static final long EXPIRE_DURATION = 60 * 1000;

    public MovieRepository(MovieApiService movieApiService) {
        this.movieApiService = movieApiService;
        movieList = new ArrayList<>();
    }
    @Override
    public Observable<List<MovieHttpResult>> getLocal() {
        return null;
    }

    @Override
    public Observable<List<MovieHttpResult>> getRemote() {
        return this.movieApiService.getMoives().map(new Function<HttpResponse, List<MovieHttpResult>>() {
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

    private boolean isStale() {
        return System.currentTimeMillis() - timeStamp > EXPIRE_DURATION;
    }
}
