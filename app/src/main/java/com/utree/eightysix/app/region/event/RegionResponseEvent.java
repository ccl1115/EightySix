package com.utree.eightysix.app.region.event;

/**
 */
public class RegionResponseEvent {

  private int region;
  private int mDistance;
  private int areaType;
  private int areaId;
  private String cityName;

  public RegionResponseEvent(int region, int distance, int areaType, int areaId, String cityName) {
    this.region = region;
    mDistance = distance;
    this.areaType = areaType;
    this.areaId = areaId;
    this.cityName = cityName;
  }

  public int getRegion() {
    return region;
  }

  public int getDistance() {
    return mDistance;
  }

  public int getAreaType() {
    return areaType;
  }

  public int getAreaId() {
    return areaId;
  }

  public String getCityName() {
    return cityName;
  }
}

