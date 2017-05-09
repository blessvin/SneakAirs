package com.sneakairs.android;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.sneakairs.android.models.NavigationPoint;

import java.util.ArrayList;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class App extends Application {

    public static Context context;
    public static ArrayList<NavigationPoint> navigationPointList = new ArrayList<>();
    public static LatLng userGeoPoint;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
