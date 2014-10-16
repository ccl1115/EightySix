package com.utree.eightysix.app.chat;

import android.os.Handler;
import android.os.Message;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatStatusEvent;

import java.io.File;
import java.io.InputStream;

/**
 * @author simon
 */
public class SenderImpl implements Sender {

  private static final int MSG_SENDING = 1;
  private static final int MSG_SENT_SUCCESS = 2;
  private static final int MSG_SENT_ERROR = 3;

  private static class SenderHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_SENDING:
          U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_SENDING_MSG, msg.obj));
          break;
        case MSG_SENT_ERROR:
          U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_SENT_MSG_ERROR, msg.obj));
          break;
        case MSG_SENT_SUCCESS:
          U.getChatBus().post(new ChatStatusEvent(ChatStatusEvent.EVENT_SENT_MSG_SUCCESS, msg.obj));
          break;
      }
    }
  }

  private static SenderHandler sSenderHandler = new SenderHandler();

  @Override
  public void txt(String username, String txt) {

    final EMMessage m = EMMessage.createSendMessage(EMMessage.Type.TXT);

    m.setReceipt(username);

    TextMessageBody body = new TextMessageBody(txt);
    m.addBody(body);

    EMConversation conversation = EMChatManager.getInstance().getConversation(username);
    conversation.addMessage(m);

    Message message = sSenderHandler.obtainMessage(MSG_SENDING, m);
    message.sendToTarget();

    EMChatManager.getInstance().sendMessage(m, new EMCallBack() {
      @Override
      public void onSuccess() {
        Message message = sSenderHandler.obtainMessage(MSG_SENT_SUCCESS, m);
        message.sendToTarget();
      }

      @Override
      public void onError(int i, String s) {
        Message message = sSenderHandler.obtainMessage(MSG_SENT_ERROR, m);
        message.sendToTarget();
      }

      @Override
      public void onProgress(int i, String s) {
      }
    });
  }

  @Override
  public void voice(String username, File f) {

  }

  @Override
  public void voice(String username, InputStream is) {

  }

  @Override
  public void photo(String username, File f) {

  }

  @Override
  public void photo(String username, InputStream is) {

  }
}
