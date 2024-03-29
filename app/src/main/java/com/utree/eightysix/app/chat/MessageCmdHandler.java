/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.app.friends.NotifyUtils;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.utils.DaoUtils;

/**
 */
public class MessageCmdHandler {

  public void handle(EMMessage message) {
    if (message.getType() == EMMessage.Type.CMD) {
      CmdMessageBody body = (CmdMessageBody) message.getBody();
      if (body.action.equals("addedFriend")) {
        handleAddedFriend(message);
      } else if (body.action.equals("passedFriend")) {
        handlePassedFriend(message);
      } else if (body.action.equals("chatNotify")) {
        handleChatNotify(message);
      }
    }
  }

  private void handleAddedFriend(EMMessage message) {
    String username = message.getStringAttribute("userName", null);
    if (username != null) {
      NotifyUtils.addedFriend(username);
    }
  }

  private void handlePassedFriend(EMMessage message) {
    String viewId = message.getStringAttribute("viewId", null);
    String username = message.getStringAttribute("userName", null);

    if (username != null && viewId != null) {
      NotifyUtils.passedFriend(Integer.parseInt(viewId), username);
    }
  }

  private void handleChatNotify(EMMessage message) {
    String chatId = message.getStringAttribute("chatId", null);
    String content = message.getStringAttribute("content", null);
    String chatType = message.getStringAttribute("chatType", null);

    if (chatId != null && content != null) {
      if ("whisper".equals(chatType)) {
        Message warning = ChatUtils.warningMsg(chatId, content);
        DaoUtils.getMessageDao().insertOrReplace(warning);
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_WARNING_MSG_RECEIVE, warning));
      } else if ("stranger".equals(chatType)) {
        FriendMessage warning = ChatUtils.warningFriendMsg(chatId, content);
        DaoUtils.getFriendMessageDao().insertOrReplace(warning);
        U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_WARNING_MSG_RECEIVE, warning));
      }
    }
  }
}
