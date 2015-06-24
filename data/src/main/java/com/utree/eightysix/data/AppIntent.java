/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
*/
public class AppIntent implements Parcelable {

  /**
   * 1000 push
   * 1001 ad
   * 1002 push text
   */
  @SerializedName("type")
  public int type;

  @SerializedName("pushFlag")
  public String pushFlag;

  /**
   * "feed:factoryId"
   */
  @SerializedName("cmd")
  public String cmd;

  @SerializedName ("title")
  public String title;

  @SerializedName ("content")
  public String content;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.type);
    dest.writeString(this.pushFlag);
    dest.writeString(this.cmd);
    dest.writeString(this.title);
    dest.writeString(this.content);
  }

  public AppIntent() {
  }

  private AppIntent(Parcel in) {
    this.type = in.readInt();
    this.pushFlag = in.readString();
    this.cmd = in.readString();
    this.title = in.readString();
    this.content = in.readString();
  }

  public static final Parcelable.Creator<AppIntent> CREATOR = new Parcelable.Creator<AppIntent>() {
    public AppIntent createFromParcel(Parcel source) {
      return new AppIntent(source);
    }

    public AppIntent[] newArray(int size) {
      return new AppIntent[size];
    }
  };
}
