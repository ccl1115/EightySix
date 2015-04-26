/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.friends;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.chat.FChatActivity;
import com.utree.eightysix.app.chat.FConversationActivity;
import com.utree.eightysix.app.chat.FConversationUtil;
import com.utree.eightysix.response.FriendChatResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class NotifyUtils {

  private static final int ID_ADDED_FRIEND = 0x9000;
  private static final int ID_PASSED_FRIEND = 0x9001;

  private static Set<String> sAddedFriendNames = new HashSet<String>();

  private static Set<String> sPassedFriendNames = new HashSet<String>();

  private static final Bitmap sLargeIcon;

  static {
    sLargeIcon = BitmapFactory.decodeResource(U.getContext().getResources(), R.drawable.ic_launcher);
  }

  public static void addedFriend(String name) {
    NotificationManager manager =
        (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

    sAddedFriendNames.add(name);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(U.getContext());
    builder.setContentTitle(String.format("收到%d条朋友请求", sAddedFriendNames.size()));
    StringBuilder b = new StringBuilder();
    for (String s : sAddedFriendNames) {
      b.append(s);
      b.append("，");
    }
    b.deleteCharAt(b.length() - 1);
    builder.setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ? Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setSmallIcon(R.drawable.ic_launcher);
    builder.setContentText(b.toString());
    builder.setContentIntent(PendingIntent.getActivity(U.getContext(),
            0,
            new Intent(U.getContext(), RequestListActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT));

    manager.notify(ID_ADDED_FRIEND, builder.build());
  }

  public static void passedFriend(final int viewId, final String name) {
    final NotificationManager manager =
        (NotificationManager) U.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

    sPassedFriendNames.add(name);

    String chatId = FConversationUtil.getChatIdByViewId(viewId);

    if (chatId == null) {
      U.request("get_friend_chat_info", new OnResponse2<FriendChatResponse>() {
        @Override
        public void onResponseError(Throwable e) {
        }

        @Override
        public void onResponse(FriendChatResponse response) {
          if (RESTRequester.responseOk(response)) {
            FConversationUtil.createIfNotExist(response.object, viewId, "friend");

            notifyPassedFriend(response.object.chatId, name, manager);
          }
        }
      }, FriendChatResponse.class, "friend", viewId);
    } else {
      notifyPassedFriend(chatId, name, manager);
    }


  }

  private static void notifyPassedFriend(String chatId, String name, NotificationManager manager) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(U.getContext());
    builder.setContentTitle("朋友申请通过");
    builder.setAutoCancel(true)
        .setDefaults(Account.inst().getSilentMode() ?
            Notification.DEFAULT_LIGHTS : Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setSmallIcon(R.drawable.ic_launcher);
    if (sPassedFriendNames.size() == 1) {
      builder.setContentText(String.format("你与%s已经是蓝莓朋友了，聊聊天吧", name));
      builder.setContentIntent(PendingIntent.getActivity(U.getContext(),
          0,
          FChatActivity.getIntent(U.getContext(), chatId),
          PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      StringBuilder b = new StringBuilder();
      for (String s : sPassedFriendNames) {
        b.append(s);
        b.append("，");
      }
      b.deleteCharAt(b.length() - 1);
      builder.setContentText(String.format("你与%s等%d人已经是蓝莓朋友了",
          b.toString(), sPassedFriendNames.size()));

      builder.setContentIntent(PendingIntent.getActivity(U.getContext(),
          0,
          new Intent(U.getContext(), FConversationActivity.class),
          PendingIntent.FLAG_UPDATE_CURRENT
      ));
    }

    manager.notify(ID_PASSED_FRIEND, builder.build());
  }
}
