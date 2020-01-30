package com.example.wifibluetoothreminder.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WifiBluetoothRepository {

    private WifiBluetoothListDao wifiBluetoothListDao;

    private LiveData<List<WifiBluetoothList>> Wifibluetoothlist;

    public WifiBluetoothRepository(Application application){
        ListDatabase listDatabase = ListDatabase.getDatabase(application);

        wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();

        Wifibluetoothlist = wifiBluetoothListDao.getAll();
    }

    public LiveData<List<WifiBluetoothList>> getAllData(){
        return Wifibluetoothlist;
    }

    public void insert(final WifiBluetoothList wifiBluetoothList){
        Runnable addRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.insert(wifiBluetoothList);
            }
        };
        Executor IO = Executors.newSingleThreadExecutor();
        IO.execute(addRunnable);
    }

    public void update(final String SSID, final String NickName){
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.update(SSID, NickName);
            }
        };
        Executor Update = Executors.newSingleThreadExecutor();
        Update.execute(updateRunnable);
    }

    public void delete(final String SSID){
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.delete(SSID);
            }
        };
        Executor Delete = Executors.newSingleThreadExecutor();
        Delete.execute(deleteRunnable);
    }
}
