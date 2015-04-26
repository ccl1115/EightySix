/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 */
public class MessageUnreadResponse  {

  public static class MessageUnread {

    @SerializedName("passedFriend")
    public List<PassedFriend> passedFriends;

    @SerializedName("passedFriendCount")
    public int passedFriendCount;
  }

  public static class PassedFriend {

    @SerializedName("username")
    public String username;

    @SerializedName("viewId")
    public int viewId;
  }
}
