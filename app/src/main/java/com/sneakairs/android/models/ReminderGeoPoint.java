package com.sneakairs.android.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sumodkulkarni on 04/05/17.
 */

public class ReminderGeoPoint extends RealmObject {

    @PrimaryKey
    int id;

    Double latitude;
    Double longitude;
    int range;
    String message;
    boolean isDeleted = false;

    public ReminderGeoPoint() {
    }

    public ReminderGeoPoint(Double latitude, Double longitude, int range, String message) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.message = message;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
