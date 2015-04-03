/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageDao;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.DaoUtils;

import java.util.List;

/**
* Created by Administrator on 2015/4/3 0003.
*/
public class MessageUtil {

  /**
   * 分页获取一个对话的消息
   *
   * @param chatId 会话Id
   * @param page   页数
   * @return the messages in this page
   */
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

  public static boolean hasCommentSummaryMessage(String chatId, String commentId) {
    return DaoUtils.getMessageDao().queryBuilder()
        .where(MessageDao.Properties.ChatId.eq(chatId),
            MessageDao.Properties.Type.eq(MessageConst.TYPE_COMMENT),
            MessageDao.Properties.CommentId.eq(commentId)).count() > 0;
  }

  /**
   * Set all message read and conversation unread count to 0
   * Callback received by event status {@link com.utree.eightysix.app.chat.event.ChatEvent#EVENT_CONVERSATIONS_RELOAD}
   */
  public static void setAllRead() {
    List<Message> list = DaoUtils.getMessageDao().queryBuilder().where(MessageDao.Properties.Read.eq(false)).list();
    for (Message message : list) {
      message.setRead(true);
    }
    DaoUtils.getMessageDao().updateInTx(list);

    List<Conversation> conversations = DaoUtils.getConversationDao().queryBuilder().where(ConversationDao.Properties.UnreadCount.notEq(0)).list();
    for (Conversation conversation : conversations) {
      conversation.setUnreadCount(0L);
    }
    DaoUtils.getConversationDao().updateInTx(conversations);

    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
  }

  public static Conversation setRead(String chatId) {
    List<Message> list = DaoUtils.getMessageDao().queryBuilder()
        .where(MessageDao.Properties.ChatId.eq(chatId))
        .listLazy();

    for (Message m : list) {
      m.setRead(true);
    }

    DaoUtils.getMessageDao().updateInTx(list);

    Conversation conversation = DaoUtils.getConversationDao()
        .queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(chatId))
        .limit(1)
        .unique();

    if (conversation != null) {
      conversation.setUnreadCount(0l);
      DaoUtils.getConversationDao().update(conversation);
      return conversation;
    } else {
      return null;
    }
  }

  public static long getUnreadCount() {
    return DaoUtils.getMessageDao().queryBuilder()
        .where(MessageDao.Properties.Read.eq(false)).count();
  }

  public static Message addPostSummaryInfo(String chatId, long timestamp, Post post) {
    if (!MessageUtil.hasPostSummaryMessage(chatId)) {
      Message message =
          ChatUtils.infoMsg(chatId,
              "主题：" + (post.content.length() > 80 ? post.content.substring(0, 76) + "..." : post.content));

      message.setPostId(post.id);
      message.setType(MessageConst.TYPE_POST);
      message.setTimestamp(timestamp);

      DaoUtils.getMessageDao().insert(message);
      return message;
    } else {
      return null;
    }

  }

  public static Message addPostSummaryInfo(String chatId, long timestamp, String postId, String postContent) {
    if (!MessageUtil.hasPostSummaryMessage(chatId)) {
      Message message =
          ChatUtils.infoMsg(chatId,
              "主题：" + (postContent.length() > 80 ? postContent.substring(0, 76) + "..." : postContent));

      message.setPostId(postId);
      message.setType(MessageConst.TYPE_POST);
      message.setTimestamp(timestamp);

      DaoUtils.getMessageDao().insert(message);
      return message;
    } else {
      return null;
    }
  }

  public static Message addCommentSummaryInfo(String chatId, long timestamp, Post post, Comment comment) {
    if (!MessageUtil.hasCommentSummaryMessage(chatId, comment.id)) {
      Message message =
          ChatUtils.infoMsg(chatId,
              "评论：" + (comment.content.length() > 80 ? comment.content.substring(0, 76) + "..." : comment.content));

      message.setPostId(post.id);
      message.setCommentId(comment.id);
      message.setType(MessageConst.TYPE_COMMENT);
      message.setTimestamp(timestamp);

      DaoUtils.getMessageDao().insert(message);
      return message;
    } else {
      return null;
    }
  }

  public static Message addCommentSummaryInfo(String chatId,
                                              long timestamp,
                                              String postId,
                                              String postContent,
                                              String commentId,
                                              String commentContent) {
    if (!MessageUtil.hasCommentSummaryMessage(chatId, commentId)) {
      Message message =
          ChatUtils.infoMsg(chatId,
              "评论：" + (commentContent.length() > 80 ? commentContent.substring(0, 76) + "..." : commentContent));

      message.setPostId(postId);
      message.setCommentId(commentId);
      message.setType(MessageConst.TYPE_COMMENT);
      message.setTimestamp(timestamp);

      DaoUtils.getMessageDao().insert(message);
      return message;
    } else {
      return null;
    }
  }

}