/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageConst;

/**
 */
class Utils {

  static Message convert(EMMessage message) throws EaseMobException {
    Message m = new Message();

    m.setContent(((TextMessageBody) message.getBody()).getMessage());
    m.setChatId(message.getStringAttribute("chatId"));
    m.setPostId(message.getStringAttribute("postId"));
    m.setCommentId(message.getStringAttribute("commentId"));
    m.setDirection(message.direct == EMMessage.Direct.RECEIVE ? MessageConst.DIRECTION_RECEIVE : MessageConst.DIRECTION_SEND);
    m.setFrom(message.getFrom());
    m.setMsgId(message.getMsgId());
    m.setRead(false);
    switch (message.getType()) {
      case TXT:
        m.setType(MessageConst.TYPE_TXT);
        break;
      case IMAGE:
        m.setType(MessageConst.TYPE_IMAGE);
        break;
    }

    return m;
  }

}
