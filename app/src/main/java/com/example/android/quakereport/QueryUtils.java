package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;

import static android.content.ContentValues.TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private final static String TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * creates a url object from a string url
     * @param stringUrl
     * @return
     */
    private static URL createUrl(String stringUrl){
        try {
            URL url = new URL(stringUrl);
            return url;
        } catch (MalformedURLException e) {
                Log.e("URL", "problems creating the url object");
            return null;
        }
    }

    /**
     * parse the input stream to a String
     * @param inputStream
     * @return
     */
    private static String readFromStream(InputStream inputStream){
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader  bufferReader = new BufferedReader(reader);
            try {
                String line = bufferReader.readLine();
                while(line != null){
                    output.append(line);
                    line = bufferReader.readLine();
                }
            } catch (IOException e)  {
                Log.e(TAG,"error trying to parse inputstream to String");
            }
        }
        return output.toString();
    }
    /**
     * extracts response of the server and returns it as a string
     * @param url
     * @return
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String JSONresponse= "";
        if(url == null){
            return JSONresponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //getting the output
            if(httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                JSONresponse = readFromStream(inputStream);
            }
            else{
                Log.d(TAG, "Error response code : " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error when trying to make the connection");
        }
        finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONresponse;
    }

    /**
     * converts a JSON strings to a array of earthquakes if possible
     * @param JSONstring
     * @return
     */
    private static ArrayList<Earthquake> extractEarthquakesFromJSON(String JSONstring){
        if(JSONstring.isEmpty()){
            return null;
        }
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject output = new JSONObject(JSONstring);
            JSONArray JSONearthquakes = output.getJSONArray("features");
            JSONObject current;
            for(int i =0; i < JSONearthquakes.length(); i ++){
                current = JSONearthquakes.getJSONObject(i).getJSONObject("properties");
                earthquakes.add(new Earthquake(current.getDouble("mag"),current.getString("place"),current.getLong("time"),current.getString("url")));
            }
        } catch (JSONException e) {
            Log.e(TAG,"problem parsing jason string to earthquakes");
        }
        return earthquakes;
    }
    /**
     * creates array with earthquakes information from the UGS server given a url
     * @param stringUrl url from which we will receive the data of earthquakes
     * @return
     */
    public static ArrayList<Earthquake> fetchEarthQuakes(String stringUrl){
        // create url object
        URL url = createUrl(stringUrl);


        // try to get the JSON response from server
        String JSONstring = "";
        try {
            JSONstring = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // extract earthquakes from JSON response and return
        return extractEarthquakesFromJSON(JSONstring);
    }
}