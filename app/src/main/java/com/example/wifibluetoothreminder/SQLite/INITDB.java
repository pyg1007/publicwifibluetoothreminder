package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class INITDB {
    private Context mcontext;
    //myDBHelper
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;
    public void InitDB_(Context context){
        this.mcontext =context;
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        myDBHelper.onUpgrade(sqlDB, 1, 2); // 인수는 아무거나 입력하면 됨.
        sqlDB.close();
    }
}
