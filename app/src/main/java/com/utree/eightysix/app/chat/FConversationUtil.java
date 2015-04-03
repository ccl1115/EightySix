/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.text.TextUtils;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.dao.FriendConversationDao;
import com.utree.eightysix.response.FriendChatResponse;
import com.utree.eightysix.utils.DaoUtils;

import java.util.List;

/**
*/
public class FConversationUtil {

  public static void startChat(int viewId) {

  }

  public static void createOrUpdateFConversation(EMMessage emMessage) throws EaseMobException {
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

    conversation.setTimestamp(System.currentTimeMillis());

    DaoUtils.getFriendConversationDao().insertOrReplace(conversation);
  }

  public static FriendConversation getByChatId(String chatId) {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(chatId))
        .unique();
  }

  public static List<FriendConversation> getConversations() {
    return DaoUtils.getFriendConversationDao().queryBuilder()
        .orderDesc(FriendConversationDao.Properties.Timestamp)
        .list();
  }


  public static String getChatIdByViewId(int viewId) {
    FriendConversation friendConversation = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ViewId.eq(viewId))
        .unique();
    return friendConversation == null ? null : friendConversation.getChatId();
  }

  public static void createIfNotExist(FriendChatResponse.FriendChat chat, int viewId) {
    FriendConversation conversation = DaoUtils.getFriendConversationDao().queryBuilder()
        .where(FriendConversationDao.Properties.ChatId.eq(chat.chatId))
        .unique();

    if (conversation == null) {
      conversation = new FriendConversation();
      conversation.setViewId(viewId);
      conversation.setChatId(chat.chatId);
      conversation.setUnreadCount(0l);
      conversation.setLastMsg("");
      conversation.setTimestamp(System.currentTimeMillis());
      conversation.setMyAvatar(chat.myAvatar);
      conversation.setMyName(chat.myName);
      conversation.setTargetName(chat.targetName);
      conversation.setTargetAvatar(chat.targetAvatar);
      conversation.setSource(chat.factoryName);
      DaoUtils.getFriendConversationDao().insert(conversation);
    }
  }
}
