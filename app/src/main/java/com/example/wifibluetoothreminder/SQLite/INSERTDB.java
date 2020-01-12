package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class INSERTDB {
    private Context mcontext;
    //myDBHelper
    private SQLiteDatabase sqlDB;
    private myDBHelper myDBHelper;

    public void INSERTDB(Context context){
        this.mcontext = context;
    }

    public void insert(String NickName, String ContentsCount){
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                + NickName + "' , '"
                + ContentsCount + "');");
        sqlDB.close();
    }

    public void insert_content(String NickName, String Contents){
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                + NickName + "' , '"
                + Contents + "');");
        sqlDB.close();
    }
}
