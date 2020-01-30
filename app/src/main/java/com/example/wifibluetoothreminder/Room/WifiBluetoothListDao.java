package com.example.wifibluetoothreminder.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WifiBluetoothListDao {

    @Query("SELECT * FROM wb_table")
    LiveData<List<WifiBluetoothList>> getAll();

    @Query("SELECT * FROM wb_table")
    List<WifiBluetoothList> getAll_Service();

    @Insert
    void insert(WifiBluetoothList wifiBluetoothList);

    @Query("UPDATE wb_table SET NickName = :NickName WHERE SSID = :SSID")
    void update(String SSID, String NickName);

    @Query("Delete FROM wb_table WHERE SSID = :SSID")
    void delete(String SSID);
}
