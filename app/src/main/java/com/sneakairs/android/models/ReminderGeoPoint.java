package com.sneakairs.android.models;

/**
 * Created by sumodkulkarni on 04/05/17.
 */

public class ReminderGeoPoint {
    Double latitude;
    Double longitude;
    int range;

    public ReminderGeoPoint() {
    }

    public ReminderGeoPoint(Double latitude, Double longitude, int range) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return range;
    }

    public void setRadius(int range) {
        this.range = range;
    }
}
