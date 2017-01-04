package com.jiangruicheng.btlight.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.adapter.BleAdapter;
import com.jiangruicheng.btlight.adapter.DeviceAdpater;
import com.jiangruicheng.btlight.ble.ConnBle;
import com.jiangruicheng.btlight.ble.HandlerCmd;
import com.jiangruicheng.btlight.ble.SendCmd;
import com.jiangruicheng.btlight.eventtype.BluetoothSearch;
import com.jiangruicheng.btlight.eventtype.ConnSucc;
import com.jiangruicheng.btlight.spp.Connect;
import com.jiangruicheng.btlight.spp.ConnectedThread;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends Activity {
    BluetoothAdapter bluetoothAdapter;
    List<BluetoothDevice> mArrayAdapter;
    DeviceAdpater deviceAdpater;
    ListView listview;
    SendCmd sendCmd;
    private ConnBle connBle;
    EditText editsendmesg;
    private boolean isOFF = true;
    ConnectedThread connectedThread;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (deviceAdpater == null) {
                    deviceAdpater = new DeviceAdpater();
                    listview.setAdapter(deviceAdpater);
                }
                if (mArrayAdapter == null) {
                    mArrayAdapter = new ArrayList<>();
                }
                if (!mArrayAdapter.contains(device)) {
                    mArrayAdapter.add(device);
                    deviceAdpater.setDevicelist(mArrayAdapter);
                    deviceAdpater.notifyDataSetChanged();
                }

            }
        }
    };
    // Register the BroadcastReceiver
    private Subscription getble;
    private Subscription connsucc;
    private BleAdapter bleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持BLE", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
        Intent service = new Intent(MainActivity.this, BleService.class);
        startService(service);
        //mArrayAdapter = new ArrayList<>();
        // deviceAdpater = new DeviceAdpater();
        listview = (ListView) findViewById(R.id.devicelist);
        //bleAdapter = new BleAdapter(MainActivity.this);
       /* bleAdapter.registerDataSetObserver(new BleAdapter.DataSetObserver() {
            @Override
            public void datachange(int statue) {

            }
        });*/

        //listview.setAdapter(deviceAdpater);
        editsendmesg = (EditText) findViewById(R.id.editsendmesg);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listview.getAdapter() instanceof DeviceAdpater) {
                    Connect connect = new Connect(deviceAdpater.devicelist.get(i));
                    connect.start();
                } else if (listview.getAdapter() instanceof BleAdapter) {
                    BluetoothDevice device = (BluetoothDevice) bleAdapter.getItem(i);
                    connBle = new ConnBle();
                    sendCmd = connBle;
                    connBle.connble(device, MainActivity.this);
                    connBle.registerhandler(new HandlerCmd() {
                        @Override
                        public void handler(byte[] data) {

                        }
                    });
                }
            }
        });
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            getble = RxBus.getDefault().toObservable(android.bluetooth.le.ScanResult.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<android.bluetooth.le.ScanResult>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onNext(android.bluetooth.le.ScanResult scanResult) {
                    if (bleAdapter == null) {
                        bleAdapter = new BleAdapter(MainActivity.this);
                        listview.setAdapter(bleAdapter);
                    }
                    bleAdapter.addDevice(scanResult.getDevice());
                    //listview.setAdapter(bleAdapter);
                    //bleAdapter.notifyDataSetChanged();
                }
            });
        }
        connsucc = RxBus.getDefault().toObservable(ConnSucc.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ConnSucc>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ConnSucc connSucc) {
                Intent intent = new Intent(MainActivity.this, ShowAcivity.class);
                startActivity(intent);
            }
        });
        /*Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // mArrayAdapter.add(device);
            }
        }*/
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        Log.i("TAG", "onDestroy: " + "activity");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (getble != null && getble.isUnsubscribed()) {
            getble.unsubscribe();
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                } else {
                    RxBus.getDefault().post(new BluetoothSearch());
                }

                break;
            case R.id.send:
                if (connectedThread != null) {
                    connectedThread.write(editsendmesg.getText().toString().getBytes());
                } else {
                    Toast.makeText(MainActivity.this, "未连接", Toast.LENGTH_SHORT).show();
                }
                /*if (isOFF) {
                    sendCmd.write(new byte[]{0x41, 0x54, 0x02, 0x17, 0x01, 0x51});
                    Log.i("TAG", "onClick: ON");
                    isOFF = false;
                } else {
                    isOFF = true;
                    sendCmd.write(new byte[]{0x41, 0x54, 0x02, 0x17, 0x00, 0x52});
                    Log.i("TAG", "onClick: OFF");
                }*/
                break;
        }
    }

   /* private class Connect extends Thread {
        private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

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
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
            Log.i("TAG", "run: nice");
            // Do work to manage the connection (in a separate thread)
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity

                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }*/

}
