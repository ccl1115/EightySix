/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class FeedIntent extends BaseItem {

  @SerializedName("message")
  public AppIntent appIntent;

  @SerializedName("title")
  public String title;

  @SerializedName("subTitle")
  public String subTitle;

  @SerializedName("buttonText")
  public String buttonText;
}
