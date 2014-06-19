package com.utree.eightysix.response.data;

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
  public int id;

  @SerializedName ("userAvatar")
  public String avatar;

  public char portrait;

  public int praised;

  public int isHost;
}
