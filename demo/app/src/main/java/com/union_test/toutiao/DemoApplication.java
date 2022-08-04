package com.union_test.toutiao;

import android.annotation.SuppressLint;
import android.content.Context;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;







import com.union_test.toutiao.config.TTAdManagerHolder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("unused")
public class DemoApplication extends MultiDexApplication {

    public static String PROCESS_NAME_XXXX = "process_name_xxxx";
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        DemoApplication.context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现context为null的异常
        TTAdManagerHolder.init(this);

    }

    public static Context getAppContext() {
        return DemoApplication.context;
    }








































































































}
