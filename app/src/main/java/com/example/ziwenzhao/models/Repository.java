package com.example.ziwenzhao.models;

import java.util.List;

import io.reactivex.Observable;

public interface Repository {

    Observable<List<MovieHttpResult>> getLocal();

    Observable<List<MovieHttpResult>> getRemote();

}
