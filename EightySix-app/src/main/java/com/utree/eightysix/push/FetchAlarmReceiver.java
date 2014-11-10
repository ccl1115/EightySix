package com.utree.eightysix.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, INTERVAL,
        PendingIntent.getBroadcast(context, 0, new Intent(ACTION), PendingIntent.FLAG_UPDATE_CURRENT));

    File f = IOUtils.createTmpFile("fetch_log");

    FileWriter wf = null;
    try {
      wf = new FileWriter(f, true);
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

  public static void stopAlarm(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(PendingIntent.getBroadcast(context, 0, new Intent(ACTION), PendingIntent.FLAG_CANCEL_CURRENT));
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (ACTION.equals(intent.getAction())) {
      FetchNotificationService.start(context, true, false);
    }
  }
}
