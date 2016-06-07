package com.example.rafaelanastacioalves.popularmovies.entities;

/**
 * Created by rafael.alves on 07/06/16.
 */

public class Movie {
    private String id;
    private String posterPath;
    private String originalTitle;
    private String plotedSynopsis;
    private String userRating;
    private String releaseDate;


    public Movie (String id, String posterPath){
        this.id=id;
        this.posterPath=posterPath;
    }

    public Movie (String id){
        this.id=id;
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
}
