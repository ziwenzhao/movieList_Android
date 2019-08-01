package com.example.ziwenzhao.ui.movielist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.ziwenzhao.Utils.ImageSize;
import com.example.ziwenzhao.db.Database;
import com.example.ziwenzhao.models.HttpResponse;
import com.example.ziwenzhao.models.MovieHttpResult;
import com.example.ziwenzhao.models.MovieModel;
import com.example.ziwenzhao.models.Repository;
import com.example.ziwenzhao.service.MovieImageApiService;
import com.example.ziwenzhao.service.MovieJSONApiService;
import com.example.ziwenzhao.service.MovieRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResponseBody.class, AsyncTask.class, BitmapFactory.class, ConnectivityManager.class})
public class MovieRepositoryTests {

    Context mockContext;
    Database mockDatabase;
    MovieJSONApiService mockMovieJSONApiService;
    MovieImageApiService mockMovieImageApiService;
    private Repository repository;
    Long lastSyncTimeStamp = System.currentTimeMillis();


    @Before
    public void setup() {

        mockContext = mock(Context.class);
        mockDatabase = mock(Database.class);
        mockMovieJSONApiService = mock(MovieJSONApiService.class);
        mockMovieImageApiService = mock(MovieImageApiService.class);

        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockContext.getSharedPreferences(Mockito.<String>any(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.contains(Mockito.<String>any())).thenReturn(true);
        when(mockSharedPreferences.getLong(anyString(), anyLong())).thenReturn(lastSyncTimeStamp);

        repository = new MovieRepository(mockContext, mockDatabase, mockMovieJSONApiService, mockMovieImageApiService);
    }


    @Test
    public void shouldGetMovieJsonFromRemote() {

        TestObserver<List<MovieHttpResult>> observer = new TestObserver<>();

        MovieHttpResult movieHttpResult1 = new MovieHttpResult();
        movieHttpResult1.setId(1);
        movieHttpResult1.setTitle("title1");

        MovieHttpResult movieHttpResult2 = new MovieHttpResult();
        movieHttpResult2.setId(2);
        movieHttpResult2.setTitle("title2");

        HttpResponse response = new HttpResponse();

        List<MovieHttpResult> httpResultsList = Arrays.asList(new MovieHttpResult[]{movieHttpResult1, movieHttpResult2});
        response.setResults(httpResultsList);

        when(mockMovieJSONApiService.getMoives()).thenReturn(Observable.just(response));
        repository.getMovieJSONRemote().subscribe(observer);

        observer.assertSubscribed();
        observer.assertComplete();
        observer.assertValue(httpResultsList);
    }

    @Test
    public void shouldGetMovieImageFromRemote() {

        TestObserver<Bitmap> observer = new TestObserver<>();

        final ResponseBody mockResponseBody = PowerMockito.mock(ResponseBody.class);
        PowerMockito.mockStatic(BitmapFactory.class);
        Bitmap mockBitmap = PowerMockito.mock(Bitmap.class);

        when(BitmapFactory.decodeStream(Mockito.<InputStream>any())).thenReturn(mockBitmap);
        when(mockResponseBody.byteStream()).thenReturn(null);
        when(mockMovieImageApiService.getMovieImageByPath(ImageSize.size_w154.toString(), "path"))
                .thenReturn(Observable.just(mockResponseBody));

        repository.getMovieImageRemote(ImageSize.size_w154, "path").subscribe(observer);

        observer.assertSubscribed();
        observer.assertComplete();
        observer.assertValue(mockBitmap);
    }

    @Test
    public void shouldGetErrorResponseIfGetMovieJsonRequestFail() {

        TestObserver<List<MovieHttpResult>> observer = new TestObserver<>();

        Throwable throwable = new Throwable("Request Fail");

        when(mockMovieJSONApiService.getMoives()).thenReturn(Observable.error(throwable));

        repository.getMovieJSONRemote().subscribe(observer);

        observer.assertError(throwable);
    }

    @Test
    public void shouldGetMovieModelsFromRemoteIfStale() {

        TestObserver<List<MovieModel>> observer = new TestObserver<>();

        MovieModel movieModel = new MovieModel(1, "title", null);
        List<MovieModel> movieModelList = new ArrayList<>();
        movieModelList.add(movieModel);

        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        ConnectivityManager mockConnectivityManager = PowerMockito.mock(ConnectivityManager.class);
        NetworkInfo mockNetworkInfo = PowerMockito.mock((NetworkInfo.class));

        when(mockSharedPreferences.contains(Mockito.<String>any())).thenReturn(true);
        when(mockSharedPreferences.getLong(Mockito.<String>any(), anyLong())).thenReturn(System.currentTimeMillis() - 61 * 1000);
        when(mockContext.getSharedPreferences(Mockito.<String>any(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnectedOrConnecting()).thenReturn(true);

        repository = new MovieRepository(mockContext, mockDatabase, mockMovieJSONApiService, mockMovieImageApiService);

        MovieRepository spyRepository = (MovieRepository) spy(repository);
        doReturn(Observable.just(movieModelList)).when(spyRepository).getMovieModelsRemote();

        spyRepository.getMovieModels().subscribe(observer);

        observer.assertComplete();
        observer.assertValue(movieModelList);
    }
}
