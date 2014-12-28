/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class ChatInfoResponse extends Response {

  @SerializedName("object")
  public ChatInfo object;

  public static class ChatInfo {

    @SerializedName("chatId")
    public String chatId;

    @SerializedName("targetAvatar")
    public String targetAvatar;

    @SerializedName("myAvatar")
    public String myAvatar;
  }
}
