package com.utree.eightysix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import com.utree.eightysix.C;
import com.utree.eightysix.U;

/**
 */
public class Env {

    public static boolean firstRun() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getBoolean("first_run_" + C.VERSION, true);
    }

    public static boolean firstRun(String key) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getBoolean(String.format("first_run_%s_%d", key, C.VERSION), true);
    }

    public static void setFirstRun(boolean firstRun) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("first_run_" + C.VERSION, firstRun).apply();
    }

    public static void setFirstRun(String key, boolean firstRun) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putBoolean(String.format("first_run_%s_%d", key, C.VERSION), firstRun).apply();
    }

    public static boolean isPatternLocked() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getBoolean("pattern_locked", true);
    }

    public static void setPatternLock(boolean lock) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("pattern_locked", lock).apply();
    }

    public static String getPushChannelId() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getString("push_channel_id", null);
    }

    public static void setPushChannelId(String id) {
        if (id == null) return;
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putString("push_channel_id", id).commit();
    }

    public static String getPushUserId() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getString("push_user_id", null);
    }

    public static void setPushUserId(String id) {
        if (id == null) return;
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putString("push_user_id", id).commit();
    }

    public static String getImei() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("application", Context.MODE_PRIVATE);
        String imei = preferences.getString("deviceId", null);
        if (imei == null) {
            TelephonyManager t = (TelephonyManager) U.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = t.getDeviceId();
            preferences.edit().putString("deviceId", imei).commit();
        }

        return imei;
    }
}
