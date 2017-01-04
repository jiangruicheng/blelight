package com.jiangruicheng.btlight.adapter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jiang_ruicheng on 15/11/25.
 */
public class PreData {
    private Context context;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    public String ALARM = "ALARM";
    public String BackLightPower = "BACKLIGHTPOWER";
    public String Bright = "BRIGHT";
    public String Mode = "Mode";
    public String SleepTime = "SleepTime";
    public String McuVersion = "Mcuversion";
    public String BluethoothVersion = "BluethoothVersion";
    public String PowerStatu = "PowerStatu";
    public String RateStatu = "RateStatu";


    public PreData(Context context) {
        this.context = context;
        data = context.getSharedPreferences("data", Context.MODE_APPEND);
        editor = data.edit();
    }

    public SharedPreferences.Editor setData() {
        return editor;
    }

    public SharedPreferences getData() {
        return data;
    }

}
