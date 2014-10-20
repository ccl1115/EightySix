package com.utree.eightysix.app.msg;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.app.msg.event.NewAllPostCountEvent;
import com.utree.eightysix.app.msg.event.NewFriendsPostCountEvent;
import com.utree.eightysix.app.msg.event.NewHotPostCountEvent;
import com.utree.eightysix.data.PullNotification;
import com.utree.eightysix.push.PushMessageReceiver;
import com.utree.eightysix.request.FetchNotificationRequest;
import com.utree.eightysix.response.FetchResponse;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import de.akquinet.android.androlog.Log;

import java.util.List;

/**
 * @author simon
 */
public class FetchNotificationService extends Service {

  public static final String TAG = "FetchNotificationService";
  private static final int FETCH_INTERVAL = 60000;
  private static final int MSG_FETCH = 0x1;

  private static int sCircleId;

  private NotifyUtil mNotifyUtil;

  private boolean mShowCommentNotify;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      sendEmptyMessageDelayed(MSG_FETCH, FETCH_INTERVAL);
      requestFetch();
    }
  };

  public static void setCircleId(int circleId) {
    sCircleId = circleId;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "start FetchService");
    mHandler.sendEmptyMessageDelayed(MSG_FETCH, 1000);
    mShowCommentNotify = intent.getBooleanExtra("showCommentNotify", false);
    return START_NOT_STICKY;
  }

  public static void start(Context context, boolean showCommentNotify) {
    Intent intent = new Intent(context, FetchNotificationService.class);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    intent.putExtra("showCommentNotify", showCommentNotify);

    context.startActivity(intent);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mNotifyUtil = new NotifyUtil(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void requestFetch() {

    RequestData data = U.getRESTRequester().convert(new FetchNotificationRequest(sCircleId));
    U.getRESTRequester().request(data, new HandlerWrapper<FetchResponse>(data, new OnResponse2<FetchResponse>() {

      private final int circleId = sCircleId;

      @Override
      public void onResponse(FetchResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.newPraise != null &&
              response.object.newPraise.praise == 1) {
            Account.inst().setHasNewPraise(true);
          }

          int count = 0;
          int type = 0;

          PullNotification comments = null;
          if (response.object.newComment != null) {
            comments = response.object.newComment;
            type = comments.type;
            count = comments.unread;
          } else if (response.object.myPostComment != null) {
            comments = response.object.myPostComment;
            type = comments.type;
            count = comments.unread;
          }

          if (mShowCommentNotify) {
            if (count == 1) {
              getNM().notify(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ? NotifyUtil.ID_FOLLOW_COMMENT : NotifyUtil.ID_OWN_COMMENT,
                  mNotifyUtil.buildComment(count, comments.lists.get(0).value, type));
            } else if (count > 1) {
              getNM().notify(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ? NotifyUtil.ID_FOLLOW_COMMENT : NotifyUtil.ID_OWN_COMMENT,
                  mNotifyUtil.buildComment(count, null, type));
            }
          }

          Account.inst().setNewCommentCount(count);


          PullNotification newPost = response.object.newPost;
          if (newPost != null) {
            if (newPost.lists != null && newPost.lists.size() != 0) {
              List<PullNotification.Item> lists = newPost.lists;
              for (int i = 0, listsSize = lists.size(); i < listsSize; i++) {
                PullNotification.Item item = lists.get(i);
                getNM().notify(item.value, NotifyUtil.ID_POST, mNotifyUtil.buildPost(i, item.value, item.shortName));
              }
            }
          }

          PullNotification unlock = response.object.newFactoryUnlock;
          if (unlock != null) {
            if (unlock.lists != null && unlock.lists.size() != 0) {
              for (PullNotification.Item item : unlock.lists) {
                getNM().notify(item.value, NotifyUtil.ID_FRIEND_L1_JOIN,
                    mNotifyUtil.buildFriendJoin(item.value, item.shortName, item.friendCount));
              }
            }
          }

          PullNotification friendL1Join = response.object.friendL1Join;
          if (friendL1Join != null) {
            if (friendL1Join.lists != null && friendL1Join.lists.size() != 0) {
              for (PullNotification.Item item : friendL1Join.lists) {
                getNM().notify(item.value, NotifyUtil.ID_FRIEND_L1_JOIN,
                    mNotifyUtil.buildFriendJoin(item.value, item.shortName, item.friendCount));
              }
            }
          }

          U.getBus().post(new NewAllPostCountEvent(circleId, response.object.newPostAllCount));
          U.getBus().post(new NewHotPostCountEvent(circleId, response.object.newPostHotCount));
          U.getBus().post(new NewFriendsPostCountEvent(circleId, response.object.newPostFriendsCount));
        }
      }

      @Override
      public void onResponseError(Throwable e) {
      }
    }, FetchResponse.class));
  }

  private NotificationManager getNM() {
    return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @Override
  public void onDestroy() {
    mHandler.removeMessages(MSG_FETCH);
    super.onDestroy();
  }
}
