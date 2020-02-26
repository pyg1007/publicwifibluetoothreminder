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

    public WifiBluetoothListViewModel(Application application){
        super(application);
        wifiBluetoothRepository = new WifiBluetoothRepository(application);
        Wifibluetoothlist = wifiBluetoothRepository.getAllData();
    }

    public LiveData<List<WifiBluetoothList>> getAllData(){
        return Wifibluetoothlist;
    }

    public void insert(WifiBluetoothList wifiBluetoothList){
        wifiBluetoothRepository.insert(wifiBluetoothList);
    }

    public void update(String SSID, String NickName){
        wifiBluetoothRepository.update(SSID, NickName);
    }

    public void updateCount(String SSID, int count){
        wifiBluetoothRepository.updatecount(SSID, count);
    }

    public void delete(String Mac){
        wifiBluetoothRepository.delete(Mac);
    }
}
