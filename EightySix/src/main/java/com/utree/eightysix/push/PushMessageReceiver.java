package com.utree.eightysix.push;

import android.content.Context;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import java.util.List;

/**
 */
public final class PushMessageReceiver extends FrontiaPushMessageReceiver {
    @Override
    public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId) {

        if (errorCode == 0) {
            Env.setPushChannelId(channelId);
            Env.setPushUserId(userId);
        } else {
            U.getAnalyser().reportError(context, "bind push service failed");
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

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s2, String s3) {

    }
}
