package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.*;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.ParamsRunnable;
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
      // 不使用环信默认的通知提醒
      EMChatManager.getInstance().getChatOptions().setNotificationEnable(false);
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

      final Message m = ChatUtils.convert(message);

      if (m != null) {
        m.setStatus(MessageConst.STATUS_SUCCESS);
        new NewMessageWorker(m).execute();
      }
    }
  }

  private class NewMessageWorker extends AsyncTask<Void, Integer, Void> {


    private Message mMessage;

    private long mUnreadConversationCount;
    private Post mPost;
    private Comment mComment;
    private Conversation mConversation;

    public NewMessageWorker(Message message) {
      mMessage = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {

      if (mMessage.getCommentId() == null) {
        ChatUtils.ConversationUtil.createByPostIdIfNotExist(mMessage.getChatId(), mMessage.getPostId(),
            new ParamsRunnable() {
              @Override
              public void run(Object... params) {
                DaoUtils.getMessageDao().insert(mMessage);
                mPost = ((Post) params[0]);
              }
            });
      } else {
        ChatUtils.ConversationUtil.createByPostCommentIdIfNotExist(mMessage.getChatId(), mMessage.getPostId(), mMessage.getCommentId(),
            new ParamsRunnable() {
              @Override
              public void run(Object... params) {
                DaoUtils.getMessageDao().insert(mMessage);
                mPost = ((Post) params[0]);
                mComment = ((Comment) params[1]);
              }
            });
      }
      publishProgress(1);

      mUnreadConversationCount = ChatUtils.ConversationUtil.getUnreadConversationCount();
      publishProgress(2);

      mConversation = ChatUtils.ConversationUtil.setLastMessage(mMessage);
      publishProgress(3);

      mConversation = ChatUtils.ConversationUtil.updateUnreadCount(mMessage.getChatId());
      publishProgress(4);

      return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      switch (values[0]) {
        case 1:
          ChatUtils.NotifyUtil.notifyNewMessage(mMessage, mPost, mComment);
          break;
        case 2:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
          break;
        case 3:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
          break;
        case 4:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
          break;
      }
    }
  }
}
