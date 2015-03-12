/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import com.utree.eightysix.U;
import com.utree.eightysix.rest.OnResponse;
import com.utree.eightysix.rest.Response;

import java.util.Calendar;

/**
 */
class Utils {

  static class Constellation {
    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();
    String name;

    Constellation(int startMonth, int startDayOfMonth, int endMonth, int endDayOfMonth, String name) {
      start.set(2014, startMonth, startDayOfMonth);
      end.set(2014, endMonth, endDayOfMonth);
      this.name = name;
    }

    boolean is(Calendar calendar) {
      Calendar c = (Calendar) calendar.clone();
      c.set(Calendar.YEAR, 2014);
      return (c.after(start) && c.before(end));
    }

    static String get(Calendar calendar) {
      if (ARIES.is(calendar)) {
        return ARIES.name;
      } else if (TAURUS.is(calendar)) {
        return TAURUS.name;
      } else if (GEMINI.is(calendar)) {
        return GEMINI.name;
      } else if (CANCER.is(calendar)) {
        return CANCER.name;
      } else if (LEO.is(calendar)) {
        return LEO.name;
      } else if (VIRGO.is(calendar)) {
        return VIRGO.name;
      } else if (LIBRA.is(calendar)) {
        return LIBRA.name;
      } else if (SCORPIO.is(calendar)) {
        return SCORPIO.name;
      } else if (SAGITTARIUS.is(calendar)) {
        return SAGITTARIUS.name;
      } else if (CAPRICORN.is(calendar)) {
        return CAPRICORN.name;
      } else if (AQUARIUS.is(calendar)) {
        return AQUARIUS.name;
      } else if (PISCES.is(calendar)) {
        return PISCES.name;
      } else {
        return "";
      }
    }

    static Constellation ARIES = new Constellation(2, 20, 3, 20, "白羊座");
    static Constellation TAURUS = new Constellation(3, 19, 4, 19, "金牛座");
    static Constellation GEMINI = new Constellation(4, 20, 5, 22, "双子座");
    static Constellation CANCER = new Constellation(5, 21, 6, 23, "巨蟹座");
    static Constellation LEO = new Constellation(6, 22, 7, 23, "狮子座");
    static Constellation VIRGO = new Constellation(7, 22, 8, 23, "处女座");
    static Constellation LIBRA = new Constellation(8, 22, 9, 24, "天平座");
    static Constellation SCORPIO = new Constellation(9, 23, 10, 22, "天蝎座");
    static Constellation SAGITTARIUS = new Constellation(10, 21, 11, 22, "射手座");
    static Constellation CAPRICORN = new Constellation(11, 21, 0, 20, "摩羯座");
    static Constellation AQUARIUS = new Constellation(0, 19, 1, 19, "水瓶座");
    static Constellation PISCES = new Constellation(1, 18, 2, 21, "双鱼座");
  }

  static void updateProfile(String avatar,
                            String name,
                            String sex,
                            Long birthday,
                            String constellation,
                            String background,
                            String signature,
                            Integer age,
                            OnResponse<Response> response) {
    U.request("profile_fill", response, Response.class,
        avatar, sex, name, birthday, constellation, background, signature, age);
  }


  static int computeAge(Calendar now, Calendar calendar) {
    long nowTimeInMillis = now.getTimeInMillis();

    long t = nowTimeInMillis - calendar.getTimeInMillis();

    return (int) (t / 31536000000L);
  }
}
