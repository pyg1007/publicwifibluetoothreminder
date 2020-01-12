package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class UPDATEDB {

    private Context mcontext;
    //myDBHelper
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;

    public void UpdateDB_(Context context){
        this.mcontext = context;
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        if (edtName.getText().toString() != "") {
            sqlDB.execSQL("UPDATE groupTBL SET gNumber ="
                    + edtPlan.getText() + " WHERE gName = '"
                    + edtName.getText().toString() + "';");
        }
        sqlDB.close();

        btnSelect.callOnClick();
    }
}
