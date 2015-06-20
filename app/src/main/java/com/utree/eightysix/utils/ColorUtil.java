package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * @author simon
 */
public class ColorUtil {
  private static final String TAG = "ColorUtil";

  private static final int[] COLOR_SCHEME = {
      0xffffa200,
      0xff66cccc,
      0xff30a1f1,
      0xfff26d5f,
      0xffe6507b,
      0xff5bb4da,
      0xff35b87f,
      0xffbdbe4d,
      0xffb17fea
  };

  private static Random sRandom = new Random();

  public static int strToColor(String color) {
    try {
      return (int) Long.parseLong(color, 16);
    } catch (NumberFormatException e) {
      return 0x00000000;
    }
  }

  public static int monochromizing(int color) {
    return (color & 0xff) > 0x88 && ((color >> 8) & 0xff) > 0x88 && ((color >> 16) & 0xff) > 0x88
        ? Color.BLACK : Color.WHITE;
  }

  public static int lighten(int color) {
    return (0x4b << 24) + (color & 0xffffff);
  }

  public static class ThemedColorEvent {
    private Bitmap mBitmap;
    private int mColor;

    public ThemedColorEvent(Bitmap bitmap, int color) {
      mBitmap = bitmap;
      mColor = color;
    }

    public int getColor() {
      return mColor;
    }

    public Bitmap getBitmap() {
      return mBitmap;
    }
  }

  public static int getRandomColor() {
    return COLOR_SCHEME[sRandom.nextInt(COLOR_SCHEME.length)];
  }
}
