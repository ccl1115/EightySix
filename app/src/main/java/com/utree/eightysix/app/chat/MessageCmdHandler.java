/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMMessage;
import com.utree.eightysix.app.friends.NotifyUtils;

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
}
