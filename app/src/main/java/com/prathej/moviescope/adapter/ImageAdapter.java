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

package com.prathej.moviescope.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.prathej.moviescope.R;
import com.prathej.moviescope.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.prathej.moviescope.utils.Const.TMDB_POSTER_BASE_URL;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Movie> mMovies;




    public ImageAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;

    }

    @Override
    public int getCount() {
        if (mMovies == null || mMovies.size() == 0) {
            return -1;
        }

        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        if (mMovies == null || mMovies.size() == 0) {
            return null;
        }

        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        String poster = mMovies.get(position).getPosterPath();
        Log.d(ImageAdapter.class.getSimpleName(),"poster path is "+ poster);
        String posterUrl = TMDB_POSTER_BASE_URL + poster;

        Picasso.get()
                .load(posterUrl)
                .resize(185, 278)
                .error(R.drawable.ic_error_outline_black_48dp)
                .placeholder(R.drawable.ic_sync_black_48dp)
                .into(imageView);

        return imageView;
    }
}
