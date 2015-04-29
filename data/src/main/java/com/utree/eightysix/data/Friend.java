/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Friend {

  @SerializedName("userName")
  public String userName;

  @SerializedName("name")
  public String name;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("level")
  public int level;

  @SerializedName("levelIcon")
  public String levelIcon;

  @SerializedName("signature")
  public String signature;

  @SerializedName("viewId")
  public int viewId;

  @SerializedName("workinFactoryId")
  public int workinFactoryId;

  @SerializedName("initial")
  public String initial;

  @SerializedName("workinFactory")
  public String workinFactory;

  @SerializedName("relation")
  public String relation;

  @SerializedName("type")
  public String type;

  @SerializedName("source")
  public String source;

  @SerializedName("isFriend")
  public int isFriend;
}

