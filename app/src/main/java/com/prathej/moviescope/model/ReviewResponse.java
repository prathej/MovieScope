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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {

    @SerializedName("page")
    private int mPage;
    @SerializedName("id")
    private int mId;
    @SerializedName("results")
    private List<Review> mReviewResults;

    ReviewResponse() {

    }

    public List<Review> getReviewList() {

        return mReviewResults;
    }

    public void setReviews(List<Review> reviews) {
        this.mReviewResults = reviews;
    }


}
