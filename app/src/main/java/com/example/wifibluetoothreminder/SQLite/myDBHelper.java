package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDBHelper extends SQLiteOpenHelper {
    public myDBHelper(Context context) {
        super(context, "groupDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  groupTBL ( gName CHAR(20) PRIMARY KEY, gNumber CHAR(20));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS groupTBL");
        onCreate(db);
    }
}
