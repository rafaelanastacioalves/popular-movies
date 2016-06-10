package com.example.rafaelanastacioalves.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MoviesFragment extends Fragment {

    private CustomMoviesListAdapter adapter;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private String currentSortParam;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey("sort")) {
            Log.d(LOG_TAG,"We have sort parameter saved");
            currentSortParam = savedInstanceState.getString("sort");
        }


            if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            // if there is no saved instance
            adapter = new CustomMoviesListAdapter(this.getContext(), new ArrayList<Movie>());
            updateMoviesDatabase();
        }else {
                Log.d(LOG_TAG,"We have list of movies saved");

                adapter = new CustomMoviesListAdapter(this.getContext(), savedInstanceState.<Movie>getParcelableArrayList("movies"));

        }
    }



    private void updateMoviesDatabase() {
        FetchMoviesTask getMovieTask = new FetchMoviesTask();
        currentSortParam = getSortParam();
        Log.d(LOG_TAG,"updateMovieDatabase with param " + getSortParam());
        getMovieTask.execute(currentSortParam);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.movies_list_menu,menu);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!currentSortParam.equals(getSortParam())){
            Log.d(LOG_TAG,"Preferences changed: updating");
            updateMoviesDatabase();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", adapter.getItems());
        outState.putString("sort",currentSortParam);
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView aGridView = (GridView) view.findViewById(R.id.movie_list_grid_view);

        aGridView.setAdapter(adapter);

        aGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie aMovie = adapter.getItem(position);
                Intent aMovieDetailIntent = new Intent(getContext(),MovieDetail.class)
                        .putExtra("movie",aMovie);
                startActivity(aMovieDetailIntent);


            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    private String getSortParam() {
        String sortParam;

        sortParam = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.ordering_list_key),getString(R.string.highly_rated_title_option));

        return sortParam;
    }


    private class FetchMoviesTask extends AsyncTask<String, Void,ArrayList<Movie>> {

        private String LOG_TAG = this.getClass().getSimpleName();
        private final String MOVIEDB_BASE_URL =
                "https://api.themoviedb.org/3/discover/movie";
        private final String IMAGETMDB_BASE_URL =
                "http://image.tmdb.org/t/p/";

        private final String image_size_default = "w342";
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String APPID_KEY = "api_key";
                final String ORDERING_PARAM = "sort_by";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .appendQueryParameter(ORDERING_PARAM, params[0])
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
                    return null;
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
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> aResult) {
            Log.d(LOG_TAG,"onPostExecute");
            if(aResult != null){
                adapter.clear();
                adapter.addAll(aResult);
                adapter.notifyDataSetChanged();
            }




        }

        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            Log.d(LOG_TAG,"Parsing Movies JSON");

            ArrayList<Movie> moviesList = new ArrayList<Movie>();

            String MDBM_RESULTS = "results";
            String MDBM_POSTER_PATH = "poster_path";
            String MDBM_ID = "id";
            String MDBM_ORIGINAL_TITLE = "original_title";
            String MDBM_PLOT_SYNOPSIS = "overview";
            String MDBM_VOTE_AVARAGE = "vote_average";
            String MDBM_RELEASE_DATE = "release_date";



            String movie_id;


            JSONObject moviesObject = new JSONObject(moviesJsonStr);
            JSONArray moviesListArray = moviesObject.getJSONArray(MDBM_RESULTS);

            Movie aMovieTemp;
            for (int i = 0; i < moviesListArray.length(); i++) {
                movie_id = moviesListArray.getJSONObject(i).getString(MDBM_ID);
                aMovieTemp = new Movie(movie_id);

                aMovieTemp.setOriginalTitle(moviesListArray.getJSONObject(i).getString(MDBM_ORIGINAL_TITLE));
                aMovieTemp.setPlotedSynopsis(moviesListArray.getJSONObject(i).getString(MDBM_PLOT_SYNOPSIS));
                aMovieTemp.setPosterPath(
                        IMAGETMDB_BASE_URL +
                        image_size_default +
                        moviesListArray.getJSONObject(i).getString(MDBM_POSTER_PATH))

                        ;
                aMovieTemp.setUserRating(moviesListArray.getJSONObject(i).getString(MDBM_VOTE_AVARAGE));
                aMovieTemp.setReleaseDate(moviesListArray.getJSONObject(i).getString(MDBM_RELEASE_DATE));

                moviesList.add(
                        aMovieTemp
                );
            }

            return moviesList;

        }
    }
}
