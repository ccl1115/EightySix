package com.utree.eightysix.app.msg;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.utree.eightysix.U;
import com.utree.eightysix.request.PullNotificationRequest;
import com.utree.eightysix.rest.HandlerWrapper;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.RequestData;
import com.utree.eightysix.rest.Response;

/**
 * 拉通知
 *
 * @author simon
 */
public class PullNotificationService extends IntentService {

  private Handler mHandler = new Handler();

  public PullNotificationService() {
    super("PullNotificationService");
  }

  public static void start(Context context, int type, String seq) {
    Intent intent = new Intent(context, PullNotificationService.class);
    intent.putExtra("type", type);
    intent.putExtra("seq", seq);
    context.startService(intent);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    int type = intent.getIntExtra("type", 0);
    String seq = intent.getStringExtra("seq");
    requestPullNotification(type, seq);
  }

  private void requestPullNotification(final int type, final String seq) {

    mHandler.post(new Runnable() {
      @Override
      public void run() {
        RequestData data = U.getRESTRequester().convert(new PullNotificationRequest(type, seq));
        U.getRESTRequester().request(data, new HandlerWrapper<Response>(data, new OnResponse<Response>() {

          @Override
          public void onResponse(Response response) {

          }
        }, Response.class));
      }
    });
  }
}
