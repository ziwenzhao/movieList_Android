package com.example.ziwenzhao.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.db.Database;
import com.example.ziwenzhao.db.MoviePersistData;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.Utils.Callback;
import com.example.ziwenzhao.ui.movielist.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;


public class MovieRepository implements Repository{

    private List<MovieHttpResult> movieResultList;

    private MovieJSONApiService movieJSONApiService;

    private MovieImageApiService movieImageApiService;

    private Database database;

    private Context context;

    private long lastSyncTimeStamp;

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
                .map(httpResponse -> httpResponse.getResults())
                .doOnNext(movieHttpResults -> movieResultList = movieHttpResults);
    }


    @Override
    public Observable<Bitmap> getMovieImageRemote(ImageSize imageSize, String path) {

        return movieImageApiService.getMovieImageByPath(imageSize.toString(), path)
                .map(responseBody -> BitmapFactory.decodeStream(responseBody.byteStream()));
    }


    @Override
    public Observable<List<Bitmap>> getMultipleMovieImageRemote(ImageSize imageSize, List<String> paths) {

        List<Observable<ResponseBody>> tasks = new ArrayList<>();
        for(int i = 0; i < paths.size(); i++) {
            tasks.add(movieImageApiService.getMovieImageByPath(imageSize.toString(), paths.get(i)));
        }

        return Observable.zip(tasks, objects -> {

            List<Bitmap> list = new ArrayList<>();

            for (Object object: objects) {
                list.add(BitmapFactory.decodeStream(((ResponseBody) object).byteStream()));
            }

            return list;
        });
    }


    public Observable<List<MovieModel>> getMovieModelsRemote() {

        return getMovieJSONRemote().concatMap(mapMovieHttpResultToBitmapRequest()).concatMap(mapToMovieModelListRequest());
    }


    public Observable<List<MovieModel>> getMovieModelsLocal() {

        return Observable.create(emitter -> new AsyncTask<Void, Void, List<MovieModel>>() {

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
        }.execute());
    }


    private Function<List<MovieHttpResult>, ObservableSource<List<Bitmap>>> mapMovieHttpResultToBitmapRequest() {
        return movieHttpResults -> {

            movieResultList = movieHttpResults;

            List<String> paths = new ArrayList<>();
            for (MovieHttpResult movieHttpResult : movieHttpResults) {
                paths.add(movieHttpResult.getPosterPath().substring(1));
            }

            return getMultipleMovieImageRemote(ImageSize.size_w154, paths);
        };
    }


    private Function<List<Bitmap>, Observable<List<MovieModel>>> mapToMovieModelListRequest() {


        return bitmaps -> Observable.create((ObservableOnSubscribe<List<MovieModel>>) emitter -> {

            final List<MovieModel> movieModels = new ArrayList<>();
            for (int i = 0; i < bitmaps.size(); i++) {
                movieModels.add(new MovieModel(movieResultList.get(i).getId(), movieResultList.get(i).getTitle(), bitmaps.get(i)));
            }

            List<MoviePersistData> moviePersistDataList = new ArrayList<>();
            for (MovieModel movieModel: movieModels) {
                moviePersistDataList.add(convertMovieModelToPersistData(movieModel));
            }

            refreshDatabaseAsyncTask(moviePersistDataList, (Callback) () -> { emitter.onNext(movieModels); emitter.onComplete(); });
        });
    }

    @Override
    public void refreshDatabaseAsyncTask(final List<MoviePersistData> moviePersistDataList, Callback callback) {

        new AsyncTask<List<MoviePersistData>, Void, Void>() {

            @Override
            protected Void doInBackground(List<MoviePersistData>... lists) {

                database.moviePersistDataDao().deleteAll();
                database.moviePersistDataDao().insertAll(lists[0]);

                updateLastSyncTimeStamp();

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                callback.apply();
            }
        }.execute(moviePersistDataList);
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

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        lastSyncTimeStamp = System.currentTimeMillis();

        editor.putLong(context.getString(R.string.last_sync_timestamp), lastSyncTimeStamp);
        editor.commit();
    }


    private void getLastSyncTimeStamp() {

        String lastSyncTimestampKey = context.getString(R.string.last_sync_timestamp);

        SharedPreferences sharedPref = getSharedPreferences();

        lastSyncTimeStamp = sharedPref.contains(lastSyncTimestampKey) ? sharedPref.getLong(lastSyncTimestampKey, 0) : 0;

    }


    private boolean isStale() {

        return lastSyncTimeStamp == 0 ? true : System.currentTimeMillis() - lastSyncTimeStamp > EXPIRE_DURATION;
    }


    private boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    private SharedPreferences getSharedPreferences() {

        return context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

    }
}
