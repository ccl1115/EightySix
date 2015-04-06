package com.utree.eightysix.app.chat;

import android.content.IntentFilter;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class ChatAccount {

  private static ChatAccount sChatAccount;
  private NewMessageBroadcastReceiver mNewMessageBroadcastReceiver;

  private Sender mSender;
  private FriendSender mFriendSender;

  private boolean mIsLogin;

  private ChatAccount() {
    mSender = new SenderImpl();
    mFriendSender = new FriendSenderImpl();
  }

  public static ChatAccount inst() {
    if (sChatAccount == null) {
      sChatAccount = new ChatAccount();
      // 不使用环信默认的通知提醒
      EMChatManager.getInstance().getChatOptions().setNotificationEnable(false);
      EMChatManager.getInstance().getChatOptions().setShowNotificationInBackgroud(false);
      EMChat.getInstance().setDebugMode(BuildConfig.DEBUG);
      sChatAccount.login();
    }
    return sChatAccount;
  }

  public Sender getSender() {
    return mSender;
  }

  public FriendSender getFriendSender() {
    return mFriendSender;
  }

  public void login() {
    if (Account.inst().isLogin()) {
      EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
        @Override
        public void onConnected() {
          if (BuildConfig.DEBUG) Log.d("Chat onConnected");
        }

        @Override
        public void onDisconnected(int i) {
          if (BuildConfig.DEBUG) Log.d("Chat onDisconnected code: " + i);
        }
      });

      EMChatManager.getInstance().addConnectionListener(new ConnectionListener() {
        @Override
        public void onConnected() {
          if (BuildConfig.DEBUG) Log.d("Chat onConnected");
        }

        @Override
        public void onDisConnected(String s) {
          if (BuildConfig.DEBUG) Log.d("Chat onDisConnected msg: " + s);
        }

        @Override
        public void onReConnected() {
          if (BuildConfig.DEBUG) Log.d("Chat onReConnected");
        }

        @Override
        public void onReConnecting() {
          if (BuildConfig.DEBUG) Log.d("Chat onReConnecting");
        }

        @Override
        public void onConnecting(String s) {
          if (BuildConfig.DEBUG) Log.d("Chat onConnecting");
        }
      });

      EMChatManager.getInstance().login(Account.inst().getUserId(), Account.inst().getToken(),
          new EMCallBack() {

            @Override
            public void onSuccess() {
              mIsLogin = true;

              BaseApplication.getHandler().post(new Runnable() {
                @Override
                public void run() {
                  U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_LOGIN_SUC, "登录成功"));

                  if (BuildConfig.DEBUG) U.showToast("聊天服务器登录成功");

                  mNewMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
                  IntentFilter filter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
                  filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
                  U.getContext().getApplicationContext().registerReceiver(mNewMessageBroadcastReceiver, filter);

                  EMChat.getInstance().setAppInited();

                  U.request("chat_online", new OnResponse2<Response>() {
                    @Override
                    public void onResponseError(Throwable e) {
                    }

                    @Override
                    public void onResponse(Response response) {
                    }
                  }, Response.class, null, null);
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

    unregisterReceiver();

    M.getRegisterHelper().unregister(this);
  }

  public void unregisterReceiver() {
    if (mNewMessageBroadcastReceiver != null) {
      U.getContext().getApplicationContext().unregisterReceiver(mNewMessageBroadcastReceiver);
    }
  }

}
