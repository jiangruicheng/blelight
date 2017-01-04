package com.jiangruicheng.btlight.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;

import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.adapter.BleAdapter;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScanBle {

    private Handler handler;
    private BleAdapter baseAdapter;
    private int ScanTIME = 20000;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner leScanner = adapter.getBluetoothLeScanner();

    public ScanBle(BleAdapter adapter, Handler handler) {
        baseAdapter = adapter;
        this.handler = handler;

    }

    public void scantble() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopscantble();
            }
        }, ScanTIME);
        leScanner.startScan(scanCallback);
    }

    public void stopscantble() {
        leScanner.stopScan(scanCallback);
/*
        baseAdapter.notifyDataSet(BleAdapter.DataSetObserver.NOT_ZORE);
*/
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
           // baseAdapter.addDevice(result.getDevice());
            RxBus.getDefault().post(result);
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    baseAdapter.addDevice(result.getDevice());
                    baseAdapter.notifyDataSetChanged();
                    Log.d("ble", result.getDevice().getAddress());
                }
            });*/
        }
    };
}
