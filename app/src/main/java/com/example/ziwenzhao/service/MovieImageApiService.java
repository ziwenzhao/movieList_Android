package com.example.ziwenzhao.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieImageApiService {

    @GET("{size}/{path}")
    Observable<ResponseBody> getMovieImageByPath(@Path("size") String size, @Path("path") String path);
}
