package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.ScanFriends;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class ScanFriendsResponse extends Response {

  @SerializedName("object")
  public ScanFriends object;
}
