package com.example.rafaelanastacioalves.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.rafaelanastacioalves.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by rafael.alves on 08/06/16.
 */

public class CustomMoviesListAdapter extends ArrayAdapter<Movie> {

    private final ArrayList<Movie> aMovieList;

    public CustomMoviesListAdapter(Context context, ArrayList<Movie> objects) {
        super(context, 0, objects);
        aMovieList = objects;
    }

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

    public ArrayList<Movie> getItems(){
        return aMovieList;
    }
}
