package com.utree.eightysix.app.region.event;

/**
 */
public class RegionResponseEvent {

  private int region;

  public RegionResponseEvent(int region, int distance) {
    this.region = region;
  }

  public int getRegion() {
    return region;
  }
}

