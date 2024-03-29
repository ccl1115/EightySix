/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.text.TextUtils;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.FriendChatEvent;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.dao.FriendConversationDao;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.dao.FriendMessageDao;
import com.utree.eightysix.response.FriendChatResponse;
import com.utree.eightysix.utils.DaoUtils;

import java.util.List;

/**
*/
public class FConversationUtil {

  static FriendConversation createOrUpdateFConversation(EMMessage emMessage) throws EaseMobException {
    String chatId = emMessage.getStringAttribute("chatId", null);
    if (TextUtils.isEmpty(chatId) || "0".equals(chatId)) {
      throw new EaseMobException("chatId is empty or equals 0");
    }

    FriendConversation conversation = getByChatId(chatId);

    if (conversation == null) {
      conversation = new FriendConversation();
      conversation.setChatId(chatId);
      conversation.setLastMsg("");
      conversation.setUnreadCount(0l);
    }

    conversation.setViewId(Integer.valueOf(emMessage.getStringAttribute("viewId")));
    conversation.setMyAvatar(emMessage.getStringAttribute("myUserAvatar"));
    conversation.setTargetAvatar(emMessage.getStringAttribute("targetUserAvatar"));
    conversation.setTargetName(emMessage.getStringAttribute("targetUserName"));
    conversation.setChatType(emMessage.getStringAttribute("chatType"));

    conversation.setTimestamp(System.currentTimeMillis());

    DaoUtils.getFriendConversationDao().insertOrReplace(conversation);
    return conversation;
  }

  static FriendConversation getByChatId(String chatId) {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(chatId))
        .limit(1)
        .unique();
  }

  static List<FriendConversation> getConversations() {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatType.eq("friend"))
        .orderDesc(FriendConversationDao.Properties.Timestamp)
        .list();
  }

  static List<FriendConversation> getConversations(int page, int size) {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatType.eq("friend"),
            FriendConversationDao.Properties.LastMsg.isNotNull())
        .orderDesc(FriendConversationDao.Properties.Timestamp)
        .offset(page * size)
        .limit(size)
        .list();
  }

  public static void deleteConversation(FriendConversation conversation) {
    DaoUtils.getMessageDao().queryBuilder()
        .where(FriendMessageDao.Properties.ChatId.eq(conversation.getChatId()))
        .buildDelete()
        .executeDeleteWithoutDetachingEntities();
    DaoUtils.getFriendConversationDao().delete(conversation);
    U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT,
        FConversationUtil.getUnreadConversationCount()));
  }

  public static String getChatIdByViewId(int viewId) {
    FriendConversation friendConversation = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ViewId.eq(viewId))
        .limit(1)
        .unique();
    return friendConversation == null ? null : friendConversation.getChatId();
  }

  public static void createIfNotExist(FriendChatResponse.FriendChat chat, int viewId, String chatType) {
    FriendConversation conversation = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(chat.chatId))
        .limit(1)
        .unique();

    if (conversation == null) {
      conversation = new FriendConversation();
      conversation.setViewId(viewId);
      conversation.setChatId(chat.chatId);
      conversation.setUnreadCount(0l);
      conversation.setTimestamp(System.currentTimeMillis());
      conversation.setMyAvatar(chat.myAvatar);
      conversation.setMyName(chat.myName);
      conversation.setTargetName(chat.targetName);
      conversation.setTargetAvatar(chat.targetAvatar);
      conversation.setSource(chat.factoryName);
      conversation.setChatType(chatType);
      DaoUtils.getFriendConversationDao().insert(conversation);
    }
  }


  static void deleteAllConversation() {
    DaoUtils.getFriendConversationDao().deleteAll();
    DaoUtils.getFriendMessageDao().deleteAll();
    U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
    U.getChatBus().post(new FriendChatEvent(FriendChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT,
        FConversationUtil.getUnreadConversationCount()));
  }

  static FriendConversation setLastMessage(FriendMessage message) {
    FriendConversation conversation = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(message.getChatId()))
        .limit(1)
        .unique();

    if (conversation != null) {
      if (message.getType() == MessageConst.TYPE_IMAGE) {
        conversation.setLastMsg("[图片]");
      } else {
        conversation.setLastMsg(message.getContent());
      }
      conversation.setTimestamp(message.getTimestamp());
      DaoUtils.getFriendConversationDao().update(conversation);
      return conversation;
    } else {
      return null;
    }
  }

  public static long getUnreadConversationCount() {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.UnreadCount.gt(0))
        .where(FriendConversationDao.Properties.ChatType.eq("friend"))
        .count();
  }

  static FriendConversation updateUnreadCount(String chatId) {
    FriendConversation conversation = getByChatId(chatId);

    if (conversation != null) {
      final long count = DaoUtils.getFriendMessageDao().queryBuilder()
          .where(FriendMessageDao.Properties.ChatId.eq(chatId), FriendMessageDao.Properties.Read.eq(false))
          .count();
      conversation.setUnreadCount(count);

      DaoUtils.getFriendConversationDao().update(conversation);
      return conversation;
    } else {
      return null;
    }
  }

  static String getAssistantChatId() {
    FriendConversation assistant = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatType.eq("assistant"))
        .limit(1)
        .unique();
    return assistant == null ? null : assistant.getChatId();
  }
}
