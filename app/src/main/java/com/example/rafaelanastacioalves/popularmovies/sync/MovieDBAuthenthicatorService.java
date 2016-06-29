package com.example.rafaelanastacioalves.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by rafael.alves on 27/06/16.
 */

public class MovieDBAuthenthicatorService extends Service {
    MovieDBAuthenthicator mAuthenthicator ;
    @Override
    public void onCreate() {
         mAuthenthicator = new MovieDBAuthenthicator(getBaseContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  mAuthenthicator.getIBinder();
    }
}
