package com.utree.eightysix.app.feed.event;

/**
 * @author simon
 */
public class UpdatePraiseCountEvent {
  public int mCount;
  public String mPercent;

  public UpdatePraiseCountEvent(int count, String percent) {
    mCount = count;
    mPercent = percent;
  }

  public int getCount() {
    return mCount;
  }

  public String getPercent() {
    return mPercent;
  }
}
