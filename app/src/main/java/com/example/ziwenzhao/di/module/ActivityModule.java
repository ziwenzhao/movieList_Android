package com.example.ziwenzhao.di.module;

import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.ui.movielist.MovieListActivityMVP;
import com.example.ziwenzhao.ui.movielist.MovieListModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    @Provides
    @Singleton
    public MovieListActivityMVP.Model provideMovieListActivityModel(Repository repository) {
        return new MovieListModel(repository);
    }
}
