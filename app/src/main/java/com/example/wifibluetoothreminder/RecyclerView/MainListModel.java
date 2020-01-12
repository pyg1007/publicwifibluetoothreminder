package com.example.wifibluetoothreminder.RecyclerView;

public class MainListModel {

    private String NickName;
    private String ContentsCount;

    public MainListModel(String nickName, String contentsCount){
        this.NickName = nickName;
        this.ContentsCount = contentsCount;
    }

    public String getNickName() {
        return NickName;
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
