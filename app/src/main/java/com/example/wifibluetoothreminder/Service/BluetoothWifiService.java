package com.example.wifibluetoothreminder.Service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wifibluetoothreminder.AlarmReceiver;
import com.example.wifibluetoothreminder.MainActivity;
import com.example.wifibluetoothreminder.R;
import com.example.wifibluetoothreminder.RunningCheck.ForeGround;
import com.example.wifibluetoothreminder.SQLite.DbOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class BluetoothWifiService extends Service {

    private ArrayList<String> SSID_list;
    public static Intent serviceIntent = null;

    public BluetoothWifiService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;

        WifiStatus();

        return START_NOT_STICKY;
    }

    public boolean isExist(String SSID){ // 디비에 SSID가 존재하는지 유무
        boolean check = false;

        SSID_list = new ArrayList<>();

        DbOpenHelper openHelper = new DbOpenHelper(this);
        openHelper.open();
        openHelper.create();

        Cursor cursor = openHelper.selectColumns();
        while(cursor.moveToNext()){
            SSID_list.add(cursor.getString(2));
        }
        cursor.close();
        openHelper.close();

        for(String str : SSID_list){
            if(str.equals(SSID)) {
                check = true;
                break;
            }
        }
        return check;
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

    public void WifiStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = wifiManager.getConnectionInfo();
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        CM.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                if (!isExist(info.getSSID()) && isPermission() && ForeGround.get().isBackGround()) {
                    // TODO : 디비에 존재하지 않고, 퍼미션허용되어있으며, 백그라운드일 때 노티피 알림
                    Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("DetectType", "Wifi");
                    intent.putExtra("SSID", info.getSSID());
                    PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
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
                }else if(!isExist(info.getSSID()) && isPermission() && !ForeGround.get().isBackGround()){
                    // TODO : 디비에 존재하지 않고, 퍼미션허용되어있으며, 백그라운드가 아닐 때 등록 다이얼로그
                    sendMessage("Wifi", info.getSSID());
                }
            }

            @Override
            public void onLost(Network network) {
                if (!isExist(info.getSSID()) && isPermission() && ForeGround.get().isBackGround()) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(3);
                }
            }
        });
    }

    public void sendMessage(String DeviceType, String SSID){
        Intent intent = new Intent("Service-Activity");
        intent.putExtra("DeviceType", DeviceType);
        intent.putExtra("SSID", SSID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceIntent = null;
        setAlarmTimer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        serviceIntent = null;
        setAlarmTimer();
    }
}
