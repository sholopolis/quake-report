/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private final String UGS_QUERY_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";
    ListView earthquakeListView;
    EarthquakeAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView((TextView) findViewById(R.id.emptyTextView));
        mAdapter = new EarthquakeAdapter(this,new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(mAdapter);

        // create a loader
        LoaderManager loaderManager = getLoaderManager();
        if(internetConnectionAvailable()) {
            loaderManager.initLoader(0, null, this);
        }
        else{
            ((TextView)findViewById(R.id.emptyTextView)).setText("No internet connection");
            ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(GONE);
        }

        // listener to open a detailed webpage of the clicked earthquake
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake current = mAdapter.getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(current.getUrl()));
                if(i.resolveActivity(getPackageManager())!=null)
                    startActivity(i);
            }
        });


    }

    /**
     * check if internet connection is available
     */
    private boolean internetConnectionAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    @Override
    public android.content.Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i("LOADER","creating a new EarthquakeLoader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        Uri baseUri = Uri.parse(UGS_QUERY_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("format", "geojson");
        builder.appendQueryParameter("limit", "10");
        builder.appendQueryParameter("minmag", minMagnitude);
        builder.appendQueryParameter("orderby", orderBy);

        return  new EarthquakeLoader(this,builder.toString());
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Earthquake>> loader, List<Earthquake> data) {
        mAdapter.clear();
        ((TextView) findViewById(R.id.emptyTextView)).setText("No earthquake found :D");
        Log.i("LOADER","adding the earthquakes to the earthquake adapter");
        ((ProgressBar)findViewById(R.id.progressBar)).setVisibility(GONE);
       if(data!= null &&!data.isEmpty()){
           mAdapter.addAll(data);
       }

    }

    @Override
    public void onLoaderReset(android.content.Loader<List<Earthquake>> loader) {
        Log.i("LOADER","reset loader");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
