package com.utree.eightysix.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.otto.Subscribe;
import com.utree.eightysix.utils.ImageUtils;
import com.utree.eightysix.utils.MD5Util;

/**
 * @author simon
 */
public class AsyncImageDrawable extends Drawable {

  private String mHash;
  private Resources mResources;
  private String mUrl;

  private BitmapDrawable mBitmapDrawable;

  public AsyncImageDrawable(Resources res, String url) {
    mResources = res;
    mUrl = url;
    mHash = MD5Util.getMD5String(mUrl.getBytes()).toLowerCase();
  }

  @Subscribe
  public void onImageLoadedEvent(ImageUtils.ImageLoadedEvent event) {
    if (mHash != null && mHash.equals(event.getHash())) {
      mBitmapDrawable = new BitmapDrawable(mResources, event.getBitmap());
      mBitmapDrawable.setBounds(getBounds());
      mBitmapDrawable.invalidateSelf();
      invalidateSelf();
    }
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    if (mBitmapDrawable != null) {
      mBitmapDrawable.setBounds(bounds);
    }
  }

  private boolean mLoaded;

  @Override
  public void draw(Canvas canvas) {
    if (!mLoaded) {
      ImageUtils.asyncLoadThumbnail(mUrl, mHash);
      mLoaded = true;
    }

    if (mBitmapDrawable != null) {
      mBitmapDrawable.draw(canvas);
    }
  }

  @Override
  public void setAlpha(int alpha) {

  }

  @Override
  public void setColorFilter(ColorFilter cf) {

  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }
}
