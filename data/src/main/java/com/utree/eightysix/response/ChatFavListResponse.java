/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.ChatFav;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class ChatFavListResponse extends Response {


  @SerializedName("object")
  public ChatFavs object;


  public static class ChatFavs {
    @SerializedName("list")
    public List<ChatFav> list;
  }
}
