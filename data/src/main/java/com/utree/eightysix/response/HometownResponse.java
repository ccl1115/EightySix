/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.Hometown;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class HometownResponse extends Response {

  @SerializedName("object")
  public Hometowns object;

  public static class Hometowns {
    @SerializedName("lists")
    public List<Hometown> lists;
  }
}
