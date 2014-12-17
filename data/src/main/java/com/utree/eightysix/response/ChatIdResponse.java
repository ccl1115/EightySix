/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class ChatIdResponse extends Response {

  @SerializedName("object")
  public ChatId object;

  public static class ChatId {

    @SerializedName("chatId")
    public String chatId;
  }
}
