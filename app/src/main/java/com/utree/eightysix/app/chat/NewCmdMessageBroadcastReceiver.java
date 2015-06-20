/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.chat.EMMessage;
import com.utree.eightysix.C;
import de.akquinet.android.androlog.Log;

/**
 */
public class NewCmdMessageBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    abortBroadcast();


    EMMessage message = intent.getParcelableExtra("message");

    if (message == null) {
      return;
    }

    Log.d(C.TAG.CH, "onReceiveMessage");
    Log.d(C.TAG.CH, message.toString());

    if (message.getType() == EMMessage.Type.CMD) {
      ChatAccount.inst().getMessageCmdHandler().handle(message);
    }

  }
}
