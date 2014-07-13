package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 */
public class Post extends BaseItem implements Parcelable {

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Post post = (Post) o;

    if (!id.equals(post.id)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.comments);
    dest.writeInt(this.praise);
    dest.writeString(this.id);
    dest.writeInt(this.myPraiseCount);
    dest.writeString(this.source);
    dest.writeInt(this.read);
    dest.writeString(this.comment);
    dest.writeString(this.commentHead);
    dest.writeString(this.commentTail);
    dest.writeString(this.whoAtMe);
    dest.writeInt(this.praised);
    dest.writeString(this.bgUrl);
    dest.writeString(this.bgColor);
    dest.writeString(this.content);
    dest.writeInt(this.type);
  }

  public Post() {
  }

  private Post(Parcel in) {
    this.comments = in.readInt();
    this.praise = in.readInt();
    this.id = in.readString();
    this.myPraiseCount = in.readInt();
    this.source = in.readString();
    this.read = in.readInt();
    this.comment = in.readString();
    this.commentHead = in.readString();
    this.commentTail = in.readString();
    this.whoAtMe = in.readString();
    this.praised = in.readInt();
    this.bgUrl = in.readString();
    this.bgColor = in.readString();
    this.content = in.readString();
    this.type = in.readInt();
  }

  public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
    public Post createFromParcel(Parcel source) {
      return new Post(source);
    }

    public Post[] newArray(int size) {
      return new Post[size];
    }
  };
}
