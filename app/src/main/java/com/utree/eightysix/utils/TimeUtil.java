/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import com.utree.eightysix.U;

import java.util.Calendar;
import java.util.Date;

/**
 */
public class TimeUtil {
  public static String getElapsed(long timestamp) {
    final long now = new Date().getTime();
    final long t = now - timestamp;
    if (t < 0) {
      return "刚刚";
    } else if (t < U.MINUTE_IN_MS) {
      return (t / U.SECOND_IN_MS) + "秒前";
    } else if (t < U.HOUR_IN_MS) {
      return (t / U.MINUTE_IN_MS) + "分钟前";
    } else if (t < U.DAY_IN_MS) {
      return (t / U.HOUR_IN_MS) + "小时前";
    } else {
      return (t / U.DAY_IN_MS) + "天前";
    }
  }

  public static String getDuration(long duration) {
    if (duration < 0) {
      return "刚刚";
    } else if (duration < U.MINUTE_IN_MS) {
      return (duration / U.SECOND_IN_MS) + "秒前";
    } else if (duration < U.HOUR_IN_MS) {
      return (duration / U.MINUTE_IN_MS) + "分钟前";
    } else if (duration < U.DAY_IN_MS) {
      return (duration / U.HOUR_IN_MS) + "小时前";
    } else {
      return (duration / U.DAY_IN_MS) + "天前";
    }
  }

  public static String getDate(Calendar calendar) {
    return String.format("%d-%d",
              calendar.get(Calendar.MONTH) + 1,
              calendar.get(Calendar.DAY_OF_MONTH));
  }

  public static String getDate(long timestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp);
    return getDate(calendar);
  }
}
