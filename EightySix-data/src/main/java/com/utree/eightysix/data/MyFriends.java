package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author simon
 */
public class MyFriends {

  @SerializedName("friendCount")
  public int friendCount;

  @SerializedName("contactsCount")
  public int contactsCount;

  @SerializedName("qrCodeFriends")
  public int qrCodeFriends;

  @SerializedName("myViewId")
  public String myViewId;


  @SerializedName("factoryFriends")
  public List<CircleFriends> circleFriends;

  public static class CircleFriends {

    @SerializedName("name")
    public String name;

    @SerializedName("friendCount")
    public int friendCount;
  }
}
