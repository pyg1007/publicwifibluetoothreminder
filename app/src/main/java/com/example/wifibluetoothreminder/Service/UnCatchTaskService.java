package com.example.wifibluetoothreminder.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.wifibluetoothreminder.CheckingService.RunningService;

public class UnCatchTaskService extends Service {
    public UnCatchTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("TAG : ", "onTaskRemoved "+rootIntent);
        RunningService runningService = new RunningService(this);
        if (!runningService.isRunning("com.example.wifibluetoothreminder.Service.BluetoothWifiService")) {
            Intent intent = new Intent(this, BluetoothWifiService.class);
            startService(intent);
        }
        stopSelf();
    }
}
