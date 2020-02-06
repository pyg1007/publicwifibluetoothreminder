package com.example.wifibluetoothreminder.Service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wifibluetoothreminder.AlarmReceiver;
import com.example.wifibluetoothreminder.Contents;
import com.example.wifibluetoothreminder.MainActivity;
import com.example.wifibluetoothreminder.R;
import com.example.wifibluetoothreminder.Room.ContentList;
import com.example.wifibluetoothreminder.Room.ContentListDao;
import com.example.wifibluetoothreminder.Room.ListDatabase;
import com.example.wifibluetoothreminder.Room.WifiBluetoothList;
import com.example.wifibluetoothreminder.Room.WifiBluetoothListDao;
import com.example.wifibluetoothreminder.RunningCheck.ForeGround;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BluetoothWifiService extends Service {

    public boolean RestartCheck;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        RestartCheck = intent.getBooleanExtra("Restart", false);

        WifiStatus();

        return START_NOT_STICKY;
    }

    public boolean isExist(String SSID) { // 디비에 SSID가 존재하는지 유무
        boolean check = false;
        ListDatabase listDatabase = ListDatabase.getDatabase(getApplication());
        WifiBluetoothListDao wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();
        List<WifiBluetoothList> list = wifiBluetoothListDao.getAll_Service();
        List<String> ssid_list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ssid_list.add(list.get(i).getSSID());
        }
        for (String str : ssid_list) {
            if (SSID.equals(str)) {
                check = true;
                break;
            }
        }
        return check;
    }

    public boolean checkGPSService() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    public List<String> isContentExist(String SSID) {

        ListDatabase listDatabase = ListDatabase.getDatabase(getApplication());
        ContentListDao contentListDao = listDatabase.contentListDao();
        List<ContentList> list = contentListDao.getItem(SSID);
        List<String> content = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            content.add(list.get(i).Content);
        }

        return content;
    }

    public String getWifiName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = wifiManager.getConnectionInfo();
        String Name = info.getSSID();
        Name = Name.substring(1, Name.length() - 1);
        return Name;
    }

    public void WifiStatus() {
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        CM.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                Log.e("onAvailable : ", "onAvailable");
                if (checkGPSService()) {
                    if (!isExist(getWifiName())) {
                        if (ForeGround.get().isBackGround() || RestartCheck) {
                            // TODO : 디비에 존재하지 않고, 백그라운드일 때 노티피 알림
                            Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                String ChannelID = "Channel_1";
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationChannel Noti = new NotificationChannel(ChannelID, "android", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
                                builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText("WIFI가 연결되었습니다. 등록하시겠습니까?").setContentTitle("WIFI 연결감지").setAutoCancel(true);
                                notificationManager.createNotificationChannel(Noti);
                                notificationManager.notify(3, builder.build());
                            } else {
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, "Channel_2");
                                builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText("WIFI가 연결되었습니다. 등록하시겠습니까?").setContentTitle("WIFI 연결감지").setAutoCancel(true);
                                notificationManager.notify(3, builder.build());
                            }
                        } else if (!ForeGround.get().isBackGround() || !RestartCheck) {
                            // TODO : 디비에 존재하지 않고, 백그라운드가 아닐 때 등록 다이얼로그
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(3);
                            sendMessage("Wifi", getWifiName());
                        }
                    } else if (isExist(getWifiName())) {
                        if (ForeGround.get().isBackGround() || RestartCheck) {
                            // TODO : 디비에 존재하고, 백그라운드일때 Contents 노티피 쌓기알림
                            List<String> contents = isContentExist(getWifiName());
                            if (contents.size() != 0) {
                                int notify_count = 4;
                                Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    String ChannelID = "Channel_3";
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    NotificationChannel Noti = new NotificationChannel(ChannelID, "android", NotificationManager.IMPORTANCE_DEFAULT);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
                                    builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("WIFI 연결감지").setAutoCancel(true);
                                    for (int i = 0; i < contents.size(); i++) {
                                        builder.setContentText(contents.get(i));
                                        builder.setGroup("Contents");
                                        notificationManager.createNotificationChannel(Noti);
                                        notificationManager.notify(notify_count++, builder.build());
                                    }
                                    Summary_Notify(contents);
                                } else {
                                    String ChannelID = "Channel_4";
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
                                    builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("WIFI 연결감지").setAutoCancel(true);
                                    for (int i = 0; i < contents.size(); i++) {
                                        builder.setContentText(contents.get(i));
                                        builder.setGroup("Contents");
                                        notificationManager.notify(notify_count++, builder.build());
                                    }
                                    Summary_Notify(contents);
                                }
                            }
                        }
                    } else if (isExist(getWifiName()) && (!ForeGround.get().isBackGround() || !RestartCheck)) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(0);
                    }
                }
            }

            @Override
            public void onLost(Network network) {
                if (!isExist(getWifiName()) && (ForeGround.get().isBackGround() || RestartCheck)) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(3);
                }
            }
        });
    }

    public void Summary_Notify(List<String> contents) {
        Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String ChannelID = "Channel_2";
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel Noti = new NotificationChannel(ChannelID, "android", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("WIFI 연결감지").setAutoCancel(true);
            builder.setNumber(contents.size());
            builder.setGroup("Contents");
            builder.setGroupSummary(true);
            notificationManager.createNotificationChannel(Noti);
            notificationManager.notify(0, builder.build());
        }
    }

    public void sendMessage(String DeviceType, String SSID) {
        Intent intent = new Intent("Service-Activity");
        intent.putExtra("DeviceType", DeviceType);
        intent.putExtra("SSID", SSID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

        setAlarmTimer();
    }


}
