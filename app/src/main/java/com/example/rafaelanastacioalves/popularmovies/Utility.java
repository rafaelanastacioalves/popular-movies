package com.example.rafaelanastacioalves.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by rafaelanastacioalves on 6/28/16.
 */
public class Utility {

    public static String getSortParam(Context mContext) {
        String sortParam;

        sortParam = PreferenceManager.getDefaultSharedPreferences(mContext).getString(
                mContext.getString(R.string.ordering_list_key),
                mContext.getString(R.string.highly_rated_title_option));

        return sortParam;
    }
}
