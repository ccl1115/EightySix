/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class HometownInfoResponse extends Response {

  @SerializedName("object")
  public HometownInfos object;

  public static class HometownInfos {
    @SerializedName("lists")
    public List<HometownInfo> lists;
  }

  public static class HometownInfo {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("info")
    public String info;

    /**
     * 0 province
     * 1 city
     * 2 county
     */
    @SerializedName("hometownType")
    public int hometownType;
  }
}
