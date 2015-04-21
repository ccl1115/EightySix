/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class UserSetupResponse extends Response {

  @SerializedName("object")
  public Status object;

  public static class Status {
    @SerializedName("status")
    public String status;

    @SerializedName("praiseNotRemind")
    public int praiseNotRemind;
  }
}
