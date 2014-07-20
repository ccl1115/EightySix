package com.utree.eightysix.utils;

/**
 * @author simon
 */
public class Utils {

  public static String getDisplayDistance(int distance) {
    if (distance < 100) {
      return "小于100米";
    } else if (distance < 1000) {
      return String.format("%d千米", 100 * (distance / 100));
    } else {
      return String.format("%.1f千米", distance / 1000f);
    }
  }
}
