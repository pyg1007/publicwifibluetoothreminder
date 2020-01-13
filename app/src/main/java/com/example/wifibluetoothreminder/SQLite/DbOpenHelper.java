package com.example.wifibluetoothreminder.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";

    private static final int DATABASE_VERSION = 1;

    public static SQLiteDatabase mDB;

    private DatabaseHelper mDBHelper;

    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{


        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override

        public void onCreate(SQLiteDatabase db){

            db.execSQL(DataBases.CreateDB._CREATE0);
            db.execSQL(DataBases.CreateDB2._CREATE1);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);

            db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB2._TABLENAME1);

            onCreate(db);

        }
    }


    public DbOpenHelper(Context context){

        this.mCtx = context;

    }



    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }



    public void create(){
        mDBHelper.onCreate(mDB);
    }



    public void close() {
        mDB.close();
    }



    // Insert DB

    public long insertColumn(String userid, String name){

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.NAME, name);

        values.put(DataBases.CreateDB.USERID, userid);

        values.put(DataBases.CreateDB2.USERID, userid);

        values.put(DataBases.CreateDB2.NAME, name);
/*
        values.put(DataBases.CreateDB.NAME, name);

        values.put(DataBases.CreateDB.CONTENTS, Contents);
*/

        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);

    }



    // Update DB

    public boolean updateColumn(long id, String userid, String name){

        ContentValues values = new ContentValues();

        values.put(DataBases.CreateDB.USERID, userid);

        values.put(DataBases.CreateDB.NAME, name);

        values.put(DataBases.CreateDB2.USERID, userid);

        values.put(DataBases.CreateDB2.NAME, name);

        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;

    }



    // Delete All

    public void deleteAllColumns() {

        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
        mDB.delete(DataBases.CreateDB2._TABLENAME1, null, null);
    }



    // Delete DB

    public boolean deleteColumn(long id){

        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;

    }

    // Select DB

    public Cursor selectColumns() {

        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectColumns2() {

        return mDB.query(DataBases.CreateDB2._TABLENAME1, null, null, null, null, null, null);
    }




    // sort by column

    public Cursor sortColumn(String sort){

        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);

        return c;
    }
}