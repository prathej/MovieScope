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

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prathej.moviescope.AppExecutors;
import com.prathej.moviescope.R;
import com.prathej.moviescope.RecyclerClickListener;
import com.prathej.moviescope.adapter.ImageAdapter;
import com.prathej.moviescope.adapter.ReviewAdapter;
import com.prathej.moviescope.adapter.TrailerAdapter;
import com.prathej.moviescope.data.AppDatabase;
import com.prathej.moviescope.isFavoriteTaskCompleted;
import com.prathej.moviescope.model.Movie;
import com.prathej.moviescope.model.Review;
import com.prathej.moviescope.model.ReviewResponse;
import com.prathej.moviescope.model.Trailer;
import com.prathej.moviescope.model.TrailerResponse;
import com.prathej.moviescope.network.ApiClient;
import com.prathej.moviescope.network.ApiService;
import com.prathej.moviescope.utils.DateUtilities;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.prathej.moviescope.utils.Const.TMDB_POSTER_BASE_URL;


public class DetailActivity extends AppCompatActivity implements isFavoriteTaskCompleted {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;
    // ActivityDetailBinding mBinding;
    @BindView(R.id.trailersRecyclerView)
    protected RecyclerView mTrailersRecyclerView;

    @BindView(R.id.reviewsRecyclerView)
    protected RecyclerView mReviewsRecyclerView;

    private CompositeDisposable disposable = new CompositeDisposable();


    List<Trailer> mTrailerList;
    List<Review> mReviewList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;

    Movie mFavMovie;
    Boolean isFav = false;

    @BindView(R.id.noTrailerView)
    TextView notrailerview;

    @BindView(R.id.noReviewView)
    TextView noreviewview;

    @BindView(R.id.favorite_button)
    ImageButton mFavoriteButton;

    @BindView(R.id.textview_original_title)
    TextView mTextviewOriginalTitle;

    @BindView(R.id.imageview_poster)
    ImageView mImageviewPoster;

    @BindView(R.id.textview_overview)
    TextView mTextviewOverview;

    @BindView(R.id.textview_vote_average)
    TextView mTextviewVoteAverage;

    @BindView(R.id.textview_release_date)
    TextView mTextviewReleaseDate;


    ApiService mApiService;


    AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mApiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());


        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(getString(R.string.movie_data))) {
            mMovie = intent.getParcelableExtra(getString(R.string.movie_data));
        }

        mMovie.mListener = this;
        mMovie.isFavorite(this, mDb);

        mTextviewOriginalTitle.setText(mMovie.getOriginalTitle());


        String poster = mMovie.getPosterPath();
        Log.d(ImageAdapter.class.getSimpleName(), "poster path is " + poster);
        String posterUrl = TMDB_POSTER_BASE_URL + poster;

        Picasso.get()
                .load(posterUrl)
                .resize(225, 280)
                .error(R.drawable.ic_error_outline_black_48dp)
                .placeholder(R.drawable.ic_sync_black_48dp)
                .into(mImageviewPoster);


        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String overView = mMovie.getOverview();
        if (overView == null) {
            mTextviewOverview.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_summary_found);
        }
        mTextviewOverview.setText(overView);
        mTextviewVoteAverage.setText(mMovie.getDetailedVoteAverage());


        String releaseDate = getFormattedReleaseData(mMovie.getReleaseDate());

        mTextviewReleaseDate.setText(releaseDate);

        loadTrailers();

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersRecyclerView.setLayoutManager(trailerLayoutManager);


        mTrailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = "https://www.youtube.com/watch?v=".concat(mTrailerList.get(position).getKey());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }));

        loadReviews();
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(reviewLayoutManager);

        mReviewsRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mReviewList.get(position).getUrl()));
                startActivity(i);
            }
        }));
    }



    private void loadTrailers() {
        disposable.add(
                mApiService.getTrailers(mMovie.getId(), getString(R.string.trailer_language))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<TrailerResponse>() {


                            @Override
                            public void onNext(TrailerResponse trailerResponse) {
                                List<Trailer> trailers = trailerResponse.getTrailersList();
                                mTrailerList = trailers;
                                //mTrailersRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
                                if (mTrailerList.size() == 0) {
                                    mTrailersRecyclerView.setVisibility(View.INVISIBLE);
                                    notrailerview.setVisibility(View.VISIBLE);
                                } else {
                                    trailerAdapter = new TrailerAdapter(DetailActivity.this, mTrailerList);
                                    mTrailersRecyclerView.setAdapter(trailerAdapter);
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

    private void loadReviews() {
        disposable.add(
                mApiService.getReviews(mMovie.getId(), getString(R.string.review_language))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ReviewResponse>() {

                            @Override
                            public void onNext(ReviewResponse reviewResponse) {
                                List<Review> reviews = reviewResponse.getReviewList();
                                mReviewList = reviews;
                                if (mReviewList.size() == 0) {
                                    noreviewview.setVisibility(View.VISIBLE);
                                    mReviewsRecyclerView.setVisibility(View.INVISIBLE);

                                } else {
                                    reviewAdapter = new ReviewAdapter(DetailActivity.this, mReviewList);
                                    mReviewsRecyclerView.setAdapter(reviewAdapter);
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


    public void onClickAddMovies(View view) {
        if (mFavMovie == null) {
            //Movie is not Favorite and if user wishes to set it as favorite
            saveToFavorites(mMovie);
        } else {
            //Movie is Favorite and if user wishes to remove it as favorite
            removeFromFavorites(mMovie);
        }
    }

    public void saveToFavorites(Movie movie) {
        Log.d(LOG_TAG, "inside saveToFavorites method");
        Log.d(LOG_TAG, "Movie original Title is =" + movie.getOriginalTitle());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertMovie(movie);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                    }
                });
            }
        });


        if (movie.getOriginalTitle() != null) {
            Toast.makeText(this, "Movie added to Favorites = " + movie.getOriginalTitle(), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Error adding movie to Favorites ", Toast.LENGTH_LONG).show();

        }
    }

    public void removeFromFavorites(Movie movie) {
        Log.d(LOG_TAG, "inside removeFromFavorites method");
        Log.d(LOG_TAG, "Movie original Title is =" + movie.getOriginalTitle());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().deleteMovieById(movie.getId());
                Log.d(LOG_TAG, "Movie list after deleting this movie is " + mDb.movieDao().getAllMovies());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                    }
                });
            }
        });

    }

    public String getFormattedReleaseData(String releaseDate) {
        String formattedDate;
        formattedDate = releaseDate;

        if (formattedDate != null) {
            try {
                formattedDate = DateUtilities.getLocalizedDate(this,
                        formattedDate, mMovie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {
            mTextviewReleaseDate.setTypeface(null, Typeface.ITALIC);
            formattedDate = getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }

    @Override
    public void onTaskCompleted(Movie movie) {
        mFavMovie = movie;
        if (mFavMovie != null) {
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);

        } else {
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);

        }

    }

}





