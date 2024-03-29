package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.utree.eightysix.C;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.dao.Message;
import de.akquinet.android.androlog.Log;

/**
*/
public class NewMessageBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    abortBroadcast();

    EMMessage message = EMChatManager.getInstance().getMessage(intent.getStringExtra("msgid"));

    Log.d(C.TAG.CH, "onReceiveMessage");
    Log.d(C.TAG.CH, message.toString());

    String chatType = message.getStringAttribute("chatType", null);

    if ("friend".equals(chatType) || "assistant".equals(chatType)) {
      final FriendMessage fm = ChatUtils.toFriendMessage(message);
      if (fm != null) {
        new NewFriendMessageWorker(fm, message).execute();
      }
    } else if ("whisper".equals(chatType)) {
      final Message m = ChatUtils.toMessage(message);
      if (m != null) {
        new NewMessageWorker(m, message).execute();
      }
    }
  }
}
