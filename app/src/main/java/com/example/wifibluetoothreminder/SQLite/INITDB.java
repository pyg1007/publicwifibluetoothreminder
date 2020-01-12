package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class INITDB {

    private Context mcontext;
    //myDBHelper
    private SQLiteDatabase sqlDB;
    private myDBHelper myDBHelper;


    public INITDB(Context context){
        this.mcontext =context;
    }

    public void init(){
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        myDBHelper.onUpgrade(sqlDB, 1, 2); // 인수는 아무거나 입력하면 됨.
        sqlDB.close();
    }
}
