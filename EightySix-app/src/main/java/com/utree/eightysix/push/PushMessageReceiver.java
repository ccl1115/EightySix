package com.utree.eightysix.push;

import android.content.Context;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.U;
import com.utree.eightysix.app.msg.PullNotificationService;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Log;
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
    Log.d("PushService", "            message = " + message);
    Log.d("PushService", "customContentString = " + customContentString);

    Message m = U.getGson().fromJson(message, Message.class);

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
