package com.example.rafaelanastacioalves.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by rafael.alves on 27/06/16.
 */

@Database(version = MoviesDatabase.VERSION)
public final class MoviesDatabase {
    private MoviesDatabase(){

    }
    public static final int VERSION = 2;

    @Table(MovieColumns.class) public static final String MOVIES_TABLE = "movies";
}
