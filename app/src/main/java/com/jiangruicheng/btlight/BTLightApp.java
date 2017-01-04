package com.jiangruicheng.btlight;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by jiang_ruicheng on 16/12/28.
 */
public class BTLightApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "f22f6c497c", true);
    }
}
