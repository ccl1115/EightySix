/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseApplication;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Note this only use for send text, image and voice message.
 */
public class SenderImpl implements Sender {
  @Override
  public void send(final Message message) {

    if (message == null) {
      return;
    }

    message.setRead(true);
    message.setTimestamp(System.currentTimeMillis());
    message.setStatus(MessageConst.STATUS_CREATE);

    switch (message.getType()) {
      case MessageConst.TYPE_TXT:

        U.getRESTRequester().request("chat_send", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {
            message.setStatus(MessageConst.STATUS_FAILED);
            DaoUtils.getMessageDao().insertOrReplace(message);
            U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENT_MSG_ERROR, message));
          }

          @Override
          public void onResponse(Response response) {
            if (response.code != 0) {
              U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_MSG_REMOVE, message));
            } else {
              message.setStatus(MessageConst.STATUS_SUCCESS);
              DaoUtils.getMessageDao().insertOrReplace(message);
              U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENT_MSG_SUCCESS, message));
            }
          }
        }, Response.class,
            message.getChatId(),
            "txt",
            message.getContent(),
            message.getPostId(),
            message.getCommentId());

        break;
      case MessageConst.TYPE_IMAGE:
        ImageContent content = U.getGson().fromJson(message.getContent(), ImageContent.class);
        U.request("chat_send", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (response.code != 0) {
              U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_MSG_REMOVE, message));
            } else {
              message.setStatus(MessageConst.STATUS_SUCCESS);
              DaoUtils.getMessageDao().insertOrReplace(message);
              U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_SENT_MSG_SUCCESS, message));
            }
          }
        }, Response.class,
            message.getChatId(),
            "img",
            message.getContent(),
            message.getPostId(),
            message.getCommentId(),
            new File(content.local));
        break;
    }


    DaoUtils.getMessageDao().insertOrReplace(message);
    Conversation conversation = ChatUtils.ConversationUtil.setLastMessage(message);
    message.setStatus(MessageConst.STATUS_IN_PROGRESS);
    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, conversation));
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
    m.setDirection(MessageConst.DIRECTION_SEND);
    m.setTimestamp(System.currentTimeMillis());
    m.setStatus(MessageConst.STATUS_CREATE);

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
    if (!f.exists()) return null;
    Message m = new Message();

    m.setChatId(chatId);
    m.setPostId(postId);
    m.setCommentId(commentId);
    m.setContent(U.getGson().toJson(new ImageContent(f.getAbsolutePath(), "", "")));
    m.setType(MessageConst.TYPE_IMAGE);
    m.setDirection(MessageConst.DIRECTION_SEND);
    m.setTimestamp(System.currentTimeMillis());
    m.setStatus(MessageConst.STATUS_CREATE);

    send(m);

    return m;
  }

  @Override
  public Message photo(String chatId, String postId, String commentId, InputStream is) {
    return null;
  }
}
