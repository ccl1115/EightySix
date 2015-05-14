/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class BaseUser {
  @SerializedName("userName")
  public String userName;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("viewId")
  public int viewId;

  @SerializedName("workinFactoryId")
  public int workinFactoryId;

  @SerializedName("workinFactory")
  public String workinFactory;

}
