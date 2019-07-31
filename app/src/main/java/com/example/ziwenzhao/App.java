package com.example.ziwenzhao;

import android.app.Application;

import com.example.ziwenzhao.di.component.ApplicationComponent;
import com.example.ziwenzhao.di.component.DaggerApplicationComponent;
import com.example.ziwenzhao.di.module.ApplicationModule;

public class App extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {

        super.onCreate();

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
