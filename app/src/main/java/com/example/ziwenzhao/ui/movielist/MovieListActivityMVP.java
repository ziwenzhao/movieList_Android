package com.example.ziwenzhao.ui.movielist;

import io.reactivex.Observable;

import com.example.ziwenzhao.models.MovieModel;

import java.util.List;

/**
 * MovieListActivityMVP is an interface for defining the MVP pattern.
 */
public interface MovieListActivityMVP {

    interface View {

        void updateData(List<MovieModel> movieModels);

        void showSnackbar(String s);

        void setLoading(boolean b);
    }

    interface Presenter {

        void loadData();

        void attachView(MovieListActivityMVP.View view);

        void detachView();
    }

    interface Model {

        Observable<List<MovieModel>> getMovieModels();

    }
}
