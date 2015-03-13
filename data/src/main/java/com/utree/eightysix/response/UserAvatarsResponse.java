/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class UserAvatarsResponse extends Response {

  @SerializedName("object")
  public List<Avatar> object;

  public static class Avatar {

    @SerializedName("avatar")
    public String avatar;
  }
}
