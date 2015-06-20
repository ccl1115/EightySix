/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

import java.util.List;

/**
 */
public class MessageUnreadResponse extends Response {

  @SerializedName("object")
  public MessageUnread object;

  public static class MessageUnread {

    @SerializedName("passedFriend")
    public List<PassedFriend> passedFriends;

    @SerializedName("passedFriendCount")
    public int passedFriendCount;

    @SerializedName("addedFriendCount")
    public int addedFriendCount;
  }

  public static class PassedFriend {

    @SerializedName("username")
    public String username;

    @SerializedName("viewId")
    public int viewId;
  }
}
