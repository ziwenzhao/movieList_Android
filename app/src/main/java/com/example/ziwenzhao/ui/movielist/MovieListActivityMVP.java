package com.example.ziwenzhao.ui.movielist;

import android.database.Observable;

import com.example.ziwenzhao.models.MovieHttpResult;

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
        Observable<MovieHttpResult> getMovies();
    }
}
