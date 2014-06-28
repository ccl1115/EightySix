package com.utree.eightysix.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Comparator;

/**
 */
public class Circle extends BaseCircle implements Comparator<Circle>, Comparable<Circle>, Parcelable {

  @SerializedName ("cityName")
  public String cityName = "";

  /**
   * unit mitre
   */
  @SerializedName ("distance")
  public int distance;

  /**
   * 1 is Factory
   * 2 is Business
   */
  @SerializedName ("factoryType")
  public int circleType;

  /**
   * 1 is lock
   * 0 is unlock
   */
  @SerializedName ("lock")
  public int lock;

  /**
   *
   * 最佳建议(1)
   * 智能推荐(2)
   * 在职工厂(3)
   * 工作过的地方(4) 活动的等级
   *
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
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(cityName);
    dest.writeInt(circleType);
    dest.writeInt(lock);
    dest.writeString(viewGroupType);
    dest.writeInt(viewType);
    dest.writeInt(selected ? 1 : 0);
    dest.writeInt(distance);
    dest.writeInt(workmateCount);
    dest.writeInt(friendCount);
  }

  public static final Creator<Circle> CREATOR = new Creator<Circle>() {
    @Override
    public Circle createFromParcel(Parcel source) {
      Circle circle = new Circle();
      circle.name = source.readString();
      circle.cityName = source.readString();
      circle.circleType = source.readInt();
      circle.lock = source.readInt();
      circle.viewGroupType = source.readString();
      circle.viewType = source.readInt();
      circle.selected = source.readInt() == 1;
      circle.distance = source.readInt();
      circle.workmateCount = source.readInt();
      circle.friendCount = source.readInt();
      return circle;
    }

    @Override
    public Circle[] newArray(int size) {
      return new Circle[size];
    }
  };

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Circle)) return false;

    Circle circle = (Circle) o;

    if (name != null ? !name.equals(circle.name) : circle.name != null) return false;

    return true;
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
}
