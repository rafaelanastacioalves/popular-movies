package com.example.rafaelanastacioalves.popularmovies.entities;

/**
 * Created by rafael.alves on 07/06/16.
 */

public class Movie {
    private String id;
    private String posterPath;

    public Movie (String id, String posterPath){
        this.id=id;
        this.posterPath=posterPath;
    }
}
