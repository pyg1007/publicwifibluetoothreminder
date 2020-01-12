package com.example.wifibluetoothreminder.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wifibluetoothreminder.RecyclerView.MainListModel;

import java.util.ArrayList;

public class SELECTDB {
    private Context mcontext;
    //myDBHelper
    private ArrayList<MainListModel> item;
    SQLiteDatabase sqlDB;
    myDBHelper myDBHelper;

    public void SELECTDB(Context context, ArrayList<MainListModel> list) {
        this.mcontext = context;
        this.item = list;
    }

    public void select(){
        myDBHelper = new myDBHelper(mcontext);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);


        while (cursor.moveToNext()) {
            item.add(new MainListModel(cursor.getString(0), cursor.getString(1)));
        }

        cursor.close();
        sqlDB.close();
    }

    public void selectContents(){

    }
}
