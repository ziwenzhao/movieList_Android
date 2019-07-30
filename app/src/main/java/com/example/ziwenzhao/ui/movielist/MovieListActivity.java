package com.example.ziwenzhao.ui.movielist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ziwenzhao.App;
import com.example.ziwenzhao.models.MovieHttpResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class MovieListActivity extends AppCompatActivity implements  MovieListActivityMVP.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Inject
    MovieListActivityMVP.Model movieListActivityModel;

    private MovieListAdapter movieListAdapter;
    private List<MovieHttpResult> movieList =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ((App) getApplication()).getComponent().inject(this);

        ButterKnife.bind(this);

        movieList.add(new MovieHttpResult());
        movieList.add(new MovieHttpResult());
        movieListAdapter = new MovieListAdapter(movieList);
        recyclerView.setAdapter(movieListAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void updateData() {

    }
}
