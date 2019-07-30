package com.example.ziwenzhao.service;

import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface MovieApiService {
    @GET("movie/popular")
    Observable<HttpResponse> getMoives();
}
