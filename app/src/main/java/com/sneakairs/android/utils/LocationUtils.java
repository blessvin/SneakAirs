package com.sneakairs.android.utils;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class LocationUtils {

    private static double EARTH_MEAN_RADIUS_KM = 6371.0;

    public static double calculateDistanceinRadians(LatLng latLng1, LatLng latLng2) {
        double d2r = Math.PI / 180.0; // radian conversion factor
        double lat1rad = latLng1.latitude * d2r;
        double long1rad = latLng1.longitude * d2r;
        double lat2rad = latLng2.latitude * d2r;
        double long2rad = latLng2.longitude * d2r;
        double deltaLat = lat1rad - lat2rad;
        double deltaLong = long1rad - long2rad;
        double sinDeltaLatDiv2 = Math.sin(deltaLat / 2.);
        double sinDeltaLongDiv2 = Math.sin(deltaLong / 2.);
        // Square of half the straight line chord distance between both points.
        // [0.0, 1.0]
        double a =
                sinDeltaLatDiv2 * sinDeltaLatDiv2 + Math.cos(lat1rad) * Math.cos(lat2rad)
                        * sinDeltaLongDiv2 * sinDeltaLongDiv2;
        a = Math.min(1.0, a);
        return 2. * Math.asin(Math.sqrt(a));
    }

    public static double calculateDistanceInKms(LatLng latLng1, LatLng latLng2) {
        return calculateDistanceinRadians(latLng1, latLng2) * EARTH_MEAN_RADIUS_KM;
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
