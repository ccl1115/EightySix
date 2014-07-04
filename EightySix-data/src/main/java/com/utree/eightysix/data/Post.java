package com.utree.eightysix.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 */
public class Post implements Serializable {
  @SerializedName ("bgUrl")
  public String bgUrl;

  @SerializedName ("bgColor")
  public String bgColor;

  @SerializedName ("content")
  public String content;

  @SerializedName ("countComment")
  public int comments;

  @SerializedName ("countPraise")
  public int praise;

  @SerializedName ("id")
  public String id;

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  @SerializedName ("postSource")
  public String source;

  @SerializedName ("readed")
  public int read;

  @SerializedName ("commentMsg")
  public String comment;

  @SerializedName("commentHead")
  public String commentHead;

  @SerializedName("commentTail")
  public String commentTail;

  @SerializedName ("whoAtMe")
  public String whoAtMe;

  @SerializedName("praised")
  public int praised;

  public int permission;


}
