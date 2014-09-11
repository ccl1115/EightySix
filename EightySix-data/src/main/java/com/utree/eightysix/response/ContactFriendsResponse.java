package com.utree.eightysix.response;

import com.google.gson.annotations.SerializedName;
import com.utree.eightysix.data.ContactFriends;
import com.utree.eightysix.rest.Response;

/**
 * @author simon
 */
public class ContactFriendsResponse extends Response {

  @SerializedName("object")
  public ContactFriends object;
}
