package com.example.wifibluetoothreminder.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Content_table")
public class ContentList {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int ID;

    @NonNull
    public String Content_SSID;

    @NonNull
    public String Content;

    public ContentList(int ID, @NonNull String Content_SSID, @NonNull String Content){
        this.ID = ID;
        this.Content_SSID = Content_SSID;
        this.Content = Content;
    }
}
