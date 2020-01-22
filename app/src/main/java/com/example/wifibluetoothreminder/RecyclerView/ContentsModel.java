package com.example.wifibluetoothreminder.RecyclerView;

public class ContentsModel {

    String _ID;
    String Contents;
    String SSID;

    public ContentsModel(String _id, String ssid, String contents){
        this._ID = _id;
        this.SSID = ssid;
        this.Contents = contents;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getContents() {
        return Contents;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setContents(String contents) {
        Contents = contents;
    }
}
