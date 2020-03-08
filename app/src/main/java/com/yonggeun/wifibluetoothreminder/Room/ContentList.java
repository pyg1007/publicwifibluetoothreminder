package com.yonggeun.wifibluetoothreminder.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Content_table")
public class ContentList {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public Integer ID;

    @NonNull
    public String Mac;

    @NonNull
    public String Content_SSID;

    @NonNull
    public String Content;

    public ContentList(@NonNull String Mac, @NonNull String Content_SSID, @NonNull String Content) {
        this.Mac = Mac;
        this.Content_SSID = Content_SSID;
        this.Content = Content;
    }


    @NonNull
    public String getMac() {
        return Mac;
    }

    @NonNull
    public String getContent_SSID() {
        return Content_SSID;
    }

    @NonNull
    public String getContent() {
        return Content;
    }
}
