package com.example.rafaelanastacioalves.popularmovies.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.rafaelanastacioalves.popularmovies.BuildConfig;
import com.example.rafaelanastacioalves.popularmovies.Utility;
import com.example.rafaelanastacioalves.popularmovies.data.MovieContract;
import com.example.rafaelanastacioalves.popularmovies.entities.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by rafael.alves on 27/06/16.
 */

public class MovieDBSyncAdapter extends AbstractThreadedSyncAdapter {
    private String LOG_TAG = this.getClass().getSimpleName();
    private final String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    private final String IMAGETMDB_BASE_URL =
            "http://image.tmdb.org/t/p/";

    private final String image_size_default = "w342";


    public MovieDBSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // If there's no zip code, there's nothing to look up.  Verify size of params.


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;


        try {

            final String APPID_KEY = "api_key";
            final String ORDERING_PARAM = "sort_by";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath( Utility.getSortParam(getContext()))
                    .appendQueryParameter(APPID_KEY, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG,"Accessing url: "+ url.toString() );
            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null
    }
    private void getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        Log.d(LOG_TAG,"Parsing Movies JSON");


        String MDBM_RESULTS = "results";
        String MDBM_POSTER_PATH = "poster_path";
        String MDBM_ID = "id";
        String MDBM_ORIGINAL_TITLE = "original_title";
        String MDBM_PLOT_SYNOPSIS = "overview";
        String MDBM_VOTE_AVARAGE = "vote_average";
        String MDBM_POPULARITY = "popularity";
        String MDBM_RELEASE_DATE = "release_date";



        String movie_id;


        JSONObject moviesObject = new JSONObject(moviesJsonStr);
        JSONArray moviesListArray = moviesObject.getJSONArray(MDBM_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesListArray.length());

        Movie aMovieTemp;
        for (int i = 0; i < moviesListArray.length(); i++) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieColumns._ID,MDBM_ID);
            movieValues.put(MovieContract.MovieColumns.ORIGINAL_TITLE,MDBM_ORIGINAL_TITLE);
            movieValues.put(MovieContract.MovieColumns.PLOTED_SYNOPSIS,MDBM_PLOT_SYNOPSIS);
            //TODO refatorar essa concatenação
            movieValues.put(MovieContract.MovieColumns.POSTER_PATH,IMAGETMDB_BASE_URL +
                                                                image_size_default +
                                                                moviesListArray.getJSONObject(i).getString(MDBM_POSTER_PATH));
            movieValues.put(MovieContract.MovieColumns.USER_RATING,MDBM_VOTE_AVARAGE);
            movieValues.put(MovieContract.MovieColumns.POPULARITY,MDBM_POPULARITY);
            movieValues.put(MovieContract.MovieColumns.RELEASE_DATE,MDBM_RELEASE_DATE);
            cVVector.add(movieValues);

            //TODO remover esse código
//            movie_id = moviesListArray.getJSONObject(i).getString(MDBM_ID);
//            aMovieTemp = new Movie(movie_id);
//
//            aMovieTemp.setOriginalTitle(moviesListArray.getJSONObject(i).getString(MDBM_ORIGINAL_TITLE));
//            aMovieTemp.setPlotedSynopsis(moviesListArray.getJSONObject(i).getString(MDBM_PLOT_SYNOPSIS));
//            aMovieTemp.setPosterPath(
//                    IMAGETMDB_BASE_URL +
//                            image_size_default +
//                            moviesListArray.getJSONObject(i).getString(MDBM_POSTER_PATH))
//
//            ;
//            aMovieTemp.setUserRating(moviesListArray.getJSONObject(i).getString(MDBM_VOTE_AVARAGE));
//            aMovieTemp.setReleaseDate(moviesListArray.getJSONObject(i).getString(MDBM_RELEASE_DATE));
//
//            moviesList.add(
//                    aMovieTemp
//            );
        }


        int inserted = 0;
        if(cVVector.size()>0){
            ContentValues[] cvArray = new  ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MovieContract., cvArray);


        }
}

}
