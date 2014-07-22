package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;
import java.util.HashMap;
import org.michaelevans.colorart.library.ColorArt;

/**
 * @author simon
 */
public class ColorUtil {
  private static final String TAG = "ColorUtil";

  private static HashMap<String, Integer> mCachedColor = new HashMap<String, Integer>();

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
    return Color.argb(0xff,
        Math.min(0xff, Color.red(color) + 0xAA),
        Math.min(0xff, Color.green(color) + 0xAA),
        Math.min(0xff, Color.blue(color) + 0xAA));
  }

  public static void asyncThemedColor(final Bitmap bitmap) {
    if (bitmap == null) return;
    if (mCachedColor.containsKey(String.valueOf(bitmap.hashCode()))) {
      Log.d(TAG, "Get color from cache");
      U.getBus().post(new ThemedColorEvent(bitmap, mCachedColor.get(String.valueOf(bitmap.hashCode()))));
    } else {
      Log.d(TAG, "Get color from ColorArt");
      new ThemedColorWorker(bitmap).execute();
    }
  }

  private static int themedColor(final Bitmap bitmap) {
    final int color = new ColorArt(bitmap).getBackgroundColor();
    mCachedColor.put(String.valueOf(bitmap.hashCode()), color);
    return color;
  }

  private static class ThemedColorWorker extends AsyncTask<Void, Void, Integer> {
    private Bitmap mBitmap;

    public ThemedColorWorker(Bitmap bitmap) {
      mBitmap = bitmap;
    }

    @Override
    protected Integer doInBackground(Void... params) {
      return themedColor(mBitmap);
    }

    @Override
    protected void onPostExecute(Integer integer) {
      U.getBus().post(new ThemedColorEvent(mBitmap, integer));
    }
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
