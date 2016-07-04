package com.example.rafaelanastacioalves.popularmovies.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by rafael.alves on 29/06/16.
 */


    public interface MovieColumns {

        @DataType(INTEGER)  @PrimaryKey @Unique
        String _ID = "_id";
        @DataType(TEXT)  @NotNull
        String ORIGINAL_TITLE = "original_title";
        @DataType(TEXT)  @NotNull
        String PLOTED_SYNOPSIS = "ploted_synopsis";
        @DataType(TEXT)  @NotNull
        String POPULARITY = "popularity";
        @DataType(TEXT)  @NotNull
        String VOTE_AVERAGE = "user_rating";
        @DataType(TEXT)  @NotNull
        String RELEASE_DATE = "release_date";
        @DataType(TEXT)  @NotNull
        String POSTER_PATH = "poster_path";
        @DataType(INTEGER) @NotNull @DefaultValue("0")
        String FAVORITE = "favorite";


    }

