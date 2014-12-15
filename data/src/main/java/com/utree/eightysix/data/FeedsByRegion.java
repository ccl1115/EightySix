package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class FeedsByRegion extends Feeds {

  @SerializedName("regionType")
  public int regionType;

  @SerializedName("subInfo")
  public String subInfo;
}
