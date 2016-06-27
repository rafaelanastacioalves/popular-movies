package com.example.rafaelanastacioalves.popularmovies.data;

import com.example.rafaelanastacioalves.popularmovies.entities.Movie;

import net.simonvt.schematic.annotation.Table;

/**
 * Created by rafael.alves on 27/06/16.
 */

public final class MoviesDatabase {
    public static final int VERSION = 1;

    @Table(Movie.class) public static final String MOVIES = "movie";
}
