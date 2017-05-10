package com.sneakairs.android.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.services.BluetoothService;
import com.sneakairs.android.services.ReminderService;
import com.sneakairs.android.services.ReminderService_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.palaima.smoothbluetooth.SmoothBluetooth;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

@EActivity(R.layout.activity_main)
@Fullscreen
public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final String TAG = "MainActivity.java";

    public static final String EXTRA_ADDRESS = "address";

//    @ViewById(R.id.button_search) Button buttonSearch;
//    @ViewById(R.id.lisView) ListView listView;
    @ViewById(R.id.pulsator) PulsatorLayout pulsatorLayout;
    @ViewById(R.id.android_device) ImageView androidDevice;
    @ViewById(R.id.arduino_logo) ImageView arduinoLogo;
    @ViewById(R.id.andruido) ImageView andruidoLogo;

    Animation animFadeIn, animFadeOut, animZoomIn, animZoomOut;

    private Context context;

    private SmoothBluetooth mSmoothBluetooth;
    ArrayAdapter<String> listAdapter;

    BroadcastReceiver mReceiver;
    BluetoothAdapter bluetoothAdapter = null;
    Set pairedDevices;


//    @Click(R.id.button_search)
    protected void searchForDevices() {
//        pairedDevicesList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        //discovery starts, we can show progress dialog or perform other tasks

                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        //discovery finishes, dismis progress dialog

                    } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        //bluetooth device found

                        BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Toast.makeText(context, "Found device " + device.getName(), Toast.LENGTH_LONG).show();
                    }
                }
            };

            registerReceiver(mReceiver, new IntentFilter());
        }
    }

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
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else if (bluetoothAdapter.isEnabled()){

        } else {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

//        pulsatorLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (pulsatorLayout.isStarted()) {
//                    Intent intent = new Intent(context, StartActivity_.class);
//                    intent.putExtra(EXTRA_ADDRESS, App.deviceMACAddress);
//                    if (getIntent().hasExtra("navigationPointsList"))
//                        intent.putExtra("navigationPointsList", getIntent().getStringExtra("navigationPointsList"));
//
//                    startActivity(intent);
//                    finish();
//                } else {
//                    pulsatorLayout.start();
//                    Toast.makeText(context, "Searching devices", Toast.LENGTH_SHORT).show();
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            arduinoLogo.setVisibility(View.VISIBLE);
//                            arduinoLogo.startAnimation(animFadeIn);
//                            androidDevice.startAnimation(animFadeOut);
//                        }
//                    }, 3000);
//
//                }
//            }
//        });
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
            }, 3000);
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
        }, 3000);
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
        }, 3000);
    }


//    private void pairedDevicesList()
//    {
//        pairedDevices = bluetoothAdapter.getBondedDevices();
//        ArrayList list = new ArrayList();
//
//        if (pairedDevices.size()>0) {
//            List<BluetoothDevice> devices = new ArrayList<>();
//            devices.addAll(pairedDevices);
//
//            for (BluetoothDevice device : devices) {
//                list.add(device.getName() + "\n" + device.getAddress());
//            }
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
//        }
//
//        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String info = ((TextView) view).getText().toString();
//                String address = info.substring(info.length() - 17);
//                Intent intent = new Intent(context, StartActivity_.class);
//
//                App.deviceMACAddress = address;
//                Log.d(TAG, "BT Address = " + address);
//
//                intent.putExtra(EXTRA_ADDRESS, address);
//                if (getIntent().hasExtra("navigationPointsList"))
//                    intent.putExtra("navigationPointsList", getIntent().getStringExtra("navigationPointsList"));
//
//
//                startActivity(intent);
//                finish();
//            }
//        }); //Method called when the device from the list is clicked
//
//    }
}
