package com.utree.eightysix.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.app.msg.FetchNotificationService;
import com.utree.eightysix.utils.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author simon
 */
public class FetchAlarmReceiver extends BroadcastReceiver {

  private static final String TAG = "FetchAlarmReceiver";
  private static final String ACTION = "com.utree.eightysix.action.FETCH";
  private static final int INTERVAL = 3600000;

  public static void setupAlarm(Context context) {
    Log.d(TAG, "setupAlarm");
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, INTERVAL,
        PendingIntent.getBroadcast(context, 0, new Intent(ACTION), PendingIntent.FLAG_UPDATE_CURRENT));

    if (BuildConfig.DEBUG) {
      File f = IOUtils.createTmpFile("fetch_log");

      FileWriter wf = null;
      try {
        wf = new FileWriter(f, true);
        wf.write('\n');
        wf.write("setup alarm at " + new Date().toString());
        wf.write('\n');
        wf.flush();
      } catch (IOException e) {
        e.printStackTrace();
        if (wf != null) {
          try {
            wf.close();
          } catch (IOException ignored) {
          }
        }
      }
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (ACTION.equals(intent.getAction())) {
      Log.d(TAG, "onReceive");
      context.startService(new Intent(context, FetchNotificationService.class));

      if (BuildConfig.DEBUG) {
        File f = IOUtils.createTmpFile("fetch_log");

        FileWriter wf = null;
        try {
          wf = new FileWriter(f, true);
          wf.write('\n');
          wf.write("start fetch service at " + new Date().toString());
          wf.write('\n');
          wf.flush();
        } catch (IOException e) {
          e.printStackTrace();
          if (wf != null) {
            try {
              wf.close();
            } catch (IOException ignored) {
            }
          }
        }
      }
    }
  }
}
