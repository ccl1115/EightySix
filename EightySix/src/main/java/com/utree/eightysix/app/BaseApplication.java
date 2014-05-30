package com.utree.eightysix.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import com.tencent.stat.StatConfig;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;
import java.io.IOException;
import java.util.Properties;

/**
 */
public class BaseApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        initIMEI();

        Log.init(this);
        Log.activateLogging();
        Log.setDefaultLogLevel(Constants.VERBOSE);
    }

    private void initIMEI() {
        SharedPreferences preferences = getSharedPreferences("application", MODE_PRIVATE);
        String imei = preferences.getString("deviceId", null);
        if (imei == null) {
            TelephonyManager t = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = t.getDeviceId();
            preferences.edit().putString("deviceId", imei).commit();
        }

        C.IMEI = imei;
    }

    public static Context getContext() {
        return sContext;
    }

}
