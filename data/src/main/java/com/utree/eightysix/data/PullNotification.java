package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author simon
 */
public class PullNotification {

  @SerializedName ("type")
  public int type;

  @SerializedName ("msg")
  public String msg;

  @SerializedName ("praise")
  public int praise;

  @SerializedName ("praiseCount")
  public int praiseCount;

  @SerializedName ("percent")
  public String percent;

  @SerializedName("unread")
  public int unread;

  @SerializedName ("lists")
  public List<Item> lists;

  public static class Item {

    @SerializedName ("value")
    public String value;

    @SerializedName ("shortName")
    public String shortName;

    @SerializedName ("friendCount")
    public int friendCount;

    @SerializedName ("factoryId")
    public int factoryId;

    @SerializedName ("currFactory")
    public int currFactory;
  }
}
