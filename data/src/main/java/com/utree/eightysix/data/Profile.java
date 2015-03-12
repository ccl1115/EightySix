/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Profile {

  @SerializedName("id")
  public String id;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("sex")
  public String sex;

  @SerializedName("age")
  public int age;

  @SerializedName("userName")
  public String userName;

  @SerializedName("birthday")
  public long birthday;

  @SerializedName("constellation")
  public String constellation;

  @SerializedName("background")
  public String background;

  @SerializedName("signature")
  public String signature;

  @SerializedName("experience")
  public String experience;

  @SerializedName("nextExperience")
  public String nextExperience;

  @SerializedName("level")
  public int level;

  @SerializedName("levelIcon")
  public String levelIcon;

  @SerializedName("nextLevel")
  public int nextLevel;

  @SerializedName("workinFactoryId")
  public int workinFactoryId;

  @SerializedName("workinFactoryName")
  public String workinFactoryName;

  @SerializedName("hometown")
  public String hometown;
}