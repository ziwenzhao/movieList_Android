package com.example.ziwenzhao.ui.movielist;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.ziwenzhao.App;
import com.example.ziwenzhao.Utils.MovieListDiffUtilCallBack;
import com.example.ziwenzhao.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieListActivity extends AppCompatActivity implements  MovieListActivityMVP.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView; // RecyclerView is a more advanced and flexible version of ListView.

    @BindView(R.id.root_view)
    ViewGroup rootView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout; // SwipeRefreshLayout can refresh the contents of a view via a vertical swipe gesture.

    @Inject
    MovieListActivityMVP.Presenter presenter;

    private MovieListAdapter movieListAdapter;
    private List<MovieModel> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_list);
        ((App) getApplication()).getComponent().inject(this);

        ButterKnife.bind(this); // View binding through annotation

        initRecyclerView();

        setRefreshListener();
    }

    @Override
    protected  void onStart() {
        super.onStart();

        presenter.attachView(this);
        presenter.loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.detachView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void updateData(List<MovieModel> movieModels) {

        movieModels.sort((m1, m2) -> m1.getTitle().compareTo(m2.getTitle()));

        // Use DiffUtil to calculate the minimum operations for updating list.
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MovieListDiffUtilCallBack(movieList, movieModels));
        diffResult.dispatchUpdatesTo(movieListAdapter);

        movieList.clear();
        movieList.addAll(movieModels);
    }

    @Override
    public void showSnackbar(String msg) { Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show(); }

    @Override
    public void setLoading(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
    }

    private void initRecyclerView() {
        movieListAdapter = new MovieListAdapter(movieList);

        recyclerView.setAdapter(movieListAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadData());
    }
}
