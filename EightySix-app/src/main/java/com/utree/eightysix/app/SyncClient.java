package com.utree.eightysix.app;

import com.squareup.otto.Subscribe;
import com.utree.eightysix.U;
import com.utree.eightysix.data.Sync;

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
    mSync = sync;
  }

  public Sync getSync() {
    if (mSync == null) {
      SyncService.start(U.getContext());
    }
    return mSync;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    U.getBus().unregister(this);
  }
}
