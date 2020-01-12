package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SELECTDB {
    private Context mcontext;
    //myDBHelper
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;

    public void SelectDB_(Context context){
        this.mcontext = context;
        myDBHelper = new myDBHelper(mcontext);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

        String strNames = "SSID(닉네임)" + "\r\n" + "--------" + "\r\n";
        String strNumbers = "일정" + "\r\n" + "--------" + "\r\n";

        while (cursor.moveToNext()) {
            strNames += cursor.getString(0) + "\r\n";
            strNumbers += cursor.getString(1) + "\r\n";
        }

        edtNameResult.setText(strNames);
        edtPlanResult.setText(strNumbers);

        cursor.close();
        sqlDB.close();
    }
}
