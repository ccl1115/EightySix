package com.utree.eightysix.app.region.event;

import com.utree.eightysix.data.Circle;

/**
 */
public class RegionUpdateEvent {

  private int region;

  private Circle circle;

  public RegionUpdateEvent(int region, Circle circle) {
    this.region = region;
    this.circle = circle;
  }

  public int getRegion() {
    return region;
  }

  public Circle getCircle() {
    return circle;
  }
}

