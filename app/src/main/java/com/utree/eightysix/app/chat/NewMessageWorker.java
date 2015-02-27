package com.utree.eightysix.app.chat;

import android.os.AsyncTask;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;
import de.akquinet.android.androlog.Log;

/**
 */
class NewMessageWorker extends AsyncTask<Void, Integer, Void> {


  private static final int PROGRESS_NOTIFY = 1;
  private static final int PROGRESS_INSERT_MESSAGE = 5;
  private static final int PROGRESS_UNREAD_CONVERSATION_COUNT = 2;
  private static final int PROGRESS_UPDATE_CONVERSATION = 3;
  private static final int PROGRESS_INFO_MESSAGE = 6;
  private static final int PROGRESS_MESSAGE_DOWNLOADED = 7;
  private static final int PROGRESS_MESSAGE_ACK = 8;

  private Message mMessage;
  private EMMessage mEmMessage;
  private Message mInfoMessage;


  private long mUnreadConversationCount;
  private Conversation mConversation;

  public NewMessageWorker(Message message, EMMessage emMessage) {
    mMessage = message;
    mEmMessage = emMessage;
  }

  @Override
  protected Void doInBackground(Void... voids) {

    try {
      ChatUtils.ConversationUtil.createOrUpdateConversation(mEmMessage);
    } catch (EaseMobException e) {
      Log.d(C.TAG.CH, e.toString());
      return null;
    }

    if (mMessage.getCommentId() == null) {
      mInfoMessage = ChatUtils.MessageUtil.addPostSummaryInfo(mMessage.getChatId(),
          mMessage.getTimestamp() - 1,
          mEmMessage.getStringAttribute("postId", null),
          mEmMessage.getStringAttribute("postContent", null));
      if (mInfoMessage != null) {
        publishProgress(PROGRESS_INFO_MESSAGE);
      }
    } else {
      mInfoMessage = ChatUtils.MessageUtil.addCommentSummaryInfo(mMessage.getChatId(),
          mMessage.getTimestamp() - 1,
          mEmMessage.getStringAttribute("postId", null),
          mEmMessage.getStringAttribute("postContent", null),
          mEmMessage.getStringAttribute("commentId", null),
          mEmMessage.getStringAttribute("commentContent", null));
      if (mInfoMessage != null) {
        publishProgress(PROGRESS_INFO_MESSAGE);
      }
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
      mConversation = ChatUtils.ConversationUtil.updateUnreadCount(mMessage.getChatId());
      publishProgress(PROGRESS_UPDATE_CONVERSATION);

      mUnreadConversationCount = ChatUtils.ConversationUtil.getUnreadConversationCount();
      publishProgress(PROGRESS_UNREAD_CONVERSATION_COUNT);
    }

    if (mMessage.getType() == MessageConst.TYPE_IMAGE) {
      EMChatManager.getInstance().asyncFetchMessage(mEmMessage);
      ((ImageMessageBody) mEmMessage.getBody()).setDownloadCallback(new EMCallBack() {
        @Override
        public void onSuccess() {
          mMessage.setStatus(MessageConst.STATUS_SUCCESS);
          DaoUtils.getMessageDao().update(mMessage);
          BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
              U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mMessage));
            }
          });
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onProgress(int i, String s) {

        }
      });
    }

    publishProgress(PROGRESS_MESSAGE_ACK);

    return null;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    switch (values[0]) {
      case PROGRESS_NOTIFY:
        ChatUtils.NotifyUtil.notifyNewMessage(mMessage);
        break;
      case PROGRESS_UNREAD_CONVERSATION_COUNT:
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT, mUnreadConversationCount));
        break;
      case PROGRESS_UPDATE_CONVERSATION:
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_INSERT_OR_UPDATE, mConversation));
        break;
      case PROGRESS_INSERT_MESSAGE:
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mMessage));
        break;
      case PROGRESS_INFO_MESSAGE:
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_RECEIVE_MSG, mInfoMessage));
        break;
      case PROGRESS_MESSAGE_DOWNLOADED:
        break;
      case PROGRESS_MESSAGE_ACK:
        U.request("chat_ack", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {

          }
        }, Response.class, new String[]{mEmMessage.getMsgId()});
        break;
    }
  }
}
