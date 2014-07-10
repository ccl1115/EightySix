package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
* @author simon
*/
public class PullNotification {

  @SerializedName ("type")
  public int type;

  @SerializedName("ids")
  public String[] ids;

  @SerializedName("msg")
  public String msg;

  @SerializedName("praise")
  public int praise;

  @SerializedName("shortName")
  public String shortName;
}
