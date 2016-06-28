package com.example.rafaelanastacioalves.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rafaelanastacioalves.popularmovies.entities.Movie;
import com.example.rafaelanastacioalves.popularmovies.sync.MovieDBSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.CallBack{

    private final String LOG_TAG = getClass().getSimpleName() ;
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_details_container) != null){
            mTwoPane = true;
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_list_fragment_container, new MoviesFragment())
                    .commit();

            // adding details fragment for the first time if two pane
//            if(mTwoPane){
//                getSupportFragmentManager().beginTransaction().add(R.id.fragment_details_container,new MovieDetailFragment())
//                        .commit();
//            }
        }

        MovieDBSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public void onItemSelected(Movie aMovie) {

        if(mTwoPane){
            Fragment detailFragment = new MovieDetailFragment();
            Bundle args = new Bundle();
            args.putParcelable("movie", aMovie);
            detailFragment.setArguments(args);
            Log.d(LOG_TAG, "Calling Detail Fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_details_container, detailFragment).commit();

        }else {
            Intent aMovieDetailIntent = new Intent(this,MovieDetail.class)
                    .putExtra("movie",aMovie);
            startActivity(aMovieDetailIntent);
        }

    }


}
