package com.utree.eightysix.push;

import android.content.Context;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.app.msg.PullNotificationService;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.IOUtils;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.math.ode.jacobians.FirstOrderIntegratorWithJacobians;

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
    Message m = U.getGson().fromJson(message, Message.class);

    if (BuildConfig.DEBUG) {
      Log.d("PushService", "            message = " + message);
      Log.d("PushService", "customContentString = " + customContentString);

      File file = IOUtils.createTmpFile("push_message_log");

      FileWriter os = null;
      try {
        os = new FileWriter(file, true);
        os.write("\n\n");
        os.write(Calendar.getInstance().toString());
        os.write("\n\n");
        os.write("userId = " + Account.inst().getUserId());
        os.write("message = " + message);
        os.write("type = " + m.type);
        os.write("pushSeq = " + m.pushSeq);
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

    PullNotificationService.start(context, m.type, m.pushSeq);
  }

  @Override
  public void onNotificationClicked(Context context, String s, String s2, String s3) {

  }


  static class Message {

    @SerializedName("type")
    int type;

    @SerializedName("pushSeq")
    String pushSeq;
  }

}
