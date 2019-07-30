package com.example.ziwenzhao.ui.movielist;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MovieListModel implements MovieListActivityMVP.Model {
    private Repository repository;
    private List<MovieHttpResult> movieResultList;

    public MovieListModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<List<MovieModel>> getMovieModels() {
//        List<MovieHttpResult> movieResultList = new ArrayList<>();
       return repository.getMovieJSONRemote().concatMap(new Function<List<MovieHttpResult>, ObservableSource<List<InputStream>>>() {
           @Override
           public ObservableSource<List<InputStream>> apply(List<MovieHttpResult> movieHttpResults) throws Exception {
               movieResultList = movieHttpResults;
               List<String> paths = new ArrayList<>();
               for (MovieHttpResult movieHttpResult: movieHttpResults) {
                   paths.add(movieHttpResult.getPosterPath().substring(1));
               }
               return repository.getMultipleMovieImageRemote(ImageSize.size_w92, paths);
           }
       }).map(new Function<List<InputStream>, List<MovieModel>>() {
           @Override
           public List<MovieModel> apply(List<InputStream> inputStreams) throws Exception {
               List<MovieModel> movieModels = new ArrayList<>();
               for (int i = 0; i < inputStreams.size(); i++) {
                   movieModels.add(new MovieModel(movieResultList.get(i).getId(), movieResultList.get(i).getTitle(), inputStreams.get(i)));
               }
               return movieModels;
           }
       });
    }
}
