/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.RankedUser;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class WeekRankResponse extends Response {

  @SerializedName("object")
  public List<RankedUser> object;

  @SerializedName("extra")
  public Extra extra;

  public static class Extra {

    @SerializedName("banner")
    public Banner banner;

    @SerializedName("copywriting")
    public String info;
  }

  public static class Banner {

    @SerializedName("flag")
    public String flag;

    @SerializedName("bgUrl")
    public String bgUrl;

    @SerializedName("text")
    public String text;

    @SerializedName("cmd")
    public String cmd;

    @SerializedName("bgColor")
    public String bgColor;

  }
}
