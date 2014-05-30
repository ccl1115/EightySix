package com.utree.eightysix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.utree.eightysix.C;
import com.utree.eightysix.U;

/**
 */
public class Env {

    public static boolean firstRun() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getBoolean("first_run_" + C.VERSION, true);
    }

    public static void setFirstRun(boolean firstRun) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("first_run_" + C.VERSION, firstRun).apply();
    }

    public static boolean isPatternLocked() {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        return preferences.getBoolean("pattern_locked", true);
    }

    public static void setPatternLock(boolean lock) {
        SharedPreferences preferences = U.getContext().getSharedPreferences("env", Context.MODE_PRIVATE);
        preferences.edit().putBoolean("pattern_locked", lock).apply();
    }
}
