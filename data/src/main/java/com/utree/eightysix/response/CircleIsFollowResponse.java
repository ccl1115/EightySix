/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class CircleIsFollowResponse extends Response {

  @SerializedName("object")
  public Followed object;

  public static class Followed {

    @SerializedName("whetherLike")
    public int followed;
  }
}
