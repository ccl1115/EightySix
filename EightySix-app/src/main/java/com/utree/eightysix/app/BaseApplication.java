package com.utree.eightysix.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import com.baidu.frontia.FrontiaApplication;
import com.utree.eightysix.C;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;
import java.util.Calendar;
import java.util.List;

/**
 */
public class BaseApplication extends FrontiaApplication {

  private static Context sContext;

  public static Context getContext() {
    return sContext;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sContext = this;

    try {
      C.VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      C.VERSION = -1;
    }

    if (isMainProcess()) {
      Log.init(this);
      Log.activateLogging();
      Log.setDefaultLogLevel(Constants.VERBOSE);

      long last = Env.getTimestamp("last_location");
      Calendar lastCal = Calendar.getInstance();
      lastCal.setTimeInMillis(last);
      if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != lastCal.get(Calendar.DAY_OF_YEAR)) {
        M.getLocation().requestLocation();
        Env.setTimestamp("last_location");
      }

      U.getReporter().init();
      U.getSyncClient().getSync();
    }
  }

  private boolean isMainProcess() {
    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();

    final int pid = android.os.Process.myPid();
    for (ActivityManager.RunningAppProcessInfo info : list) {
      if (pid == info.pid) {
        if (getContext().getPackageName().equals(info.processName)) {
          return true;
        }
      }
    }
    return false;
  }
}
