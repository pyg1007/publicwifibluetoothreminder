package com.example.wifibluetoothreminder.SQLite;


import android.provider.BaseColumns;



public final class DataBases {


    public static final class CreateDB implements BaseColumns{

        public static final String USERID = "userid";

        public static final String NAME = "name";

        public static final String _TABLENAME0 = "usertable";

        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("

                +_ID+" integer primary key autoincrement, "

                +USERID+" text not null,"

                +NAME+" text not null)";
    }
    public static final class CreateDB2 implements  BaseColumns{

        public static final String USERID = "userid";

        public static final String NAME = "name";

        public static final String _TABLENAME0 = "usertable";

        public static final String _TABLENAME1 = "plantable";

        public static final String CONTENT = "content";

        public static final String _CREATE1 = "create table if not exists " + _TABLENAME1 + "("

                + _ID + " integer primary key autoincrement not null, "

                + USERID + " text not null, "

                + NAME + " text not null, "

                + CONTENT + " text not null ,"

                + "FOREIGN KEY(" + CreateDB2.USERID + " ) REFERENCES " // foreign(현재테이블의 키) REFERENCE 가져올테이블 이름 (컬럼이름)

                + CreateDB._TABLENAME0 + "(userid)" + ")";
    }
}