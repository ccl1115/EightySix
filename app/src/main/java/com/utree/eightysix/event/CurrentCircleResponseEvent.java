package com.utree.eightysix.event;

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
