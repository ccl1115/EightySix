/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 */
public class FriendRequest implements Parcelable {

  @SerializedName("userName")
  public String userName;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("level")
  public String level;

  @SerializedName("levelIcon")
  public String levelIcon;

  @SerializedName("signature")
  public String signature;

  @SerializedName("viewId")
  public int viewId;

  @SerializedName("wokrinFactoryId")
  public String workinFactoryId;

  @SerializedName("workinFactoryName")
  public String workinFactoryName;

  @SerializedName("content")
  public String content;

  @SerializedName("type")
  public String type;

  @SerializedName("createTime")
  public long timestamp;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FriendRequest request = (FriendRequest) o;

    if (viewId != request.viewId) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return viewId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.userName);
    dest.writeString(this.avatar);
    dest.writeString(this.level);
    dest.writeString(this.levelIcon);
    dest.writeString(this.signature);
    dest.writeInt(this.viewId);
    dest.writeString(this.workinFactoryId);
    dest.writeString(this.workinFactoryName);
    dest.writeString(this.content);
    dest.writeString(this.type);
    dest.writeLong(this.timestamp);
  }

  public FriendRequest() {
  }

  private FriendRequest(Parcel in) {
    this.userName = in.readString();
    this.avatar = in.readString();
    this.level = in.readString();
    this.levelIcon = in.readString();
    this.signature = in.readString();
    this.viewId = in.readInt();
    this.workinFactoryId = in.readString();
    this.workinFactoryName = in.readString();
    this.content = in.readString();
    this.type = in.readString();
    this.timestamp = in.readLong();
  }

  public static final Parcelable.Creator<FriendRequest> CREATOR = new Parcelable.Creator<FriendRequest>() {
    public FriendRequest createFromParcel(Parcel source) {
      return new FriendRequest(source);
    }

    public FriendRequest[] newArray(int size) {
      return new FriendRequest[size];
    }
  };
}
