package com.br.rafaelanastacioalves.popularmovies.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rafaelanastacioalves on 7/2/16.
 */
public class Review implements Parcelable {
    private String author;
    private String content;

    public Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(String author, String content ){
        this.author = author;
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Review createFromParcel(Parcel in) {
        return new Review(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);

    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
