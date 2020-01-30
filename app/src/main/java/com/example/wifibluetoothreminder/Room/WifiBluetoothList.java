package com.example.wifibluetoothreminder.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "wb_table")
public class WifiBluetoothList {

    @NonNull
    public String Device_Type;

    @NonNull
    @PrimaryKey
    public String SSID;

    @NonNull
    public String NickName;

    @NonNull
    public int Count;

    public WifiBluetoothList(@NonNull String Device_Type, @NonNull String SSID, @NonNull String NickName, @NonNull int Count){
        this.Device_Type = Device_Type;
        this.SSID = SSID;
        this.NickName = NickName;
        this.Count = Count;
    }

    public int getCount() {
        return Count;
    }

    @NonNull
    public String getDevice_Type() {
        return Device_Type;
    }

    @NonNull
    public String getSSID() {
        return SSID;
    }

    @NonNull
    public String getNickName() {
        return NickName;
    }
}
