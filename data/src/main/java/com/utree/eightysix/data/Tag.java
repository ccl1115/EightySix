package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 */
public class Tag implements Parcelable {

  public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
    public Tag createFromParcel(Parcel source) {
      return new Tag(source);
    }

    public Tag[] newArray(int size) {
      return new Tag[size];
    }
  };

  @SerializedName ("id")
  public int id;

  @SerializedName ("content")
  public String content;

  public Tag(int id, String content) {
    this.id = id;
    this.content = content;
  }

  public Tag() {
  }

  private Tag(Parcel in) {
    this.id = in.readInt();
    this.content = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.content);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (content != null ? content.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tag tag = (Tag) o;

    if (id != tag.id) return false;
    if (content != null ? !content.equals(tag.content) : tag.content != null) return false;

    return true;
  }
}
