package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author simon
 */
public class ScanFriends {

  @SerializedName("friends")
  public Paginate<ScanFriend> friends;

  public static class ScanFriend {
    @SerializedName("name")
    public String name;

    @SerializedName("createTime")
    public String createTime;
  }
}
