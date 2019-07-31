package com.example.ziwenzhao.ui.movielist;


import io.reactivex.Observable;

import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;

import java.util.List;

public class MovieListModel implements MovieListActivityMVP.Model {
    private Repository repository;

    public MovieListModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<List<MovieModel>> getMovieModels() {
       return repository.getMovieModels();
    }

}
