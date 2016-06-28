package com.example.rafaelanastacioalves.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.rafaelanastacioalves.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

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
            MovieContract.MovieColumns.FAVORITE,
            MovieContract.MovieColumns.POSTER_PATH

    };

    static final int _ID_COL = 0;
    static final int ORIGINAL_TITLE_COL = 1;
    static final int PLOTED_SYNOPSIS_COL = 2;
    static final int RELEASE_DATE_COL = 3;
    static final int POPULARITY_COL = 4;
    static final int USER_RATING_COL = 5;
    static final int FAVORITE_COL = 6;
    static final int POSTER_PATH_COL = 7;





    public static class ViewHolder {
        public final ImageView iconView;

        public ViewHolder(View v){
            this.iconView =  (ImageView) v.findViewById(R.id.movie_list_item_image_view);
        }

    }

    public CustomMoviesListAdapter(Context context, Cursor c, int behavior) {
        super(context,c, behavior );
        aMovieList = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;


    }





    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        Picasso.with(context.getApplicationContext()).load(cursor.getString(cursor.getColumnIndex(PROJETION[CustomMoviesListAdapter.POSTER_PATH_COL]))).into(holder.iconView);



    }




}
