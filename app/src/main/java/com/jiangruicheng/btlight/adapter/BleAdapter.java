package com.jiangruicheng.btlight.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiangruicheng.btlight.R;

import java.util.ArrayList;
import java.util.List;


public class BleAdapter extends BaseAdapter {
    List<BluetoothDevice> devices = new ArrayList<>();
    LayoutInflater layoutInflater;
    Context context;
    DataSetObserver observer;

    /*public interface DataSetObserver {
        public static final int ADD = 0;
        public static final int SUB = 1;
        public static final int ZORE = 2;
        public static final int NOT_ZORE = 0;
        public static final boolean is_status = true;

        public void datachange(int statue);
    }*/

/*
    public void registerDataSetObserver(DataSetObserver observer) {
        this.observer = observer;
    }
*/

/*    public void notifyDataSet(int statue) {
        if (observer != null) {
            observer.datachange(statue);
        }
    }*/

    public BleAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void addDevice(BluetoothDevice device) {
        if (!devices.contains(device)) {
            devices.add(device);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView = LayoutInflater.from(context).inflate(R.layout.devicelist_item, null);
        if (devices.size() == 0) {
            /*observer.datachange(DataSetObserver.ZORE);*/
        }
        if (devices.size() != 0) {
            /*observer.datachange(DataSetObserver.NOT_ZORE);*/
            TextView textView = (TextView) convertView.findViewById(R.id.mesg);
            textView.setText(devices.get(position).getAddress());
            BluetoothDevice bluetoothDevice = devices.get(position);
            textView.setText(bluetoothDevice.getName() + "" + bluetoothDevice.getAddress());
        }
        return convertView;
    }
}
