package com.jiangruicheng.btlight.data;


public class AlarmData {

    public byte power;
    public byte index;
    public byte hour;
    public byte minute;
    public byte repeat;
    public byte snooze;
    public byte mode;
    public byte volume;

    @Override
    public String toString() {
        String time = hour + ":" + minute;
        String mpower;
        if (power == 0)
            mpower = "OFF";
        else
            mpower = "ON";
        return time + "\n" + mpower;
    }

    public byte[] getBytes() {
        byte[] buffer = new byte[8];
        buffer[0] = index;
        buffer[1] = power;
        buffer[2] = hour;
        buffer[3] = minute;
        buffer[4] = repeat;
        buffer[5] = snooze;
        buffer[6] = volume;
        buffer[7] = mode;
        return buffer;
    }

}
