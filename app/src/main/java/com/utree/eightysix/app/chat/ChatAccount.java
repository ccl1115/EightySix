package com.utree.eightysix.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import com.easemob.EMCallBack;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatDB;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
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
      EMChatManager.getInstance().getChatOptions().setUseEncryption(false);
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
        new NewMessageWorker(m, message).execute();
      }
    }
  }

  private class NewMessageWorker extends AsyncTask<Void, Integer, Void> {


    private static final int PROGRESS_NOTIFY = 1;
    private static final int PROGRESS_INSERT_MESSAGE = 5;
    private static final int PROGRESS_UNREAD_CONVERSATION_COUNT = 2;
    private static final int PROGRESS_UPDATE_CONVERSATION = 3;
    private static final int PROGRESS_INFO_MESSAGE = 6;
    private static final int PROGRESS_MESSAGE_DOWNLOADED = 7;

    private Message mMessage;
    private EMMessage mEmMessage;
    private Message mInfoMessage;


    private long mUnreadConversationCount;
    private Post mPost;
    private Comment mComment;
    private Conversation mConversation;

    public NewMessageWorker(Message message, EMMessage emMessage) {
      mMessage = message;
      mEmMessage = emMessage;
    }

    @Override
    protected Void doInBackground(Void... voids) {

      if (mMessage.getCommentId() == null) {
        ChatUtils.ConversationUtil.createByPostIdIfNotExist(mMessage.getChatId(), mMessage.getPostId(),
            new ParamsRunnable() {
              @Override
              public void run(Object... params) {
                mPost = ((Post) params[0]);
                mInfoMessage = ChatUtils.MessageUtil.addPostSummaryInfo(mMessage.getChatId(), mMessage.getTimestamp() - 1, mPost);
                if (mInfoMessage != null) {
                  publishProgress(PROGRESS_INFO_MESSAGE);
                }
              }
            });
      } else {
        ChatUtils.ConversationUtil.createByPostCommentIdIfNotExist(mMessage.getChatId(), mMessage.getPostId(), mMessage.getCommentId(), mMessage.getCommentContent(),
            new ParamsRunnable() {
              @Override
              public void run(Object... params) {
                mPost = ((Post) params[0]);
                mComment = ((Comment) params[1]);
                mInfoMessage = ChatUtils.MessageUtil.addCommentSummaryInfo(mMessage.getChatId(), mMessage.getTimestamp() - 1, mPost, mComment);
                if (mInfoMessage != null) {
                  publishProgress(PROGRESS_INFO_MESSAGE);
                }
              }
            });
      }

      boolean foreground = mMessage.getChatId().equals(ChatActivity.getCurrentChatId());
      if (foreground) {
        // 收到的消息，对应的聊天页面在前台，则不通知该条消息
        mMessage.setRead(true);
      } else {
        publishProgress(PROGRESS_NOTIFY);
      }

      mMessage.setTimestamp(System.currentTimeMillis());
      DaoUtils.getMessageDao().insert(mMessage);
      publishProgress(PROGRESS_INSERT_MESSAGE);

      mConversation = ChatUtils.ConversationUtil.setLastMessage(mMessage);
      publishProgress(PROGRESS_UPDATE_CONVERSATION);

      if (!foreground) {
        // 收到的消息，对应的聊天页面不在前台，则更新对话未读数

        mUnreadConversationCount = ChatUtils.ConversationUtil.getUnreadConversationCount();
        publishProgress(PROGRESS_UNREAD_CONVERSATION_COUNT);

        mConversation = ChatUtils.ConversationUtil.updateUnreadCount(mMessage.getChatId());
        publishProgress(PROGRESS_UPDATE_CONVERSATION);
      }

      if (mMessage.getType() == MessageConst.TYPE_IMAGE) {
        EMChatManager.getInstance().asyncFetchMessage(mEmMessage);
        ((ImageMessageBody) mEmMessage.getBody()).setDownloadCallback(new EMCallBack() {
          @Override
          public void onSuccess() {
            mMessage.setStatus(MessageConst.STATUS_SUCCESS);
            publishProgress(PROGRESS_MESSAGE_DOWNLOADED);
          }

          @Override
          public void onError(int i, String s) {

          }

          @Override
          public void onProgress(int i, String s) {

          }
        });
      }

      return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      switch (values[0]) {
        case PROGRESS_NOTIFY:
          ChatUtils.NotifyUtil.notifyNewMessage(mMessage, mPost, mComment);
          break;
        case PROGRESS_UNREAD_CONVERSATION_COUNT:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
          break;
        case PROGRESS_UPDATE_CONVERSATION:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, mConversation));
          break;
        case PROGRESS_INSERT_MESSAGE:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mMessage));
          break;
        case PROGRESS_INFO_MESSAGE:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mInfoMessage));
          break;
        case PROGRESS_MESSAGE_DOWNLOADED:
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mMessage));
          break;
      }
    }
  }
}
