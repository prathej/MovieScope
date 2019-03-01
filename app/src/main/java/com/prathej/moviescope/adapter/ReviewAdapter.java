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
import android.widget.TextView;

import com.prathej.moviescope.R;
import com.prathej.moviescope.model.Review;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<Review> mReview;

    public ReviewAdapter(Context context, List<Review> review){
        mContext=context;
        mReview=review;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list,parent,false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyItemHolder)holder).authorTextView.setText(mReview.get(position).getAuthor());
        ((MyItemHolder) holder).contentTextView.setText(mReview.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReview.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder{
        TextView authorTextView, contentTextView;

        public MyItemHolder(View itemView) {
            super(itemView);

            authorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            contentTextView= (TextView) itemView.findViewById(R.id.tv_review);
        }
    }
}
