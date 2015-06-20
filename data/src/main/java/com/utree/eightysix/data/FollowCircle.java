/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class FollowCircle {

  @SerializedName("factoryId")
  public int factoryId;

  @SerializedName("factoryName")
  public String factoryName;

  @SerializedName("factoryType")
  public int factoryType;

  @SerializedName("workerCount")
  public int workerCount;

  @SerializedName("friendCount")
  public int friendCount;
}
