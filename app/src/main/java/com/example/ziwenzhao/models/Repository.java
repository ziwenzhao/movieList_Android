package com.example.ziwenzhao.models;

import android.graphics.Bitmap;

import com.example.ziwenzhao.Utils.ImageSize;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;

/**
 * Repository is an interface that
 */
public interface Repository {


    /**
     *
     * @return return Observable of List<MovieHttpResult>, which is fetched from remote.
     */
    Observable<List<MovieHttpResult>> getMovieJSONRemote();

    /**
     * Fetch Bitmap data for a single image
     *
     * @param size The size of the image, which should be of an enum type
     * @param path The path of the image
     * @return return an observable of Bitmap
     */
    Observable<Bitmap> getMovieImageRemote(ImageSize size, String path);

    /**
     * Fetch Bitmap data for a collection of images remotely
     *
     * @param size The size of the image, which should be of an enum type
     * @param paths The path collection of images
     * @return return an observable of Bitmap Collection.
     */
    Observable<List<Bitmap>> getMultipleMovieImageRemote(ImageSize size, List<String> paths);

    /**
     * Fetch the movie models, which are used for view rendering.
     *
     * @return return an observable of movieModels collection.
     */
    Observable<List<MovieModel>> getMovieModels();
}
