package com.utree.eightysix.app;

import android.app.Application;
import android.content.Context;
import com.tencent.stat.StatConfig;
import com.utree.eightysix.BuildConfig;

/**
 */
public class BaseApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        StatConfig.setDebugEnable(BuildConfig.DEBUG);
    }

    public static Context getContext() {
        return sContext;
    }
}
