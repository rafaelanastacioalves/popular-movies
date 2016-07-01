package com.example.rafaelanastacioalves.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MovieDetail extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetail.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Fragment detailFragment = new MovieDetailFragment();
        detailFragment.setArguments(getIntent().getExtras());
        Log.d(LOG_TAG, "Calling Detail Fragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_details_container, detailFragment).commit();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


    }

}
