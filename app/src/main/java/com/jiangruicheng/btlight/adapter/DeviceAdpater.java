package com.jiangruicheng.btlight.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiangruicheng.btlight.R;

import java.util.List;

/**
 * Created by jiang_ruicheng on 16/12/3.
 */
public class DeviceAdpater extends BaseAdapter {
    public List<BluetoothDevice> getDevicelist() {
        return devicelist;
    }

    public void setDevicelist(List<BluetoothDevice> devicelist) {
        this.devicelist = devicelist;
    }

    public List<BluetoothDevice> devicelist;

    @Override
    public int getCount() {
        if (devicelist != null)
            return devicelist.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (devicelist != null)
            return devicelist.get(i);
        return null;


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.devicelist_item, null);
        TextView textView = (TextView) view.findViewById(R.id.mesg);
        textView.setText(devicelist.get(i).getName() + "\n" + devicelist.get(i).getAddress());
        return view;
    }
}
