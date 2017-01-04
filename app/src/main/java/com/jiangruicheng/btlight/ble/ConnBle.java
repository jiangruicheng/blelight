package com.jiangruicheng.btlight.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.eventtype.ConnSucc;
import com.jiangruicheng.btlight.eventtype.ReciveCmd;

import java.util.List;
import java.util.UUID;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * XIU GAI WAN CHENG
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ConnBle implements SendCmd {
    private UUID uuid_service = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB");
    private UUID uuid_characteristic = UUID.fromString("0000FFF6-0000-1000-8000-00805F9B34FB");
    private UUID uuid_getdata = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");
    //private BluetoothDevice device;
    // private Context context;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattCharacteristic getdata;
    private HandlerCmd handlerCmd;
    private BluetoothGattService gattService;
    BluetoothGatt gatt;
    private ReciveCmd reciveCmd = new ReciveCmd();
    private Subscription SendCmd;

    public ConnBle() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothGatt connble(BluetoothDevice device, Context context) {
        gatt = device.connectGatt(context, false, gattCallback);
        //getserver(gatt);
        SendCmd = RxBus.getDefault().toObservable(com.jiangruicheng.btlight.eventtype.SendCmd.class).observeOn(Schedulers.io()).subscribe(new Observer<com.jiangruicheng.btlight.eventtype.SendCmd>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(com.jiangruicheng.btlight.eventtype.SendCmd sendCmd) {
                write(sendCmd.getCmd());
                Log.i("blesend", "onNext: " + sendCmd.getCmd().toString());
            }
        });
        return gatt;

    }

    public void registerhandler(HandlerCmd handlerCmd) {
        this.handlerCmd = handlerCmd;
    }

    public void unregisterhandler(HandlerCmd handlerCmd) {
        this.handlerCmd = null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getserver(BluetoothGatt gatt) {
        /*BluetoothGattService service = gatt.getService(uuid_service);
        characteristic = service.getCharacteristic(uuid_characteristic);
        getdata = service.getCharacteristic(uuid_getdata);
        gatt.setCharacteristicNotification(getdata, true);*/
        List<BluetoothGattService> list;
        list = gatt.getServices();
        for (BluetoothGattService gattService : list) {
            if (gattService.getUuid().equals(uuid_service)) {
                this.gattService = gattService;
                characteristic = gattService.getCharacteristic(uuid_characteristic);
                gatt.setCharacteristicNotification(characteristic, true);
                RxBus.getDefault().post(new ConnSucc());
                // characteristic.setValue(new byte[]{0x42, 0x54, 0x02, 0x17, 0x01, 0x51});//41 54 02 17 00 52
                // gatt.writeCharacteristic(characteristic);
            }
            /*Log.d("TAG", "getserver: " + Integer.toHexString(Integer.valueOf(gattService.getUuid().toString())));*/
        }

    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            handlerCmd.handler(characteristic.getValue());
            reciveCmd.setCmd(characteristic.getValue());
            RxBus.getDefault().post(reciveCmd);
            Log.i("blerecive", "onCharacteristicChanged: " + reciveCmd.getCmd().toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            getserver(gatt);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "CONNECT");
                gatt.discoverServices();
                // RxBus.getDefault().post(new ConnSucc());
            } else if (status == BluetoothGatt.STATE_DISCONNECTED) {
                if (SendCmd.isUnsubscribed()) {
                    SendCmd.unsubscribe();
                }
            }
        }
    };

    @Override
    public void write(byte[] cmd) {
        characteristic.setValue(cmd);//41 54 02 17 00 52
        gatt.writeCharacteristic(characteristic);
    }
}
