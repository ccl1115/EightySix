package com.utree.eightysix.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.gson.annotations.SerializedName;
import com.tencent.android.tpush.*;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.post.PostActivity;
import com.utree.eightysix.app.msg.MsgActivity;
import com.utree.eightysix.app.msg.PraiseActivity;
import com.utree.eightysix.app.msg.PullNotificationService;
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

  public static final int TYPE_CMD = 1000;

  public CmdHandler mCmdHandler = new CmdHandler();

  @Override
  public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
    if (i == 0) {
      Env.setPushChannelId("100");
      Env.setPushUserId(xgPushRegisterResult.getToken());
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
    Message m;
    try {
      m = U.getGson().fromJson(xgPushTextMessage.getContent(), Message.class);
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

    PullNotificationService.start(context, m.type, m.pushFlag);
  }

  @Override
  public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
    try {
      Message m = U.getGson().fromJson(xgPushClickedResult.getCustomContent(), Message.class);
      if (m.type == TYPE_CMD) {
        mCmdHandler.handle(context, m.cmd);
      }
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace();
      } else {
        U.getAnalyser().reportException(context, e);
      }
    }
  }

  @Override
  public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

  }


  static class Message {

    @SerializedName("type")
    int type;

    @SerializedName("pushFlag")
    String pushFlag;

    @SerializedName("cmd")
    String cmd;
  }

  /**
   * feed:id
   *
   * post:id
   *
   * msg
   *
   * praise
   *
   */
  class CmdHandler {
    void handle(Context context, String cmd) {
      String[] args = cmd.split(":");

      if ("feed".equals(args[0])) {
        FeedActivity.start(context, Integer.parseInt(args[1]), true);
      } else if ("post".equals(args[0])) {
        FeedActivity.start(context);
        PostActivity.start(context, args[1]);
      } else if ("msg".equals(args[0])) {
        FeedActivity.start(context);
        MsgActivity.start(context, true);
      } else if ("praise".equals(args[0])) {
        FeedActivity.start(context);
        PraiseActivity.start(context, true);
      }
    }
  }
}
