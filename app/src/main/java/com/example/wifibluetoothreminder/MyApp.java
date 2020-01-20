package com.example.wifibluetoothreminder;

import android.app.Application;

import com.example.wifibluetoothreminder.RunningCheck.ForeGround;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ForeGround.init(this);
    }

}
