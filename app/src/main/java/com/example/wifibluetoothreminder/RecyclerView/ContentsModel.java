package com.example.wifibluetoothreminder.RecyclerView;

public class ContentsModel {

    String Contents;
    String SSID;

    public ContentsModel(String ssid, String contents){
        this.SSID = ssid;
        this.Contents = contents;
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
