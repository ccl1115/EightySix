package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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

  @SerializedName("sourceType")
  public int sourceType;

  @SerializedName("userCurrFactoryId")
  public int userCurrFactoryId;

  @SerializedName("tags")
  public List<Tag> tags;

  @SerializedName("relation")
  public int relation;

  @SerializedName("hometownText")
  public String hometownText;

  @SerializedName("distance")
  public String distance;

  @SerializedName("userName")
  public String userName;

  @SerializedName("avatar")
  public String avatar;

  @SerializedName("viewUserId")
  public String viewUserId;

  @SerializedName("levelIcon")
  public String levelIcon;

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

  public Post() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public String toString() {
    return "Post{" +
        "factoryId=" + factoryId +
        ", circle='" + circle + '\'' +
        ", shortName='" + shortName + '\'' +
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
        ", viewType=" + viewType +
        ", isRepost=" + isRepost +
        ", isHot=" + isHot +
        ", owner=" + owner +
        ", sourceType=" + sourceType +
        ", userCurrFactoryId=" + userCurrFactoryId +
        ", tags=" + tags +
        ", relation=" + relation +
        '}';
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.factoryId);
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
    dest.writeInt(this.viewType);
    dest.writeInt(this.isRepost);
    dest.writeInt(this.isHot);
    dest.writeInt(this.owner);
    dest.writeTypedList(tags);
    dest.writeString(this.bgUrl);
    dest.writeString(this.bgColor);
    dest.writeString(this.content);
    dest.writeInt(this.type);
  }

  private Post(Parcel in) {
    tags = new ArrayList<Tag>();
    this.factoryId = in.readInt();
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
    this.viewType = in.readInt();
    this.isRepost = in.readInt();
    this.isHot = in.readInt();
    this.owner = in.readInt();
    in.readTypedList(tags, Tag.CREATOR);
    this.bgUrl = in.readString();
    this.bgColor = in.readString();
    this.content = in.readString();
    this.type = in.readInt();
  }

  public static final Creator<Post> CREATOR = new Creator<Post>() {
    public Post createFromParcel(Parcel source) {
      return new Post(source);
    }

    public Post[] newArray(int size) {
      return new Post[size];
    }
  };
}
