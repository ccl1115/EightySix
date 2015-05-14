/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class PraisedUser extends BaseUser {

  @SerializedName("consecutiveTimes")
  public int consecutiveTimes;

  @SerializedName("totalTimes")
  public int totalTimes;

  @SerializedName("createTime")
  public long timestamp;
}
