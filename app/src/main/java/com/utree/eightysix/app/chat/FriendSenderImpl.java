/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.IOUtils;

import java.io.*;

/**
 */
public class FriendSenderImpl implements FriendSender {
  @Override
  public void send(final FriendMessage message) {


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
            DaoUtils.getFriendMessageDao().insertOrReplace(message);
            U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_SENT_MSG_ERROR, message));
          }

          @Override
          public void onResponse(Response response) {
            if (response.code != 0) {
              U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_MSG_REMOVE, message));
            } else {
              message.setStatus(MessageConst.STATUS_SUCCESS);
              DaoUtils.getFriendMessageDao().insertOrReplace(message);
              U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_SENT_MSG_SUCCESS, message));
            }
          }
        }, Response.class,
            message.getChatId(),
            "txt",
            message.getContent(),
            null,
            null,
            null);

        break;
      case MessageConst.TYPE_IMAGE:
        ImageContent content = U.getGson().fromJson(message.getContent(), ImageContent.class);
        FileInputStream in = null;
        FileOutputStream os = null;
        final File tmpFile = IOUtils.createTmpFile(System.currentTimeMillis() + ".jpg");
        try {
          in = new FileInputStream(content.local);
          os = new FileOutputStream(tmpFile);
          IOUtils.copyFile(in, os);
        } catch (FileNotFoundException ignored) {
        } finally {
          if (in != null) {
            try {
              in.close();
            } catch (IOException ignored) {
            }
          }
          if (os != null) {
            try {
              os.close();
            } catch (IOException ignored) {
            }
          }
        }
        U.request("chat_send", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {
            message.setStatus(MessageConst.STATUS_FAILED);
            DaoUtils.getFriendMessageDao().insertOrReplace(message);
            U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_SENT_MSG_SUCCESS, message));
            tmpFile.delete();
          }

          @Override
          public void onResponse(Response response) {
            if (response.code != 0) {
              U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_MSG_REMOVE, message));
            } else {
              message.setStatus(MessageConst.STATUS_SUCCESS);
              DaoUtils.getFriendMessageDao().insertOrReplace(message);
              U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_SENT_MSG_SUCCESS, message));
            }
            tmpFile.delete();
          }
        }, Response.class,
            message.getChatId(),
            "img",
            null,
            null,
            null,
            tmpFile);
        break;
    }


    DaoUtils.getFriendMessageDao().insertOrReplace(message);
    message.setStatus(MessageConst.STATUS_IN_PROGRESS);
    U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_SENDING_MSG, message));
  }

  @Override
  public FriendMessage txt(String chatId, String txt) {
    FriendMessage m = new FriendMessage();

    m.setMsgId(ChatUtils.uniqueMsgId());

    m.setChatId(chatId);
    m.setContent(txt);
    m.setType(MessageConst.TYPE_TXT);
    m.setDirection(MessageConst.DIRECTION_SEND);
    m.setTimestamp(System.currentTimeMillis());
    m.setStatus(MessageConst.STATUS_CREATE);

    send(m);

    return m;
  }

  @Override
  public FriendMessage voice(String chatId, File f) {
    return null;
  }

  @Override
  public FriendMessage photo(String chatId, File f) {
    if (!f.exists()) return null;
    FriendMessage m = new FriendMessage();

    m.setMsgId(ChatUtils.uniqueMsgId());

    m.setChatId(chatId);
    m.setContent(U.getGson().toJson(new ImageContent(f.getAbsolutePath(), null, null, null, null)));
    m.setType(MessageConst.TYPE_IMAGE);
    m.setDirection(MessageConst.DIRECTION_SEND);
    m.setTimestamp(System.currentTimeMillis());
    m.setStatus(MessageConst.STATUS_CREATE);

    send(m);

    return m;
  }

}
