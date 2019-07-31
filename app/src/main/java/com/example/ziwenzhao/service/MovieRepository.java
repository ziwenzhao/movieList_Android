package com.example.ziwenzhao.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.db.Database;
import com.example.ziwenzhao.db.MoviePersistData;
import com.example.ziwenzhao.db.MoviePersistDataDao;
import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.ui.movielist.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class MovieRepository implements Repository{

    private List<MovieHttpResult> movieResultList;

    private MovieJSONApiService movieJSONApiService;

    private MovieImageApiService movieImageApiService;

    private Database database;

    private Context context;

    private long timeStamp;

    private static final long EXPIRE_DURATION = 60 * 1000;

    public MovieRepository(Context context, Database database, MovieJSONApiService movieJSONApiService, MovieImageApiService movieImageApiService) {

        this.movieJSONApiService = movieJSONApiService;
        this.movieImageApiService = movieImageApiService;
        this.database = database;
        this.context = context;
        movieResultList = new ArrayList<>();

        getLastSyncTimeStamp();
    }


    @Override
    public Observable<List<MovieHttpResult>> getMovieJSONRemote() {

        return this.movieJSONApiService.getMoives()
                .map(new Function<HttpResponse, List<MovieHttpResult>>() {

                    @Override
                    public List<MovieHttpResult> apply(HttpResponse httpResponse) throws Exception {

                        return httpResponse.getResults();
                    }
                })
                .doOnNext(new Consumer<List<MovieHttpResult>>() {

                    @Override
                    public void accept(List<MovieHttpResult> movieHttpResults) throws Exception {

                         movieResultList = movieHttpResults;
                    }
                });
    }


    @Override
    public Observable<Bitmap> getMovieImageRemote(ImageSize imageSize, String path) {

        return movieImageApiService.getMovieImageByPath(imageSize.toString(), path)
                .map(new Function<ResponseBody, Bitmap>() {

                    @Override
                    public Bitmap apply(ResponseBody responseBody) throws Exception {

                        return BitmapFactory.decodeStream(responseBody.byteStream());
                    }
                });
    }


    @Override
    public Observable<List<Bitmap>> getMultipleMovieImageRemote(ImageSize imageSize, List<String> paths) {

        List<Observable<ResponseBody>> tasks = new ArrayList<>();
        for(int i = 0; i < paths.size(); i++) {
            tasks.add(movieImageApiService.getMovieImageByPath(imageSize.toString(), paths.get(i)));
        }

        return Observable.zip(tasks, new Function<Object[], List<Bitmap>>() {

            @Override
            public List<Bitmap> apply(Object[] objects) throws Exception {

                List<Bitmap> list = new ArrayList<>();

                for (Object object: objects) {
                    list.add(BitmapFactory.decodeStream(((ResponseBody) object).byteStream()));
                }

                return list;
            }
        });
    }


    public Observable<List<MovieModel>> getMovieModelsRemote() {


        return getMovieJSONRemote()
                .concatMap(new Function<List<MovieHttpResult>, ObservableSource<List<Bitmap>>>() {

                    @Override
                    public ObservableSource<List<Bitmap>> apply(List<MovieHttpResult> movieHttpResults) throws Exception {

                        movieResultList = movieHttpResults;

                        List<String> paths = new ArrayList<>();
                        for (MovieHttpResult movieHttpResult: movieHttpResults) {
                            paths.add(movieHttpResult.getPosterPath().substring(1));
                        }

                        return getMultipleMovieImageRemote(ImageSize.size_w154, paths);
                    }
                }).concatMap(new Function<List<Bitmap>, Observable<List<MovieModel>>>() {

                    @Override
                    public Observable<List<MovieModel>> apply(final List<Bitmap> bitmaps) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<List<MovieModel>>() {

                            @Override
                            public void subscribe(final ObservableEmitter<List<MovieModel>> emitter) throws Exception {

                                final List<MovieModel> movieModels = new ArrayList<>();
                                for (int i = 0; i < bitmaps.size(); i++) {
                                    movieModels.add(new MovieModel(movieResultList.get(i).getId(), movieResultList.get(i).getTitle(), bitmaps.get(i)));
                                }

                                List<MoviePersistData> moviePersistDataList = new ArrayList<>();
                                for (MovieModel movieModel: movieModels) {
                                    moviePersistDataList.add(convertMovieModelToPersistData(movieModel));
                                }

                                new AsyncTask<List<MoviePersistData>, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(List<MoviePersistData>... lists) {

                                        refreshDatabase(lists[0]);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void v) {

                                        emitter.onNext(movieModels);
                                        emitter.onComplete();
                                    }
                                }.execute(moviePersistDataList);
                            }
                        });

                    }
                });
    }


    public Observable<List<MovieModel>> getMovieModelsLocal() {

        return Observable.create(new ObservableOnSubscribe<List<MovieModel>>() {

            @Override
            public void subscribe(final ObservableEmitter<List<MovieModel>> emitter) throws Exception {

                new AsyncTask<Void, Void, List<MovieModel>>() {

                    @Override
                    protected List<MovieModel> doInBackground(Void... voids) {

                        List<MoviePersistData> moviePersistDataList = database.moviePersistDataDao().getAll();

                        List<MovieModel> movieModelList = new ArrayList<>();
                        for (MoviePersistData moviePersistData: moviePersistDataList) {
                            movieModelList.add(convertMoviePersistDataToModel(moviePersistData));
                        }

                        return  movieModelList;
                    }

                    @Override
                    protected void onPostExecute(List<MovieModel> list) {

                        emitter.onNext(list);
                        emitter.onComplete();
                    }
                }.execute();
            }
        });
    }


    @Override
    public Observable<List<MovieModel>> getMovieModels() {

        return isStale() && isConnected() ? getMovieModelsRemote() : getMovieModelsLocal();
    }


    private MoviePersistData convertMovieModelToPersistData(MovieModel movieModel) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        movieModel.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return new MoviePersistData(movieModel.getId(), movieModel.getTitle(), byteArray);
    }


    private MovieModel convertMoviePersistDataToModel(MoviePersistData moviePersistData) {

        byte[] bytes = moviePersistData.getImageBytes();
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return new MovieModel(moviePersistData.getId(), moviePersistData.getTitle(), bmp);
    }


    private void updateLastSyncTimeStamp() {

        SharedPreferences sharedPref = context.getSharedPreferences("movie_list_app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        timeStamp = System.currentTimeMillis();

        editor.putLong("lastSyncTimeStamp", timeStamp);
        editor.commit();
    }


    private void refreshDatabase(List<MoviePersistData> list) {

        database.moviePersistDataDao().deleteAll();
        database.moviePersistDataDao().insertAll(list);

        updateLastSyncTimeStamp();
    }


    private void getLastSyncTimeStamp() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                SharedPreferences sharedPref = context.getSharedPreferences("movie_list_app", Context.MODE_PRIVATE);
                timeStamp = sharedPref.contains("lastSyncTimeStamp") ? sharedPref.getLong("lastSyncTimeStamp", 0) : 0;
                return null;
            }
        }.execute();
    }


    private boolean isStale() {

        return timeStamp == 0 ? true : System.currentTimeMillis() - timeStamp > EXPIRE_DURATION;
    }


    private boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
