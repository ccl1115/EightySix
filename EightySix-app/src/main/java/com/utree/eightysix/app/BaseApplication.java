package com.utree.eightysix.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.tencent.cloudsdk.tsocket.GlobalContext;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.ChatAccount;
import com.utree.eightysix.push.FetchAlarmReceiver;
import de.akquinet.android.androlog.Constants;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 */
public class BaseApplication extends Application {

  private static Context sContext;

  private static Handler sHandler;

  public static Context getContext() {
    return sContext;
  }

  public static Handler getHandler() {
    return sHandler;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sContext = this;
    sHandler = new Handler();

    try {
      C.VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
    } catch (Throwable e) {
      C.VERSION = -1;
    }

    if (isMainProcess()) {
      Log.init(this);
      Log.activateLogging();
      Log.setDefaultLogLevel(Constants.VERBOSE);

      U.getReporter().init();
      U.getSyncClient().getSync();

      // 定时拉去消息服务
      FetchAlarmReceiver.setupAlarm(this);

      U.getPushHelper().startWork();

      // 腾讯移动加速初始化
      GlobalContext.initialize(this);

      // 环信聊天初始化
      EMChat.getInstance().init(this);
      ChatAccount.inst();
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
