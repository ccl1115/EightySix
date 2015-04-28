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

  static Calendar END_OF_YEAR = Calendar.getInstance();
  static Calendar START_OF_YEAR = Calendar.getInstance();

  static {
    END_OF_YEAR.set(2015, Calendar.JANUARY, 1);
    START_OF_YEAR.set(2014, Calendar.JANUARY, 1);
  }

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
      if (name.equals("摩羯座")) {
        return ((c.after(start) && c.before(END_OF_YEAR)) || (c.before(end) && c.after(START_OF_YEAR)));
      } else {
        return (c.after(start) && c.before(end));
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Constellation that = (Constellation) o;

      if (!name.equals(that.name)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    static Constellation get(Calendar calendar) {
      if (ARIES.is(calendar)) {
        return ARIES;
      } else if (TAURUS.is(calendar)) {
        return TAURUS;
      } else if (GEMINI.is(calendar)) {
        return GEMINI;
      } else if (CANCER.is(calendar)) {
        return CANCER;
      } else if (LEO.is(calendar)) {
        return LEO;
      } else if (VIRGO.is(calendar)) {
        return VIRGO;
      } else if (LIBRA.is(calendar)) {
        return LIBRA;
      } else if (SCORPIO.is(calendar)) {
        return SCORPIO;
      } else if (SAGITTARIUS.is(calendar)) {
        return SAGITTARIUS;
      } else if (CAPRICORN.is(calendar)) {
        return CAPRICORN;
      } else if (AQUARIUS.is(calendar)) {
        return AQUARIUS;
      } else if (PISCES.is(calendar)) {
        return PISCES;
      } else {
        return null;
      }
    }

    static Constellation ARIES = new Constellation(2, 20, 3, 19, "白羊座");
    static Constellation TAURUS = new Constellation(3, 19, 4, 18, "金牛座");
    static Constellation GEMINI = new Constellation(4, 20, 5, 21, "双子座");
    static Constellation CANCER = new Constellation(5, 21, 6, 22, "巨蟹座");
    static Constellation LEO = new Constellation(6, 22, 7, 22, "狮子座");
    static Constellation VIRGO = new Constellation(7, 22, 8, 22, "处女座");
    static Constellation LIBRA = new Constellation(8, 22, 9, 23, "天平座");
    static Constellation SCORPIO = new Constellation(9, 23, 10, 21, "天蝎座");
    static Constellation SAGITTARIUS = new Constellation(10, 21, 11, 21, "射手座");
    static Constellation CAPRICORN = new Constellation(11, 21, 0, 19, "摩羯座");
    static Constellation AQUARIUS = new Constellation(0, 19, 1, 18, "水瓶座");
    static Constellation PISCES = new Constellation(1, 18, 2, 20, "双鱼座");

    @Override
    public String toString() {
      return String.format("%s (%d.%d - %d.%d)", name,
          start.get(Calendar.MONTH) + 1,
          start.get(Calendar.DAY_OF_MONTH),
          end.get(Calendar.MONTH) + 1,
          end.get(Calendar.DAY_OF_MONTH));
    }
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
