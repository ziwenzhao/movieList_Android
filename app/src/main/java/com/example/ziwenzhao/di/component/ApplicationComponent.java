package com.example.ziwenzhao.di.component;

import com.example.ziwenzhao.di.module.ActivityModule;
import com.example.ziwenzhao.di.module.ApplicationModule;
import com.example.ziwenzhao.di.module.ServiceModule;
import com.example.ziwenzhao.ui.movielist.MovieListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, ServiceModule.class, ActivityModule.class})
public interface ApplicationComponent {
    void inject(MovieListActivity activity);
}
