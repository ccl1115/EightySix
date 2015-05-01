/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.dao.*;
import com.utree.eightysix.utils.DaoUtils;

import java.util.List;

/**
*/
public class FMessageUtil {

  /**
   * 分页获取一个对话的消息
   *
   * @param chatId 会话Id
   * @param page   页数
   * @return the messages in this page
   */
  public static List<FriendMessage> getMessages(String chatId, int page) {
    return DaoUtils.getFriendMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.ChatId.eq(chatId))
        .orderDesc(FriendMessageDao.Properties.Timestamp)
        .limit(20)
        .offset(20 * page)
        .list();
  }

  /**
   * Set all message read and conversation unread count to 0
   * Callback received by event status {@link com.utree.eightysix.app.chat.event.ChatEvent#EVENT_CONVERSATIONS_RELOAD}
   */
  public static void setAllRead() {
    List<FriendMessage> list = DaoUtils.getFriendMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.Read.eq(false)).list();
    for (FriendMessage message : list) {
      message.setRead(true);
    }
    DaoUtils.getFriendMessageDao().updateInTx(list);

    List<FriendConversation> conversations = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.UnreadCount.notEq(0)).list();
    for (FriendConversation conversation : conversations) {
      conversation.setUnreadCount(0L);
    }
    DaoUtils.getFriendConversationDao().updateInTx(conversations);

    U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
  }

  public static FriendConversation setRead(String chatId) {
    List<FriendMessage> list = DaoUtils.getFriendMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.ChatId.eq(chatId))
        .listLazy();

    for (FriendMessage m : list) {
      m.setRead(true);
    }

    DaoUtils.getFriendMessageDao().updateInTx(list);

    FriendConversation conversation = DaoUtils.getFriendConversationDao()
        .queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(chatId))
        .limit(1)
        .unique();

    if (conversation != null) {
      conversation.setUnreadCount(0l);
      DaoUtils.getFriendConversationDao().update(conversation);
      return conversation;
    } else {
      return null;
    }
  }

  public static long getUnreadCount() {
    return DaoUtils.getFriendMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.Read.eq(false)).count();
  }

  public static long getAssistUnreadCount() {
    return DaoUtils.getFriendMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.ChatType.eq("assistant"))
        .where(FriendMessageDao.Properties.Read.eq(false))
        .where(FriendMessageDao.Properties.Direction.eq(MessageConst.DIRECTION_RECEIVE))
        .count();
  }
}
