package com.utree.eightysix.event;

/**
 * Fire when PullNotificationService fetch new comment count
 *
 * @author simon
 */
public class NewCommentCountEvent {
  private int mCount;

  public NewCommentCountEvent(int count) {
    mCount = count;
  }

  public int getCount() {
    return mCount;
  }
}
