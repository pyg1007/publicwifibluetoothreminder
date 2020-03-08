package com.yonggeun.wifibluetoothreminder.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WifiBluetoothList.class, ContentList.class}, version = 1)
public abstract class ListDatabase extends RoomDatabase {

    public abstract WifiBluetoothListDao wifiBluetoothListDao();

    public abstract ContentListDao contentListDao();

    private static ListDatabase Instance;

    public static ListDatabase getDatabase(Context context) {
        if (Instance == null) {
            synchronized (ListDatabase.class) {
                if (Instance == null) {
                    Instance = Room.databaseBuilder(context.getApplicationContext(), ListDatabase.class, "wb_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return Instance;
    }

}
