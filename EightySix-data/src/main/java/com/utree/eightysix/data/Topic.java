package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Topic implements Parcelable {

  @SerializedName("id")
  public int id;

  @SerializedName("content")
  public String content;

  @SerializedName("tags")
  public List<Tag> tags;

  @SerializedName("postCount")
  public int postCount;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.content);
    dest.writeTypedList(tags);
    dest.writeInt(this.postCount);
  }

  public Topic() {
    tags = new ArrayList<Tag>();
  }

  private Topic(Parcel in) {
    this();
    this.id = in.readInt();
    this.content = in.readString();
    in.readTypedList(tags, Tag.CREATOR);
    this.postCount = in.readInt();
  }

  public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
    public Topic createFromParcel(Parcel source) {
      return new Topic(source);
    }

    public Topic[] newArray(int size) {
      return new Topic[size];
    }
  };
}
