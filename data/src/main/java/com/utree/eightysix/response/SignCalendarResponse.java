/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class SignCalendarResponse extends Response {

  @SerializedName("object")
  public List<SignDate> object;

  @SerializedName("extra")
  public Extra extra;

  public static class SignDate {

    @SerializedName("date")
    public String date;

    @SerializedName("signed")
    public int signed;
  }

  public static class Extra {
    @SerializedName("signConsecutiveTimes")
    public int signConsecutiveTimes;

    @SerializedName("signMissingTimes")
    public int signMissingTimes;

    @SerializedName("costBluestar")
    public int costBluestar;
  }
}
