package com.example.wifibluetoothreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wifibluetoothreminder.CheckingService.RunningService;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.Service.UnCatchTaskService;

public class MainActivity extends AppCompatActivity {

    RunningService runningService;
    @Override
    protected void onStart() {
        stopService(new Intent(MainActivity.this, BluetoothWifiService.class));
        /*
        TODO : 비정상종료(최근실행 앱에서 종료시 발생)
         */
        runningService = new RunningService(this);
        if (!runningService.isRunning("com.example.wifibluetoothreminder.Service.UnCatchTaskService"))
            startService(new Intent(this, UnCatchTaskService.class));
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UI();
        WifiStatus();
    }

    public void UI(){ //UI들 여기 작성

    }

    public void WifiStatus(){
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        CM.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                WifiManager WM = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = WM.getConnectionInfo();
                StartLog("Connect",wifiInfo.getBSSID());
            }
        });

    }

    public void AutoService(){
        runningService = new RunningService(this);
        if(!runningService.isRunning("com.example.wifibluetoothreminder.Service.BluetoothWifiService")) { // 중복실행 막기위해서 사용
            StartToast("실행중아님");
            Intent intent = new Intent(MainActivity.this, BluetoothWifiService.class);
            startService(intent);
        }else{
            StartToast("실행중");
        }
    }

    public void StartLog(String TAG, String msg){ // 빨간색로그
        Log.e(TAG, msg);
    }

    public void StartToast(String msg){ // 토스트
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        AutoService();
        StartLog("onDestroy :", "Destroy");
        super.onDestroy();
    }
}
