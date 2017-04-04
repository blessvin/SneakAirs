package com.sneakairs.android;

import android.app.Application;
import android.content.Context;

import com.sneakairs.android.models.NavigationPoint;

import java.util.ArrayList;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class App extends Application {

    private static Context context;
    public static ArrayList<NavigationPoint> navigationPointList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
