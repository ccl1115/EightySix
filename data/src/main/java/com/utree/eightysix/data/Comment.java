package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 */
public class Comment implements Parcelable {

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.floor);
    dest.writeString(this.content);
    dest.writeInt(this.praise);
    dest.writeInt(this.ownerPraise);
    dest.writeLong(this.timestamp);
    dest.writeString(this.id);
    dest.writeString(this.avatar);
    dest.writeString(this.avatarColor);
    dest.writeInt(this.praised);
    dest.writeInt(this.self);
    dest.writeInt(this.owner);
    dest.writeString(this.time);
    dest.writeString(this.distance);
    dest.writeInt(this.delete);
  }

  public Comment() {
  }

  private Comment(Parcel in) {
    this.floor = in.readInt();
    this.content = in.readString();
    this.praise = in.readInt();
    this.ownerPraise = in.readInt();
    this.timestamp = in.readLong();
    this.id = in.readString();
    this.avatar = in.readString();
    this.avatarColor = in.readString();
    this.praised = in.readInt();
    this.self = in.readInt();
    this.owner = in.readInt();
    this.time = in.readString();
    this.distance = in.readString();
    this.delete = in.readInt();
  }

  public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
    public Comment createFromParcel(Parcel source) {
      return new Comment(source);
    }

    public Comment[] newArray(int size) {
      return new Comment[size];
    }
  };
}
