package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author simon
 */
public class BaseCircle {
  @SerializedName("name")
  public String name;

  @SerializedName("shortName")
  public String shortName;

  @SerializedName("friendCount")
  public int friendCount;

  @SerializedName("id")
  public int id;

  @SerializedName("workmateCount")
  public int workmateCount;
}
