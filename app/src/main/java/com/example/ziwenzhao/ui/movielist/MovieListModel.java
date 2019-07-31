package com.example.ziwenzhao.ui.movielist;

import android.graphics.Bitmap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.service.MovieRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MovieListModel implements MovieListActivityMVP.Model {
    private Repository repository;
    private List<MovieHttpResult> movieResultList;

    public MovieListModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<List<MovieModel>> getMovieModels() {
       return repository.getMovieModels();
    }

}
