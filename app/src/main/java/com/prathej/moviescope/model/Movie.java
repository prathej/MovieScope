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

package com.prathej.moviescope.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.prathej.moviescope.AppExecutors;
import com.prathej.moviescope.data.AppDatabase;
import com.prathej.moviescope.isFavoriteTaskCompleted;

import org.jetbrains.annotations.NotNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie implements Parcelable {

    @Ignore
    public isFavoriteTaskCompleted mListener = null;
    private static final String LOG_TAG = Movie.class.getSimpleName();
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int _id;

    @ColumnInfo(name = "MovieTitle")
    @SerializedName("original_title")
    private String mOriginalTitle;

    @ColumnInfo(name = "MoviePoster")
    @SerializedName("poster_path")
    private String mPosterPath;

    @ColumnInfo(name = "MovieOverview")
    @SerializedName("overview")
    private String mOverview;

    @ColumnInfo(name = "MovieVoteAvg")
    @SerializedName("vote_average")
    private Double mVoteAverage;

    //private String mVotes;

    @ColumnInfo(name = "MovieReleaseDate")
    @SerializedName("release_date")
    private String mReleaseDate;


    @ColumnInfo(name = "MovieId")
    @SerializedName("id")
    private String mId;



    public Movie() {
    }

    @Ignore
    public Movie(String id, String originalTitle, String posterPath, String overview, Double voteAverage, String releaseDate) {
        mId = id;
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
    }

    public Movie(int id, String movieId, String originalTitle, String posterPath, String overview, Double voteAverage, String releaseDate) {
        _id = id;
        mId = movieId;
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        _id = id;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }


    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }


    public void setOverview(String overview) {
        if (!overview.equals("null")) {
            mOverview = overview;
        }
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }


    public void setReleaseDate(String releaseDate) {
        if (!releaseDate.equals("null")) {
            mReleaseDate = releaseDate;
        }
    }

    public void setId(String id) {
        mId = id;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }


    public String getPosterPath() {

        Log.d(LOG_TAG, mPosterPath);
        return mPosterPath;

    }



    public String getOverview() {
        Log.d(LOG_TAG, "control has entered getOverview() method");
        return mOverview;
    }

    public Double getVoteAverage() {
        Log.d(LOG_TAG, "control has entered getVoteAverage() method");
        return mVoteAverage;
    }


    public String getReleaseDate() {
        return mReleaseDate;
    }


    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
    }


    public String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeValue(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
    }

    public Movie(Parcel in) {
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = (Double) in.readValue(Double.class.getClassLoader());
        mReleaseDate = in.readString();
        mId = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void isFavorite(Context context, AppDatabase mDb) {

        try {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    final Movie movie = mDb.movieDao().loadMovieById(getId());
                    if (movie != null) {
                        Log.d(LOG_TAG, "Movie title is" + movie.getOriginalTitle());
                      //  mListener.onTaskCompleted(movie);
                    }
                    mListener.onTaskCompleted(movie);

                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to find data");
            e.printStackTrace();
        }

    }




 /*   public String getFormattedReleaseData(Context context, String releaseDate) {
        String formattedDate;
        formattedDate = releaseDate;

        if (formattedDate != null) {
            try {
                formattedDate = DateUtilities.getLocalizedDate(context,
                        formattedDate, getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {

            formattedDate = context.getResources().getString(R.string.no_release_date_found);
        }
        return formattedDate;
    }*/
}
