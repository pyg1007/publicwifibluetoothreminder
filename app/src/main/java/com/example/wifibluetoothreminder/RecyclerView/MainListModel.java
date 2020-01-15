package com.example.wifibluetoothreminder.RecyclerView;


public class MainListModel {

    private String NickName;
    private String ContentsCount;
    private String Device;

    public MainListModel(String Device, String nickName, String contentsCount){
        this.Device = Device;
        this.NickName = nickName;
        this.ContentsCount = contentsCount;
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
