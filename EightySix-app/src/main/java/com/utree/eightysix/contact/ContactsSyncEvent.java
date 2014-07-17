package com.utree.eightysix.contact;

import java.util.List;

/**
 */
public class ContactsSyncEvent {

  private boolean mSucceed;
  private int mFriendCount;

  public ContactsSyncEvent(boolean succeed, int friendCount) {
    mSucceed = succeed;
    mFriendCount = friendCount;
  }

  public boolean isSucceed() {
    return mSucceed;
  }

  public int getFriendCount() {
    return mFriendCount;
  }
}
