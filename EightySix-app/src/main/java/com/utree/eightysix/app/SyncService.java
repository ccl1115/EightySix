package com.utree.eightysix.app;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.utree.eightysix.data.Sync;

/**
 * @author simon
 */
public class SyncService extends IntentService {

  private static Sync sSync;

  public SyncService() {
    super("SyncService");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  protected void onHandleIntent(Intent intent) {

  }

  private void requestSync() {

  }
}
