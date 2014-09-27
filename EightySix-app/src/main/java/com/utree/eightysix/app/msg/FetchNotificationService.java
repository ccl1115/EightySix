package com.utree.eightysix.app.msg;

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
    mHandler.sendEmptyMessageDelayed(MSG_FETCH, 5000);
    return START_NOT_STICKY;
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
    Log.d(TAG, "requestFetch");
    if (sCircleId == 0) return;

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
          if (response.object.newComment != null) {
            count = response.object.newComment.unread;
          } else if (response.object.myPostComment != null) {
            count = response.object.myPostComment.unread;
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
