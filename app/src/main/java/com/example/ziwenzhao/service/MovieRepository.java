package com.example.ziwenzhao.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.db.Database;
import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class MovieRepository implements Repository{
    private List<MovieHttpResult> movieResultList;
    private MovieJSONApiService movieJSONApiService;
    private MovieImageApiService movieImageApiService;
    private long timeStamp;
    private static final long EXPIRE_DURATION = 60 * 1000;

    public MovieRepository(Context context, Database database, MovieJSONApiService movieJSONApiService, MovieImageApiService movieImageApiService) {
        this.movieJSONApiService = movieJSONApiService;
        this.movieImageApiService = movieImageApiService;
        movieResultList = new ArrayList<>();
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
                movieResultList = movieHttpResults;
            }
        });
    }

    @Override
    public Observable<Bitmap> getMovieImageLocal() {
        return null;
    }

    @Override
    public Observable<Bitmap> getMovieImageRemote(ImageSize imageSize, String path) {
        return movieImageApiService.getMovieImageByPath(imageSize.toString(), path).map(new Function<ResponseBody, Bitmap>() {
            @Override
            public Bitmap apply(ResponseBody responseBody) throws Exception {
                return BitmapFactory.decodeStream(responseBody.byteStream());
            }
        });
    }

    @Override
    public Observable<List<Bitmap>> getMultipleMovieImageRemote(ImageSize imageSize, List<String> paths) {

        List<Observable<ResponseBody>> tasks = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++) {
            tasks.add(movieImageApiService.getMovieImageByPath(imageSize.toString(), paths.get(i)));
        }

        return Observable.zip(tasks, new Function<Object[], List<Bitmap>>() {
            @Override
            public List<Bitmap> apply(Object[] objects) throws Exception {
                List<Bitmap> list = new ArrayList<>();
                for (Object object: objects) {
                    list.add(BitmapFactory.decodeStream(((ResponseBody) object).byteStream()));
                }
                return list;
            }
        });
    }

    public Observable<List<MovieModel>> getMovieModelsRemote() {
        return getMovieJSONRemote().concatMap(new Function<List<MovieHttpResult>, ObservableSource<List<Bitmap>>>() {
            @Override
            public ObservableSource<List<Bitmap>> apply(List<MovieHttpResult> movieHttpResults) throws Exception {
                movieResultList = movieHttpResults;
                List<String> paths = new ArrayList<>();
                for (MovieHttpResult movieHttpResult: movieHttpResults) {
                    paths.add(movieHttpResult.getPosterPath().substring(1));
                }
                return getMultipleMovieImageRemote(ImageSize.size_w92, paths);
            }
        }).map(new Function<List<Bitmap>, List<MovieModel>>() {
            @Override
            public List<MovieModel> apply(List<Bitmap> bitmaps) throws Exception {
                List<MovieModel> movieModels = new ArrayList<>();
                for (int i = 0; i < bitmaps.size(); i++) {
                    movieModels.add(new MovieModel(movieResultList.get(i).getId(), movieResultList.get(i).getTitle(), bitmaps.get(i)));
                }

                return movieModels;
            }
        });
    }

    public Observable<List<MovieModel>> getMovieModelsLocal() {
        return null;
    }

    @Override
    public Observable<List<MovieModel>> getMovieModels() {
        return isStale() ? getMovieModelsRemote() : getMovieModelsLocal();
    }

    private boolean isStale() {
//        return System.currentTimeMillis() - timeStamp > EXPIRE_DURATION;
        return true;
    }
}
