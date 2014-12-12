/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.dao.*;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.DaoUtils;

import java.util.List;

/**
 */
public class ChatUtils {

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
    m.setTimestamp(message.getMsgTime());
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

  public static Message infoMsg(String chatId, String msg) {
    Message m = new Message();

    m.setTimestamp(System.currentTimeMillis());
    m.setType(MessageConst.TYPE_INFO);
    m.setContent(msg);
    m.setChatId(chatId);
    m.setDirection(MessageConst.DIRECTION_NON);

    return m;
  }


  public static class ConversationUtil {
    public static void createIfNotExist(Post post) {
      if (DaoUtils.getConversationDao().queryBuilder().where(ConversationDao.Properties.ChatId.eq(post.chatId)).unique() == null) {
        Conversation conversation = new Conversation();
        conversation.setBgUrl(post.bgUrl);
        conversation.setChatId(post.chatId);
        conversation.setChatSource(post.shortName);
        conversation.setPostId(post.id);
        conversation.setLastMsg("");
        conversation.setRelation(post.viewType == 3 ? "认识的人" : "陌生人");
        conversation.setPostContent(post.content);
        conversation.setTimestamp(System.currentTimeMillis());
        conversation.setUnreadCount(0);
        conversation.setPortrait("\ue800");
        conversation.setFavorite(false);
        DaoUtils.getConversationDao().insert(conversation);
      }
    }

    public static void createIfNotExist(Post post, Comment comment) {
      if (DaoUtils.getConversationDao().queryBuilder().where(ConversationDao.Properties.ChatId.eq(post.chatId)).unique() == null) {
        Conversation conversation = new Conversation();
        conversation.setBgUrl(post.bgUrl);
        conversation.setChatId(comment.chatId);
        conversation.setChatSource(post.shortName);
        conversation.setPostId(post.id);
        conversation.setCommentId(comment.id);
        conversation.setLastMsg("");
        conversation.setRelation(post.viewType == 3 ? "认识的人" : "陌生人");
        conversation.setPostContent(post.content);
        conversation.setCommentContent(comment.content);
        conversation.setTimestamp(System.currentTimeMillis());
        conversation.setUnreadCount(0);
        conversation.setPortrait(comment.avatar);
        conversation.setFavorite(false);
        DaoUtils.getConversationDao().insert(conversation);
      }
    }

    public static void deleteConversation(String chatId) {
      DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(chatId))
          .buildDelete()
          .executeDeleteWithoutDetachingEntities();
      DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId))
          .buildDelete()
          .executeDeleteWithoutDetachingEntities();
    }
  }

  public static class MessageUtil {
    public static List<Message> getConversation(String chatId, int page) {
      return DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(chatId))
          .orderDesc(MessageDao.Properties.Timestamp)
          .limit(20)
          .offset(20 * page)
          .build()
          .list();
    }

    public static boolean hasPostSummaryMessage(String chatId) {
      return DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(chatId), MessageDao.Properties.Type.eq(MessageConst.TYPE_POST))
          .count() > 0;
    }

    public static boolean hasCommentSummrayMessage(String chatId, String commentId) {
      return DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(chatId),
              MessageDao.Properties.Type.eq(MessageConst.TYPE_COMMENT),
              MessageDao.Properties.CommentId.eq(commentId)).count() > 0;
    }
  }
}
