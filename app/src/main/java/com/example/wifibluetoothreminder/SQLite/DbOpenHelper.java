package com.example.wifibluetoothreminder.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


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

    public long insertColumn(String Kind_device, String ssid, String nickname){

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.DEVICE, Kind_device);

        values.put(DataBases.CreateDB.NICKNAME, nickname);

        values.put(DataBases.CreateDB.SSID, ssid);


        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }

    public long insertColumn2(String ssid, String contents){

        ContentValues values = new ContentValues();


        values.put(DataBases.CreateDB2.SSID, ssid);

        values.put(DataBases.CreateDB2.CONTENT,contents);


        return mDB.insert(DataBases.CreateDB2._TABLENAME1, null, values);
    }


    // Update DB

    public boolean updateUserTableColumn(String ssid, String nickname){

        ContentValues values = new ContentValues();

        values.put(DataBases.CreateDB.NICKNAME, nickname);

        String[] param = new String[]{ssid};

        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "ssid=?", param) > 0;

    }

    public boolean updatePlanTableColumn(String _id, String content){

        ContentValues values = new ContentValues();

        values.put(DataBases.CreateDB2.CONTENT, content);

        String[] param = new String[]{_id};

        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=?", param) > 0;

    }

    // Delete All

    public void deleteAllColumns() {

        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
        mDB.delete(DataBases.CreateDB2._TABLENAME1, null, null);
    }



    // Delete DB

    public boolean deleteUserTableColumn(String ssid){
        String[] params = new String[]{ssid};
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "ssid=?", params)>0;
    }

    public boolean deletePlanTableCloumn(String ssid, String content){
        String[] params = new String[]{ssid, content};
        return mDB.delete(DataBases.CreateDB2._TABLENAME1,"ssid=? and content=?",params)>0;
    }

    // Select DB

    public Cursor selectColumns() {

        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectColumns2() {

        return mDB.query(DataBases.CreateDB2._TABLENAME1, null, null, null, null, null, null);
    }

    public Cursor selectSSIDColumns(String ssid){
        String[] id = new String[]{ssid};
        return mDB.query(DataBases.CreateDB2._TABLENAME1,null,"SSID=?", id,null,null,null);
    }


    // sort by column

    public Cursor sortColumn(String sort){

        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);

        return c;
    }
}