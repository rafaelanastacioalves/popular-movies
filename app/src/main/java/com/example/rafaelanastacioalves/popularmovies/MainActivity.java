package com.example.rafaelanastacioalves.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.rafaelanastacioalves.popularmovies.constants.Constants;
import com.example.rafaelanastacioalves.popularmovies.entities.Movie;
import com.example.rafaelanastacioalves.popularmovies.sync.MovieDBSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesFragment.CallBack{

    private String currentSortParam;

    private final String LOG_TAG = getClass().getSimpleName() ;
    private boolean mTwoPane = false;
    private final static String MOVIE_FRAGMENT_TAG = "MFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_details_container) != null){
            mTwoPane = true;
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRA_SORT)) {
            Log.d(LOG_TAG,"We have sort parameter saved");
            currentSortParam = savedInstanceState.getString(Constants.EXTRA_SORT);
        }else {
            Log.d(LOG_TAG,"Instance not saved, retrieving sort param from preferences...");
            currentSortParam = Utility.getSortParam(this);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_list_fragment_container, new MoviesFragment(), MOVIE_FRAGMENT_TAG)
                    .commit();

            // adding details fragment for the first time if two pane
//            if(mTwoPane){
//                getSupportFragmentManager().beginTransaction().add(R.id.fragment_details_container,new MovieDetailFragment())
//                        .commit();
//            }
        }

        Log.i(LOG_TAG, "Initializing adapter");
        MovieDBSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentSortParam == null || !currentSortParam.equals( Utility.getSortParam(this))){
            Log.d(LOG_TAG,"Preferences changed: updating");
            MoviesFragment mf = (MoviesFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_FRAGMENT_TAG);
            currentSortParam = Utility.getSortParam(this);
            if(mf != null){
                mf.updateMoviesDatabase();

            }
        }
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
                    .putExtra(Constants.EXTRA_MOVIE,aMovie);
            startActivity(aMovieDetailIntent);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(Constants.EXTRA_SORT,Utility.getSortParam(this));
        Log.d(LOG_TAG,"Saving order param...");

        super.onSaveInstanceState(outState, outPersistentState);

    }
}
