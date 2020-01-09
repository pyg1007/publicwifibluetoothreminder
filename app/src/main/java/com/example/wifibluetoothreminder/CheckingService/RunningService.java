package com.example.wifibluetoothreminder.CheckingService;

import android.app.ActivityManager;
import android.content.Context;

public class RunningService {

    private Context context;

    public RunningService(Context context){
        this.context = context;
    }

    /*
    Service Name = 패키지명.Service.서비스명
    return false = 서비스 실행 안하는 중
    return true = 서비스 실행 중
     */
    public boolean isRunning(String ServiceName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ServiceName.equals(runningServiceInfo.service.getClassName()))
                return true;
        }

        return false;
    }
}
