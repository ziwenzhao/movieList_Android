package com.example.ziwenzhao.di.component;

import com.example.ziwenzhao.di.module.ApplicationModule;
import com.example.ziwenzhao.ui.movielist.MovieListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MovieListActivity activity);
}
