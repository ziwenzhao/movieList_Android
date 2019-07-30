package com.example.ziwenzhao.ui.movielist;

import io.reactivex.Observable;

import com.example.ziwenzhao.models.MovieHttpResult;

import java.util.List;

public interface MovieListActivityMVP {
    interface View {
        void updateData();

    }

    interface Presenter {
        void loadData();

        void attachView();

        void detachView();
    }

    interface Model {
        Observable<List<MovieHttpResult>> getMovies();
    }
}
