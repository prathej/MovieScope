/*
 *   Copyright 2019 Prahlad Vidyanand
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.prathej.moviescope.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.prathej.moviescope.AppExecutors;
import com.prathej.moviescope.R;
import com.prathej.moviescope.Settings.SettingsActivity;
import com.prathej.moviescope.adapter.ImageAdapter;
import com.prathej.moviescope.data.AppDatabase;
import com.prathej.moviescope.model.Movie;
import com.prathej.moviescope.model.MovieResponse;
import com.prathej.moviescope.network.ApiClient;
import com.prathej.moviescope.network.ApiService;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.prathej.moviescope.R.string.pref_sort_high_rated_values;
import static com.prathej.moviescope.R.string.pref_sort_key;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.gridView)
    GridView mGridView;
    ApiService mApiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    AppDatabase mDb;


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGridView.setOnItemClickListener(movieClickListener);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setUpSharedPreferences();

        mApiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        if (savedInstanceState == null) {
            String sortMethod = getSortMethod();

            if ((sortMethod.equals(getResources().getString(R.string.pref_sort_most_popular_values)))
                    || (sortMethod.equals(getResources().getString(pref_sort_high_rated_values)))) {
                loadMovieData(sortMethod);
            } else {
                loadFavoriteMovies(sortMethod);
            }
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.movie_data));
            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelable[i];
                }
                List<Movie> moviesList = Arrays.asList(movies);

                mGridView.setAdapter(new ImageAdapter(this, moviesList));
            }
        }

    }

    private void setUpSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


    private final GridView.OnItemClickListener movieClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Movie movie = (Movie) adapterView.getItemAtPosition(position);

            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(getResources().getString(R.string.movie_data), movie);

            startActivity(intent);
        }
    };

    private void loadMovieData(String sortMethod) {
        if (isNetworkAvailable()) {

            //using rx java
            if (sortMethod.equals(getResources().getString(R.string.pref_sort_most_popular_values))) {
                disposable.add(
                        mApiService.getPopularMovies()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<MovieResponse>() {
                                    @Override
                                    public void onNext(@NonNull MovieResponse movieResponse) {
                                        if (movieResponse != null) {
                                            Log.d(LOG_TAG, movieResponse.getMovieList().toString());

                                            List<Movie> movies = movieResponse.getMovieList();
                                            mGridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                                        } else {
                                            Log.d(LOG_TAG, "Movies response null");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d(LOG_TAG, "Error" + e);
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(LOG_TAG, "Completed");
                                    }
                                }));

            } else if (sortMethod.equals(getResources().getString(pref_sort_high_rated_values))) {
                //using rx java
                disposable.add(
                        mApiService.getTopRatedMovies()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<MovieResponse>() {
                                    @Override
                                    public void onNext(@NonNull MovieResponse movieResponse) {
                                        if (movieResponse != null) {
                                            Log.d(LOG_TAG, movieResponse.getMovieList().toString());

                                            List<Movie> movies = movieResponse.getMovieList();
                                            mGridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                                        } else {
                                            Log.d(LOG_TAG, "Movies response null");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d(LOG_TAG, "Error" + e);
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(LOG_TAG, "Completed");
                                    }
                                }));

            }

        } else {
            Toast.makeText(this, R.string.error_need_internet, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int MovieObjectsCount = mGridView.getCount();
        if (MovieObjectsCount > 0) {

            Movie[] movies = new Movie[MovieObjectsCount];
            for (int i = 0; i < MovieObjectsCount; i++) {
                movies[i] = (Movie) mGridView.getItemAtPosition(i);
            }

            // Save Movie objects to bundle
            outState.putParcelableArray(getString(R.string.movie_data), movies);
        }

        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(pref_sort_key),
                getString(R.string.pref_sort_most_popular_values));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String value = sharedPreferences.getString(key, "");
        Log.v(LOG_TAG, value);
        if ((value.equals(getResources().getString(R.string.pref_sort_most_popular_values))) ||
                (value.equals(getResources().getString(pref_sort_high_rated_values)))) {
            loadMovieData(value);
        } else {
            loadFavoriteMovies(getResources().getString(R.string.pref_sort_user_favourite_values));
        }
    }

    protected void loadFavoriteMovies(String sortOrder) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Movie> movieList = mDb.movieDao().getAllMovies();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (movieList.size() > 0) {
                            mGridView.setAdapter(new ImageAdapter(MainActivity.this, movieList));
                        } else {
                            Toast.makeText(MainActivity.this, "No Movies found in Database. Loading Popular Movies", Toast.LENGTH_LONG).show();
                            loadMovieData(getResources().getString(R.string.pref_sort_most_popular_values));
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortMethod = getSortMethod();
        if (sortMethod.equals(getResources().getString(R.string.pref_sort_user_favourite_values))) {
            loadFavoriteMovies(sortMethod);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);
    }

}
