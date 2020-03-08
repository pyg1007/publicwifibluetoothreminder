package com.yonggeun.wifibluetoothreminder;

import android.app.Application;

import com.yonggeun.wifibluetoothreminder.RunningCheck.ForeGround;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ForeGround.init(this);
    }

}
