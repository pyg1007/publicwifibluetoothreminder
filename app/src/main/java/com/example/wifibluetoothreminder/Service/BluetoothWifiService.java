package com.example.wifibluetoothreminder.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
        builder.setSmallIcon(R.drawable.ic_launcher_background).setContentText(content).setContentTitle(title);
        notificationManager.createNotificationChannel(notificationChannel);
        startForeground(2,builder.build());
    }

    public void Low_Level_ForeGroundNotification(String title, String content){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0); // 노티피 클릭시 이동할 수 있는 인텐트
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1234"); // 1234은 채널이름 수정가능
        builder.setSmallIcon(R.drawable.ic_launcher_background).setContentText(content).setContentTitle(title); // 이미지는 아무거나 사용했음.
        startForeground(2,builder.build());
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
                if(isPermission()) {
                    Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, 0); // 노티피 클릭시 이동할 수 있는 인텐트
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String ChannelID = "Channel_2";
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel Noti = new NotificationChannel(ChannelID, "android", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
                        builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText("WIFI가 연결되었습니다. 등록하시겠습니까?").setContentTitle("WIFI 연결감지").setAutoCancel(true);
                        notificationManager.createNotificationChannel(Noti);
                        notificationManager.notify(3, builder.build());
                    } else {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, "1235");
                        builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText("WIFI가 연결되었습니다. 등록하시겠습니까?").setContentTitle("WIFI 연결감지").setAutoCancel(true);
                        notificationManager.notify(3, builder.build());
                    }
                }
            }
            @Override
            public void onLost(Network network) {
                if(isPermission()) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(3);
                }
            }
        });


        return START_STICKY;
    }

    //TODO : 퍼미션 허용안되있으면 노피티 울리면 안댐
    public boolean isPermission(){
        boolean ischeck = false;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int ACCESS_FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED && ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED)
                ischeck = true;
        }
        return ischeck;
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
