/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Friend;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class FriendListResponse extends Response {

  @SerializedName("object")
  public List<Friend> object;

  @SerializedName("extra")
  public Extra extra;

  public static class Extra {
    @SerializedName("viewId")
    public String viewId;
  }
}
