package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.MyFriends;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class MyFriendsResponse extends Response {

  @SerializedName("object")
  public MyFriends object;
}
