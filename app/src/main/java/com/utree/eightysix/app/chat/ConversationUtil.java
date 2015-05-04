/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.chat;

import android.text.TextUtils;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.event.ChatEvent;
import com.utree.eightysix.dao.Conversation;
import com.utree.eightysix.dao.ConversationDao;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.dao.MessageDao;
import com.utree.eightysix.data.ChatFav;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.response.ChatInfoResponse;
import com.utree.eightysix.utils.DaoUtils;
import de.akquinet.android.androlog.Log;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ConversationUtil {
  public static String getChatIdByPost(Post post) {
    Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
        .where(ConversationDao.Properties.PostId.eq(post.id),
            ConversationDao.Properties.CommentId.isNull())
        .limit(1)
        .unique();

    return conversation != null ? conversation.getChatId() : null;
  }

  public static String getChatIdByPostComment(Post post, Comment comment) {
    Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
        .where(ConversationDao.Properties.PostId.eq(post.id),
            ConversationDao.Properties.CommentId.eq(comment.id))
        .limit(1)
        .unique();

    return conversation != null ? conversation.getChatId() : null;
  }

  public static List<Conversation> getConversations() {
    return DaoUtils.getConversationDao().queryBuilder()
        .whereOr(ConversationDao.Properties.LastMsg.isNotNull(),
            ConversationDao.Properties.Favorite.eq(true))
        .orderDesc(ConversationDao.Properties.Timestamp)
        .list();
  }

  public static List<Conversation> getConversations(int page, int size) {
    return DaoUtils.getConversationDao().queryBuilder()
        .whereOr(ConversationDao.Properties.LastMsg.isNotNull(),
            ConversationDao.Properties.Favorite.eq(true))
        .orderDesc(ConversationDao.Properties.Timestamp)
        .offset(page * size)
        .limit(size)
        .list();
  }

  public static long getPage(int size) {
    long count = DaoUtils.getConversationDao().count();
    return count == 0 ? count : (count + size) / size;
  }

  public static void createIfNotExist(ChatInfoResponse.ChatInfo chatInfo, Post post) {
    Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(chatInfo.chatId))
        .limit(1)
        .unique();
    if (conversation == null) {
      conversation = new Conversation();
      conversation.setChatId(chatInfo.chatId);
      conversation.setPostId(post.id);
      conversation.setPostSource(chatInfo.factoryName);
      conversation.setBgUrl(post.bgUrl);
      conversation.setBgColor(post.bgColor);
      conversation.setRelation(chatInfo.relation);
      conversation.setPostContent(post.content);
      conversation.setTimestamp(System.currentTimeMillis());
      conversation.setUnreadCount(0L);
      conversation.setFavorite(false);

      String myAvatar[] = chatInfo.myAvatar.split("_");
      conversation.setMyPortrait(myAvatar[0]);
      conversation.setMyPortraitColor(myAvatar[1]);
      String targetAvatar[] = chatInfo.targetAvatar.split("_");
      conversation.setPortrait(targetAvatar[0]);
      conversation.setPortraitColor(targetAvatar[1]);

      DaoUtils.getConversationDao().insert(conversation);
    } else {
      conversation.setCommentId(null);
      conversation.setCommentContent(null);
      DaoUtils.getConversationDao().update(conversation);
    }
  }

  public static void createIfNotExist(ChatInfoResponse.ChatInfo chatInfo, Post post, Comment comment) {
    Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(chatInfo.chatId))
        .limit(1)
        .unique();
    if (conversation == null) {
      conversation = new Conversation();
      conversation.setChatId(chatInfo.chatId);
      conversation.setPostSource(chatInfo.factoryName);
      conversation.setBgUrl(post.bgUrl);
      conversation.setBgColor(post.bgColor);
      conversation.setPostId(post.id);
      conversation.setCommentId(comment.id);
      conversation.setRelation(chatInfo.relation);
      conversation.setPostContent(post.content);
      conversation.setCommentContent(comment.content);
      conversation.setTimestamp(System.currentTimeMillis());
      conversation.setUnreadCount(0L);
      conversation.setFavorite(false);

      String myAvatar[] = chatInfo.myAvatar.split("_");
      conversation.setMyPortrait(myAvatar[0]);
      conversation.setMyPortraitColor(myAvatar[1]);
      String targetAvatar[] = chatInfo.targetAvatar.split("_");
      conversation.setPortrait(targetAvatar[0]);
      conversation.setPortraitColor(targetAvatar[1]);

      DaoUtils.getConversationDao().insert(conversation);
    } else {
      conversation.setCommentId(comment.id);
      conversation.setCommentContent(comment.content);
      DaoUtils.getConversationDao().update(conversation);
    }
  }

  public static Conversation createByChatFav(ChatFav chatFav) {
    Conversation conversation = new Conversation();
    conversation.setChatId(chatFav.chatId);
    conversation.setPostSource(chatFav.factoryName);
    conversation.setBgUrl(chatFav.bgUrl);
    conversation.setBgColor(chatFav.bgColor);
    conversation.setPostId(chatFav.postId);
    conversation.setPostContent(chatFav.postContent);
    conversation.setCommentId(chatFav.commentId);
    conversation.setCommentContent(chatFav.commentContent);
    conversation.setUnreadCount(0L);
    conversation.setTimestamp(System.currentTimeMillis());
    conversation.setRelation(chatFav.relation);
    conversation.setBanned(false);
    conversation.setFavorite(true);

    String myAvatar[] = chatFav.myAvatar.split("_");
    conversation.setMyPortrait(myAvatar[0]);
    conversation.setMyPortraitColor(myAvatar[1]);
    String targetAvatar[] = chatFav.targetAvatar.split("_");
    conversation.setPortrait(targetAvatar[0]);
    conversation.setPortraitColor(targetAvatar[1]);

    DaoUtils.getConversationDao().insert(conversation);
    return conversation;
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
    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT,
        getUnreadConversationCount()));
  }

  public static void deleteConversation(Conversation conversation) {
    DaoUtils.getMessageDao().queryBuilder()
        .where(MessageDao.Properties.ChatId.eq(conversation.getChatId()))
        .buildDelete()
        .executeDeleteWithoutDetachingEntities();
    DaoUtils.getConversationDao().delete(conversation);
    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT,
        getUnreadConversationCount()));
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
    U.getChatBus().post(new ChatEvent(ChatEvent.EVENT_UPDATE_UNREAD_CONVERSATION_COUNT,
        getUnreadConversationCount()));
  }

  public static Conversation setLastMessage(Message message) {
    Conversation conversation = DaoUtils.getConversationDao().queryBuilder()
        .where(ConversationDao.Properties.ChatId.eq(message.getChatId()))
        .limit(1)
        .unique();

    if (conversation != null) {
      if (message.getType() == MessageConst.TYPE_IMAGE) {
        conversation.setLastMsg("[图片]");
      } else {
        conversation.setLastMsg(message.getContent());
      }
      conversation.setTimestamp(message.getTimestamp());
      conversation.setCommentId(message.getCommentId());
      conversation.setCommentContent(message.getCommentContent());
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
        .where(ConversationDao.Properties.ChatId.eq(chatId))
        .limit(1)
        .unique();
  }

  public static void createOrUpdateConversation(EMMessage emMessage) throws EaseMobException {
    final String chatId = emMessage.getStringAttribute("chatId", null);
    if (TextUtils.isEmpty(chatId) || "0".equals(chatId)) {
      throw new EaseMobException("chatId is empty or 0");
    }
    final String postId = emMessage.getStringAttribute("postId", null);
    if (TextUtils.isEmpty(postId) || "0".equals(postId)) {
      throw new EaseMobException("postId is empty or 0");
    }

    Conversation conversation = getByChatId(chatId);

    if (conversation == null) {
      conversation = new Conversation();
      conversation.setUnreadCount(0l);
      conversation.setLastMsg("");
      conversation.setFavorite(false);
      conversation.setChatId(chatId);
    }
    conversation.setPostId(postId);
    conversation.setPostContent(emMessage.getStringAttribute("postContent", ""));
    String commentId = emMessage.getStringAttribute("commentId", "");
    if (!"0".equals(commentId)) {
      conversation.setCommentId(commentId);
    }
    conversation.setCommentContent(emMessage.getStringAttribute("commentContent", ""));

    String bgUrl = emMessage.getStringAttribute("bgUrl", null);
    Log.d(C.TAG.CH, "@createOrUpdateConversation bgUrl: " + bgUrl);
    conversation.setBgUrl(bgUrl);

    String bgColor = emMessage.getStringAttribute("bgColor", null);
    Log.d(C.TAG.CH, "@createOrUpdateConversation bgColor: " + bgColor);
    conversation.setBgColor(bgColor);

    String my = emMessage.getStringAttribute("myAvatar", null);
    Log.d(C.TAG.CH, "@createOrUpdateConversation myAvatar: " + my);
    if (my != null) {
      conversation.setMyPortrait(my.substring(0, 1));
      conversation.setMyPortraitColor(my.substring(2));
    }

    String target = emMessage.getStringAttribute("targetAvatar", null);
    Log.d(C.TAG.CH, "@createOrUpdateConversation targetAvatar: " + target);
    if (target != null) {
      conversation.setPortrait(target.substring(0, 1));
      conversation.setPortraitColor(target.substring(2));
    }

    String factoryName = emMessage.getStringAttribute("factoryName", "");
    Log.d(C.TAG.CH, "@createOrUpdateConversation factoryName: " + factoryName);
    conversation.setPostSource(factoryName);

    String relation = emMessage.getStringAttribute("relation", "");
    conversation.setRelation(relation);
    Log.d(C.TAG.CH, "@createOrUpdateConversation relation: " + relation);

    conversation.setTimestamp(System.currentTimeMillis());

    DaoUtils.getConversationDao().insertOrReplace(conversation);
  }

}
