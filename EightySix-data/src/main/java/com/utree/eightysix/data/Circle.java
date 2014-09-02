package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.Comparator;

/**
 */
public class Circle extends BaseCircle implements Comparator<Circle>, Comparable<Circle>, Parcelable {

  @SerializedName ("cityName")
  public String cityName = "";

  @SerializedName ("info")
  public String info;

  /**
   * unit mitre
   */
  @SerializedName ("distance")
  public int distance;

  @SerializedName("hotLevel")
  public int hotLevel;

  /**
   * 1 is Factory
   * 2 is Business
   */
  @SerializedName ("factoryType")
  public int circleType;

  @SerializedName("currFactory")
  public int currFactory;

  /**
   * 1 is lock
   * 0 is unlock
   */
  @SerializedName ("lock")
  public int lock;

  @SerializedName ("friendCount")
  public int friendCount;

  /**
   * 最佳建议(1)
   * 智能推荐(2)
   * 在职工厂(3)
   * 工作过的地方(4) 活动的等级
   */
  @SerializedName ("viewGroupName")
  public String viewGroupType = "";

  /**
   * 1 is the most friends
   * 2 is the most nearest
   * 3 is the last visited
   * 4 is unlock circles
   */
  @SerializedName ("viewType")
  public int viewType;

  public boolean selected = false;

  @Override
  public int compareTo(Circle another) {
    return viewGroupType.compareTo(another.viewGroupType);
  }

  @Override
  public int compare(Circle lhs, Circle rhs) {
    return lhs.compareTo(rhs);
  }

  @Override
  public int hashCode() {
    int result = cityName != null ? cityName.hashCode() : 0;
    result = 31 * result + distance;
    result = 31 * result + circleType;
    result = 31 * result + lock;
    result = 31 * result + (viewGroupType != null ? viewGroupType.hashCode() : 0);
    result = 31 * result + viewType;
    result = 31 * result + (selected ? 1 : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Circle)) return false;

    Circle circle = (Circle) o;

    if (name != null ? !name.equals(circle.name) : circle.name != null) return false;

    return true;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.cityName);
    dest.writeString(this.info);
    dest.writeInt(this.distance);
    dest.writeInt(this.circleType);
    dest.writeInt(this.lock);
    dest.writeInt(this.friendCount);
    dest.writeString(this.viewGroupType);
    dest.writeInt(this.viewType);
    dest.writeByte(selected ? (byte) 1 : (byte) 0);
    dest.writeString(this.name);
    dest.writeString(this.shortName);
    dest.writeInt(this.id);
    dest.writeInt(this.workmateCount);
  }

  public Circle() {
  }

  private Circle(Parcel in) {
    this.cityName = in.readString();
    this.info = in.readString();
    this.distance = in.readInt();
    this.circleType = in.readInt();
    this.lock = in.readInt();
    this.friendCount = in.readInt();
    this.viewGroupType = in.readString();
    this.viewType = in.readInt();
    this.selected = in.readByte() != 0;
    this.name = in.readString();
    this.shortName = in.readString();
    this.id = in.readInt();
    this.workmateCount = in.readInt();
  }

  public static final Creator<Circle> CREATOR = new Creator<Circle>() {
    public Circle createFromParcel(Parcel source) {
      return new Circle(source);
    }

    public Circle[] newArray(int size) {
      return new Circle[size];
    }
  };
}
