package com.br.rafaelanastacioalves.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.br.rafaelanastacioalves.popularmovies.constants.Constants;
import com.br.rafaelanastacioalves.popularmovies.data.MoviesProvider;
import com.br.rafaelanastacioalves.popularmovies.entities.Movie;
import com.br.rafaelanastacioalves.popularmovies.sync.MovieDBSyncAdapter;


public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CustomMoviesListAdapter adapter;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private GridView aGridView;

    private int mPosition = -1;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {
        setHasOptionsMenu(true);
    }


    public void updateMoviesDatabase() {
        Log.i(LOG_TAG, "Updating database");
        MovieDBSyncAdapter.syncImmediatly(getActivity());
        getLoaderManager().restartLoader(Constants.MOVIES_LOADER,null,this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.movies_list_menu,menu);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        mPosition = aGridView.getFirstVisiblePosition();

        outState.putInt(Constants.MOVIE_LIST_POSITION,mPosition);
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);


        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIE_LIST_POSITION)){
            Log.i(LOG_TAG, "Position of list retrieved");
            mPosition = savedInstanceState.getInt(Constants.MOVIE_LIST_POSITION);
        }



        adapter = new CustomMoviesListAdapter(getActivity(), null, 0);
        aGridView = (GridView) view.findViewById(R.id.movie_list_grid_view);

        aGridView.setAdapter(adapter);

        aGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                Movie aMovie = new Movie(c);
                mPosition = position;
                ((CallBack)getActivity()).onItemSelected(aMovie);



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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        getLoaderManager().initLoader(Constants.MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");

        if(Utility.getSortParam(getActivity()).equals(getString(R.string.highly_rated_title_option))){
            Log.i(LOG_TAG,"Creating Cursor Loader --> Top Rated");
            return new CursorLoader(getActivity(), MoviesProvider.Movies.MOVIES_TOP_RATED_URI, null, null,null, null );

        }else if(Utility.getSortParam(getActivity()).equals(getString(R.string.popularity_order_title_option))){
            Log.i(LOG_TAG,"Creating Cursor Loader --> Popular");
            return new CursorLoader(getActivity(), MoviesProvider.Movies.MOVIES_POPULAR_URI, null, null,null, null );
        }else if(Utility.getSortParam(getActivity()).equals(getString(R.string.favorites_title_option))) {
            Log.i(LOG_TAG, "Creating Cursor Loader --> Favorites");
            return new CursorLoader(getActivity(), MoviesProvider.Movies.MOVIES_FAVORITES_URI, null, null, null , null);
        }

            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (mPosition != -1){
            aGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }



    public interface  CallBack {
        void onItemSelected(Movie aMovie);
    }
}


