package com.example.rafaelanastacioalves.popularmovies.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rafael.alves on 07/06/16.
 */

public class Movie implements Parcelable {
    private final String id;
    private String posterPath;
    private String originalTitle;
    private String plotedSynopsis;
    private String userRating;
    private String releaseDate;
    private String popularity;


    public Movie (String id, String posterPath){
        this.id=id;
        this.posterPath=posterPath;
    }

    public Movie (String id){
        this.id=id;
    }

    private Movie(Parcel in) {
        id = in.readString();
        posterPath = in.readString();
        originalTitle = in.readString();
        plotedSynopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPlotedSynopsis() {
        return plotedSynopsis;
    }

    public void setPlotedSynopsis(String plotedSynopsis) {
        this.plotedSynopsis = plotedSynopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(plotedSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);

    }
}
