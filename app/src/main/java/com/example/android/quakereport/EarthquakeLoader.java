package com.example.android.quakereport;

/**
 * Created by arata on 20/11/2016.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to load Earthquakes on the background
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String httpQueryURL;

    @Override
    protected void onStartLoading() {
        Log.i("LOADER","force load");
        forceLoad();
    }

    public EarthquakeLoader(Context context, String url) {
        super(context);
        httpQueryURL = url;
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if(httpQueryURL.isEmpty())
            return null;
        Log.i("LOADER", "making http request");
        ArrayList<Earthquake> earthquakes =QueryUtils.fetchEarthQuakes(httpQueryURL);
        return earthquakes;
    }
}