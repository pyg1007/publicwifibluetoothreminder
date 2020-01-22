package com.example.wifibluetoothreminder.RecyclerView;


public class MainListModel {

    private String NickName;
    private String SSID;
    private String ContentsCount;
    private String Device;

    public MainListModel(String Device, String ssid, String nickName, String contentsCount){
        this.Device = Device;
        this.SSID = ssid;
        this.NickName = nickName;
        this.ContentsCount = contentsCount;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getNickName() {
        return NickName;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getContentsCount() {
        return ContentsCount;
    }

    public void setContentsCount(String contentsCount) {
        ContentsCount = contentsCount;
    }
}
