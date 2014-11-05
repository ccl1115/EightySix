package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 */
public class Post extends BaseItem implements Parcelable {

  @SerializedName ("factoryId")
  public int factoryId;

  @SerializedName("factoryName")
  public String circle;

  @SerializedName("factoryShortName")
  public String shortName;

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

  @SerializedName ("read")
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

  @SerializedName("viewType")
  public int viewType;

  @SerializedName("isRepost")
  public int isRepost;

  @SerializedName("isHot")
  public int isHot;

  @SerializedName("owner")
  public int owner;

  @SerializedName("tags")
  public List<Tag> tags;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Post.class != o.getClass()) return false;

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
    dest.writeString(this.circle);
    dest.writeString(this.shortName);
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
    dest.writeInt(this.viewType);
    dest.writeInt(this.isRepost);
    dest.writeInt(this.isHot);
  }

  public Post() {
  }

  private Post(Parcel in) {
    this.circle = in.readString();
    this.shortName = in.readString();
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
    this.viewType = in.readInt();
    this.isRepost = in.readInt();
    this.isHot = in.readInt();
  }

  public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
    public Post createFromParcel(Parcel source) {
      return new Post(source);
    }

    public Post[] newArray(int size) {
      return new Post[size];
    }
  };

  @Override
  public String toString() {
    return "Post{" +
        "circle=" + circle +
        ", comments=" + comments +
        ", praise=" + praise +
        ", id='" + id + '\'' +
        ", myPraiseCount=" + myPraiseCount +
        ", source='" + source + '\'' +
        ", read=" + read +
        ", comment='" + comment + '\'' +
        ", commentHead='" + commentHead + '\'' +
        ", commentTail='" + commentTail + '\'' +
        ", whoAtMe='" + whoAtMe + '\'' +
        ", praised=" + praised +
        '}';
  }
}
