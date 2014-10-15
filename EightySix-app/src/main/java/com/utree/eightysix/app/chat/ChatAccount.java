package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatStatusEvent;
import org.jivesoftware.smack.Chat;

/**
 * @author simon
 */
public class ChatAccount {

  private static final int MSG_LOGIN_SUCCESS = 0x1;
  private static final int MSG_LOGIN_ERROR = 0x2;
  private static final int MSG_LOGIN_PROGRESS = 0x3;
  private static final int MSG_RECEIVE_MSG = 0x4;
  private static ChatAccount sChatAccount;
  private static ChatEventHandler sChatEventHandler;
  private NewMessageBroadcastReceiver mNewMessageBroadcastReceiver;
  private Sender mSender;
  private boolean mIsLogin;

  private ChatAccount() {
    mSender = new SenderImpl();
  }

  public static ChatAccount inst() {
    if (sChatAccount == null) {
      sChatAccount = new ChatAccount();
      sChatEventHandler = new ChatEventHandler();
      sChatAccount.login();
    }
    return sChatAccount;
  }

  public Sender getSender() {
    return mSender;
  }

  public void login() {
    if (Account.inst().isLogin()) {
      EMChatManager.getInstance().login(Account.inst().getUserId(), Account.inst().getUserId(),
          new EMCallBack() {

            @Override
            public void onSuccess() {
              mIsLogin = true;
              sChatEventHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);

              mNewMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
              U.getContext().registerReceiver(mNewMessageBroadcastReceiver,
                  new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction()));
            }

            @Override
            public void onError(int i, String s) {
              mIsLogin = false;
              Message m = sChatEventHandler.obtainMessage(MSG_LOGIN_ERROR);
              m.obj = String.format("登录失败：%s(%d)", s, i);
              m.sendToTarget();
            }

            @Override
            public void onProgress(int i, String s) {
              Message m = sChatEventHandler.obtainMessage(MSG_LOGIN_PROGRESS);
              m.obj = String.format("%s(%d)", s, i);
              m.sendToTarget();
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

  private static class ChatEventHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_LOGIN_ERROR:
          break;
        case MSG_LOGIN_PROGRESS:
          break;
        case MSG_LOGIN_SUCCESS:
          U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_LOGIN_SUC, "登录成功"));
          break;
        case MSG_RECEIVE_MSG:
          U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_RECEIVE_MSG, msg.obj));
          break;
      }
    }
  }

  private class NewMessageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      EMMessage message = EMChatManager.getInstance().getMessage(intent.getStringExtra("msgid"));

      Message m = sChatEventHandler.obtainMessage(MSG_RECEIVE_MSG, message);
      m.sendToTarget();
    }
  }
}
