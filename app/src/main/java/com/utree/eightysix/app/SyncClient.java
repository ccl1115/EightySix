package com.utree.eightysix.app;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.M;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Sync;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class SyncClient {

  private Sync mSync;

  public SyncClient() {
    M.getRegisterHelper().register(this);
  }

  @Subscribe
  public void onSyncEvent(Sync sync) {
    Log.d("SyncClient", sync.toString());
    mSync = sync;
    Env.setHostIp(mSync.ip);
  }

  public void requestSync() {
    SyncService.start(U.getContext());
  }

  public Sync getSync() {
    if (mSync == null) {
      SyncService.start(U.getContext());
    }
    return mSync;
  }
}
