package com.sneakairs.android.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.App;
import com.sneakairs.android.models.NavigationPoint;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class NavigationService extends Service {

    private static final String TAG = "NavigationService";

    LocationListener locationListener;
    LocationManager locationManager;

    ParseGeoPoint userGeoPoint;

    List<NavigationPoint> navigationPointList;
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
        timer.schedule(timerTask, 1000, 1000); //
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        App.isNavigationServiceRunning = true;

        // Reset distance covered
        App.distanceCovered = 0; App.checkPointsCovered = 0;
        gson = new Gson();
        Log.d(TAG, "started");

        navigationPointList = App.navigationPointList;

        startTimer();

        return START_NOT_STICKY;
    }

    protected void queryLocation() {
        Log.d(TAG, "queryLocation called");
        getLocation();

        if (userGeoPoint.distanceInKilometersTo(App.navigationEndPoint) < 0.01) {
            Intent intent = new Intent(Constants.NAVIGATION_UPDATE_INTENT_FILTER);
            intent.putExtra(Constants.bluetooth_send_message, Constants.MESSAGE_NAVIGATION_ENDED);
            sendBroadcast(intent);
            stopSelf();
        } else {
            int reachedCount = 0;
            int distanceCovered = 0;
            for (int i = 0; i < navigationPointList.size(); i++) {
                NavigationPoint navigationPoint = navigationPointList.get(i);

                ParseGeoPoint navigationGeopoint = new ParseGeoPoint(navigationPoint.getLatitude(), navigationPoint.getLongitude());
                double distance = userGeoPoint.distanceInKilometersTo(navigationGeopoint) * 1000;

                Log.d(TAG, "Distance = " + String.valueOf(distance));

                if (distance < Constants.DEFAULT_NAVIGATION_RANGE) {
                    navigationPoint.setReached(true);
                    String maneuver = navigationPoint.getManeuver();
                    if (maneuver.trim().equals("turn-left".trim())) {

                        Intent intent = new Intent(Constants.NAVIGATION_UPDATE_INTENT_FILTER);
                        intent.putExtra(Constants.bluetooth_send_message, Constants.MESSAGE_EVENT_TURN_LEFT);
                        sendBroadcast(intent);
                    }

                    if (maneuver.trim().equals("turn-right".trim())) {
                        Intent intent = new Intent(Constants.NAVIGATION_UPDATE_INTENT_FILTER);
                        intent.putExtra(Constants.bluetooth_send_message, Constants.MESSAGE_EVENT_TURN_RIGHT);
                        sendBroadcast(intent);
                    }
                }
                if (navigationPoint.isReached()){
                    reachedCount++;
                    distanceCovered += navigationPoint.getDistance();
                }
            }
            App.distanceCovered = distanceCovered;
            App.checkPointsCovered = reachedCount;
        }
    }

    private void getLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation(service);

        // TODO: Use the app's lat long..
        if (location != null) {
            userGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

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

        App.isNavigationServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
