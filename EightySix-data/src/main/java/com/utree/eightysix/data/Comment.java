package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 */
public class Comment implements Serializable {

  @SerializedName ("floor")
  public int floor;

  @SerializedName ("content")
  public String content;

  @SerializedName ("countPraise")
  public int praise;

  @SerializedName ("ownerPraise")
  public int ownerPraise;

  @SerializedName ("createTime")
  public long timestamp;

  @SerializedName ("id")
  public String id;

  @SerializedName ("userAvatar")
  public String avatar;

  @SerializedName ("avatarColor")
  public String avatarColor;

  @SerializedName ("selfPraise")
  public int praised;

  @SerializedName ("self")
  public int self;

  @SerializedName ("poster")
  public int owner;

  @SerializedName("commentViewTime")
  public String time;

  @SerializedName("distance")
  public String distance;

  @SerializedName("chatId")
  public String chatId;

  /**
   * 1 deleted
   * 0 not deleted
   */
  @SerializedName("delete")
  public int delete;

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Comment comment = (Comment) o;

    if (!id.equals(comment.id)) return false;

    return true;
  }
}
