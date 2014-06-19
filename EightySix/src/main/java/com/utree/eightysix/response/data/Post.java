package com.utree.eightysix.response.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 */
public class Post implements Serializable {
  @SerializedName ("bgUrl")
  public String bgUrl;

  @SerializedName ("bgColor")
  public int bgColor;

  @SerializedName ("content")
  public String content;

  @SerializedName ("countComment")
  public int comments;

  @SerializedName ("countPraise")
  public int praise;

  @SerializedName ("id")
  public int id;

  @SerializedName ("myPraiseCount")
  public int myPraiseCount;

  @SerializedName ("postSource")
  public String source;

  @SerializedName ("readed")
  public boolean read;

  @SerializedName ("viewComment")
  public Comment comment;

  @SerializedName ("whoAtMe")
  public String whoAtMe;

  public int praised;

  public int permission;

}
