/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class UserSignaturesResponse extends Response {

  @SerializedName("object")
  public List<Signature> object;

  public static class Signature {

    @SerializedName("id")
    public String id;

    @SerializedName("signature")
    public String signature;

    @SerializedName("createTime")
    public long timestamp;
  }
}
