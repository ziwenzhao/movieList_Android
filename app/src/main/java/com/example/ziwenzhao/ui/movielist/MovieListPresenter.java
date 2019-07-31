package com.example.ziwenzhao.ui.movielist;

import android.util.Log;

import com.example.ziwenzhao.models.MovieModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieListPresenter implements MovieListActivityMVP.Presenter {
    private MovieListActivityMVP.View view;
    private MovieListActivityMVP.Model model;
    private CompositeDisposable subscriptions;

    public MovieListPresenter(MovieListActivityMVP.Model model) {
        this.model = model;
        this.subscriptions = new CompositeDisposable();
    }

    @Override
    public void loadData() {
        view.setLoading(true);
        subscriptions.add(model
                .getMovieModels()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        view.setLoading(false);
                    }
                })
                .subscribeWith(new DisposableObserver<List<MovieModel>>() {
                    @Override
                    public void onNext(List<MovieModel> movieModels) {
                        view.updateData(movieModels);
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
                }));

    }

    @Override
    public void attachView(MovieListActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        subscriptions.clear();
    }
}
