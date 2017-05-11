package com.sneakairs.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.models.NavigationPoint;
import com.sneakairs.android.models.NavigationPointLIst;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;
import com.sylversky.fontreplacer.FontReplacer;
import com.sylversky.fontreplacer.Replacer;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class App extends Application {

    private static final String TAG = "App.java";

    public static Context context;
    public static ArrayList<NavigationPoint> navigationPointList = new ArrayList<>();
    public static LatLng userGeoPoint;
    public static List<ReminderGeoPoint> remindersList = new ArrayList<>();
    public static List<ReminderGeoPoint> buzzRemindersList = new ArrayList<>();
    public static List<NavigationPoint> navigationPoints = new ArrayList<>();
    public static String deviceMACAddress = "98:D3:31:20:05:CC";
    public static boolean shouldPlayMusic = false;
//    public static boolean overRideMusicPlayback = false;

    public static boolean isBluetoothServiceRunning = false;
    public static boolean isMusicServiceRunning = false;
    public static boolean isReminderServiceRunning = false;
    public static boolean isNavigationServiceRunning = false;

    public static ParseGeoPoint navigationStartPoint;
    public static ParseGeoPoint navigationEndPoint;
    public static int distanceCovered = 0;
    public static int checkPointsCovered = 0;

    public static boolean isMusicPlaying = false;
    public static boolean wirelessControl = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App Started");
        context = getApplicationContext();

        Replacer replacer = FontReplacer.Build(getApplicationContext());
        replacer.setDefaultFont("century_gothic.ttf");
        replacer.setBoldFont("century_gothic_bold.ttf");
        replacer.applyFont();

        Realm.init(this);

        if (CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS) != null)
            remindersList = new Gson().fromJson(CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS), ReminderGeoPointList.class);

        if (CacheUtils.get(Constants.KEY_NAVIGATION_GEO_POINTS) != null)
            navigationPoints = new Gson().fromJson(CacheUtils.get(Constants.KEY_NAVIGATION_GEO_POINTS), NavigationPointLIst.class);

        updateRemindersList();
        Log.d(TAG, "remindersList.size() = " + remindersList.size());
    }

    public static void updateRemindersList() {

        Realm realm = Realm.getDefaultInstance();

        remindersList.clear();
        RealmResults<ReminderGeoPoint> allReminderGeoPoints = realm.where(ReminderGeoPoint.class).equalTo("isDeleted", false).findAll();
        for (ReminderGeoPoint reminderGeoPoint : allReminderGeoPoints) {
            if (!reminderGeoPoint.isDeleted()) {

                ReminderGeoPoint newReminderGeoPoint = new ReminderGeoPoint(reminderGeoPoint.getLatitude(), reminderGeoPoint.getLongitude(),
                        reminderGeoPoint.getRange(), reminderGeoPoint.getMessage());

                newReminderGeoPoint.setDeleted(reminderGeoPoint.isDeleted());

                newReminderGeoPoint.setId(reminderGeoPoint.getId());
                remindersList.add(newReminderGeoPoint);
            }
        }
        Log.d(TAG, "remindersList.size() = " + remindersList.size());
    }
}
