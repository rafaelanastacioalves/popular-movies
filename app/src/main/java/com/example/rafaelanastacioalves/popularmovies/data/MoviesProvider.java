package com.example.rafaelanastacioalves.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by rafael.alves on 27/06/16.
 */

@ContentProvider(authority = this.AUTHORITY, database = MoviesDatabase.class)
public final class MoviesProvider {
    public static final String AUTHORITY = "com.example.rafaelanastacioalves.popularmovies.data.MoviesProvider";

     interface Path{
         String MOVIE = "movie";
         String POPULAR = "popular";
         String TOP_RATED = "top_rated";

    }

    @TableEndpoint(table = MoviesDatabase.MOVIES) public static class Movies {
        @ContentUri(
                path = Path.MOVIE  + "/popular",
                type = "vnd.android.cursor.itme/planet",
                defaultSort = MovieContract.MovieColumns.POPULARITY + " DESC"
        ) public static final Uri MOVIES_POPULAR_URI = Uri.parse("content://" + AUTHORITY + "/" + Path.MOVIE + "/" + Path.POPULAR);

        @ContentUri(
                path = Path.MOVIE  + "/top_rated",
                type = "vnd.android.cursor.itme/planet",
                defaultSort = MovieContract.MovieColumns.USER_RATING + " DESC"
        ) public static final Uri MOVIES_TOP_RATED_URI = Uri.parse("content://" + AUTHORITY + "/" + Path.MOVIE + "/" + Path.TOP_RATED);

    }

}
