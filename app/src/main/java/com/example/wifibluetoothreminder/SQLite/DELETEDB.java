package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DELETEDB {

    private Context mcontext;
    //myDBHelper
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;

    public void DeleteDB_(Context context){
        this.mcontext = context;
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        if (edtName.getText().toString() != "") {
            sqlDB.execSQL("DELETE FROM groupTBL WHERE gName = '"
                    + edtName.getText().toString() + "';");

        }
        sqlDB.close();

        btnSelect.callOnClick();
    }
}
