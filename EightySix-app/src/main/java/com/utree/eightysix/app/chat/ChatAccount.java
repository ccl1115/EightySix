package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatStatusEvent;

/**
 * @author simon
 */
public class ChatAccount {

  private static ChatAccount sChatAccount;
  private NewMessageBroadcastReceiver mNewMessageBroadcastReceiver;

  public static ChatAccount inst() {
    if (sChatAccount == null) {
      sChatAccount = new ChatAccount();
    }
    return sChatAccount;
  }

  private ChatAccount() {
  }

  private boolean mIsLogin;

  private class NewMessageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      EMMessage message = EMChatManager.getInstance().getMessage(intent.getStringExtra("msgid"));

      U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_RECEIVE_MSG, message));
    }
  }

  public void login() {
    if (Account.inst().isLogin()) {
      EMChatManager.getInstance().login(Account.inst().getUserId(), Account.inst().getUserId(),
          new EMCallBack() {

            @Override
            public void onSuccess() {
              mIsLogin = true;
              U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_LOGIN_SUC, "登录成功"));

              mNewMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
              U.getContext().registerReceiver(mNewMessageBroadcastReceiver,
                  new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction()));
            }

            @Override
            public void onError(int i, String s) {
              mIsLogin = false;
              U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_LOGIN_ERR, String.format("登录失败：%s(%d)", s, i)));
            }

            @Override
            public void onProgress(int i, String s) {
              U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_LOGIN_PROGRESS, String.format("%s(%d)", s, i)));
            }
          });
    }
  }

  public boolean isLogin() {
    return mIsLogin;
  }

  @Subscribe
  public void onLogoutEvent(Account.LogoutEvent event) {
    EMChatManager.getInstance().logout();

    if (mNewMessageBroadcastReceiver != null) {
      U.getContext().unregisterReceiver(mNewMessageBroadcastReceiver);
    }
    M.getRegisterHelper().unregister(this);
  }
}
