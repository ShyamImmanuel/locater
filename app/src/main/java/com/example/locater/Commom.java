package com.example.locater;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.ActionBarContextView;

import java.text.DateFormat;
import java.util.Date;

public class Commom {
    private static final String KEY_REQUESTING_LOCATION_UPDATES ="LocationUpdateEnable" ;

    public  static String getLocationText(Location mLocation){
        return mLocation==null?"Unknown Location":new StringBuilder()
                .append((mLocation.getLatitude()))
                .append("/")
                .append(mLocation.getLongitude())
                .toString();

    }
    public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService){

        return  String.format("Location Updated:  @1$",DateFormat.getDateInstance().format(new Date()));



    }

    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.
                getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES,value)
                .apply();


    }

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES,false);
    }
}
