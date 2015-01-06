/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 */
public class ChatOnline {

  @SerializedName("chatId")
  public String chatId;

  @SerializedName("isOnline")
  public int isOnline;

  @SerializedName("offlineDuration")
  public long offlineDuration;
}
