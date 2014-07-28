package com.utree.eightysix.app;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Sync;
import de.akquinet.android.androlog.Log;

/**
 * @author simon
 */
public class SyncClient {

  private Sync mSync;

  public SyncClient() {
    U.getBus().register(this);
  }

  @Subscribe
  public void onSyncEvent(Sync sync) {
    Log.d("SyncClient", sync.toString());
    mSync = sync;
  }

  public Sync getSync() {
    if (mSync == null) {
      SyncService.start(U.getContext());
    }
    return mSync;
  }
}
