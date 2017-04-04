package com.sneakairs.android;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sneakairs.android.models.NavigationPoint;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


@EActivity(R.layout.activity_connection)
public class ConnectionActivity extends AppCompatActivity {

    Context context;
    private static final String TAG = "ConnectionActivity";

    private ConnectedThread connectedThread;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    @ViewById(R.id.input_edit_text) EditText inputEditText;
    @ViewById(R.id.send_btn) ImageView sendButton;
    @ViewById(R.id.text_view_display) TextView displayTextView;
    @ViewById(R.id.btn_refresh) ImageView refreshButton;
    private ProgressDialog progressDialog;

    private final int LOCATION_QUERY_INTERVAL = 1000;

    private double latitude, longitude;
    private String latitudeString, longitudeString;
    Gson gson;

    String address = "";
    protected boolean isBtConnected = false;
    protected static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Handler bluetoothIn;
    final int handlerState = 0;

    ArrayList<NavigationPoint> navigationPointList;

    public class NavigationPointList extends ArrayList<NavigationPoint> {}

    @AfterViews
    protected void afterViews() {
        context = this;
        gson = new Gson();
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity_.EXTRA_ADDRESS);

//        new ConnectBT().execute();
        if (getIntent().hasExtra("navigationPointsList")) {

//            navigationPointList = gson.fromJson(getIntent().getStringExtra("navigationPointsList"), NavigationPointList.class);

            navigationPointList = App.navigationPointList;
            for (NavigationPoint navigationPoint : navigationPointList)
                Log.d(TAG, navigationPoint.getLatitudeString() + " | " + navigationPoint.getLongitudeString());

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startNavigation();
                    handler.postDelayed(this, LOCATION_QUERY_INTERVAL);
                }
            }, LOCATION_QUERY_INTERVAL);
        }

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        displayTextView.setText(Calendar.getInstance().getTime().toString() + "\n" + "Data Received = " + dataInPrint);
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }



    @Click(R.id.send_btn)
    public void sendButtonClicked() {

//        if (!inputEditText.getText().toString().trim().equals("".trim())) {
//            if (bluetoothSocket!=null) {
//                try {
//                    bluetoothSocket.getOutputStream().write(inputEditText.getText().toString().getBytes());
//                }
//                catch (IOException e) {
//                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
//                }
//            }
//        } else {
//            Toast.makeText(context, "Enter some text to Send", Toast.LENGTH_LONG).show();
//        }

        if (!inputEditText.getText().toString().trim().equals("".trim())) {
            if (bluetoothSocket!=null) {
                connectedThread.write(inputEditText.getText().toString());
            }
        } else {
            Toast.makeText(context, "Enter some text to Send", Toast.LENGTH_LONG).show();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(myUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra("address");

        //create device and set the MAC address
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try {
            bluetoothSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            bluetoothSocket.connect();
        } catch (IOException e) {
            try
            {
                bluetoothSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        connectedThread.write("z");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            bluetoothSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(bluetoothAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {  // UI thread {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {
        //while the progress dialog is shown, the connection is done in background
            try {
                if (bluetoothSocket == null || !isBtConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    bluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                Toast.makeText(context, "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                Toast.makeText(context, "Connected.", Toast.LENGTH_LONG).show();
                isBtConnected = true;
            }
            progressDialog.dismiss();
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Log.e(TAG, e.getMessage());
                finish();

            }
        }
    }

    private void getLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation(service);

        // TODO: Use the app's lat long..
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            latitudeString = String.valueOf(latitude);
            longitudeString = String.valueOf(longitude);

            latitudeString = latitudeString.substring(0, 6);
            longitudeString = longitudeString.substring(0, 6);
            Log.d(TAG, "Fetched Location = " + latitudeString + " | " + longitudeString);
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

    @Background
    protected void startNavigation() {

        getLocation();

        for (int i = 0; i < navigationPointList.size(); i++) {
            NavigationPoint navigationPoint = navigationPointList.get(i);
            if (navigationPoint.getLatitudeString().trim().equals(latitudeString.trim())
                    && navigationPoint.getLatitudeString().trim().equals(latitudeString.trim())) {
                String maneuver = navigationPoint.getManeuver();

                if (maneuver.trim().equals("turn-left".trim())) {
                    connectedThread.write("L");
                    Log.d(TAG, "Sent L");
                }

                if (maneuver.trim().equals("turn-right".trim())) {
                    connectedThread.write("R");
                    Log.d(TAG, "Sent R");
                }
            }
        }


    }
}

