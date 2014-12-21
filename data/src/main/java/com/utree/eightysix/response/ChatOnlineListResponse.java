/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.ChatOnline;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class ChatOnlineListResponse extends Response {

  @SerializedName("object")
  public ChatOnlineList object;

  public static class ChatOnlineList {

    @SerializedName("list")
    public List<ChatOnline> list;
  }
}
