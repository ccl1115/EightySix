/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class FeedIntent extends BaseItem {

  @SerializedName("appIntent")
  public AppIntent appIntent;
}
