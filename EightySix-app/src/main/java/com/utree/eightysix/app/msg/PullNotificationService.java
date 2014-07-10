package com.utree.eightysix.app.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.app.feed.FeedActivity;
import com.utree.eightysix.app.feed.PostActivity;
import com.utree.eightysix.event.HasNewPraiseEvent;
import com.utree.eightysix.event.NewCommentCountEvent;
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

  private static final int ID_POST = 0x1000;
  private static final int ID_UNLOCK_FACTORY = 0x2000;
  private static final int ID_FRIEND_L1_JOIN = 0x3000;
  private static final int ID_COMMENT = 0x4000;
  private static final int ID_PRAISE = 0x5000;

  private Handler mHandler = new Handler();

  public static void start(Context context, int type, String seq) {
    Intent intent = new Intent(context, PullNotificationService.class);
    intent.putExtra("type", type);
    intent.putExtra("seq", seq);
    context.startService(intent);
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
  public IBinder onBind(Intent intent) {
    return null;
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

  private Notification buildPost(String postId) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle(postId);
    builder.setContentText("你的朋友发了一条新帖子");
    builder.setContentIntent(PendingIntent.getActivity(this, 0, PostActivity.getIntent(this, postId), Intent.FLAG_ACTIVITY_NEW_TASK));
    return builder.build();
  }

  private Notification buildUnlockCircle(String circleId, String circleName) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("新的工友圈解锁了！");
    builder.setContentText(String.format("看看%s里的朋友发的秘密吧", circleName));
    builder.setContentIntent(PendingIntent.getActivity(this, 0, FeedActivity.getIntent(this, Integer.parseInt(circleId)), Intent.FLAG_ACTIVITY_NEW_TASK));
    return builder.build();
  }

  private Notification buildComment(String[] ids) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("新提醒");
    if (ids.length == 1) {
      builder.setContentText("你关注的秘密有了新的评论");
      builder.setContentIntent(PendingIntent.getActivity(this, 0, PostActivity.getIntent(this, ids[0]), Intent.FLAG_ACTIVITY_NEW_TASK));
    } else {
      builder.setContentText(String.format("你关注的%d条秘密有了新的评论", ids.length));
      builder.setContentIntent(PendingIntent.getActivity(this, 0, MsgActivity.getIntent(this, true), Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    return builder.build();
  }

  private Notification buildFriendJoin(String circleId, String circleName) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("新朋友加入");
    builder.setContentText(String.format("%s的朋友数＋1，能猜到他是谁吗", circleName));
    builder.setContentIntent(PendingIntent.getActivity(this, 0, FeedActivity.getIntent(this, Integer.parseInt(circleId)), Intent.FLAG_ACTIVITY_NEW_TASK));
    return builder.build();
  }

  private void handleResponse(PullNotificationResponse response) {
    switch (response.object.type) {
      case TYPE_POST:
        for (String id : response.object.ids) {
          getNM().notify(id, ID_POST, buildPost(id));
        }
        break;
      case TYPE_UNLOCK_CIRCLE:
        for (String id : response.object.ids) {
          getNM().notify(id, ID_UNLOCK_FACTORY, buildUnlockCircle(id, "测试"));
        }
        break;
      case TYPE_FRIEND_L1_JOIN:
        for (String id : response.object.ids) {
          getNM().notify(id, ID_FRIEND_L1_JOIN, buildFriendJoin(id, "测试"));
        }
        break;
      case TYPE_COMMENT:
        getNM().notify(ID_COMMENT, buildComment(response.object.ids));
        Account.inst().setNewCommentCount(response.object.ids.length);
        U.getBus().post(new NewCommentCountEvent(response.object.ids.length));
        break;
      case TYPE_PRAISE:
        Account.inst().setHasNewPraise(true);
        U.getBus().post(new HasNewPraiseEvent());
        break;
    }
  }
}
