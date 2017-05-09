package com.sneakairs.android.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class LocationUtils {

    public static double calculateDistanceFromUser(LatLng userGeoPoint, double lat2, double lon2) {
        double lon1 = userGeoPoint.latitude; double lat1 = userGeoPoint.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
