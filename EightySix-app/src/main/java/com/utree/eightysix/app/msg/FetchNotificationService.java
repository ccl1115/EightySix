package com.utree.eightysix.app.msg;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.utree.eightysix.Account;
import com.utree.eightysix.U;
import com.utree.eightysix.request.FetchNotificationRequest;
import com.utree.eightysix.response.FetchResponse;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;

/**
 * @author simon
 */
public class FetchNotificationService extends Service {

  private static final int FETCH_INTERVAL = 60000;
  private static final int MSG_FETCH = 0x1;

  private long mLastFetchTime;


  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      final long now = System.currentTimeMillis();
      mLastFetchTime = now + FETCH_INTERVAL;
      sendEmptyMessageAtTime(MSG_FETCH, mLastFetchTime);
    }
  };

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    mLastFetchTime = System.currentTimeMillis() + FETCH_INTERVAL;
    mHandler.sendEmptyMessageAtTime(MSG_FETCH, mLastFetchTime);
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void requestFetch() {
    RequestData data = U.getRESTRequester().convert(new FetchNotificationRequest());
    U.getRESTRequester().request(data, new HandlerWrapper<FetchResponse>(data, new OnResponse<FetchResponse>() {

      @Override
      public void onResponse(FetchResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.newPraise.praise == 1) {
            Account.inst().setHasNewPraise(true);
          }

          int count = 0;

          count += response.object.myPostComment.lists.size();
          count += response.object.newComment.lists.size();

          Account.inst().incNewCommentCount(count);
        }
      }
    }, FetchResponse.class));
  }
}
