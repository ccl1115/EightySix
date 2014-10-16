package com.utree.eightysix.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.utree.eightysix.app.msg.FetchNotificationService;

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
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (ACTION.equals(intent.getAction())) {
      Log.d(TAG, "onReceive");
      context.startService(new Intent(context, FetchNotificationService.class));
    }
  }
}
