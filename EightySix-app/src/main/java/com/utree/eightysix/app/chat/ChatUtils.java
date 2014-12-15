/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.*;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.utils.DaoUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

  static String timestamp(long timestamp) {
    long now = System.currentTimeMillis();

    Calendar cal = Calendar.getInstance(Locale.CHINA);
    cal.setTimeInMillis(timestamp);

    Calendar nowCal = Calendar.getInstance(Locale.CHINA);
    nowCal.setTimeInMillis(now);

    if (nowCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
      // 在同一天，显示 (时段 时间)
      return String.format("%s%02d:%02d", cal.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午",
          cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));
    } else if (nowCal.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR)) {
      // 在同一周，显示 (星期 时段 时间)
      String week = "";
      switch (cal.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
          week = "星期一";
          break;
        case Calendar.TUESDAY:
          week = "星期二";
          break;
        case Calendar.WEDNESDAY:
          week = "星期三";
          break;
        case Calendar.THURSDAY:
          week = "星期四";
          break;
        case Calendar.FRIDAY:
          week = "星期五";
          break;
        case Calendar.SATURDAY:
          week = "星期六";
          break;
        case Calendar.SUNDAY:
          week = "星期日";
          break;
      }
      return String.format("%s %s%02d:%02d", week,
          cal.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));
    } else {
      // 显示 (年-月-日 时段 时间)
      return String.format("%s-%s-%s %s%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
          cal.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));
    }
  }

  public static Message infoMsg(String chatId, String msg) {
    Message m = new Message();

    m.setTimestamp(System.currentTimeMillis());
    m.setType(MessageConst.TYPE_INFO);
    m.setContent(msg);
    m.setChatId(chatId);
    m.setDirection(MessageConst.DIRECTION_NON);
    m.setStatus(MessageConst.STATUS_CREATE);

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
        conversation.setUnreadCount(0L);
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
        conversation.setUnreadCount(0L);
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

    /**
     *
     */
    public static void deleteAllConversation() {
      List<Conversation> list = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.Favorite.eq(false))
          .list();
      List<String> chatIds = new ArrayList<String>();
      for (Conversation conversation : list) {
        chatIds.add(conversation.getChatId());
      }

      DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.in(chatIds))
          .buildDelete()
          .executeDeleteWithoutDetachingEntities();

      DaoUtils.getConversationDao().deleteInTx(list);
      U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
    }

    public static Message getLastMessage(String chatId) {
      return DaoUtils.getMessageDao().queryBuilder().where(MessageDao.Properties.ChatId.eq(chatId)).limit(1).unique();
    }

    public static void setLastMessage(String chatId, Message message) {
      Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId)).unique();

      if (conversation != null) {
        conversation.setLastMsg(message.getContent());
        conversation.setTimestamp(message.getTimestamp());
        DaoUtils.getConversationDao().update(conversation);
        U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, conversation));
      }
    }

    public static void updateLastMessage(String chatId) {
      Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId)).unique();

      if (conversation != null) {
        Message message = DaoUtils.getMessageDao().queryBuilder()
            .where(MessageDao.Properties.ChatId.eq(chatId))
            .whereOr(MessageDao.Properties.Type.eq(MessageConst.TYPE_TXT), MessageDao.Properties.Type.eq(MessageConst.TYPE_IMAGE))
            .unique();

        if (message != null) {
          conversation.setLastMsg(message.getContent());
          conversation.setTimestamp(message.getTimestamp());
        } else {
          conversation.setLastMsg("");
        }

        DaoUtils.getConversationDao().update(conversation);
      }
    }

    public static void updateUnreadCount(String chatId) {
      Conversation conversation = getByChatId(chatId);

      if (conversation != null) {
        final long count = DaoUtils.getMessageDao().queryBuilder()
            .where(MessageDao.Properties.ChatId.eq(chatId), MessageDao.Properties.Read.eq(false))
            .count();
        conversation.setUnreadCount(count);

        DaoUtils.getConversationDao().updateInTx(conversation);
      }
    }

    /**
     * Callback received by event status {@link com.utree.eightysix.app.chat.event.ChatEvent#EVENT_CONVERSATION_UPDATE}
     *
     * @param chatId the chat id of a conversation
     */
    public static void increaseUnreadCount(String chatId) {
      Conversation conversation = getByChatId(chatId);

      if (conversation != null) {
        conversation.setUnreadCount(conversation.getUnreadCount() + 1);
      }

      DaoUtils.getConversationDao().update(conversation);

      U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATION_UPDATE, conversation));
    }

    public static Conversation getByChatId(String chatId) {
      return DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId)).unique();
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

      List<Conversation> list1 = DaoUtils.getConversationDao().queryBuilder().where(ConversationDao.Properties.UnreadCount.notEq(0)).list();
      for (Conversation conversation : list1) {
        conversation.setUnreadCount(0L);
      }
      DaoUtils.getConversationDao().updateInTx(list1);

      U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_CONVERSATIONS_RELOAD, null));
    }
  }
}
