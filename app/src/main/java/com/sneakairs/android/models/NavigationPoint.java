package com.sneakairs.android.models;

import android.location.Location;

/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class NavigationPoint {
    private double latitude;
    private double longitude;
    private String latitudeString;
    private String longitudeString;
    private String maneuver;
    private Location location;

    public NavigationPoint() {
        this.location = new Location("navigation_point_location");
    }

    public NavigationPoint(double latitude, double longitude, String maneuver) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maneuver = maneuver;

        this.latitudeString = String.valueOf(latitude).substring(0, 6);
        this.longitudeString = String.valueOf(longitude).substring(0, 6);
        this.location = new Location("navigation_point_location");
        this.location.setLatitude(latitude); this.setLongitude(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        this.latitudeString = String.valueOf(latitude).substring(0, 6);
        this.location.setLatitude(latitude);

    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        this.longitudeString = String.valueOf(longitude).substring(0, 6);
        this.location.setLongitude(longitude);
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
