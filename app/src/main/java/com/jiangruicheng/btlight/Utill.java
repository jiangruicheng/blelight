package com.jiangruicheng.btlight;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by jiang_ruicheng on 16/12/18.
 */
public class Utill {
    public static final String SPOTIFY_PACKAGE_NAME = "com.spotify.music";
    public static final String QQPLAY = "com.tencent.qqmusic";
    public static final String WANGYI = "com.netease.cloudmusic";
    public static final String KUWO = "cn.kuwo.player";
    public static final String KUGOU = "com.kugou.android";

    public static void launchApp(Context context, String packageName) {

        boolean installed = isAppInstalled(context, packageName);
        if (installed) {

            Intent LaunchIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            context.startActivity(LaunchIntent);
        } else {
            launchPlaystore(context, packageName);
        }
    }

    private static void launchPlaystore(Context context, String packageName) {

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + packageName)));
        }
    }

    private static boolean isAppInstalled(Context context, String uri) {

        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
