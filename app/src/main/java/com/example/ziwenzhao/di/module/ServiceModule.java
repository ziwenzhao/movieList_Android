package com.example.ziwenzhao.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.ziwenzhao.Utils.Constants;
import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.db.Database;
import com.example.ziwenzhao.service.MovieJSONApiService;
import com.example.ziwenzhao.service.MovieImageApiService;
import com.example.ziwenzhao.service.MovieRepository;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ServiceModule {

    private OkHttpClient provideHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("api_key", Constants.API_KEY).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();
    }

    private Retrofit provideRetrofit(String restUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(restUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private Retrofit provideRetrofit2(String restUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(restUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public MovieJSONApiService provideMovieJSONApiService() {
        return provideRetrofit(Constants.REST_URL_Movie_JSON, provideHttpClient()).create(MovieJSONApiService.class);
    }

    @Provides
    @Singleton
    MovieImageApiService provideMovieImageService() {
        return provideRetrofit2(Constants.REST_URL_Movie_Image, provideHttpClient()).create(MovieImageApiService.class);
    }

    @Provides
    @Singleton
    public Repository provideRepository(MovieJSONApiService movieJSONApiService, MovieImageApiService movieImageApiService) {
        return new MovieRepository(movieJSONApiService, movieImageApiService);
    }

    @Provides
    @Singleton
    public Database provideDatabase(Context context) {
        return Room.databaseBuilder(context, Database.class, "movie").build();
    }
}
