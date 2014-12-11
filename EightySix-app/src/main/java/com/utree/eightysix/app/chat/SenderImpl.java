/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;

import java.io.File;
import java.io.InputStream;

/**
 */
public class SenderImpl implements Sender {
  @Override
  public void send(final Message message) {

    String type = null;

    switch (message.getType()) {
      case MessageConst.TYPE_TXT:
        type = "txt";
        break;
      case MessageConst.TYPE_IMAGE:
        type = "image";
        break;
    }

    message.setTimestamp(System.currentTimeMillis());
    message.setDirection(MessageConst.DIRECTION_SEND);
    message.setStatus(MessageConst.STATUS_CREATE);
    message.setRead(true);

    U.getRESTRequester().request("chat_send", new OnResponse2<Response>() {
      @Override
      public void onResponseError(Throwable e) {
        message.setStatus(MessageConst.STATUS_FAILED);
        DaoUtils.getMessageDao().update(message);
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENT_MSG_ERROR, message));
      }

      @Override
      public void onResponse(Response response) {
        if (response.code != 0) {
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_MSG_REMOVE, message));
        } else {
          message.setStatus(MessageConst.STATUS_SUCCESS);
          DaoUtils.getMessageDao().update(message);
          U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENT_MSG_SUCCESS, message));
        }
      }
    }, Response.class, message.getChatId(), type, message.getContent(), message.getPostId(), message.getCommentId());

    message.setStatus(MessageConst.STATUS_CREATE);

    DaoUtils.getMessageDao().insertOrReplace(message);
    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENDING_MSG, message));
  }

  @Override
  public Message txt(String chatId, String postId, String commentId, String txt) {
    Message m = new Message();

    m.setChatId(chatId);
    m.setPostId(postId);
    m.setCommentId(commentId);
    m.setContent(txt);
    m.setType(MessageConst.TYPE_TXT);

    send(m);

    return m;
  }

  @Override
  public Message voice(String chatId, String postId, String commentId, File f) {
    return null;
  }

  @Override
  public Message voice(String chatId, String postId, String commentId, InputStream is) {
    return null;
  }

  @Override
  public Message photo(String chatId, String postId, String commentId, File f) {
    return null;
  }

  @Override
  public Message photo(String chatId, String postId, String commentId, InputStream is) {
    return null;
  }
}
