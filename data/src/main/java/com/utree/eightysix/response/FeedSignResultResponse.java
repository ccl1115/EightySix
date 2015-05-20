/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class FeedSignResultResponse extends Response {

  @SerializedName("object")
  public FeedSignResult object;

  public static class FeedSignResult {
    @SerializedName("consecutiveTimes")
    public int consecutiveTimes;

    @SerializedName("experience")
    public int experience;

    @SerializedName("bluestar")
    public int bluestar;

    @SerializedName("rank")
    public int rank;
  }
}
