package com.utree.eightysix.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.RESTRequester;
import de.akquinet.android.androlog.Log;
import java.io.IOException;

/**
 * @author simon
 */
public class PingService extends Service {

  private static final String TAG = "PingService";

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    try {
      ProcessBuilder ping = new ProcessBuilder("ping", "-c 3", U.getConfig("api.host").replace("http://", "").split(":")[0]);
      Process process = ping.start();
      if (process.waitFor() == 0) {
        Log.d(TAG, "ping success");
        U.getRESTRequester().setHost(U.getConfig("api.host"));
      } else {
        if (Env.getHostIp() != null) {
          Log.d(TAG, "ping failed, use sync ip");
          U.getRESTRequester().setHost("http://" + Env.getHostIp());
        } else {
          Log.d(TAG, "ping failed, no sync ip");
          U.getAnalyser().reportError(this, "PingService: failed to update host, not get ip from sync api");
        }

      }
    } catch (IOException e) {
      Log.d(TAG, "ping failed, io exception");
      U.getAnalyser().reportError(this, "PingService: failed to ping host, IOException");
    } catch (InterruptedException e) {
      Log.d(TAG, "ping failed, interrupted exception");
      U.getAnalyser().reportError(this, "PingService: failed to ping host, InterruptedException");
    }
    return START_NOT_STICKY;
  }

}
