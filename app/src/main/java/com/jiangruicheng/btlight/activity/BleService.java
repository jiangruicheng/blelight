package com.jiangruicheng.btlight.activity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.adapter.BleAdapter;
import com.jiangruicheng.btlight.ble.ScanBle;
import com.jiangruicheng.btlight.eventtype.BluetoothSearch;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jiang_ruicheng on 16/12/15.
 */
public class BleService extends Service {
    Subscription ble_start;
    Subscription spp_start;
    BluetoothAdapter bluetoothAdapter;
    private BleAdapter bleAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleAdapter = new BleAdapter(BleService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ble_start = RxBus.getDefault().toObservable(BluetoothSearch.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
        spp_start = RxBus.getDefault().toObservable(BluetoothSearch.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                } else {

                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        bluetoothAdapter.startDiscovery();
                    } else {
                        ScanBle scanBle = new ScanBle(bleAdapter, new Handler());
                        scanBle.scantble();
                    }
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("TAG", "onDestroy: " + "bleservice");
        super.onDestroy();

        if (spp_start.isUnsubscribed()) {
            spp_start.unsubscribe();
        }
        if (ble_start.isUnsubscribed()) {
            ble_start.unsubscribe();
        }
    }
}
