package com.utree.eightysix.app.msg.event;

/**
 */
public class NewAllPostCountEvent {

  private int mCount;
  private int mCircleId;

  public NewAllPostCountEvent(int circleId, int count) {
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
