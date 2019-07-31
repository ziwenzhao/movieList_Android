package com.example.ziwenzhao.service;

import com.example.ziwenzhao.models.HttpResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface MovieJSONApiService {
    @GET("movie/popular")
    Observable<HttpResponse> getMoives();
}
