package com.utree.eightysix.app.chat;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.utree.eightysix.Account;

/**
 * @author simon
 */
public class ChatAccount {

  private boolean mIsLogin;

  public void login() {
    if (Account.inst().isLogin()) {
      EMChatManager.getInstance().login(Account.inst().getUserId(), Account.inst().getUserId(),
          new EMCallBack() {

            @Override
            public void onSuccess() {
              mIsLogin = true;
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
          });
    }
  }

  public boolean isLogin() {
    return mIsLogin;
  }
}
