package com.utree.eightysix.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.U;
import com.utree.eightysix.response.SyncResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;

/**
 * @author simon
 */
public class SyncService extends Service {

  private static boolean sSyncing;

  public static void start(Context context) {
    if (!sSyncing) {
      context.startService(new Intent(context, SyncService.class));
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (!sSyncing) {
      requestSync();
    }
    return START_NOT_STICKY;
  }

  private void requestSync() {
    sSyncing = true;
    U.request("sync", new OnResponse2<SyncResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        if (BuildConfig.DEBUG) {
          U.showToast("Sync Error");
        }
        sSyncing = false;
      }

      @Override
      public void onResponse(SyncResponse response) {
        if (RESTRequester.responseOk(response) && response.object != null) {
          U.getBus().post(response.object);
        } else {
          if (BuildConfig.DEBUG) {
            U.showToast("Sync Error");
          }
        }
        sSyncing = false;
      }
    }, SyncResponse.class);
  }
}
