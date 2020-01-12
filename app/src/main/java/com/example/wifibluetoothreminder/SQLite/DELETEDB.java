package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DELETEDB {

    private Context mcontext;
    //myDBHelper
    private SQLiteDatabase sqlDB;
    private myDBHelper myDBHelper;

    public void DELETEDB(Context context){
        this.mcontext = context;
    }

    public void Delete(String NickName, String Content){
        myDBHelper = new myDBHelper(mcontext);
        sqlDB = myDBHelper.getWritableDatabase();
        sqlDB.execSQL("DELETE FROM groupTBL WHERE gName = '"
                    + NickName + "AND" + "일정칼럼명 = " + Content + "';");


        sqlDB.close();
    }
}
