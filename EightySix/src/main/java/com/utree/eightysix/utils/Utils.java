package com.utree.eightysix.utils;

import java.util.Date;

/**
 * @author simon
 */
public class Utils {

  private static final int DAY_IN_MS = 86400000; // 60 * 60 * 24 * 1000
  private static final int HOUR_IN_MS = 3600000; // 60 * 60 * 1000
  private static final int MINUTE_IN_MS = 60 * 1000;
  public static final int SECOND_IN_MS = 1000;

  public static String timestamp(long timestamp) {
    final long now = new Date().getTime();
    final long t = now - timestamp;
    if (t < 0) {
      return "未来";
    } else if (t < MINUTE_IN_MS) {
      return (t / SECOND_IN_MS) + "秒前";
    } else if (t < HOUR_IN_MS) {
      return (t / MINUTE_IN_MS) + "分钟前";
    } else if (t < DAY_IN_MS) {
      return (t / HOUR_IN_MS) + "小时前";
    } else {
      return (t / DAY_IN_MS) + "天前";
    }
  }
}
