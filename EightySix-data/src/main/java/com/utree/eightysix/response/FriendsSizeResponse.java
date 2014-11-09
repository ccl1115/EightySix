package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.rest.Response;

/**
 */
public class FriendsSizeResponse extends Response {

  @SerializedName("object")
  public FriendsSize object;

  public static class FriendsSize {
    @SerializedName("friendsSize")
    public int friendsSize;
  }
}
