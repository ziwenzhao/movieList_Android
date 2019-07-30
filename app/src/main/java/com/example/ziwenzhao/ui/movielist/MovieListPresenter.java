package com.example.ziwenzhao.ui.movielist;

import android.util.Log;

import com.example.ziwenzhao.models.MovieHttpResult;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieListPresenter implements MovieListActivityMVP.Presenter {
    private MovieListActivityMVP.View view;
    private MovieListActivityMVP.Model model;
    private Disposable subscription;

    public MovieListPresenter(MovieListActivityMVP.Model model) {
        this.model = model;
    }

    @Override
    public void loadData() {
        subscription = model
                .getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<MovieHttpResult>>() {
                    @Override
                    public void onNext(List<MovieHttpResult> movieHttpResults) {
                        view.updateData(movieHttpResults);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Getting Movies Fail", e.toString());
                        if (view != null) {
                            view.showSnackbar("Unable to get movies");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void attachView(MovieListActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}
