/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.*;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PostDeleteRequest;
import com.utree.eightysix.response.ChatIdResponse;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.DaoUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 */
public class ChatUtils {

  static Message convert(EMMessage message) {
    Message m = new Message();

    m.setContent(((TextMessageBody) message.getBody()).getMessage());

    try {
      m.setChatId(message.getStringAttribute("chatId"));
    } catch (EaseMobException e) {
      return null;
    }

    try {
      m.setPostId(message.getStringAttribute("postId"));
    } catch (EaseMobException ignored) {
    }

    try {
      m.setCommentId(message.getStringAttribute("commentId"));
    } catch (EaseMobException ignored) {
    }

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
      return String.format("%s %02d：%02d", cal.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午",
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
      return String.format("%s %s%02d：%02d", week,
          cal.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));
    } else {
      // 显示 (年-月-日 时段 时间)
      return String.format("%s-%s-%s %s%02d：%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
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

  public static void startChat(final BaseActivity context, final Post post, final Comment comment) {
    String chatId = ConversationUtil.getChatIdByPostComment(post, comment);
    if (chatId != null) {
      ChatActivity.start(context, chatId, post, comment);
      return;
    }

    context.showProgressBar();
    U.request("get_chat_id", new OnResponse2<ChatIdResponse>() {
      @Override
      public void onResponseError(Throwable throwable) {
        U.showToast(U.gs(R.string.create_conversation_failed));
        context.hideProgressBar();
      }

      @Override
      public void onResponse(ChatIdResponse response) {
        if (RESTRequester.responseOk(response)) {
          ConversationUtil.createIfNotExist(response.object.chatId, post, comment);
          ChatActivity.start(context, response.object.chatId, post, comment);
        } else {
          U.showToast(U.gs(R.string.create_conversation_failed));
        }
        context.hideProgressBar();
      }
    }, ChatIdResponse.class, post == null ? null : post.id, comment == null ? null : comment.id);
  }

  public static void startChat(final BaseActivity context, final Post post) {

    String chatId = ConversationUtil.getChatIdByPost(post);
    if (chatId != null) {
      ChatActivity.start(context, chatId, post, null);
      return;
    }

    context.showProgressBar();
    U.request("get_chat_id", new OnResponse2<ChatIdResponse>() {
      @Override
      public void onResponseError(Throwable throwable) {
        U.showToast(U.gs(R.string.create_conversation_failed));
        context.hideProgressBar();
      }

      @Override
      public void onResponse(ChatIdResponse response) {
        if (RESTRequester.responseOk(response)) {
          ConversationUtil.createIfNotExist(response.object.chatId, post);
          ChatActivity.start(context, response.object.chatId, post, null);
        } else {
          U.showToast(U.gs(R.string.create_conversation_failed));
        }
        context.hideProgressBar();
      }
    }, ChatIdResponse.class, post == null ? null : post.id, null);

  }


  public static class ConversationUtil {
    public static String getChatIdByPost(Post post) {
      Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.PostId.eq(post.id),
              ConversationDao.Properties.CommentId.isNotNull())
          .unique();

      return conversation != null ? conversation.getChatId() : null;
    }

    public static String getChatIdByPostComment(Post post, Comment comment) {
      Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.PostId.eq(post.id),
              ConversationDao.Properties.CommentId.eq(comment.id))
          .unique();

      return conversation != null ? conversation.getChatId() : null;
    }

    public static void createIfNotExist(String chatId, Post post) {
      if (DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId))
          .unique() == null) {
        Conversation conversation = new Conversation();
        if (post.bgUrl.startsWith("http")) {
          conversation.setBgUrl(post.bgUrl);
        } else {
          conversation.setBgUrl(post.bgColor);
        }
        conversation.setChatId(chatId);
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

    public static void createIfNotExist(String chatId, Post post, Comment comment) {
      if (DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId))
          .unique() == null) {
        Conversation conversation = new Conversation();
        if (post.bgUrl.startsWith("http")) {
          conversation.setBgUrl(post.bgUrl);
        } else {
          conversation.setBgUrl(post.bgColor);
        }
        conversation.setChatId(chatId);
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

    public static void deleteConversation(Conversation conversation) {
      DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.ChatId.eq(conversation.getChatId()))
          .buildDelete()
          .executeDeleteWithoutDetachingEntities();
      DaoUtils.getConversationDao().delete(conversation);
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

    /**
     * 获取聊天会话
     *
     * @param chatId the id of the conversation
     * @return the conversation instance
     */
    public static Conversation getByChatId(String chatId) {
      return DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId)).unique();
    }

    /**
     * 当用户收取到一条消息，如果本地没有该对话，则通过接口获取该会话的消息
     *
     * @param chatId the id of the conversation
     * @param postId the id of the post
     */
    public static void createByPostIdIfNotExist(final String chatId, String postId) {
      if (getByChatId(chatId) == null) {
        U.request(new PostDeleteRequest(postId), new OnResponse2<PostCommentsResponse>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(PostCommentsResponse response) {
            if (RESTRequester.responseOk(response)) {
              createIfNotExist(chatId, response.object.post);
            }
          }
        }, PostCommentsResponse.class);
      }
    }

    /**
     * 当用户收取到一条消息，如果本地没有该对话，则通过接口获取该会话的消息
     *
     * @param chatId    the id of the conversation
     * @param postId    the id of the post
     * @param commentId the comment of the post
     */
    public static void createByPostCommentIdIfNotExist(final String chatId, String postId, final String commentId) {
      if (getByChatId(chatId) == null) {
        U.request(new PostDeleteRequest(postId), new OnResponse2<PostCommentsResponse>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(PostCommentsResponse response) {
            if (RESTRequester.responseOk(response)) {
              for (Comment comment : response.object.comments.lists) {
                if (comment.id.equals(commentId)) {
                  createIfNotExist(chatId, response.object.post, comment);
                }
              }
            }
          }
        }, PostCommentsResponse.class);
      }
    }
  }

  public static class MessageUtil {

    /**
     * 分页获取一个对话的消息
     * @param chatId 会话Id
     * @param page 页数
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

    public static long getUnreadCount() {
      return DaoUtils.getMessageDao().queryBuilder()
          .where(MessageDao.Properties.Read.eq(false)).count();
    }
  }

  public static class NotifyUtil {

    private static final int ID_MESSAGE = 0x1000;

    public static void notifyNewMessage(Message message) {
      if (message.getChatId().equals(ChatActivity.getCurrentChatId())) {
        // 收到的消息，对应的聊天页面在前台，则不通知该条消息
        return;
      }

      long count = MessageUtil.getUnreadCount();
      NotificationManager manager = (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

      Notification not = new NotificationCompat.Builder(U.getContext())
          .setContentTitle(String.format("你收到了%d条匿名聊天消息", count))
          .setLargeIcon(BitmapFactory.decodeResource(U.getContext().getResources(), R.drawable.ic_launcher))
          .setSmallIcon(R.drawable.ic_launcher)
          .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
          .setAutoCancel(true)
          .build();

      manager.notify(ID_MESSAGE, not);

    }
  }
}
