package com.utree.eightysix.app.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.M;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.data.PullNotification;
import com.utree.eightysix.request.PullNotificationRequest;
import com.utree.eightysix.response.PullNotificationResponse;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import de.akquinet.android.androlog.Log;
import java.util.List;

/**
 * 拉通知
 *
 * @author simon
 */
public class PullNotificationService extends Service {

  /**
   * 新帖子
   */
  private static final int TYPE_NEW_POST = 1;

  /**
   * 圈子解锁
   */
  private static final int TYPE_UNLOCK_CIRCLE = 2;

  /**
   * 新朋友加入
   */
  private static final int TYPE_FRIEND_L1_JOIN = 3;

  /**
   * 自己发布的帖子被评论
   */
  private static final int TYPE_FOLLOW_COMMENT = 4;

  /**
   * 被赞
   */
  private static final int TYPE_PRAISE = 5;

  /**
   * 圈子创建审核通过
   */
  private static final int TYPE_CIRCLE_CREATION_APPROVE = 6;

  /**
   * 关注的帖子被评论
   */
  private static final int TYPE_OWN_COMMENT = 7;

  private static final int ID_POST = 0x10;
  private static final int ID_UNLOCK_FACTORY = 0x20;
  private static final int ID_FRIEND_L1_JOIN = 0x30;
  private static final int ID_FOLLOW_COMMENT = 0x40;
  private static final int ID_PRAISE = 0x50;
  private static final int ID_APPROVE = 0x60;
  private static final int ID_OWN_COMMENT = 0x70;
  private static Bitmap sLargeIcon;

  static {
    sLargeIcon = BitmapFactory.decodeResource(U.getContext().getResources(), R.drawable.ic_app_icon);
  }

  public static void start(Context context, int type, String seq) {
    Intent intent = new Intent(context, PullNotificationService.class);
    intent.putExtra("type", type);
    intent.putExtra("seq", seq);
    context.startService(intent);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    M.getRegisterHelper().register(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    int type = intent.getIntExtra("type", 0);
    String seq = intent.getStringExtra("seq");
    if (seq != null) {
      requestPullNotification(type, seq);
    }
    return Service.START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    M.getRegisterHelper().unregister(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Subscribe
  public void onLogoutEvent(Account.LogoutEvent event) {
    getNM().cancelAll();
  }

  private void requestPullNotification(final int type, final String seq) {
    RequestData data = U.getRESTRequester().convert(new PullNotificationRequest(type, seq));
    U.getRESTRequester().request(data, new HandlerWrapper<PullNotificationResponse>(data,
        new OnResponse<PullNotificationResponse>() {
          @Override
          public void onResponse(PullNotificationResponse response) {
            if (RESTRequester.responseOk(response)) {
              handleResponse(response);
            }
          }
        }, PullNotificationResponse.class));
  }

  private NotificationManager getNM() {
    return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  private Notification buildPost(int i, String postId, String shortName) {
    Log.d(C.TAG.NT, "build post: " + postId);
    return new NotificationCompat.Builder(this)
        .setTicker(getString(R.string.notification_friend_new_post))
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setContentTitle(shortName)
        .setContentText(getString(R.string.notification_friend_new_post))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            PostActivity.getIntent(this, postId), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();
  }

  private Notification buildUnlockCircle(String circleId, String circleName) {
    Log.d(C.TAG.NT, "build unlock circle: " + circleId);
    return new NotificationCompat.Builder(this).setTicker(getString(R.string.notification_circle_unlocked))
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle(getString(R.string.notification_circle_unlocked))
        .setContentText(getString(R.string.notification_circle_unlocked_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();
  }

  private Notification buildComment(int count, String id, int type) {
    Log.d(C.TAG.NT, String.format("build comment: count = %d id = %s", count, id));
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle(getString(R.string.notification_new))
        .setAutoCancel(true)
        .setTicker(getString(R.string.notification_new))
        .setDefaults(Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon);
    if (count == 1) {
      builder.setContentText(getString(type == TYPE_FOLLOW_COMMENT ?
          R.string.notification_new_follow_comment : R.string.notification_new_own_comment));
      builder.setContentIntent(PendingIntent.getActivity(this, 0,
          PostActivity.getIntent(this, id), PendingIntent.FLAG_UPDATE_CURRENT));
    } else {
      builder.setContentText(getString(type == TYPE_FOLLOW_COMMENT ?
          R.string.notification_new_follow_comments : R.string.notification_new_own_comments, count));
      builder.setContentIntent(PendingIntent.getActivity(this, 0,
          MsgActivity.getIntent(this, true), PendingIntent.FLAG_UPDATE_CURRENT));
    }
    return builder.build();
  }

  private Notification buildApprove(String circleId, String circleName) {
    Log.d(C.TAG.NT, "build approve: " + circleId);
    return new NotificationCompat.Builder(this).setDefaults(Notification.DEFAULT_ALL)
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setTicker(getString(R.string.notification_circle_create_approve))
        .setDefaults(Notification.DEFAULT_ALL)
        .setContentTitle(getString(R.string.notification_circle_create_approve))
        .setContentText(getString(R.string.notification_circle_create_approve_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();
  }

  private Notification buildFriendJoin(String circleId, String circleName, int count) {
    Log.d(C.TAG.NT, "build friend join: " + circleId);
    return new NotificationCompat.Builder(this)
        .setLargeIcon(sLargeIcon)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_ALL)
        .setTicker(getString(R.string.notification_new_friend))
        .setContentTitle(getString(R.string.notification_new_friend))
        .setContentText(count > 1 ? getString(R.string.notification_new_friend_tip_plural, circleName, count) :
            getString(R.string.notification_new_friend_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();
  }

  private void handleResponse(PullNotificationResponse response) {
    if (!Account.inst().isLogin()) return;
    final int type = response.object.type;
    switch (type) {
      case TYPE_NEW_POST:
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        List<PullNotification.Item> lists = response.object.lists;
        for (int i = 0, listsSize = lists.size(); i < listsSize; i++) {
          PullNotification.Item item = lists.get(i);
          getNM().notify(item.value, ID_POST, buildPost(i, item.value, item.shortName));
        }
        break;
      case TYPE_UNLOCK_CIRCLE:
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, ID_UNLOCK_FACTORY, buildUnlockCircle(item.value, item.shortName));
        }
        break;
      case TYPE_FRIEND_L1_JOIN:
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, ID_FRIEND_L1_JOIN, buildFriendJoin(item.value, item.shortName, item.friendCount));
        }
        break;
      case TYPE_OWN_COMMENT:
      case TYPE_FOLLOW_COMMENT:
        int count = 0;
        try {
          count = Integer.parseInt(response.object.msg);
        } catch (NumberFormatException ignored) {

        }
        if (count == 1) {
          getNM().notify(type == TYPE_FOLLOW_COMMENT ? ID_FOLLOW_COMMENT : ID_OWN_COMMENT,
              buildComment(count, response.object.lists.get(0).value, type));
        } else if (count > 1) {
          getNM().notify(type == TYPE_FOLLOW_COMMENT ? ID_FOLLOW_COMMENT : ID_OWN_COMMENT,
              buildComment(count, null, type));
        }
        Account.inst().incNewCommentCount(count);
        break;
      case TYPE_PRAISE:
        Account.inst().setHasNewPraise(true);
        break;
      case TYPE_CIRCLE_CREATION_APPROVE:
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, ID_APPROVE, buildApprove(item.value, item.shortName));
        }
        break;
    }
  }

}
