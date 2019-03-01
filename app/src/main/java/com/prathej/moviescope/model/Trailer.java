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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Trailer implements Parcelable {
    @SerializedName("id")
    private String mId;
    @SerializedName("key")
    private String mKey;

    public Trailer(){

    }

    private Trailer (Parcel in){
        mId = in.readString();
        mKey = in.readString();
    }

    public void setId(String id){
        mId = id;
    }

    public void setKey(String key){
        mKey=key;
    }

    public String getId(){
    return mId;
    }

    public String getKey(){
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
    }

    public static final Creator<Trailer> CREATOR= new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };


}
