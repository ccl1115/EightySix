/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class Hometown {

  @SerializedName("id")
  public int id;

  @SerializedName("name")
  public String name;

  @Override
  public String toString() {
    return name;
  }
}
