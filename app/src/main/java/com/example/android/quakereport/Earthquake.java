package com.example.android.quakereport;

import java.io.Serializable;

/**
 * Created by arata on 11/10/2016.
 */

public class Earthquake {
    private double mMagnitud;
    private String mLocation;
    private Long mTimeInMili;
    private String mUrl;

    /**
     *
     * @param magnitud
     * @param location
     * @param date
     */
    public Earthquake(double magnitud, String location, Long date, String url){
        mMagnitud = magnitud;
        mLocation = new String(location);
        mTimeInMili     = date;
        mUrl      = new String(url);
    }

    public double getMagnitud(){
        return mMagnitud;
    }

    public String getLocation(){
        return   mLocation;
    }

    public Long getTimeInMili(){
        return mTimeInMili;
    }
    public String getUrl(){
        return mUrl;
    }
}
