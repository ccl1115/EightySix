package com.utree.eightysix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Circle;

/**
 * Persistent key/value storage back-end by Android SharedPreference
 *
 * @see android.content.SharedPreferences
 */
public class Env {

  /**
   * If the application is first launch
   *
   * @return true if firstly run
   */
  public static boolean firstRun() {
    SharedPreferences preferences = getSharedPreferences();
    return preferences.getBoolean("first_run_" + C.VERSION, true);
  }

  /**
   * If the key is not been set to false
   *
   * @param key the key
   * @return true if not been set
   */
  public static boolean firstRun(String key) {
    return getSharedPreferences().getBoolean(String.format("first_run_%s_%d", key, C.VERSION), true);
  }

  public static void setFirstRun(boolean firstRun) {
    getSharedPreferences().edit().putBoolean("first_run_" + C.VERSION, firstRun).apply();
  }

  public static void setFirstRun(String key, boolean firstRun) {
    getSharedPreferences().edit().putBoolean(String.format("first_run_%s_%d", key, C.VERSION), firstRun).apply();
  }

  @Deprecated
  public static boolean isPatternLocked() {
    return getSharedPreferences().getBoolean("pattern_locked", true);
  }

  @Deprecated
  public static void setPatternLock(boolean lock) {
    getSharedPreferences().edit().putBoolean("pattern_locked", lock).apply();
  }

  public static String getPushChannelId() {
    return getSharedPreferences().getString("push_channel_id", null);
  }

  public static void setPushChannelId(String id) {
    if (id == null) return;
    getSharedPreferences().edit().putString("push_channel_id", id).apply();
  }

  public static String getPushUserId() {
    return getSharedPreferences().getString("push_user_id", null);
  }

  public static void setPushUserId(String id) {
    if (id == null) return;
    getSharedPreferences().edit().putString("push_user_id", id).apply();
  }

  public static String getImei() {
    String imei = getSharedPreferences().getString("deviceId", null);
    if (imei == null) {
      TelephonyManager t = (TelephonyManager) U.getContext().getSystemService(Context.TELEPHONY_SERVICE);
      imei = t.getDeviceId();
      getSharedPreferences().edit().putString("deviceId", imei).apply();
    }

    return imei;
  }

  public static String getLastLatitude() {
    return getSharedPreferences().getString("location_last_latitude", "0");
  }

  public static void setLastLatitude(double lat) {
    getSharedPreferences().edit().putString("location_last_latitude", String.valueOf(lat)).apply();
  }

  public static String getLastLongitude() {
    return getSharedPreferences().getString("location_last_longitude", "0");
  }

  public static void setLastLongitude(double lon) {
    getSharedPreferences().edit().putString("location_last_longitude", String.valueOf(lon)).apply();
  }

  public static void setLastCity(String name) {
    getSharedPreferences().edit().putString("location_last_city", String.valueOf(name)).apply();
  }

  public static String getLastCity() {
    return getSharedPreferences().getString("location_last_city", "");
  }

  public static long getUpgradeCanceledTimestamp() {
    return getSharedPreferences().getLong(String.format("upgrade_canceled_time_%d", C.VERSION), 0);
  }

  public static void setUpgradeCanceledTimestamp() {
    getSharedPreferences().edit().putLong(String.format("upgrade_canceled_time_%d", C.VERSION),
        System.currentTimeMillis()).apply();
  }

  public static void setLastCircle(Circle circle) {
    if (circle == null) return;
    getSharedPreferences().edit().putString(String.format("last_circle_%s", Account.inst().getUserId()),
        U.getGson().toJson(circle)).apply();
  }

  public static Circle getLastCircle() {
    String str = getSharedPreferences().getString(String.format("last_circle_%s", Account.inst().getUserId()), null);
    if (str == null) return null;
    return U.getGson().fromJson(str, Circle.class);
  }

  public static void setTimestamp(String key) {
    getSharedPreferences().edit().putLong(key, System.currentTimeMillis()).apply();
  }

  public static long getTimestamp(String key){
    return getSharedPreferences().getLong(key, System.currentTimeMillis());
  }

  private static SharedPreferences getSharedPreferences() {
    return U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
  }
}
