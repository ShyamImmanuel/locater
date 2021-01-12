package com.example.locater.Interface;

import com.example.locater.MyLatLng;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface IOLoadLocationListener {
    void onLoadLocationSucess(List<MyLatLng> latLngs);
    void onLoadLocationFailed(String message);
}
