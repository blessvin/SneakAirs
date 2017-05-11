package com.sneakairs.android.activities.reminders;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.services.ReminderService;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.security.Permission;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_reminder)
public class ReminderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private static final String TAG = "ReminderActivity";

    private SupportMapFragment supportMapFragment;
    private android.support.v4.app.FragmentManager supportFragmentManager;
    GoogleApiClient googleApiClient;

    Double latitude, longitude;
    Gson gson;

    private Realm realm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();

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

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        latitude = cameraPosition.target.latitude;
        longitude = cameraPosition.target.longitude;
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

    @Click(R.id.submit_button)
    protected void submitButtonClicked() {
        showRangeSelectionDialog();
    }

    private void showRangeSelectionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.range_selection_dialog);

        final EditText range = (EditText) dialog.findViewById(R.id.range_edit_text);
        final EditText message = (EditText) dialog.findViewById(R.id.reminder_message);
        Button submitButton = (Button) dialog.findViewById(R.id.submit_reminder);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.getText().toString().equals("".trim())) {
                    Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        ReminderGeoPoint test = realm.where(ReminderGeoPoint.class).equalTo("id", 0).findFirst();

                        if (test != null) {
                            int nextID = realm.where(ReminderGeoPoint.class).max("id").intValue() + 1;
                            ReminderGeoPoint reminderGeoPoint = realm.createObject(ReminderGeoPoint.class, nextID);
                            reminderGeoPoint.setLatitude(latitude);
                            reminderGeoPoint.setLongitude(longitude);
                            reminderGeoPoint.setRange(Integer.valueOf(range.getText().toString()));
                            reminderGeoPoint.setMessage(message.getText().toString());
                        } else {
                            ReminderGeoPoint reminderGeoPoint = realm.createObject(ReminderGeoPoint.class, 0);
                            reminderGeoPoint.setLatitude(latitude);
                            reminderGeoPoint.setLongitude(longitude);
                            reminderGeoPoint.setRange(Integer.valueOf(range.getText().toString()));
                            reminderGeoPoint.setMessage(message.getText().toString());

                            startService(new Intent(getApplicationContext(), ReminderService.class));
                        }

                        App.updateRemindersList();
                        sendBroadcast(new Intent());
                    }
                });
                Toast.makeText(getApplicationContext(), "Reminder saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}
