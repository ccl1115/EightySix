/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.utils;

import com.utree.eightysix.U;

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
}
