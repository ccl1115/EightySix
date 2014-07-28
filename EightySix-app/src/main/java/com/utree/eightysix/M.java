package com.utree.eightysix;

import android.os.Looper;
import com.utree.eightysix.location.BdLocationImpl;
import com.utree.eightysix.location.Location;
import com.utree.eightysix.utils.RegisterHelper;

/**
 * To be moved to base
 * @author simon
 */
public class M {
  static Location sLocation;

  static RegisterHelper sRegisterHelper;

  public static Location getLocation() {
    checkThread();
    if (sLocation == null) {
      sLocation = new BdLocationImpl(U.getContext());
    }
    return sLocation;
  }

  public static RegisterHelper getRegisterHelper() {
    checkThread();
    if (sRegisterHelper == null) {
      sRegisterHelper = new RegisterHelper();
    }
    return sRegisterHelper;
  }

  public static void checkThread() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new IllegalThreadStateException("This static method in U class must be invoked in main thread");
    }
  }
}
