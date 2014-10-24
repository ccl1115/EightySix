package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class Tag implements Parcelable {

  public String id;

  public String content;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.content);
  }

  public Tag() {
  }

  private Tag(Parcel in) {
    this.id = in.readString();
    this.content = in.readString();
  }

  public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
    public Tag createFromParcel(Parcel source) {
      return new Tag(source);
    }

    public Tag[] newArray(int size) {
      return new Tag[size];
    }
  };
}
