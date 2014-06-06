package com.utree.eightysix.app;

import android.content.Context;
import com.baidu.frontia.FrontiaApplication;
import com.utree.eightysix.push.PushHelper;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;

/**
 */
public class BaseApplication extends FrontiaApplication {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        Log.init(this);
        Log.activateLogging();
        Log.setDefaultLogLevel(Constants.VERBOSE);

        PushHelper.startWork();
    }

    public static Context getContext() {
        return sContext;
    }

}
