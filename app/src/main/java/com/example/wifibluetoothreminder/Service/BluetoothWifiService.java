package com.example.wifibluetoothreminder.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.wifibluetoothreminder.MainActivity;
import com.example.wifibluetoothreminder.R;

public class BluetoothWifiService extends Service {


    public BluetoothWifiService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {//OreO이상이면 채널생성이 필요함
            High_Level_ForeGroundNotification("Test","Testing");
        }else{
            Low_Level_ForeGroundNotification("Test","Testing");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void High_Level_ForeGroundNotification(String title, String content){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0); // 노티피 클릭시 이동할 수 있는 인텐트
        String ChannelID = "Channel_1"; // 채널이름
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(ChannelID,"Android",NotificationManager.IMPORTANCE_LOW);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelID);
        builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText(content).setContentTitle(title);
        notificationManager.createNotificationChannel(notificationChannel);
        startForeground(2,builder.build());
    }

    public void Low_Level_ForeGroundNotification(String title, String content){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0); // 노티피 클릭시 이동할 수 있는 인텐트
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1234"); // 1234은 채널이름 수정가능
        builder.setSmallIcon(R.drawable.ic_launcher_background).setContentText(content).setContentTitle(title); // 이미지는 아무거나 사용했음.
        builder.setContentIntent(pendingIntent); // 이부분없애면 페딩인텐트 작동안함.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO : Service 최초 시작시 할 일
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        CM.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                Log.e("WIFI : ", info.getBSSID());
                Log.e("WIFI : ", info.getSSID());
                Log.e("WIFI : ", info.getMacAddress());
            }
        });

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        //TODO : Service 종료시 할 일
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
