package com.example.wifibluetoothreminder.SQLite;


import android.provider.BaseColumns;



public final class DataBases {


    public static final class CreateDB implements BaseColumns{



        public static final String DEVICE = "device";

        public static final String SSID = "ssid";

        public static final String NICKNAME = "nickname";

        public static final String _TABLENAME0 = "usertable";

        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("

                +_ID+" integer auto_increment, "

                +DEVICE+" text not null,"

                +SSID+" text not null ,"

                +NICKNAME+ " text not null,"

                + "PRIMARY KEY (" + SSID + "))";
    }
    public static final class CreateDB2 implements  BaseColumns{

        public static final String SSID = "ssid";

        public static final String CONTENT = "content";

        public static final String _TABLENAME0 = "usertable";

        public static final String _TABLENAME1 = "plantable";

        public static final String _CREATE1 = "create table if not exists " + _TABLENAME1 + "("

                + _ID + " integer auto_increment, "

                + SSID + " text not null, "

                + CONTENT + " text not null ,"

                + "FOREIGN KEY(" + CreateDB2.SSID + " ) REFERENCES " // foreign(현재테이블의 키) REFERENCE 가져올테이블 이름 (컬럼이름)

                + CreateDB._TABLENAME0 + "(ssid),"

                + "PRIMARY KEY (" + SSID + "))";
    }
}