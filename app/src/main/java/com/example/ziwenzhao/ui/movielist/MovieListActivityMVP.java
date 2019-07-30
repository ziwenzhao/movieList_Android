package com.example.ziwenzhao.ui.movielist;

import io.reactivex.Observable;

import com.example.ziwenzhao.models.MovieHttpResult;

import java.util.List;

public interface MovieListActivityMVP {
    interface View {
        void updateData(List<MovieHttpResult> movieList);
        void showSnackbar(String s);
    }

    interface Presenter {
        void loadData();

        void attachView(MovieListActivityMVP.View view);

        void detachView();
    }

    interface Model {
        Observable<List<MovieHttpResult>> getMovies();
    }
}
