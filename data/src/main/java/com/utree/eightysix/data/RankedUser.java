/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class RankedUser extends BaseUser {

  @SerializedName("rank")
  public int rank;

  @SerializedName("level")
  public int level;

  @SerializedName("levelIcon")
  public String levelIcon;

  @SerializedName("experience")
  public int experience;

  @SerializedName("change")
  public String change;
}
