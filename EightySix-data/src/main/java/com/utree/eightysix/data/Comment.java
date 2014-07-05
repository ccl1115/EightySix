package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 */
public class Comment implements Serializable {

  @SerializedName ("content")
  public String content;

  @SerializedName ("countPraise")
  public int praise;

  @SerializedName ("createTime")
  public long timestamp;

  @SerializedName ("id")
  public String id;

  @SerializedName ("userAvatar")
  public String avatar;

  @SerializedName("avatarColor")
  public String avatarColor;

  @SerializedName("selfPraise")
  public int praised;

  @SerializedName("self")
  public int isHost;
}
