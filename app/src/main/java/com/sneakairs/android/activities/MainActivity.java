package com.sneakairs.android.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;


import com.sneakairs.android.App;
import com.sneakairs.android.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;


import io.palaima.smoothbluetooth.SmoothBluetooth;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

@EActivity(R.layout.activity_main)
@Fullscreen
public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final String TAG = "MainActivity.java";

    public static final String EXTRA_ADDRESS = "address";

    @ViewById(R.id.pulsator) PulsatorLayout pulsatorLayout;
    @ViewById(R.id.android_device) ImageView androidDevice;
    @ViewById(R.id.arduino_logo) ImageView arduinoLogo;
    @ViewById(R.id.andruido) ImageView andruidoLogo;

    Animation animFadeIn, animFadeOut, animZoomIn, animZoomOut;

    private Context context;

    BroadcastReceiver mReceiver;
    BluetoothAdapter bluetoothAdapter = null;

    @AfterViews
    protected void afterViews() {

        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);

        // set animation listeners
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);

        if (App.isBluetoothServiceRunning) {
            Intent intent = new Intent(this, StartActivity_.class);
            startActivity(intent);
            finish();
        }

        context = this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else if (bluetoothAdapter.isEnabled()){

        } else {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animFadeIn) {
            Toast.makeText(context, "Found one Arduino", Toast.LENGTH_LONG).show();
        }

        if (animation == animFadeOut) {
            androidDevice.setVisibility(View.GONE);
        }

        if (animation == animZoomOut) {
            arduinoLogo.setVisibility(View.GONE);
        }

        if (animation == animZoomIn) {
            Toast.makeText(context, "Connection Established!", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, StartActivity_.class);
                    intent.putExtra(EXTRA_ADDRESS, App.deviceMACAddress);
                    if (getIntent().hasExtra("navigationPointsList"))
                        intent.putExtra("navigationPointsList", getIntent().getStringExtra("navigationPointsList"));

                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Click(R.id.android_device)
    protected void androidDeviceClicked() {
        pulsatorLayout.start();
        Toast.makeText(context, "Searching devices", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                arduinoLogo.setVisibility(View.VISIBLE);
                arduinoLogo.startAnimation(animFadeIn);
                androidDevice.startAnimation(animFadeOut);
            }
        }, 2000);
    }

    @Click(R.id.arduino_logo)
    protected void arduinoClicked() {

        if (!pulsatorLayout.isStarted()) pulsatorLayout.start();

        Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                andruidoLogo.setVisibility(View.VISIBLE);
                andruidoLogo.startAnimation(animZoomIn);
                arduinoLogo.startAnimation(animZoomOut);
            }
        }, 2000);
    }
}
