package com.utree.eightysix.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.tencent.android.tpush.*;
import com.utree.eightysix.*;
import com.utree.eightysix.app.msg.NotifyUtil;
import com.utree.eightysix.app.msg.PullNotificationService;
import com.utree.eightysix.data.AppIntent;
import com.utree.eightysix.utils.CmdHandler;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import de.akquinet.android.androlog.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 */
public final class PushMessageReceiver extends XGPushBaseReceiver {
  /**
   * 新帖子
   */
  public static final int TYPE_NEW_POST = 1;
  /**
   * 圈子解锁
   */
  public static final int TYPE_UNLOCK_CIRCLE = 2;
  /**
   * 新朋友加入
   */
  public static final int TYPE_FRIEND_L1_JOIN = 3;
  /**
   * 关注的帖子被评论
   */
  public static final int TYPE_FOLLOW_COMMENT = 4;
  /**
   * 被赞
   */
  public static final int TYPE_PRAISE = 5;
  /**
   * 圈子创建审核通过
   */
  public static final int TYPE_CIRCLE_CREATION_APPROVE = 6;
  /**
   * 自己发布的帖子被评论
   */
  public static final int TYPE_OWN_COMMENT = 7;

  /**
   * 合并的新帖子
   */
  public static final int TYPE_MERGED_NEW_POST = 8;

  /**
   * 举报提示
   */
  public static final int TYPE_REPORT = 9;

  /**
   * 蓝星提示
   */
  public static final int TYPE_BLUE_STAR = 10;

  public static final int TYPE_CMD = 1000;

  public static final int TYPE_PUSH_TEXT = 1002;

  public CmdHandler mCmdHandler = new CmdHandler();
  private NotifyUtil mNotifyUtil;

  private static Bitmap sLargeIcon;

  static {
    sLargeIcon = BitmapFactory.decodeResource(U.getContext().getResources(), R.drawable.ic_launcher);
  }

  @Override
  public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
    if (i == 0) {
      Env.setPushChannelId("100");
      Env.setPushUserId(xgPushRegisterResult.getToken());
    } else if (BuildConfig.DEBUG) {
      Log.d(C.TAG.PSH, "Xg register failed return code: " + i);
    }
  }

  @Override
  public void onUnregisterResult(Context context, int i) {

  }

  @Override
  public void onSetTagResult(Context context, int i, String s) {

  }

  @Override
  public void onDeleteTagResult(Context context, int i, String s) {

  }

  @Override
  public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
    AppIntent m;
    try {
      m = U.getGson().fromJson(xgPushTextMessage.getContent(), AppIntent.class);
    } catch (Exception e) {
      return;
    }

    if (BuildConfig.DEBUG) {
      Log.d("PushService", "message = " + xgPushTextMessage.getContent());

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder.setContentTitle("PushService Debug");
      builder.setContentText(xgPushTextMessage.getContent());
      builder.setTicker("PushService Debug");
      builder.setSmallIcon(R.drawable.ic_app_icon);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivity(context, 0, null, 0));
      }
      ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0x100, builder.build());

      Date date = new Date();

      File file = IOUtils.createTmpFile("push_message_log_" + DateFormat.getDateInstance(DateFormat.SHORT).format(date));

      FileWriter os = null;
      try {
        os = new FileWriter(file, true);
        os.write("\n\n");
        os.write(String.format("[%s]", DateFormat.getInstance().format(date)));
        os.write("\nuserId = " + Account.inst().getUserId());
        os.write("\nmessage = " + xgPushTextMessage.getContent());
        os.write("\ntype = " + m.type);
        os.write("\npushFlag = " + m.pushFlag);
        os.write("\n");
        os.flush();
      } catch (FileNotFoundException ignored) {
      } catch (IOException ignored) {
      } finally {
        if (os != null) {
          try {
            os.close();
          } catch (IOException ignored) {
          }
        }
      }
    }

    if (m.type == PushMessageReceiver.TYPE_REPORT) {
      NotificationManager manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
      if (mNotifyUtil == null) {
        mNotifyUtil = new NotifyUtil(context);
      }
      manager.notify(0x2000, mNotifyUtil.buildReport());
    } else if (m.type == TYPE_PUSH_TEXT) {
      NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder.setAutoCancel(true)
          .setTicker(m.title)
          .setContentTitle(m.title)
          .setContentText(m.content)
          .setDefaults(Account.inst().getSilentMode() ?
              Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
          .setLargeIcon(sLargeIcon)
          .setSmallIcon(R.drawable.ic_launcher);

      builder.setContentIntent(PendingIntent.getActivity(context, 0, PushTextHandleActivity.getIntent(context, m),
          PendingIntent.FLAG_UPDATE_CURRENT));

      manager.notify(m.cmd, 0x3000, builder.build());
    } else {
      PullNotificationService.start(context, m.type, m.pushFlag);
    }
  }

  @Override
  public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
  }

  @Override
  public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
    if (!Account.inst().isLogin()) {
      ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
  }


}
