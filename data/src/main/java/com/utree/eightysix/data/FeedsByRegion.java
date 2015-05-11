package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class FeedsByRegion extends Feeds {

  @SerializedName("regionType")
  public int regionType;

  @SerializedName("regionRadius")
  public int regionRadius;

  @SerializedName("areaType")
  public int areaType;

  @SerializedName("areaId")
  public int areaId;

  @SerializedName("cityName")
  public String cityName;
}
