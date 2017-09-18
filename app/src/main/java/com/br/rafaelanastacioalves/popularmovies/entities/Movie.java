package com.br.rafaelanastacioalves.popularmovies.entities;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.br.rafaelanastacioalves.popularmovies.data.MovieColumns;

import java.util.ArrayList;

/**
 * Created by rafael.alves on 07/06/16.
 */

public class Movie implements Parcelable {
    private String  id;
    private String posterPath;
    private String originalTitle;
    private String plotedSynopsis;
    private String voteAverage;
    private String releaseDate;
    private String popularity;
    private boolean favorite;
    private ArrayList<String> videosArray;


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
        voteAverage = in.readString();
        releaseDate = in.readString();
        popularity = in.readString();
        favorite = (in.readInt() == 1 ? true : false);
        videosArray = in.createStringArrayList();
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

    public Movie(Cursor c) {
        this.id= c.getString(c.getColumnIndex(MovieColumns._ID)) ;
        this.posterPath = c.getString(c.getColumnIndex(MovieColumns.POSTER_PATH)) ;
        this.originalTitle = c.getString(c.getColumnIndex(MovieColumns.ORIGINAL_TITLE)) ;
        this.plotedSynopsis = c.getString(c.getColumnIndex(MovieColumns.PLOTED_SYNOPSIS));
        this.voteAverage = c.getString(c.getColumnIndex(MovieColumns.VOTE_AVERAGE));
        this.releaseDate = c.getString(c.getColumnIndex(MovieColumns.RELEASE_DATE));
        this.popularity = c.getString(c.getColumnIndex(MovieColumns.POPULARITY));
        this.favorite = c.getInt(c.getColumnIndex(MovieColumns.FAVORITE)) == 1 ? true : false;

    }


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

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId(){
        return id;
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
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(popularity);
        dest.writeInt(favorite == true ? 1 : 0);
        dest.writeStringList(videosArray);

    }

    public boolean getFavoriteBooleanStatus(){
        return favorite;
    }

    public int getFavoriteIntegerStatus (){
        return favorite == true ? 1 : 0;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setVideosArray(ArrayList<String> videosArray) {
        this.videosArray = videosArray;
    }

    public ArrayList<String> getVideosArray(){
        return videosArray;
    }
}
