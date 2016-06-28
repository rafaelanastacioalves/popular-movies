package com.example.rafaelanastacioalves.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by rafael.alves on 27/06/16.
 */

public class MovieDBSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieDBSyncAdapter aMovieDBSyncAdapter = null;


    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if (aMovieDBSyncAdapter == null){
                aMovieDBSyncAdapter = new MovieDBSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return aMovieDBSyncAdapter.getSyncAdapterBinder();
    }
}
