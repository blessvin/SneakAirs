package com.sneakairs.android.activities;

import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.App;

import com.sneakairs.android.R;
import com.sneakairs.android.models.NavigationPoint;
import com.sneakairs.android.services.NavigationService;
import com.sneakairs.android.utils.CacheUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_directions)
public class DirectionsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private static final String TAG = "DirectionsActivity";

    @ViewById(R.id.map_relativeLayout) RelativeLayout mapRelativeLayout;
    @ViewById(R.id.map_container) FrameLayout mapFrameLayout;

    @ViewById(R.id.linearLayout_views) LinearLayout linearLayoutViews;
    @ViewById(R.id.text_view_origin) TextView originView;
    @ViewById(R.id.text_view_destination) TextView destinationView;
    @ViewById(R.id.directions_response) TextView directionsResponse;

    private SupportMapFragment supportMapFragment;
    private android.support.v4.app.FragmentManager supportFragmentManager;
    GoogleApiClient googleApiClient;

    LocationManager locationManager;
    Double latitude, longitude;
    LatLng origin, destination;

    List<NavigationPoint> navigationPointList = new ArrayList<>();

    JSONArray steps;
    Gson gson;

    protected enum TypeOfLocation{Origin, Destination}

    int typeOfLocation;

    @Click(R.id.button_origin)
    protected void buttonOriginClicked() {
        linearLayoutViews.setVisibility(View.GONE);
        mapRelativeLayout.setVisibility(View.VISIBLE);
        typeOfLocation = 0;
    }

    @Click(R.id.button_destination)
    protected void buttonDestinationClicked() {
        linearLayoutViews.setVisibility(View.GONE);
        mapRelativeLayout.setVisibility(View.VISIBLE);
        typeOfLocation = 1;
    }

    @Click(R.id.button_start_navigation)
    protected void buttonStartNavigation() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" +
                String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude) +

                "&destination=" +
                String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude) +

                "&key=AIzaSyDiIoahHwikVAVHaPsrH5U8PdsgJMKVL_Y";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        // directionsResponse.setText("Response is: \n"+ response.substring(0,500));
                        Log.d(TAG, response);

                        parseDirections(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                directionsResponse.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Click(R.id.submit_button)
    void submitButtonClick() {
        mapRelativeLayout.setVisibility(View.GONE);
        linearLayoutViews.setVisibility(View.VISIBLE);

        switch (typeOfLocation) {
            case 0:
                originView.setText(latitude.toString() + ", " + longitude.toString());
                origin = new LatLng(latitude, longitude);
                break;

            case 1:
                destinationView.setText(latitude.toString() + ", " + longitude.toString());
                destination = new LatLng(latitude, longitude);
                break;
        }
    }

    @Click(R.id.button_get_directions)
    protected void getDirectionsClicked() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" +
                String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude) +

                "&destination=" +
                String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude) +

                "&key=AIzaSyDiIoahHwikVAVHaPsrH5U8PdsgJMKVL_Y";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, response);
                        directionsResponse.setText("Response is: \n"+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                directionsResponse.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportMapFragment = SupportMapFragment.newInstance();
        supportFragmentManager = getSupportFragmentManager();
        supportMapFragment.getMapAsync(this);

        if (!supportMapFragment.isAdded()) {
            supportFragmentManager.beginTransaction().add(R.id.map_container, supportMapFragment).commit();
        } else {
            supportFragmentManager.beginTransaction().show(supportMapFragment).commit();
        }

        getLocation();
    }

    @AfterViews
    protected void afterViews() {

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        gson = new Gson();

    }

    private void getLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation(service);

        // TODO: Use the app's lat long..
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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

    @SuppressWarnings("MissingPermission")
    void grabLocation() {
        Criteria locationCritera = new Criteria();
        String providerName = locationManager.getBestProvider(locationCritera, true);

        if (providerName != null) {
            android.location.Location location = locationManager.getLastKnownLocation(providerName);

            if (location == null) return;
        }
    }

    @Override @SuppressWarnings("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(this);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = getLastKnownLocation(service);

        // TODO: Use the app's lat long..
        if (location != null) {
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        latitude = cameraPosition.target.latitude;
        longitude = cameraPosition.target.longitude;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void parseDirections(String response) {

        App.navigationStartPoint = new ParseGeoPoint(origin.latitude, origin.longitude);
        App.navigationEndPoint = new ParseGeoPoint(destination.latitude, destination.longitude);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("status").trim().equals("OK".trim())) {
                JSONArray routes = jsonResponse.getJSONArray("routes");
                JSONArray legs  = routes.getJSONObject(0).getJSONArray("legs");
                steps = legs.getJSONObject(0).getJSONArray("steps");

                App.navigationPointList.clear();
                for (int i = 0; i < steps.length(); i++) {

                    if (((JSONObject) steps.get(i)).has("maneuver")) {
                        NavigationPoint navigationPoint = new NavigationPoint();
                        Log.d(TAG, String.valueOf(i));
                        navigationPoint.setLatitude(((JSONObject) steps.get(i)).getJSONObject("start_location").getDouble("lat"));
                        navigationPoint.setLongitude(((JSONObject) steps.get(i)).getJSONObject("start_location").getDouble("lng"));
                        navigationPoint.setManeuver(((JSONObject) steps.get(i)).getString("maneuver"));
                        App.navigationPointList.add(navigationPoint);
                    }
                }
                CacheUtils.set(getApplicationContext(), "navigationPoints", gson.toJson(App.navigationPointList));

                Intent intent = new Intent(getApplicationContext(), NavigationService.class);
                intent.putExtra("navigationPointsList", gson.toJson(App.navigationPointList));
                startService(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

