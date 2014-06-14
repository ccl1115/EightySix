package com.utree.eightysix.response.data;

import com.google.gson.annotations.SerializedName;
import java.util.Comparator;

/**
 */
public class Circle extends BaseCircle implements Comparator<Circle>, Comparable<Circle> {

  @SerializedName ("cityName")
  public String cityName;

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
  @SerializedName ("viewGroupType")
  public String viewGroupType;

  /**
   * 1 is the most friends
   * 2 is the most nearest
   * 3 is the last visited
   * 4 is unlock circles
   */
  @SerializedName ("viewType")
  public int viewType;

  @Override
  public int compareTo(Circle another) {
    return viewGroupType.compareTo(another.viewGroupType);
  }

  @Override
  public int compare(Circle lhs, Circle rhs) {
    return lhs.compareTo(rhs);
  }
}
