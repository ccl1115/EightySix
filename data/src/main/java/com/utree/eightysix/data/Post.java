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

  @SerializedName("viewerName")
  public String viewerName;

  @SerializedName("jump")
  public int jump;

  @SerializedName("topicId")
  public int topicId;

  @SerializedName("topicPrev")
  public String topicPrev;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Post.class != o.getClass()) return false;

    Post post = (Post) o;

    return id != null && id.equals(post.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public Post() {
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
  public int describeContents() {
    return 0;
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
    dest.writeInt(this.sourceType);
    dest.writeInt(this.userCurrFactoryId);
    dest.writeTypedList(tags);
    dest.writeInt(this.relation);
    dest.writeString(this.hometownText);
    dest.writeString(this.distance);
    dest.writeString(this.userName);
    dest.writeString(this.avatar);
    dest.writeString(this.viewUserId);
    dest.writeString(this.levelIcon);
    dest.writeString(this.viewerName);
    dest.writeInt(this.jump);
    dest.writeInt(this.topicId);
    dest.writeString(this.topicPrev);
    dest.writeString(this.bgUrl);
    dest.writeString(this.bgColor);
    dest.writeString(this.content);
    dest.writeInt(this.type);
  }

  private Post(Parcel in) {
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
    this.sourceType = in.readInt();
    this.userCurrFactoryId = in.readInt();
    tags = new ArrayList<Tag>();
    in.readTypedList(tags, Tag.CREATOR);
    this.relation = in.readInt();
    this.hometownText = in.readString();
    this.distance = in.readString();
    this.userName = in.readString();
    this.avatar = in.readString();
    this.viewUserId = in.readString();
    this.levelIcon = in.readString();
    this.viewerName = in.readString();
    this.jump = in.readInt();
    this.topicId = in.readInt();
    this.topicPrev = in.readString();
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
