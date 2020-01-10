package com.example.wifibluetoothreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wifibluetoothreminder.CheckingService.RunningService;
import com.example.wifibluetoothreminder.Service.BluetoothWifiService;
import com.example.wifibluetoothreminder.Service.UnCatchTaskService;

public class MainActivity extends AppCompatActivity {

    RunningService runningService;

    // 변경되지 않을 상수
    private static final int REQUEST_ACCESS_LOCATION = 1000;



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
        checkpermmission();
    }

    public void UI(){ //UI들 여기 작성

    }

    public void checkpermmission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if ( ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED || ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_ACCESS_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("권한").setMessage("WIFI의 정보를 받아오기 위해서 필요합니다. 정말 거부하시겠습니까?");
            builder.setPositiveButton("승인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    checkpermmission();
                }
            });
            builder.setNegativeButton("거부", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    AlertDialog.Builder no = new AlertDialog.Builder(MainActivity.this);
                    no.setMessage("해당기능을 사용하기 위해서는 설정 -> App 및 알림 -> App 선택 -> 권한 -> 허용을 해주세요").show();
                }
            });
            builder.show();
        }
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
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                startForegroundService(intent);
            else
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
