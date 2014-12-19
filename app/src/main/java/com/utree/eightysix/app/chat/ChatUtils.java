/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.app.home.HomeActivity;
import com.utree.eightysix.dao.*;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.request.PostCommentsRequest;
import com.utree.eightysix.response.ChatIdResponse;
import com.utree.eightysix.response.PostCommentsResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.utils.DaoUtils;
import com.utree.eightysix.utils.ParamsRunnable;
import de.akquinet.android.androlog.Log;

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
      Log.d(C.TAG.CH, "receive chatId: " + m.getChatId());
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
    }, ChatIdResponse.class, null, comment == null ? null : comment.id);
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
              ConversationDao.Properties.CommentId.isNull())
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

    public static List<Conversation> getConversations() {
      return DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.LastMsg.isNotNull())
          .list();
    }

    public static void createIfNotExist(String chatId, Post post) {
      if (DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(chatId))
          .unique() == null) {
        Conversation conversation = new Conversation();
        conversation.setChatId(chatId);
        conversation.setPostId(post.id);
        conversation.setPostSource(post.shortName);
        conversation.setBgUrl(post.bgUrl);
        conversation.setBgColor(post.bgColor);
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
        conversation.setChatId(chatId);
        conversation.setPostSource(post.shortName);
        conversation.setBgUrl(post.bgUrl);
        conversation.setBgColor(post.bgColor);
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

    public static Conversation setLastMessage(Message message) {
      Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.ChatId.eq(message.getChatId()))
          .limit(1).unique();

      if (conversation != null) {
        conversation.setLastMsg(message.getContent());
        conversation.setTimestamp(message.getTimestamp());
        DaoUtils.getConversationDao().update(conversation);
        return conversation;
      } else {
        return null;
      }
    }

    public static Conversation updateUnreadCount(String chatId) {
      Conversation conversation = getByChatId(chatId);

      if (conversation != null) {
        final long count = DaoUtils.getMessageDao().queryBuilder()
            .where(MessageDao.Properties.ChatId.eq(chatId), MessageDao.Properties.Read.eq(false))
            .count();
        conversation.setUnreadCount(count);

        DaoUtils.getConversationDao().update(conversation);
        return conversation;
      } else {
        return null;
      }
    }

    public static long getUnreadConversationCount() {
      return DaoUtils.getConversationDao().queryBuilder()
          .where(ConversationDao.Properties.UnreadCount.gt(0))
          .count();
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
    public static void createByPostIdIfNotExist(final String chatId, String postId, final ParamsRunnable runnable) {
      Conversation conversation = getByChatId(chatId);
      if (conversation == null) {
        U.getRESTRequesterSync().request(new PostCommentsRequest(postId, 0, 0, 0, 1), new OnResponse2<PostCommentsResponse>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(PostCommentsResponse response) {
            if (RESTRequester.responseOk(response)) {
              createIfNotExist(chatId, response.object.post);
              runnable.run(response.object.post);
            }
          }
        }, PostCommentsResponse.class);
      } else {
        Post post = new Post();
        post.id = conversation.getPostId();
        post.content = conversation.getPostContent();
        post.shortName = conversation.getPostSource();
        runnable.run(post);
      }
    }

    /**
     * 当用户收取到一条消息，如果本地没有该对话，则通过接口获取该会话的消息
     *
     * @param chatId    the id of the conversation
     * @param postId    the id of the post
     * @param commentId the comment of the post
     */
    public static void createByPostCommentIdIfNotExist(final String chatId, String postId, final String commentId, final ParamsRunnable runnable) {
      Conversation conversation = getByChatId(chatId);
      if (conversation == null) {
        U.getRESTRequesterSync().request(new PostCommentsRequest(postId, 0, 0, 0, 1), new OnResponse2<PostCommentsResponse>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(PostCommentsResponse response) {
            if (RESTRequester.responseOk(response)) {
              for (Comment comment : response.object.comments.lists) {
                if (comment.id.equals(commentId)) {
                  createIfNotExist(chatId, response.object.post, comment);
                  runnable.run(response.object.post, comment);
                }
              }
            }
          }
        }, PostCommentsResponse.class);
      } else {
        Post post = new Post();
        post.id = conversation.getPostId();
        post.shortName = conversation.getPostSource();
        post.content = conversation.getPostContent();
        post.bgUrl = conversation.getBgUrl();
        Comment comment = new Comment();
        comment.id = conversation.getCommentId();
        comment.content = conversation.getCommentContent();
        runnable.run(post, comment);
      }
    }
  }

  public static class MessageUtil {

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
          .orderAsc(MessageDao.Properties.Timestamp)
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

    public static Message addPostSummaryInfo(String chatId, Post post) {
      if (!ChatUtils.MessageUtil.hasPostSummaryMessage(chatId)) {
        Message message =
            ChatUtils.infoMsg(chatId,
                "主题：" + (post.content.length() > 80 ? post.content.substring(0, 76) + "..." : post.content));

        message.setType(MessageConst.TYPE_POST);
        message.setTimestamp(0l);

        DaoUtils.getMessageDao().insertOrReplace(message);
        return message;
      } else {
        return null;
      }

    }

    public static Message addCommentSummaryInfo(String chatId, long timestamp, Comment comment) {
      if (!ChatUtils.MessageUtil.hasCommentSummaryMessage(chatId, comment.id)) {
        Message message =
            ChatUtils.infoMsg(chatId,
                "评论：" + (comment.content.length() > 80 ? comment.content.substring(0, 76) + "..." : comment.content));

        message.setCommentId(comment.id);
        message.setType(MessageConst.TYPE_COMMENT);
        message.setTimestamp(timestamp);

        DaoUtils.getMessageDao().insertOrReplace(message);
        return message;
      } else {
        return null;
      }
    }

  }

  public static class NotifyUtil {

    private static final int ID_MESSAGE = 0x1000;

    public static void notifyNewMessage(Message message, Post post, Comment comment) {

      long count = MessageUtil.getUnreadCount();
      Context context = U.getContext();
      NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      Intent[] intents;
      if (count == 1) {
        intents = new Intent[]{
            HomeActivity.getIntent(context, 0, 0),
            ConversationActivity.getIntent(context),
            ChatActivity.getIntent(context, message.getChatId(), post, comment)
        };
      } else {

        intents = new Intent[]{
            HomeActivity.getIntent(context, 0, 0),
            ConversationActivity.getIntent(context)
        };
      }

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle(String.format("你收到了%d条匿名聊天消息", count))
          .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
          .setSmallIcon(R.drawable.ic_launcher)
          .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
          .setAutoCancel(true);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
            ChatActivity.getIntent(context, message.getChatId(), post, comment),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }

      manager.notify(ID_MESSAGE, builder.build());

    }
  }
}
