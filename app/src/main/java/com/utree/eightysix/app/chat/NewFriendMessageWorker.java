/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.os.AsyncTask;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.C;
import com.utree.eightysix.dao.FriendMessage;
import de.akquinet.android.androlog.Log;

/**
 */
public class NewFriendMessageWorker extends AsyncTask<Void, Void, Void> {

  private final FriendMessage mMessage;
  private final EMMessage mEmMessage;

  public NewFriendMessageWorker(FriendMessage message, EMMessage emMessage) {
    mMessage = message;
    mEmMessage = emMessage;
  }

  @Override
  protected Void doInBackground(Void... params) {
    handleFriend();
    return null;
  }

  private void handleFriend() {
    try {
      FConversationUtil.createOrUpdateFConversation(mEmMessage);
    } catch (EaseMobException e) {
      Log.d(C.TAG.CH, e.toString());
    }
  }

}
