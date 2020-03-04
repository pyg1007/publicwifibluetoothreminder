package com.example.wifibluetoothreminder.Room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WifiBluetoothRepository {

    private WifiBluetoothListDao wifiBluetoothListDao;

    private LiveData<List<WifiBluetoothList>> Wifibluetoothlist;

    public WifiBluetoothRepository(Application application) {
        ListDatabase listDatabase = ListDatabase.getDatabase(application);

        wifiBluetoothListDao = listDatabase.wifiBluetoothListDao();

        Wifibluetoothlist = wifiBluetoothListDao.getAll();
    }

    public LiveData<List<WifiBluetoothList>> getAllData() {
        return Wifibluetoothlist;
    }


    public void insert(final WifiBluetoothList wifiBluetoothList) {
        Runnable addRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.insert(wifiBluetoothList);
            }
        };
        Executor IO = Executors.newSingleThreadExecutor();
        IO.execute(addRunnable);
    }

    public void update(final String Mac, final String NickName) {
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.update(Mac, NickName);
            }
        };
        Executor Update = Executors.newSingleThreadExecutor();
        Update.execute(updateRunnable);
    }

    public void updatecount(final String Mac, final int Count) {
        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.updateCount(Mac, Count);
            }
        };
        Executor Update = Executors.newSingleThreadExecutor();
        Update.execute(updateRunnable);
    }

    public void delete(final String Mac) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                wifiBluetoothListDao.delete(Mac);
            }
        };
        Executor Delete = Executors.newSingleThreadExecutor();
        Delete.execute(deleteRunnable);
    }
}
