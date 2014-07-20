package com.utree.eightysix.app.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
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

/**
 * 拉通知
 *
 * @author simon
 */
public class PullNotificationService extends Service {

  private static final int TYPE_POST = 1;
  private static final int TYPE_UNLOCK_CIRCLE = 2;
  private static final int TYPE_FRIEND_L1_JOIN = 3;
  private static final int TYPE_COMMENT = 4;
  private static final int TYPE_PRAISE = 5;
  private static final int TYPE_CIRCLE_CREATION_APPROVE = 6;

  private static final int ID_POST = 0x1000;
  private static final int ID_UNLOCK_FACTORY = 0x2000;
  private static final int ID_FRIEND_L1_JOIN = 0x3000;
  private static final int ID_COMMENT = 0x4000;
  private static final int ID_PRAISE = 0x5000;
  private static final int ID_APPROVE = 0x6000;

  public static void start(Context context, int type, String seq) {
    Intent intent = new Intent(context, PullNotificationService.class);
    intent.putExtra("type", type);
    intent.putExtra("seq", seq);
    context.startService(intent);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    U.getBus().register(this);
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
    U.getBus().unregister(this);
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

  private Notification buildPost(String postId, String shortName) {
    return new NotificationCompat.Builder(this)
        .setTicker(getString(R.string.notification_friend_new_post))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_app_icon)
        .setContentTitle(shortName)
        .setContentText(getString(R.string.notification_friend_new_post))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            PostActivity.getIntent(this, postId), PendingIntent.FLAG_ONE_SHOT))
        .build();
  }

  private Notification buildUnlockCircle(String circleId, String circleName) {
    return new NotificationCompat.Builder(this).setTicker(getString(R.string.notification_circle_unlocked))
        .setSmallIcon(R.drawable.ic_app_icon)
        .setAutoCancel(true)
        .setContentTitle(getString(R.string.notification_circle_unlocked))
        .setContentText(getString(R.string.notification_circle_unlocked_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_ONE_SHOT))
        .build();
  }

  private Notification buildComment(int count, String id) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle(getString(R.string.notification_new))
        .setAutoCancel(true)
        .setTicker(getString(R.string.notification_new))
        .setSmallIcon(R.drawable.ic_app_icon);
    if (count == 1) {
      builder.setContentText(getString(R.string.notification_new_comment));
      builder.setContentIntent(PendingIntent.getActivity(this, 0,
          PostActivity.getIntent(this, id), PendingIntent.FLAG_ONE_SHOT));
    } else {
      builder.setContentText(String.format(getString(R.string.notification_new_comments), count));
      builder.setContentIntent(PendingIntent.getActivity(this, 0,
          MsgActivity.getIntent(this, true), PendingIntent.FLAG_ONE_SHOT));
    }
    return builder.build();
  }

  private Notification buildApprove(String circleId, String circleName) {
    return new NotificationCompat.Builder(this).setDefaults(Notification.DEFAULT_ALL)
        .setSmallIcon(R.drawable.ic_app_icon)
        .setAutoCancel(true)
        .setTicker(getString(R.string.notification_circle_create_approve))
        .setContentTitle(getString(R.string.notification_circle_create_approve))
        .setContentText(getString(R.string.notification_circle_create_approve_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_ONE_SHOT))
        .build();
  }

  private Notification buildFriendJoin(String circleId, String circleName, int count) {
    return new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_app_icon)
        .setAutoCancel(true)
        .setTicker(getString(R.string.notification_new_friend))
        .setContentTitle(getString(R.string.notification_new_friend))
        .setContentText(count > 1 ? getString(R.string.notification_new_friend_tip, circleName, count) :
            getString(R.string.notification_new_friend_tip, circleName))
        .setContentIntent(PendingIntent.getActivity(this, 0,
            FeedActivity.getIntent(this, Integer.parseInt(circleId)), PendingIntent.FLAG_ONE_SHOT))
        .build();
  }

  private void handleResponse(PullNotificationResponse response) {
    if (!Account.inst().isLogin()) return;
    switch (response.object.type) {
      case TYPE_POST:
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, ID_POST, buildPost(item.value, item.shortName));
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
      case TYPE_COMMENT:
        int count = 0;
        try {
          count = Integer.parseInt(response.object.msg);
        } catch (NumberFormatException ignored) {

        }
        if (count == 1) {
          getNM().notify(ID_COMMENT, buildComment(count, response.object.lists.get(0).value));
        } else {
          getNM().notify(ID_COMMENT, buildComment(count, null));
        }
        Account.inst().setNewCommentCount(response.object.lists.size());
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
