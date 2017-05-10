package com.sneakairs.android.utils;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class LocationUtils {

    public static double calculateDistanceFromUser(LatLng userGeoPoint, double lat2, double lon2) {
        double lon1 = userGeoPoint.latitude; double lat1 = userGeoPoint.longitude;
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
