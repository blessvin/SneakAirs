package com.sneakairs.android.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.App;
import com.sneakairs.android.activities.ConnectionActivity;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by sumodkulkarni on 10/05/17.
 */

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";

    private ConnectedThread connectedThread;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;

    String address = "";
    protected boolean isBtConnected = false;
    protected static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Handler bluetoothIn;
    final int handlerState = 0;

    BroadcastReceiver reminderBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        address = App.deviceMACAddress;

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;

                    // This is where you receive messages from Bluetooth Client

                    Log.d(TAG, "Received in Handler = " + readMessage);
                }
            }
        };

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

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

        if (reminderBroadcastReceiver == null) {
            reminderBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<ReminderGeoPoint> buzzReminders = new Gson().fromJson(intent.getStringExtra("buzzReminders"), ReminderGeoPointList.class);

                    if (buzzReminders.size() > 0) {
                        connectedThread.write(Constants.MESSAGE_EVENT_REMINGER);
                        Log.d(TAG, "Sent \'z\' to bluetooth client");
                    }
                }
            };
        }

        registerReceiver(reminderBroadcastReceiver, new IntentFilter(Constants.REMINDER_UPDATE_INTENT_FILTER));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reminderBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(bluetoothAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
            } else {

            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(myUUID);
        //creates secure outgoing connecetion with BT device using UUID
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
                    Log.d(TAG, "Received in inputStream = " + readMessage);
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
                stopSelf();
            }
        }
    }
}
