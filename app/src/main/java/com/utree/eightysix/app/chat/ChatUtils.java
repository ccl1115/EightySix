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
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.chat.content.ImageContent;
import com.utree.eightysix.app.home.HomeTabActivity;
import com.utree.eightysix.dao.FriendConversation;
import com.utree.eightysix.dao.FriendMessage;
import com.utree.eightysix.dao.Message;
import com.utree.eightysix.data.Comment;
import com.utree.eightysix.data.Post;
import com.utree.eightysix.response.ChatInfoResponse;
import com.utree.eightysix.response.FriendChatResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import de.akquinet.android.androlog.Log;
import org.jivesoftware.smack.packet.Packet;

import java.util.Calendar;
import java.util.Locale;

/**
 */
public class ChatUtils {

  static Message toMessage(EMMessage message) {
    final Message m = new Message();

    try {
      m.setChatId(message.getStringAttribute("chatId"));
      Log.d(C.TAG.CH, "receive chatId: " + m.getChatId());
    } catch (EaseMobException e) {
      return null;
    }

    m.setPostId(message.getStringAttribute("postId", null));
    Log.d(C.TAG.CH, "receive post id: " + m.getPostId());

    String commentId = message.getStringAttribute("commentId", null);
    if (!TextUtils.isEmpty(commentId) && !"0".equals(commentId)) {
      m.setCommentId(commentId);
      Log.d(C.TAG.CH, "receive comment id: " + m.getCommentId());
    }

    try {
      m.setCommentContent(message.getStringAttribute("commentContent"));
      Log.d(C.TAG.CH, "receive comment content: " + m.getCommentContent());
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
        m.setStatus(MessageConst.STATUS_SUCCESS);
        m.setContent(((TextMessageBody) message.getBody()).getMessage());
        Log.d(C.TAG.CH, "receive post content: " + m.getContent());
        break;
      case IMAGE:
        m.setType(MessageConst.TYPE_IMAGE);
        m.setStatus(MessageConst.STATUS_IN_PROGRESS);
        ImageMessageBody body = (ImageMessageBody) message.getBody();


        // notice
        //环信在下载图片缩略图的时候，会在加载的文件名前加字符串"th"，会导致和EMMessage指定的本地目录不一致
        String local = body.getLocalUrl().substring(0, body.getLocalUrl().lastIndexOf('/') + 1)
            .concat("th")
            .concat(body.getLocalUrl().substring(body.getLocalUrl().lastIndexOf('/') + 1));
        // end

        ImageContent content = new ImageContent(body.getLocalUrl(), body.getRemoteUrl(), body.getSecret(), local, body.getThumbnailUrl());
        m.setContent(U.getGson().toJson(content));
        Log.d(C.TAG.CH, "receive post content: " + m.getContent());
        break;
    }

    return m;
  }

