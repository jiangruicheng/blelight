package com.jiangruicheng.btlight.spp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by jiang_ruicheng on 16/12/3.
 */

public class Connect extends Thread {
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothDevice mmDevice;
    private final BluetoothSocket mmSocket;
    private ConnectedThread connectedThread;

    public Connect(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection


        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
    }


    /**
     * Will cancel an in-progress connection, and close the socket
     */

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}

