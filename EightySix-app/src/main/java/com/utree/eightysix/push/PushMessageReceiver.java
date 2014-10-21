package com.utree.eightysix.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.feed.PostActivity;
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
import java.util.List;

/**
 */
public final class PushMessageReceiver extends FrontiaPushMessageReceiver {
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
  public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId) {

    if (errorCode == 0) {
      Env.setPushChannelId(channelId);
      Env.setPushUserId(userId);
    }
  }

  @Override
  public void onUnbind(Context context, int errorCode, String requestId) {
  }

  @Override
  public void onSetTags(Context context, int i, List<String> strings, List<String> strings2, String s) {

  }

  @Override
  public void onDelTags(Context context, int i, List<String> strings, List<String> strings2, String s) {

  }

  @Override
  public void onListTags(Context context, int i, List<String> strings, String s) {

  }

  @Override
  public void onMessage(Context context, String message, String customContentString) {
    Message m;
    try {
      m = U.getGson().fromJson(message, Message.class);
    } catch (Exception e) {
      return;
    }

    if (BuildConfig.DEBUG) {
      Log.d("PushService", "message = " + message);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder.setContentTitle("PushService Debug");
      builder.setContentText(message);
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
        os.write("\nmessage = " + message);
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
  public void onNotificationClicked(Context context, String s, String s2, String s3) {
    try {
      Message m = U.getGson().fromJson(s3, Message.class);
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
