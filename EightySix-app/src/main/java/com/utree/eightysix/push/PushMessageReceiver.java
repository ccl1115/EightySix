package com.utree.eightysix.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
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
  @Override
  public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId) {

    if (errorCode == 0) {
      Log.v("PushService", "channelId = " + channelId);
      Log.v("PushService", "   userId = " + userId);
      Env.setPushChannelId(channelId);
      Env.setPushUserId(userId);
    } else {
      U.getAnalyser().reportError(context, "bind push service failed : " + errorCode);
    }
  }

  @Override
  public void onUnbind(Context context, int errorCode, String requestId) {
    if (errorCode != 0) {
      U.getAnalyser().reportError(context, "unbind push service failed");
    }
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
      builder.setContentIntent(PendingIntent.getActivity(context, 0, null, 0));
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

  }


  static class Message {

    @SerializedName("type")
    int type;

    @SerializedName("pushFlag")
    String pushFlag;
  }

}
