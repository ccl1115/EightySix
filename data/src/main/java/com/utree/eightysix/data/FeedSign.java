/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.response.FeedsByRegionResponse;

/**
 */
public class FeedSign extends BaseItem {

  @SerializedName("signConsecutiveTimes")
  public int signConsecutiveTimes;

  @SerializedName("signed")
  public int signed;

  @SerializedName("signMissingTimes")
  public int signMissingTimes;

  public FeedSign(FeedsByRegionResponse.Extra extra) {
    super(BaseItem.TYPE_SIGN);
    signConsecutiveTimes = extra.signConsecutiveTimes;
    signed = extra.signed;
    signMissingTimes = extra.signMissingTimes;
  }
}