  static FriendMessage toFriendMessage(EMMessage message) {

    final FriendMessage m = new FriendMessage();

    try {
      m.setChatId(message.getStringAttribute("chatId"));
      Log.d(C.TAG.CH, "receive chatId: " + m.getChatId());
    } catch (EaseMobException e) {
      return null;
    }

    m.setChatType(message.getStringAttribute("chatType", ""));
    m.setDirection(message.direct == EMMessage.Direct.RECEIVE ? MessageConst.DIRECTION_RECEIVE : MessageConst.DIRECTION_SEND);
    m.setFrom(message.getFrom());
    m.setMsgId(message.getMsgId());
    m.setRead(false);
    m.setTimestamp(message.getMsgTime());
    switch (message.getType()) {
      case TXT:
        m.setType(MessageConst.TYPE_TXT);
        m.setStatus(MessageConst.STATUS_SUCCESS);
        m.setContent(((TextMessageBody) message.getBody()).getMessage());
        Log.d(C.TAG.CH, "receive post content: " + m.getContent());
        break;
      case IMAGE:
        m.setType(MessageConst.TYPE_IMAGE);
        m.setStatus(MessageConst.STATUS_IN_PROGRESS);
        ImageMessageBody body = (ImageMessageBody) message.getBody();

        // notice
        //环信在下载图片缩略图的时候，会在加载的文件名前加字符串"th"，会导致和EMMessage指定的本地目录不一致
        String local = body.getLocalUrl().substring(0, body.getLocalUrl().lastIndexOf('/') + 1)
            .concat("th")
            .concat(body.getLocalUrl().substring(body.getLocalUrl().lastIndexOf('/') + 1));
        // end

        ImageContent content = new ImageContent(body.getLocalUrl(), body.getRemoteUrl(), body.getSecret(), local, body.getThumbnailUrl());
        m.setContent(U.getGson().toJson(content));
        Log.d(C.TAG.CH, "receive post content: " + m.getContent());
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
      return String.format("%02d：%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
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
      return String.format("%s %02d：%02d", week, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    } else {
      // 显示 (年-月-日 时段 时间)
      return String.format("%s-%s-%s %02d：%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
          cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
  }

  static String uniqueMsgId() {
    String var0 = Long.toHexString(System.currentTimeMillis());
    var0 = var0.substring(6);
    return Packet.nextID() + "-" + var0;
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

  public static Message warningMsg(String chatId, String msg) {
    Message m = new Message();

    m.setTimestamp(System.currentTimeMillis());
    m.setType(MessageConst.TYPE_WARNING);
    m.setContent(msg);
    m.setChatId(chatId);
    m.setDirection(MessageConst.DIRECTION_NON);
    m.setStatus(MessageConst.STATUS_CREATE);

    return m;
  }

  public static FriendMessage infoFriendMsg(String chatId, String msg) {
    FriendMessage m = new FriendMessage();

    m.setTimestamp(System.currentTimeMillis());
    m.setType(MessageConst.TYPE_INFO);
    m.setContent(msg);
    m.setChatId(chatId);
    m.setDirection(MessageConst.DIRECTION_NON);
    m.setStatus(MessageConst.STATUS_CREATE);

    return m;
  }

  public static FriendMessage warningFriendMsg(String chatId, String msg) {
    FriendMessage m = new FriendMessage();

    m.setTimestamp(System.currentTimeMillis());
    m.setType(MessageConst.TYPE_WARNING);
    m.setContent(msg);
    m.setChatId(chatId);
    m.setDirection(MessageConst.DIRECTION_NON);
    m.setStatus(MessageConst.STATUS_CREATE);

    return m;
  }

  public static void startChat(final BaseActivity context, final Post post, final Comment comment) {
    if (comment.self == 1) {
      U.showToast("不能向自己发起聊天哦！");
      return;
    }

    String chatId = ConversationUtil.getChatIdByPostComment(post, comment);
    if (chatId != null) {
      ChatActivity.start(context, chatId);
      return;
    }

    context.showProgressBar();
    U.request("get_chat_info", new OnResponse2<ChatInfoResponse>() {
      @Override
      public void onResponseError(Throwable throwable) {
        U.showToast(U.gs(R.string.create_conversation_failed));
        context.hideProgressBar();
      }

      @Override
      public void onResponse(ChatInfoResponse response) {
        if (RESTRequester.responseOk(response)) {
          ConversationUtil.createIfNotExist(response.object, post, comment);
          ChatActivity.start(context, response.object.chatId);
        } else {
          U.showToast(U.gs(R.string.create_conversation_failed));
        }
        context.hideProgressBar();
      }
    }, ChatInfoResponse.class, post.id, comment.id);
  }

  public static void startChat(final BaseActivity context, final Post post) {
    if (post.owner == 1) {
      U.showToast("不能向自己发起聊天哦！");
      return;
    }

    String chatId = ConversationUtil.getChatIdByPost(post);
    if (chatId != null) {
      ChatActivity.start(context, chatId);
      return;
    }

    context.showProgressBar();
    U.request("get_chat_info", new OnResponse2<ChatInfoResponse>() {
      @Override
      public void onResponseError(Throwable throwable) {
        context.hideProgressBar();
      }

      @Override
      public void onResponse(ChatInfoResponse response) {
        if (RESTRequester.responseOk(response)) {
          ConversationUtil.createIfNotExist(response.object, post);
          ChatActivity.start(context, response.object.chatId);
        }
        context.hideProgressBar();
      }
    }, ChatInfoResponse.class, post.id, null);
  }

  public static void startFriendChat(final BaseActivity context, final int viewId) {
    String chatId = FConversationUtil.getChatIdByViewId(viewId);

    if (chatId != null) {
      FChatActivity.start(context, chatId);
      return;
    }

    U.request("get_friend_chat_info", new OnResponse2<FriendChatResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        context.hideProgressBar();
      }

      @Override
      public void onResponse(FriendChatResponse response) {
        if (RESTRequester.responseOk(response)) {
          FConversationUtil.createIfNotExist(response.object, viewId, "friend");
          FChatActivity.start(context, response.object.chatId);
        }
        context.hideProgressBar();
      }
    }, FriendChatResponse.class, "friend", viewId);

    context.showProgressBar();
  }

  public static void startAssistantChat(final BaseActivity context) {
    String chatId = FConversationUtil.getAssistantChatId();

    if (chatId != null) {
      FChatActivity.start(context, chatId);
      return;
    }

    U.request("get_friend_chat_info", new OnResponse2<FriendChatResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        context.hideProgressBar();
      }

      @Override
      public void onResponse(FriendChatResponse response) {
        if (RESTRequester.responseOk(response)) {
          FConversationUtil.createIfNotExist(response.object, response.object.viewId, "assistant");
          FChatActivity.start(context, response.object.chatId);
        }
        context.hideProgressBar();
      }
    }, FriendChatResponse.class, "assistant", null);

    context.showProgressBar();
  }


  public static class NotifyUtil {

    private static final int ID_MESSAGE = 0x101000;
    private static final int ID_FRIEND_MESSAGE = 0x101001;
    private static final int ID_ASSISTANT = 0x101002;

    public static void clear() {
      NotificationManager manager = (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
      manager.cancel(ID_MESSAGE);
    }

    public static void clearFriendMessage() {
      NotificationManager manager = (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
      manager.cancel(ID_FRIEND_MESSAGE);
    }

    public static void clearAssistant() {
      NotificationManager manager = (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
      manager.cancel(ID_ASSISTANT);
    }

    public static void notifyNewMessage(Message message) {

      long count = ConversationUtil.getUnreadConversationCount();
      if (count == 0) {
        return;
      }
      Context context = U.getContext();
      NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      Intent[] intents;
      if (count == 1) {
        intents = new Intent[]{
            HomeTabActivity.getIntent(context, 0),
            ConversationActivity.getIntent(context),
            ChatActivity.getIntent(context, message.getChatId())
        };
      } else {
        intents = new Intent[]{
            HomeTabActivity.getIntent(context, 0),
            ConversationActivity.getIntent(context)
        };
      }

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
          .setContentTitle("悄悄话")
          .setContentText(String.format("你收到了%d条悄悄话", count))
          .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
          .setSmallIcon(R.drawable.ic_launcher)
          .setLights(Color.GREEN, 500, 2000)
          .setAutoCancel(true);

      if (Account.inst().getSilentMode()) {
        builder.setSound(null);
      } else {
        builder.setDefaults(Notification.DEFAULT_SOUND);
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
            ChatActivity.getIntent(context, message.getChatId()),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }

      Notification build = builder.build();

      manager.notify(ID_MESSAGE, build);

    }

    public static void notifyNewMessage(FriendMessage message, FriendConversation conversation) {
      if (message.getChatType().equals("assistant")) {
        notifyNewAssistantMessage(message);
      } else if (message.getChatType().equals("friend")) {
        notifyNewFriendMessage(message, conversation);
      }
    }

    private static void notifyNewAssistantMessage(FriendMessage message) {
      Context context = U.getContext();
      Intent[] intents;
      NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      NotificationCompat.Builder builder;
      intents = new Intent[]{
          HomeTabActivity.getIntent(context, 0),
          FChatActivity.getIntent(context, message.getChatId())
      };

      builder = new NotificationCompat.Builder(context)
          .setContentTitle("蓝莓小助手")
          .setContentText(message.getContent())
          .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
          .setSmallIcon(R.drawable.ic_launcher)
          .setLights(Color.GREEN, 500, 2000)
          .setAutoCancel(true);

      if (Account.inst().getSilentMode()) {
        builder.setSound(null);
      } else {
        builder.setDefaults(Notification.DEFAULT_SOUND);
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        builder.setContentIntent(PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT));
      } else {
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
            FChatActivity.getIntent(context, message.getChatId()),
            PendingIntent.FLAG_UPDATE_CURRENT));
      }

      Notification build = builder.build();

      manager.notify(ID_ASSISTANT, build);
    }

    private static void notifyNewFriendMessage(FriendMessage message, FriendConversation conversation) {
      long count = FConversationUtil.getUnreadConversationCount();
      if (count == 0) {
        return;
      }
      Context context = U.getContext();
      NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

      Intent[] intents;
      NotificationCompat.Builder builder;
      if (count == 1) {
        intents = new Intent[]{
            HomeTabActivity.getIntent(context, 0),
            FChatActivity.getIntent(context, message.getChatId())
        };

        builder = new NotificationCompat.Builder(context)
            .setContentTitle("聊天")
            .setContentText(String.format("%s：%s", conversation.getTargetName(),
                message.getType() == MessageConst.TYPE_IMAGE ? "[图片]" : message.getContent()))
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
            .setSmallIcon(R.drawable.ic_launcher)
            .setLights(Color.GREEN, 500, 2000)
            .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          builder.setContentIntent(PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
          builder.setContentIntent(PendingIntent.getActivity(context, 0,
              FConversationActivity.getIntent(context),
              PendingIntent.FLAG_UPDATE_CURRENT));
        }

      } else {
        intents = new Intent[]{
            HomeTabActivity.getIntent(context, 0),
            FConversationActivity.getIntent(context)
        };

        builder = new NotificationCompat.Builder(context)
            .setContentTitle("聊天")
            .setContentText(String.format("你收到了%d条聊天消息", count))
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
            .setSmallIcon(R.drawable.ic_launcher)
            .setLights(Color.GREEN, 500, 2000)
            .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          builder.setContentIntent(PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
          builder.setContentIntent(PendingIntent.getActivity(context, 0,
              FChatActivity.getIntent(context, message.getChatId()),
              PendingIntent.FLAG_UPDATE_CURRENT));
        }

      }

      if (Account.inst().getSilentMode()) {
        builder.setSound(null);
      } else {
        builder.setDefaults(Notification.DEFAULT_SOUND);
      }


      Notification build = builder.build();

      manager.notify(ID_FRIEND_MESSAGE, build);
    }
  }
}
