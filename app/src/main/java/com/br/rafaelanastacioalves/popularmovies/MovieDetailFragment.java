package com.br.rafaelanastacioalves.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.br.rafaelanastacioalves.popularmovies.constants.Constants;
import com.br.rafaelanastacioalves.popularmovies.data.MovieColumns;
import com.br.rafaelanastacioalves.popularmovies.data.MoviesProvider;
import com.br.rafaelanastacioalves.popularmovies.entities.Movie;
import com.br.rafaelanastacioalves.popularmovies.entities.Review;
import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {


    private final String LOG_TAG = getClass().getSimpleName();
    private final String TAG_NAME = this.getClass().getSimpleName();



    private Movie aMovie;
    private ArrayList<Review> aReviewList;
    private View rootView;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movie_detail_content, container, false);
        Log.d(TAG_NAME,"retrieving EXTRAS");


        Bundle args = getArguments();

        if(args!= null && !args.isEmpty()) {
            if (args.containsKey(Constants.EXTRA_MOVIE)) {
                aMovie = args.getParcelable(Constants.EXTRA_MOVIE);

                ImageView aMovieDetailImageView = (ImageView) rootView.findViewById(R.id.movie_detail_image_view);
                Picasso.with(getActivity().getApplicationContext()).load(aMovie.getPosterPath()).into(aMovieDetailImageView);

                final Button aButtonView = (Button) rootView.findViewById(R.id.movie_detail_favorite);
                Log.i(TAG_NAME,"Deciding Button status");
                setFavoriteText(aButtonView);
                aButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aMovie.setFavorite(!aMovie.getFavoriteBooleanStatus());
                        setFavorite(v);
                        setFavoriteText(aButtonView);
                    }
                });

                TextView aMovieDetailOriginalTitleTextView = (TextView) rootView.findViewById(R.id.movie_detail_original_title);
                aMovieDetailOriginalTitleTextView.setText(aMovie.getOriginalTitle());


                TextView aMovieDetailReleaseDateTextView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
                aMovieDetailReleaseDateTextView.setText(getString(R.string.movie_detail_release_date) + aMovie.getReleaseDate());


                TextView aMovieDetailSynopsis = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);
                aMovieDetailSynopsis.setText(aMovie.getPlotedSynopsis());

                RatingBar aMovieDetailRating = (RatingBar) rootView.findViewById(R.id.movie_detail_rating);
                aMovieDetailRating.setRating(Float.valueOf(aMovie.getVoteAverage())/2);

            }


            if(savedInstanceState != null && savedInstanceState.containsKey(Constants.REVIEWS)){
                Log.i(LOG_TAG, "We already have a reviewList ");
                aReviewList = savedInstanceState.getParcelableArrayList(Constants.REVIEWS);
                buildReviewList(aReviewList);
            }else if(aReviewList==null){
                    Log.i(LOG_TAG, "No Review List: Retrieving from API ");
                    new DownloadReviewInformation().execute(aMovie);
                }




            if(aMovie.getVideosArray()==null || aMovie.getVideosArray().isEmpty()){
                Log.i(LOG_TAG, "No Video List: Retrieving from API ");
                new DownloadVideoInformation().execute(aMovie);
            }else {
                Log.i(LOG_TAG, "We already have videoList... ");

                buildVideosListView(aMovie.getVideosArray());
            }
        }








        return rootView;
    }

    private void setFavoriteText(Button aButtonView) {
        if(aMovie.getFavoriteBooleanStatus()){
            aButtonView.setText(R.string.movie_detail_favorited);
        }else{
            aButtonView.setText(R.string.mark_as_favorite);

        }
    }


    private void setFavorite(View v) {

        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.FAVORITE,aMovie.getFavoriteIntegerStatus());
        Log.i(LOG_TAG,"Making it "+ (aMovie.getFavoriteBooleanStatus()? "":"not") + " favorite and persisting");
        int rows = getContext().getContentResolver().update(MoviesProvider.Movies.MOVIES_URI, cv, MovieColumns._ID + " = " + aMovie.getId(), null);
        Log.i(LOG_TAG, rows + "Values persisted");


        //Have to create a URI referencing the ID of the movie and set it to the value we want. Update value...

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG,"onSaveInstance");
        outState.putParcelableArrayList(Constants.REVIEWS, aReviewList);
        super.onSaveInstanceState(outState);
    }

    private void buildReviewList(ArrayList<Review> reviewArrayList) {
        Review r;
        if(reviewArrayList!= null && !reviewArrayList.isEmpty()){
            LinearLayout reviewContainer = (LinearLayout) rootView.findViewById(R.id.movie_detail_reviews_container);
            for (int i = 0; i < reviewArrayList.size(); i++) {
                if (getContext() !=null && getContext().getResources()!=null){
                    TextView v = new TextView(getContext());
                    r = reviewArrayList.get(i);
                    v.setText("Author: " + r.getAuthor() + "\n" +
                            "Review: " + r.getContent());
                    reviewContainer.addView(v);
                }


            }
        }
    }

    private class DownloadReviewInformation extends AsyncTask<Movie,Void,ArrayList<Review>> {
        private String LOG_TAG = this.getClass().getSimpleName();
        private final String MOVIEDB_BASE_URL =
                "https://api.themoviedb.org/3/movie";

        private Movie iMovie;
        private ArrayList<Review> iReviewList;

        @Override
        protected ArrayList<Review> doInBackground(Movie... movies) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            iReviewList= new ArrayList<Review>();

            iMovie = movies[0];
            try {

                final String APPID_KEY = "api_key";
                final String ORDERING_PARAM = "sort_by";
                final String REVIEWS_PATH_SEGMENT = "reviews";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(iMovie.getId())
                        .appendPath(REVIEWS_PATH_SEGMENT)
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
                    return iReviewList;
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
                    return iReviewList;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return iReviewList;
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
            return iReviewList;
        }

        private void getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            Log.d(LOG_TAG,"Parsing Movies JSON");


            String MDBM_RESULTS = "results";
            String MDBM_NAME = "author";
            String MDBM_CONTENT = "content";





            String movie_id;


            JSONObject moviesObject = new JSONObject(moviesJsonStr);
            JSONArray reviewsJSONArray = moviesObject.getJSONArray(MDBM_RESULTS);


            for (int i = 0; i < reviewsJSONArray.length(); i++) {
                 Review r = new Review(
                         reviewsJSONArray.getJSONObject(i).getString(MDBM_NAME),
                         reviewsJSONArray.getJSONObject(i).getString(MDBM_CONTENT)
                 );

                iReviewList.add(r);

            }


        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviewArrayList) {
            aReviewList = reviewArrayList;
            buildReviewList(aReviewList);
        }
    }


    private class DownloadVideoInformation extends AsyncTask<Movie,Void,Movie> {
        private String LOG_TAG = this.getClass().getSimpleName();
        private final String MOVIEDB_BASE_URL =
                "https://api.themoviedb.org/3/movie";

        private Movie iMovie;

        @Override
        protected Movie doInBackground(Movie... movies) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            iMovie = movies[0];
            try {

                final String APPID_KEY = "api_key";
                final String ORDERING_PARAM = "sort_by";
                final String VIDEOS_PATH_SEGMENT = "videos";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(iMovie.getId())
                        .appendPath(VIDEOS_PATH_SEGMENT)
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
                    return iMovie;
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
                    return iMovie;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return iMovie;
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
            return iMovie;
        }

        private void getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            Log.d(LOG_TAG,"Parsing Movies JSON");


            String MDBM_RESULTS = "results";
            String MDBM_KEY = "key";




            String movie_id;


            JSONObject moviesObject = new JSONObject(moviesJsonStr);
            JSONArray videoListArray = moviesObject.getJSONArray(MDBM_RESULTS);

            ArrayList<String> videosArray = new ArrayList<String>();

            for (int i = 0; i < videoListArray.length(); i++) {
                ContentValues movieValues = new ContentValues();

                videosArray.add(videoListArray.getJSONObject(i).getString(MDBM_KEY));


            }

            aMovie.setVideosArray(videosArray);


        }

        @Override
        protected void onPostExecute(Movie movie) {
            ArrayList<String> videosArray = movie.getVideosArray();
            buildVideosListView(videosArray);
        }


    }

    private void buildVideosListView(ArrayList<String> videosArray) {
        if(videosArray!= null && !videosArray.isEmpty()){
            LinearLayout videosContainer = (LinearLayout) rootView.findViewById(R.id.movie_detail_videos_container);
            for (int i = 0; i < videosArray.size(); i++) {
                LinearLayout videoContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.video_content, null);
                TextView tv = (TextView) videoContent.findViewById(R.id.video_content_text);
                tv.setText(String.format(getString(R.string.video_number_formatted), (i+1)));
                videoContent.setTag(videosArray.get(i));
                videoContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = (String) v.getTag();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+key)));

                    }
                });
                videosContainer.addView(videoContent);

            }
        }
    }


}



