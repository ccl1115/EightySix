package com.utree.eightysix.app;

import android.content.Context;
import com.baidu.frontia.FrontiaApplication;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;
import java.util.Calendar;

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

    Log.init(this);
    Log.activateLogging();
    Log.setDefaultLogLevel(Constants.VERBOSE);


    long last = Env.getTimestamp("last_location");

    Calendar lastCal = Calendar.getInstance();
    lastCal.setTimeInMillis(last);
    if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != lastCal.get(Calendar.DAY_OF_YEAR)) {
      U.getLocation().requestLocation();
      Env.setTimestamp("last_location");
    }
  }

}
