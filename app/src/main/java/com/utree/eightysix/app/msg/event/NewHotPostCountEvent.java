package com.utree.eightysix.app.msg.event;

/**
 */
public class NewHotPostCountEvent {

  private int mCount;
  private int mCircleId;

  public NewHotPostCountEvent(int circleId, int count) {
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
