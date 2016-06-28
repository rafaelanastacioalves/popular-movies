package com.example.rafaelanastacioalves.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.rafaelanastacioalves.popularmovies.data.MovieContract;
import com.example.rafaelanastacioalves.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rafael.alves on 08/06/16.
 */

public class CustomMoviesListAdapter extends CursorAdapter {

    private final Cursor aMovieList;

    public static final String[] PROJETION = new String[] {
            MovieContract.MovieColumns._ID,
            MovieContract.MovieColumns.ORIGINAL_TITLE,
            MovieContract.MovieColumns.PLOTED_SYNOPSIS,
            MovieContract.MovieColumns.RELEASE_DATE,
            MovieContract.MovieColumns.POPULARITY,
            MovieContract.MovieColumns.USER_RATING,
            MovieContract.MovieColumns.FAVORITE
    };

    public CustomMoviesListAdapter(Context context, Cursor c, int behavior) {
        super(context,c, behavior );
        aMovieList = c;
    }

    public CustomMoviesListAdapter()

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie aMovie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_item,parent,false);
        }
        ImageView aMovieItemImageView = (ImageView) convertView.findViewById(R.id.movie_list_item_image_view);
        Picasso.with(getContext()).load(aMovie.getPosterPath()).into(aMovieItemImageView);
        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    //TODO adapt to cursor...
    public ArrayList<Movie> getItems(){
        return aMovieList;
    }
}
