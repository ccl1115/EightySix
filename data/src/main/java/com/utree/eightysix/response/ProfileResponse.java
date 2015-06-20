/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Profile;
import com.utree.eightysix.rest.Response;

/**
 */
public class ProfileResponse extends Response {

  @SerializedName("object")
  public Profile object;
}
