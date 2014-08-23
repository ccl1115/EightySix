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
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.RequestData;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class FetchNotificationService extends Service {

  public static final String TAG = "FetchNotificationService";
  private static final int FETCH_INTERVAL = 60000;
  private static final int MSG_FETCH = 0x1;


  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      sendEmptyMessageDelayed(MSG_FETCH, FETCH_INTERVAL);
      requestFetch();
    }
  };

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "start FetchService");
    mHandler.sendEmptyMessage(MSG_FETCH);
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void requestFetch() {
    Log.d(TAG, "requestFetch");
    RequestData data = U.getRESTRequester().convert(new FetchNotificationRequest());
    U.getRESTRequester().request(data, new HandlerWrapper<FetchResponse>(data, new OnResponse2<FetchResponse>() {

      @Override
      public void onResponse(FetchResponse response) {
        if (RESTRequester.responseOk(response)) {
          if (response.object.newPraise != null &&
              response.object.newPraise.praise == 1) {
            Account.inst().setHasNewPraise(true);
          }

          int count = response.object.newComment.unread;

          Account.inst().setNewCommentCount(count);
        }
      }

      @Override
      public void onResponseError(Throwable e) {
        e.printStackTrace();
      }
    }, FetchResponse.class));
  }

  @Override
  public void onDestroy() {
    mHandler.removeMessages(MSG_FETCH);
    super.onDestroy();
  }
}
