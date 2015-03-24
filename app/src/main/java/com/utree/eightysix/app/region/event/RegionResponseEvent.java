package com.utree.eightysix.app.region.event;

/**
 */
public class RegionResponseEvent {

  private int region;
  private int mDistance;

  public RegionResponseEvent(int region, int distance) {
    this.region = region;
    mDistance = distance;
  }

  public int getRegion() {
    return region;
  }

  public int getDistance() {
    return mDistance;
  }
}

