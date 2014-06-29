package com.utree.eightysix.contact;

import java.util.List;

/**
 */
public class ContactsSyncEvent {

  private boolean mSucceed;

  public ContactsSyncEvent(boolean succeed) {
    mSucceed = succeed;
  }

  public boolean isSucceed() {
    return mSucceed;
  }

}
