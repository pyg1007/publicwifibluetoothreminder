package com.example.wifibluetoothreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.Service.RestartService;

public class Reboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, BluetoothWifiService.class);
            context.startService(in);
        }
    }
}
