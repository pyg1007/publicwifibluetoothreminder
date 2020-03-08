package com.yonggeun.wifibluetoothreminder.RunningCheck;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ForeGround implements Application.ActivityLifecycleCallbacks {

    private static ForeGround instance;

    private AppStatus mAppStatus = AppStatus.RETURNED_TO_FOREGROUND;

    private int running = 0;

    private ForeGround() {

    }

    public static void init(Application app) {
        if (instance == null)
            instance = new ForeGround();
        app.registerActivityLifecycleCallbacks(instance);
    }

    public static ForeGround get() {
        return instance;
    }


    public AppStatus getmAppStatus() {
        return mAppStatus;
    }

    public boolean isBackGround() {
        return mAppStatus.ordinal() == mAppStatus.BACKGROUND.ordinal();
    }

    public enum AppStatus {
        BACKGROUND, // app is background
        RETURNED_TO_FOREGROUND, // app returned to foreground(or first launch)
        FOREGROUND; // app is foreground
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++running == 1)
            mAppStatus = AppStatus.RETURNED_TO_FOREGROUND;
        else if (running > 1)
            mAppStatus = AppStatus.FOREGROUND;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--running == 0)
            mAppStatus = AppStatus.BACKGROUND;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
