package com.example.android.quakereport;
import android.graphics.drawable.GradientDrawable;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.format;
import static android.R.attr.layout_margin;

/**
 * Created by arata on 11/10/2016.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    /**
     * helper functions to format Date and time
     */
    private String formatDate(Date date){
        return (new SimpleDateFormat("MMM DD, yyyy")).format(date);
    }
    private String formatTime(Date date){
        return (new SimpleDateFormat("h:mm a")).format(date);
    }
    /**
     * helper function to extract distance specifications and city from location string of an earthquake
     */
    private String getDistance(String location){
        if(location.contains("of")){
            return location.substring(0,location.indexOf('f'));
        }
        else{
            return "Near the";
        }
    }
    private String getCity(String location){
        if(location.contains("of")){
            return location.substring(location.indexOf('f'), location.length()-1);
        }
        else{
            return location;
        }
    }

    /**
     * helper function to round magnitud to 1 decimal place
     */
    private String formatMagnitud(double mag){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(mag);
    }

    /**
     * helper function to get the proper color for a given magnitud
     */
    private int getMagnitudColor(double mag){
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(mag);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
    // constructor
    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes){
        super(context, 0, earthquakes);
    }
    private static class ViewHolder{
        TextView magnitud;
        TextView distance;
        TextView city;
        TextView date;
        TextView time;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list_item = convertView;
        ViewHolder holder;
        if( list_item == null){
            holder = new ViewHolder();
            list_item = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder.magnitud = (TextView) list_item.findViewById(R.id.magnitude);
            holder.city = (TextView) list_item.findViewById(R.id.city);
            holder.date     = (TextView) list_item.findViewById(R.id.date);
            holder.time     = (TextView) list_item.findViewById(R.id.time);
            holder.distance     = (TextView) list_item.findViewById(R.id.distance);
            list_item.setTag(holder);
        }
        else{
            holder  = (ViewHolder) list_item.getTag();
        }
        //get current earthquake
        Earthquake curretnEarthquake = getItem(position);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitud.getBackground();
        //setting the proper color for the current magnitud
        magnitudeCircle.setColor(getMagnitudColor(curretnEarthquake.getMagnitud()));


        holder.magnitud.setText(formatMagnitud(curretnEarthquake.getMagnitud()));
        holder.distance.setText(getDistance(curretnEarthquake.getLocation()));
        holder.city.setText(getCity(curretnEarthquake.getLocation()));
        Date date = new Date(curretnEarthquake.getTimeInMili());
        holder.date.setText(formatDate(date));
        holder.time.setText(formatTime(date));
        return list_item;
    }
}
