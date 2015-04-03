/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class FriendChatResponse extends Response {

  @SerializedName("object")
  public FriendChat object;

  public static class FriendChat {

    @SerializedName("chatId")
    public String chatId;

    @SerializedName("myAvatar")
    public String myAvatar;

    @SerializedName("myName")
    public String myName;

    @SerializedName("targetAvatar")
    public String targetAvatar;

    @SerializedName("targetName")
    public String targetName;

    @SerializedName("factoryName")
    public String factoryName;
  }
}
