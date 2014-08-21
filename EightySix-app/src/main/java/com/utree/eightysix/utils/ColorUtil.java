package com.utree.eightysix.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author simon
 */
public class ColorUtil {
  private static final String TAG = "ColorUtil";

  private static ConcurrentHashMap<String, Integer> mCachedColor = new ConcurrentHashMap<String, Integer>();

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

  public static void asyncThemedColor(final String hash, final Bitmap bitmap) {
    if (bitmap == null) return;
    if (mCachedColor.containsKey(hash)) {
      Log.d(TAG, "Get color from cache");
      U.getBus().post(new ThemedColorEvent(bitmap, mCachedColor.get(hash)));
    } else {
      Log.d(TAG, "Get color from ColorArt");
      new ThemedColorWorker(hash, bitmap).execute();
    }
  }

  private static int themedColor(final String hash, final Bitmap bitmap) {
    return 0;
  }

  private static class ThemedColorWorker extends AsyncTask<Void, Void, Integer> {
    private Bitmap mBitmap;
    private String mHash;

    public ThemedColorWorker(String hash, Bitmap bitmap) {
      mBitmap = bitmap;
      mHash = hash;
    }

    @Override
    protected Integer doInBackground(Void... params) {
      return themedColor(mHash, mBitmap);
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
