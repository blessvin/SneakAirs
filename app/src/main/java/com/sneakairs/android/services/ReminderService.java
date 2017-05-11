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
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.App;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;
import com.sneakairs.android.utils.LocationUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sumodkulkarni on 09/05/17.
 */
@EService
public class ReminderService extends Service {

    private static final String TAG = "ReminderService";

    LocationListener locationListener;
    LocationManager locationManager;

    List<ReminderGeoPoint> remindersList;
    Gson gson;

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        timerTask = new TimerTask() {
            public void run() {
                queryLocation();
            }
        };

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10000); //
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        App.isReminderServiceRunning = true;

        gson = new Gson();
        Log.d(TAG, "started");
        // Create the Realm instance

        remindersList = gson.fromJson(CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS), ReminderGeoPointList.class);

        startTimer();

        return START_STICKY;
    }

    protected void queryLocation() {
        Log.d(TAG, "queryLocation called");
        getLocation();

        List<ReminderGeoPoint> buzzReminders = new ArrayList<>();
        for (ReminderGeoPoint reminderGeoPoint : remindersList) {

            ParseGeoPoint userGeoPoint = new ParseGeoPoint(App.userGeoPoint.latitude, App.userGeoPoint.longitude);
            ParseGeoPoint otherGeoPoint = new ParseGeoPoint(reminderGeoPoint.getLatitude(), reminderGeoPoint.getLongitude());

            double distance = userGeoPoint.distanceInKilometersTo(otherGeoPoint) * 1000;

            Log.d(TAG, reminderGeoPoint.getMessage() + " | "
                    + distance + " | "
                    + reminderGeoPoint.getRange() + " | "
                    + reminderGeoPoint.getLatitude() + "|" + reminderGeoPoint.getLongitude());

            if (distance <= reminderGeoPoint.getRange()) {
                buzzReminders.add(reminderGeoPoint);

            }

        }
        Intent intent = new Intent(Constants.REMINDER_UPDATE_INTENT_FILTER);
        intent.putExtra("buzzReminders", gson.toJson(buzzReminders));
        sendBroadcast(intent);
    }

    private void getLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation(service);

        // TODO: Use the app's lat long..
        if (location != null) {
            App.userGeoPoint = new LatLng(location.getLatitude(), location.getLongitude());

            Log.d(TAG, "Fetched Location = " + location.getLatitude() + " | " + location.getLongitude());
        }
    }
    @SuppressWarnings("MissingPermission")
    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);

        App.isReminderServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
