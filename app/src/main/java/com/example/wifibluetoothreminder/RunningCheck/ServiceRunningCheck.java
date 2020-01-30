package com.example.wifibluetoothreminder.RunningCheck;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceRunningCheck {

    public Context mContext;

    public ServiceRunningCheck(Context context){
        this.mContext = context;
    }

    public boolean RunningCheck(String ServiceName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (ServiceName.equals(runningServiceInfo.service.getClassName()))
                return true;
        }

        return false;
    }

    public int RunningCount(){
        int count = 0;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            count++;
        }
        return count;
    }
}
