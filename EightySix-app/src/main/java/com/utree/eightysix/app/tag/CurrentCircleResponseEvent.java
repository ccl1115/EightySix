package com.utree.eightysix.app.tag;

import com.utree.eightysix.data.Circle;

/**
 */
public class CurrentCircleResponseEvent {
  private Circle mCircle;

  public CurrentCircleResponseEvent(Circle circle) {
    mCircle = circle;
  }

  public Circle getCircle() {
    return mCircle;
  }
}
