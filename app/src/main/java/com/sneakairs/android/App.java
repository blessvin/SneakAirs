package com.sneakairs.android;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sneakairs.android.models.NavigationPoint;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class App extends Application {

    public static Context context;
    public static ArrayList<NavigationPoint> navigationPointList = new ArrayList<>();
    public static LatLng userGeoPoint;
    public static List<ReminderGeoPoint> remindersList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        if (CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS) != null)
            remindersList = new Gson().fromJson(CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS), ReminderGeoPointList.class);

    }
}
