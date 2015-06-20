/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.region.event;

import com.utree.eightysix.data.Circle;

/**
 */
public class CircleResponseEvent {

  private Circle mCircle;

  public CircleResponseEvent(Circle circle) {

    mCircle = circle;
  }

  public Circle getCircle() {
    return mCircle;
  }
}
