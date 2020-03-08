package com.yonggeun.wifibluetoothreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.yonggeun.wifibluetoothreminder.Service.BluetoothWifiService;
import com.yonggeun.wifibluetoothreminder.Service.RestartService;

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
