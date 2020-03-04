package com.example.wifibluetoothreminder.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wifibluetoothreminder.Room.WifiBluetoothList;
import com.example.wifibluetoothreminder.Room.WifiBluetoothRepository;

import java.util.List;

public class WifiBluetoothListViewModel extends AndroidViewModel {

    private WifiBluetoothRepository wifiBluetoothRepository;

    private LiveData<List<WifiBluetoothList>> Wifibluetoothlist;

    public WifiBluetoothListViewModel(Application application) {
        super(application);
        wifiBluetoothRepository = new WifiBluetoothRepository(application);
        Wifibluetoothlist = wifiBluetoothRepository.getAllData();
    }

    public LiveData<List<WifiBluetoothList>> getAllData() {
        return Wifibluetoothlist;
    }

    public void insert(WifiBluetoothList wifiBluetoothList) {
        wifiBluetoothRepository.insert(wifiBluetoothList);
    }

    public void update(String Mac, String NickName) {
        wifiBluetoothRepository.update(Mac, NickName);
    }

    public void updateCount(String Mac, int count) {
        wifiBluetoothRepository.updatecount(Mac, count);
    }

    public void delete(String Mac) {
        wifiBluetoothRepository.delete(Mac);
    }
}
