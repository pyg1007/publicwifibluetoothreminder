package com.example.wifibluetoothreminder.Service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wifibluetoothreminder.AlarmReceiver;
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

    private boolean isEnableWifi = false;

    private String NickName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        WifiStatus();

        return START_NOT_STICKY;
    }

    public boolean isExist(String Mac) { // 디비에 Mac이 존재하는지 유무
        boolean check = false;
        final List<String> ssid_list = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ListDatabase listDatabase = ListDatabase.getDatabase(getApplication());
                WifiBluetoothListDao wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();
                List<WifiBluetoothList> list = wifiBluetoothListDao.getAll_Service();
                for (int i = 0; i < list.size(); i++) {
                    ssid_list.add(list.get(i).getMac());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String str : ssid_list) {
            if (Mac.equals(str)) {
                check = true;
                break;
            }
        }
        return check;
    }

    public List<String> isContentExist(String Mac) {

        ListDatabase listDatabase = ListDatabase.getDatabase(getApplication());
        ContentListDao contentListDao = listDatabase.contentListDao();
        List<ContentList> list = contentListDao.getItem(Mac);
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

    public String getWifiMacName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String Mac = info.getMacAddress();
        return Mac;
    }

    public String getWifiNickName(final String Mac) {
        NickName = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ListDatabase listDatabase = ListDatabase.getDatabase(getApplication());
                WifiBluetoothListDao wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();
                List<WifiBluetoothList> list = wifiBluetoothListDao.getAll_Service();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getMac().equals(Mac)) {
                        NickName = list.get(i).getNickName();
                        break;
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return NickName;
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
                isEnableWifi = true;
                if (!isExist(getWifiMacName())) {
                    if (ForeGround.get().isBackGround()) {
                        String ChannelID = "ChannelID_1";
                        String ChannelName = "Wifi";
                        int Notification_id = 3;
                        String Title = "Wifi 연결 감지";
                        String Content = "Wifi 연결 감지, 연결하시겠습니까?";
                        String DeviceType = "Wifi";
                        setNotification(ChannelID, ChannelName, Notification_id, Title, Content, DeviceType, getWifiMacName(), getWifiName());
                    } else if (!ForeGround.get().isBackGround()) {
                        int Notification_id = 3;
                        Intent action = new Intent("Service_to_Activity");
                        action.putExtra("DeviceType", "Wifi");
                        action.putExtra("Mac", getWifiMacName());
                        action.putExtra("SSID", getWifiName());
                        LocalBroadcastManager.getInstance(BluetoothWifiService.this).sendBroadcast(action);
                        CancleNotification(Notification_id);
                    }
                } else {
                    if (ForeGround.get().isBackGround()) {
                        List<String> Contents = isContentExist(getWifiMacName());
                        if (Contents.size() != 0) {
                            String ChannelID = "ChannelId_2";
                            String ChannelName = "Memo";
                            int Notification_id = 4;
                            String Title = getWifiNickName(getWifiMacName()) + "에 등록된 메세지";
                            String GroupName = "Content_Group";
                            for (String str : Contents) {
                                setGroupNotification(ChannelID, ChannelName, Notification_id++, Title, str, GroupName);
                            }
                            setGroupSummaryNotification(ChannelID, ChannelName, 0, "일정 알림", Contents.size(), GroupName);
                        }
                    } else if (!ForeGround.get().isBackGround()) {
                        CancleNotification(0);
                    }
                }
            }

            @Override
            public void onLost(Network network) {
                isEnableWifi = false;
                CancleNotification(0);
                CancleNotification(3);
            }
        });
    }

    public void setNotification(String ChannelID, String Notifi_Channel_Name, int Notifi_id, String Title, String Content, String DeviceType, String Mac, String SSID) {
        Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
        intent.putExtra("DeviceType", DeviceType);
        intent.putExtra("Mac", Mac);
        intent.putExtra("SSID", SSID);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel Noti = new NotificationChannel(ChannelID, Notifi_Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText(Content).setContentTitle(Title).setAutoCancel(true);
            notificationManager.createNotificationChannel(Noti);
            notificationManager.notify(Notifi_id, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText(Content).setContentTitle(Title).setAutoCancel(true);
            notificationManager.notify(Notifi_id, builder.build());
        }
    }

    public void setGroupNotification(String ChannelID, String Notifi_Channel_Name, int Notifi_id, String Title, String Content, String GroupName) {
        Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel Noti = new NotificationChannel(ChannelID, Notifi_Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText(Content).setContentTitle(Title).setAutoCancel(true).setGroup(GroupName);
            notificationManager.createNotificationChannel(Noti);
            notificationManager.notify(Notifi_id, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentText(Content).setContentTitle(Title).setAutoCancel(true).setGroup(GroupName);
            notificationManager.notify(Notifi_id, builder.build());
        }
    }

    public void setGroupSummaryNotification(String ChannelID, String Notifi_Channel_Name, int Notifi_id, String Title, int size, String GroupName) {
        Intent intent = new Intent(BluetoothWifiService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothWifiService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // 노티피 클릭시 이동할 수 있는 인텐트
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel Noti = new NotificationChannel(ChannelID, Notifi_Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(Title).setAutoCancel(true).setGroup(GroupName).setGroupSummary(true).setNumber(size);
            notificationManager.createNotificationChannel(Noti);
            notificationManager.notify(Notifi_id, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothWifiService.this, ChannelID);
            builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(Title).setAutoCancel(true).setGroup(GroupName).setGroupSummary(true).setNumber(size);
            notificationManager.notify(Notifi_id, builder.build());
        }
    }

    public void CancleNotification(int Notify_id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Notify_id);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("Activity_to_Service".equals(intent.getAction())) { // 어플 메인엑티비티 진입시 받는 리시버
                if (isEnableWifi && !isExist(getWifiMacName())) {
                    Log.e("Mac", getWifiMacName());
                    Intent action = new Intent("Service_to_Activity");
                    action.putExtra("DeviceType", "Wifi");
                    action.putExtra("Mac", getWifiMacName());
                    action.putExtra("SSID", getWifiName());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(action);
                }
                CancleNotification(3);
                CancleNotification(0);
                Log.e("TAG : ", "Activity_to_Service");
            }
            try {
                    Log.e("receiver : ", intent.getAction());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){ // 연결될 때 감지
                Log.e("Mac : " , device.getAddress());
                Log.e("Name : ", device.getName());
            }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())){ // 연결 끊길때 감지
                Log.e("ACTION_ACL_DISCONNECTED", "DISCONNECTED");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG : " , "onDestroy");
        setAlarmTimer();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ReStartService");
        intentFilter.addAction("Activity_to_Service");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver, filter);
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
        Log.e("TAG : " , "onTaskRemoved");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        unregisterReceiver(mBroadcastReceiver);
    }


}
