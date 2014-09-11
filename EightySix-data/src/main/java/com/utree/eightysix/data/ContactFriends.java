package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author simon
 */
public class ContactFriends {

  @SerializedName("unRegCount")
  public int unRegCount;

  @SerializedName("friends")
  public Paginate<Friend> friends;

  public static class Friend {
    @SerializedName("name")
    public String name;

    @SerializedName("viewId")
    public String viewId;
  }
}
