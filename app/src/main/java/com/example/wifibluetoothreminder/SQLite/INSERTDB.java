package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class INSERTDB {
    private Context mcontext;
    //myDBHelper
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;
    public void InsertDB_(Context context){
        this.mcontext = context;
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                + edtName.getText().toString() + "' , '"
                + edtPlan.getText().toString() + "');");
        sqlDB.close();
    }
}
