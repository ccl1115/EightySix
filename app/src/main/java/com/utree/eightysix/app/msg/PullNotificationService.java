package com.utree.eightysix.app.msg;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.Account;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.data.PullNotification;
import com.utree.eightysix.push.PushMessageReceiver;
import com.utree.eightysix.request.PullNotificationRequest;
import com.utree.eightysix.response.PullNotificationResponse;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import java.util.List;

/**
 * 拉通知
 *
 * @author simon
 */
public class PullNotificationService extends Service {

  private static final int ID_REPORT = 0x2000;

  private NotifyUtil mNotifyUtil;

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

    mNotifyUtil = new NotifyUtil(this);
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
    RequestData data = new RequestData(new PullNotificationRequest(type, seq));
    U.getRESTRequester().request(data, new HandlerWrapper<PullNotificationResponse>(data,
        new OnResponse2<PullNotificationResponse>() {
          @Override
          public void onResponse(PullNotificationResponse response) {
            if (RESTRequester.responseOk(response)) {
              handleResponse(response);
            }
          }

          @Override
          public void onResponseError(Throwable e) {
            if (BuildConfig.DEBUG) e.printStackTrace();
          }
        }, PullNotificationResponse.class));
  }

  private NotificationManager getNM() {
    return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }


  private void handleResponse(PullNotificationResponse response) {
    if (!Account.inst().isLogin() || response.object == null) return;
    final int type = response.object.type;
    handleType(response, type);
  }

  private void handleType(PullNotificationResponse response, int type) {
    switch (type) {
      case PushMessageReceiver.TYPE_NEW_POST: {
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        List<PullNotification.Item> lists = response.object.lists;
        for (int i = 0, listsSize = lists.size(); i < listsSize; i++) {
          PullNotification.Item item = lists.get(i);
          getNM().notify(item.value, NotifyUtil.ID_POST, mNotifyUtil.buildPost(i, item.value, item.shortName));
        }
        break;
      }
      case PushMessageReceiver.TYPE_UNLOCK_CIRCLE: {
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, NotifyUtil.ID_UNLOCK_FACTORY,
              mNotifyUtil.buildUnlockCircle(item.value, item.shortName, item.currFactory == 1));
        }
        break;
      }
      case PushMessageReceiver.TYPE_FRIEND_L1_JOIN: {
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, NotifyUtil.ID_FRIEND_L1_JOIN,
              mNotifyUtil.buildFriendJoin(item.value, item.shortName, item.currFactory == 1));
        }
        break;
      }
      case PushMessageReceiver.TYPE_OWN_COMMENT:
      case PushMessageReceiver.TYPE_FOLLOW_COMMENT: {
        int count = 0;
        if (response.object.lists != null) {
          count = response.object.unread;
          if (count == 1) {
            PullNotification.Item item = response.object.lists.get(0);
            getNM().notify(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ? NotifyUtil.ID_FOLLOW_COMMENT : NotifyUtil.ID_OWN_COMMENT,
                mNotifyUtil.buildComment(count, item.value, type, item.currFactory == 1, item.factoryId));
          } else if (count > 1) {
            getNM().notify(type == PushMessageReceiver.TYPE_FOLLOW_COMMENT ? NotifyUtil.ID_FOLLOW_COMMENT : NotifyUtil.ID_OWN_COMMENT,
                mNotifyUtil.buildComment(count, null, type, false, 0));
          }
          Account.inst().setNewCommentCount(count);
        }
        break;
      }
      case PushMessageReceiver.TYPE_PRAISE: {
        Account.inst().setHasNewPraise(true);
        break;
      }
      case PushMessageReceiver.TYPE_CIRCLE_CREATION_APPROVE: {
        if (response.object.lists == null || response.object.lists.size() == 0) break;
        for (PullNotification.Item item : response.object.lists) {
          getNM().notify(item.value, NotifyUtil.ID_APPROVE, mNotifyUtil.buildApprove(item.value, item.shortName));
        }
        break;
      }
      case PushMessageReceiver.TYPE_MERGED_NEW_POST: {
        if (response.object.lists == null || response.object.lists.size() == 0) break;

        for (PullNotification.Item item : response.object.lists) {
          int count = Integer.parseInt(item.value);
          if (count == 0) return;
          getNM().notify(String.valueOf(item.factoryId), NotifyUtil.ID_POST,
              mNotifyUtil.buildPosts(item.shortName, item.currFactory == 1, item.factoryId, count));
        }
        break;
      }
      case PushMessageReceiver.TYPE_BLUE_STAR: {
        if (!TextUtils.isEmpty(response.object.msg)) {
          getNM().notify(NotifyUtil.ID_BLUE_STAR, mNotifyUtil.buildBlueStar(response.object.msg));
        }
      }
    }
  }

}
