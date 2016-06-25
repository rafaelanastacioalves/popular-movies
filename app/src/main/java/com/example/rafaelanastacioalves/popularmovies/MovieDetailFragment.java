package com.example.rafaelanastacioalves.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rafaelanastacioalves.popularmovies.constants.Constants;
import com.example.rafaelanastacioalves.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {


    private final String TAG_NAME = this.getClass().getSimpleName();



    private Movie aMovie;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Log.d(TAG_NAME,"retrieving EXTRAS");

        Intent intent = getActivity().getIntent();


        if(intent!= null) {
            if (intent.hasExtra(Constants.EXTRA_MOVIE)) {
                aMovie = intent.getParcelableExtra(Constants.EXTRA_MOVIE);

                ImageView aMovieDetailImageView = (ImageView) rootView.findViewById(R.id.movie_detail_image_view);
                Picasso.with(this.getContext()).load(aMovie.getPosterPath()).into(aMovieDetailImageView);


                TextView aMovieDetailOriginalTitleTextView = (TextView) rootView.findViewById(R.id.movie_detail_original_title);
                aMovieDetailOriginalTitleTextView.setText(aMovie.getOriginalTitle());


                TextView aMovieDetailReleaseDateTextView = (TextView) rootView.findViewById(R.id.movie_detail_release_date);
                aMovieDetailReleaseDateTextView.setText(getString(R.string.movie_detail_release_date) + aMovie.getReleaseDate());


                TextView aMovieDetailSynopsis = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);
                aMovieDetailSynopsis.setText(aMovie.getPlotedSynopsis());

                RatingBar aMovieDetailRating = (RatingBar) rootView.findViewById(R.id.movie_detail_rating);
                aMovieDetailRating.setRating(Float.valueOf(aMovie.getUserRating())/2);


            }
        }








        return rootView;
    }







}
