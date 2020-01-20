package com.example.wifibluetoothreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.wifibluetoothreminder.Service.MyService;
import com.example.wifibluetoothreminder.Service.RestartService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, MyService.class);
            context.startService(in);
        }
    }
}
