package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class ActiveJoin {

  @SerializedName("msg1")
  public String msg1;

  @SerializedName("msg2")
  public String msg2;

  @SerializedName("msg3")
  public String msg3;

  @SerializedName("needFriends")
  public int needFriends;

  @SerializedName("virtualId")
  public String virtualId;

  @SerializedName("currentFactory")
  public int currentFactory;
}
