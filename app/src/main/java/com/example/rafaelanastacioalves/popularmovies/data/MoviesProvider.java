package com.example.rafaelanastacioalves.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by rafael.alves on 27/06/16.
 */

@ContentProvider(authority = MoviesProvider.AUTHORITY , database = MoviesDatabase.class)
public final class MoviesProvider {
    public static final String AUTHORITY = "com.example.rafaelanastacioalves.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    interface Path{
         String MOVIE = "movie";
         String POPULAR = "popular";
         String TOP_RATED = "top_rated";
         String FAVORITES = "favorites";

    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }


    @TableEndpoint(table = MoviesDatabase.MOVIES_TABLE) public static class Movies {
        @ContentUri(
                path = Path.MOVIE,
                type = "vnd.android.cursor.dir/movie")
        public static final Uri MOVIES_URI = buildUri(Path.MOVIE);


        @ContentUri(
                path = Path.MOVIE + "/"+ Path.POPULAR,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.POPULARITY + " DESC"
        )
        public static final Uri MOVIES_POPULAR_URI = buildUri(Path.MOVIE, Path.POPULAR);
        @ContentUri(
                path = Path.MOVIE + "/"+ Path.TOP_RATED,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.VOTE_AVERAGE + " DESC"
        )
        public static final Uri MOVIES_TOP_RATED_URI = buildUri(Path.MOVIE, Path.TOP_RATED);

        @ContentUri(
                path = Path.MOVIE + "/"+ Path.FAVORITES,
                type = "vnd.android.cursor.dir/movie",
                where = MovieColumns.FAVORITE + " = 1",
                defaultSort = MovieColumns.VOTE_AVERAGE + " DESC"
        )
        public static final Uri MOVIES_FAVORITES_URI = buildUri(Path.MOVIE, Path.FAVORITES);



     }
}




