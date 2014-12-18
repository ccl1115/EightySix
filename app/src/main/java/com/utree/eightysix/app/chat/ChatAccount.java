package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.*;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.utils.DaoUtils;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class ChatAccount {

  private static final int MSG_LOGIN_SUCCESS = 0x1;
  private static final int MSG_LOGIN_ERROR = 0x2;
  private static final int MSG_LOGIN_PROGRESS = 0x3;
  private static final int MSG_RECEIVE_MSG = 0x4;
  private static ChatAccount sChatAccount;
  private NewMessageBroadcastReceiver mNewMessageBroadcastReceiver;
  private Sender mSender;
  private boolean mIsLogin;

  private ChatAccount() {
    mSender = new SenderImpl();
  }

  public static ChatAccount inst() {
    if (sChatAccount == null) {
      sChatAccount = new ChatAccount();
      sChatAccount.login();
    }
    return sChatAccount;
  }

  public Sender getSender() {
    return mSender;
  }

  public void login() {
    if (Account.inst().isLogin()) {
      EMChatManager.getInstance().login(Account.inst().getUserId(), Account.inst().getToken(),
          new EMCallBack() {

            @Override
            public void onSuccess() {
              mIsLogin = true;

              BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                  U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_LOGIN_SUC, "登录成功"));
                }
              });

              mNewMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
              U.getContext().registerReceiver(mNewMessageBroadcastReceiver,
                  new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction()));

              BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                  if (BuildConfig.DEBUG) U.showToast("聊天服务器登录成功");
                }
              });
            }

            @Override
            public void onError(final int i, final String s) {
              mIsLogin = false;
              BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                  if (BuildConfig.DEBUG) U.showToast("聊天服务器登录失败:" + s);
                  U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_LOGIN_ERR, String.format("登录失败：%s(%d)", s, i)));
                }
              });
            }

            @Override
            public void onProgress(final int i, final String s) {
              BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                  U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_LOGIN_PROGRESS, String.format("%s(%d)", s, i)));
                }
              });
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

  private class NewMessageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      EMMessage message = EMChatManager.getInstance().getMessage(intent.getStringExtra("msgid"));

      Log.d(C.TAG.CH, "onReceiveMessage");
      Log.d(C.TAG.CH, message.toString());

      Message m = ChatUtils.convert(message);


      if (m != null) {
        if (m.getCommentId() == null) {
          ChatUtils.ConversationUtil.createByPostIdIfNotExist(m.getChatId(), m.getPostId());
        } else {
          ChatUtils.ConversationUtil.createByPostCommentIdIfNotExist(m.getChatId(), m.getPostId(), m.getCommentId());
        }

        DaoUtils.getMessageDao().insert(m);
        ChatUtils.NotifyUtil.notifyNewMessage(m);
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, m));
      }
    }
  }
}
