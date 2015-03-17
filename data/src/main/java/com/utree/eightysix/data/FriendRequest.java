/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 */
public class FriendRequest implements Serializable {

  @SerializedName("userName")
  public String userName;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("level")
  public String level;

  @SerializedName("levelIcon")
  public String levelIcon;

  @SerializedName("signature")
  public String signature;

  @SerializedName("viewId")
  public int viewId;

  @SerializedName("wokrinFactoryId")
  public String workinFactoryId;

  @SerializedName("workinFactoryName")
  public String workinFactoryName;

  @SerializedName("content")
  public String content;

  @SerializedName("type")
  public String type;

  @SerializedName("createTime")
  public long timestamp;
}
