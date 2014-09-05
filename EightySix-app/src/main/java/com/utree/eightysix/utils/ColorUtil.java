package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

/**
 * @author simon
 */
public class ColorUtil {
  private static final String TAG = "ColorUtil";

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
}
