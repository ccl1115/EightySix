package com.utree.eightysix.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.utree.eightysix.U;
import com.utree.eightysix.rest.RESTRequester;
import java.io.IOException;

/**
 * @author simon
 */
public class PingService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    try {
      Process process = new ProcessBuilder("ping", U.getConfig("api.host").replace("http://", ""), "-c 3").start();
      if (process.waitFor() == 0) {
        U.getRESTRequester().setHost(U.getConfig("api.host"));
      } else {
        if (Env.getHostIp() != null) {
          U.getRESTRequester().setHost("http://" + Env.getHostIp());
        } else {
          U.getAnalyser().reportError(this, "PingService: failed to update host, not get ip from sync api");
        }

      }
    } catch (IOException ignored) {
      U.getAnalyser().reportError(this, "PingService: failed to ping host, IOException");
    } catch (InterruptedException ignored) {
      U.getAnalyser().reportError(this, "PingService: failed to ping host, InterruptedException");
    }
    return START_NOT_STICKY;
  }

}
