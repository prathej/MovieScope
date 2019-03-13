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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prathej.moviescope.R;
import com.prathej.moviescope.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Trailer> mTrailer;
    private final Context mContext;

    public TrailerAdapter(Context context, List<Trailer> data){
        mContext=context;
        mTrailer=data;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyItemHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.trailerImage);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;

        view= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.trailer_list,parent,false
        );
        viewHolder=new MyItemHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //String id = mTrailer.get(position).getKey();   -- if we use arraylist instead of array
        String id = mTrailer.get(position).getKey();
     String thumbnailURL = "https://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Picasso.get()
                .load(thumbnailURL)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.thumbnail)
                .into(((MyItemHolder)holder)
                .imageView);
    }

    @Override
    public int getItemCount() {
        return mTrailer.size();
    }
}
