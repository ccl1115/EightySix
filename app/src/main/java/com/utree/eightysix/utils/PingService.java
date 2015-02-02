package com.utree.eightysix.utils;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseApplication;
import de.akquinet.android.androlog.Log;
import java.io.IOException;

/**
 * @author simon
 */
public class PingService extends IntentService {

  private static final String TAG = "PingService";

  public PingService() {
    super(TAG);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      ProcessBuilder ping = new ProcessBuilder("ping", "-c 3", U.getConfig("api.host").replace("http://", "").split(":")[0]);
      Process process = ping.start();
      if (process.waitFor() == 0) {
        Log.d(TAG, "ping success");
        BaseApplication.getHandler().post(new Runnable() {
          @Override
          public void run() {
            U.getRESTRequester().setHost(U.getConfig("api.host"));
          }
        });
      } else {
        if (Env.getHostIp() != null) {
          Log.d(TAG, "ping failed, use sync ip");
          BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
              U.getRESTRequester().setHost("http://" + Env.getHostIp());
            }
          });
        } else {
          Log.d(TAG, "ping failed, no sync ip");
          BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
              U.getRESTRequester().setHost("http://" + U.getConfig("api.ip"));
            }
          });
        }

      }
    } catch (IOException e) {
      Log.d(TAG, "ping failed, io exception");
    } catch (InterruptedException e) {
      Log.d(TAG, "ping failed, interrupted exception");
    } catch (Exception e) {
      Log.d(TAG, "ping failed: " + e.toString());
    }
  }

}
