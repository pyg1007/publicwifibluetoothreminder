package com.yonggeun.wifibluetoothreminder.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WifiBluetoothListDao {

    @Query("SELECT * FROM wb_table")
    LiveData<List<WifiBluetoothList>> getAll();

    @Query("SELECT * FROM wb_table")
    List<WifiBluetoothList> getAll_Service();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WifiBluetoothList wifiBluetoothList);

    @Query("UPDATE wb_table SET NickName = :NickName WHERE Mac = :Mac")
    void update(String Mac, String NickName);

    @Query("UPDATE wb_table SET Count = :Count WHERE Mac = :Mac")
    void updateCount(String Mac, int Count);

    @Query("Delete FROM wb_table WHERE Mac = :Mac")
    void delete(String Mac);
}
