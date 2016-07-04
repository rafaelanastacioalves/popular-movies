package com.example.rafaelanastacioalves.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.rafaelanastacioalves.popularmovies.BuildConfig;
import com.example.rafaelanastacioalves.popularmovies.R;
import com.example.rafaelanastacioalves.popularmovies.Utility;
import com.example.rafaelanastacioalves.popularmovies.data.MovieColumns;
import com.example.rafaelanastacioalves.popularmovies.data.MoviesProvider;
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
import java.util.Vector;

/**
 * Created by rafael.alves on 27/06/16.
 */

public class MovieDBSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int SYNC_INTERVAL =  24 * 60 ;
    private static final int SYNC_FLEXTIME =  SYNC_INTERVAL/3 ;
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


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;


        try {

            final String APPID_KEY = "api_key";
            //noinspection UnusedAssignment
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

        return;
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


        //noinspection UnusedAssignment
        String movie_id;


        JSONObject moviesObject = new JSONObject(moviesJsonStr);
        JSONArray moviesListArray = moviesObject.getJSONArray(MDBM_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesListArray.length());

        //noinspection UnusedAssignment
        Movie aMovieTemp;
        for (int i = 0; i < moviesListArray.length(); i++) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieColumns._ID,moviesListArray.getJSONObject(i).getString(MDBM_ID));
            movieValues.put(MovieColumns.ORIGINAL_TITLE,moviesListArray.getJSONObject(i).getString(MDBM_ORIGINAL_TITLE));
            movieValues.put(MovieColumns.PLOTED_SYNOPSIS,moviesListArray.getJSONObject(i).getString(MDBM_PLOT_SYNOPSIS));
            //TODO refatorar essa concatenação
            movieValues.put(MovieColumns.POSTER_PATH,IMAGETMDB_BASE_URL +
                                                                image_size_default +
                                                                moviesListArray.getJSONObject(i).getString(MDBM_POSTER_PATH));
            movieValues.put(MovieColumns.VOTE_AVERAGE,moviesListArray.getJSONObject(i).getString(MDBM_VOTE_AVARAGE));
            movieValues.put(MovieColumns.POPULARITY,moviesListArray.getJSONObject(i).getString(MDBM_POPULARITY));
            movieValues.put(MovieColumns.RELEASE_DATE,moviesListArray.getJSONObject(i).getString(MDBM_RELEASE_DATE));
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
//            aMovieTemp.setVoteAverage(moviesListArray.getJSONObject(i).getString(MDBM_VOTE_AVARAGE));
//            aMovieTemp.setReleaseDate(moviesListArray.getJSONObject(i).getString(MDBM_RELEASE_DATE));
//
//            moviesList.add(
//                    aMovieTemp
//            );
        }

        for (int i = 0; i < cVVector.size(); i++) {
            ContentValues cv = cVVector.get(i);
            try {

                Log.d(LOG_TAG, "trying to insert a row...");
                //noinspection UnusedAssignment
                Uri rowNumber = getContext().getContentResolver().insert(MoviesProvider.Movies.MOVIES_URI, cv);
                Log.d(LOG_TAG, "inserted ok! Count: " + (i+1) );

            }catch (Exception e){
                Log.e(LOG_TAG, "Insert not possible:" + e.getCause());
                Log.d(LOG_TAG, "Trying to update");
                int rows = getContext().getContentResolver().update(MoviesProvider.Movies.MOVIES_URI, cv, MovieColumns._ID + " = " + cv.getAsString(MovieColumns._ID) , null);
                if (rows > 0){
                    Log.i(LOG_TAG, "updated successfuly. Count: " + (i+1));
                }
            }

        }


//            Cursor c = getContext().getContentResolver().query(MoviesProvider.Movies.MOVIES_URI,null,null,null,null);
//        if(c !=null ){
//            Log.d(LOG_TAG,"Deleting previous data. All of it");
//
//            getContext().getContentResolver().delete(MoviesProvider.Movies.MOVIES_URI,null,null);
//        }

//        Log.d(LOG_TAG,"Inserting the new ones");
//        int inserted = 0;
//        if(cVVector.size()>0){
//            ContentValues[] cvArray = new  ContentValues[cVVector.size()];
//            cVVector.toArray(cvArray);
//            getContext().getContentResolver().bulkInsert(MoviesProvider.Movies.MOVIES_URI, cvArray);
//            getContext().getContentResolver().inser
//
//        }
//        c.close();
}
    public static void initializeSyncAdapter(Context mContext){
        getSyncAccount(mContext);

    }

    private static Account getSyncAccount(Context mContext) {
        AccountManager accountManager = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                mContext.getString(R.string.app_name), mContext.getString(R.string.sync_account_type));

        if( accountManager.getPassword(newAccount) == null ){
            if(!accountManager.addAccountExplicitly(newAccount,"",null)){
                return null;
            }

            onAccountCreated(newAccount, mContext);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context mContext) {
        MovieDBSyncAdapter.configurePeriodicSync(mContext, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,mContext.getString(R.string.content_authority),true);
        syncImmediatly(mContext);
    }

    private static void configurePeriodicSync(Context mContext, int syncInterval, int syncFlextime) {
        Account account = getSyncAccount(mContext);
        String authority = mContext.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, syncFlextime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }else {
            ContentResolver.addPeriodicSync(account,authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediatly(Context mContext) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(mContext), mContext.getString(R.string.content_authority),bundle);
    }

}
