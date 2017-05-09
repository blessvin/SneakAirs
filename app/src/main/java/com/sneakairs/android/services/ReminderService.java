package com.sneakairs.android.services;

import android.Manifest;
import android.app.RemoteInput;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sneakairs.android.App;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;
import com.sneakairs.android.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class ReminderService extends Service {

    LocationListener locationListener;
    LocationManager locationManager;

    List<ReminderGeoPoint> remindersList = new ArrayList<>();
    Gson gson;

    @Override
    public void onCreate() {
        gson = new Gson();
        remindersList = gson.fromJson(CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS), ReminderGeoPointList.class);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                List<ReminderGeoPoint> buzzReminders = new ArrayList<>();
                for (ReminderGeoPoint reminderGeoPoint : remindersList) {

                    if (LocationUtils.calculateDistanceFromUser(new LatLng(location.getLatitude(), location.getLongitude())
                            , reminderGeoPoint.getLatitude(), reminderGeoPoint.getLatitude()) <= reminderGeoPoint.getRange()) {
                        buzzReminders.add(reminderGeoPoint);
                    }

                }
                Intent intent = new Intent(Constants.REMINDER_UPDATE_INTENT_FILTER);
                intent.putExtra("buzzReminders", gson.toJson(buzzReminders));
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
