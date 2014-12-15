package com.utree.eightysix.app.msg.event;

/**
 */
public class NewFriendsPostCountEvent {

  private int mCount;
  private int mCircleId;

  public NewFriendsPostCountEvent(int circleId, int count) {
    mCircleId = circleId;
    mCount = count;
  }

  public int getCount() {
    return mCount;
  }

  public int getCircleId() {
    return mCircleId;
  }
}
