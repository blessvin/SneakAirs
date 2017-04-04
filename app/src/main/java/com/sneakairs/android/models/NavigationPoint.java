package com.sneakairs.android.models;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class NavigationPoint {
    private double latitude;
    private double longitude;
    private String latitudeString;
    private String longitudeString;
    private String maneuver;

    public NavigationPoint() {
    }

    public NavigationPoint(double latitude, double longitude, String maneuver) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maneuver = maneuver;

        this.latitudeString = String.valueOf(latitude).substring(0, 6);
        this.longitudeString = String.valueOf(longitude).substring(0, 6);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.latitudeString = String.valueOf(latitude).substring(0, 6);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.longitudeString = String.valueOf(longitude).substring(0, 6);
    }

    public String getLatitudeString() {
        return latitudeString;
    }

    public void setLatitudeString(String latitudeString) {
        this.latitudeString = latitudeString;
    }

    public String getLongitudeString() {
        return longitudeString;
    }

    public void setLongitudeString(String longitudeString) {
        this.longitudeString = longitudeString;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
