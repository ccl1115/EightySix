package com.utree.eightysix.drawable;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class AsyncImageDrawable extends Drawable {

  private Resources mResources;
  private String mUrl;
  private final int mWidth;
  private final int mHeight;

  private BitmapDrawable mBitmapDrawable;


  public AsyncImageDrawable(Resources res, String url, int width, int height) {
    mResources = res;
    mUrl = url;
    mWidth = width;
    mHeight = height;
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    if (mBitmapDrawable != null) {
      mBitmapDrawable.setBounds(bounds);
    }
  }

  public void setCallbackExtended(Callback callback) {
    setCallback(callback);
  }

  private boolean mLoaded;

  @Override
  public void draw(Canvas canvas) {
    if (!mLoaded) {
      Picasso.with(U.getContext()).load(mUrl).resize(mWidth, mHeight).into(new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
          mBitmapDrawable = new BitmapDrawable(mResources, bitmap);
          mBitmapDrawable.setBounds(getBounds());
          mBitmapDrawable.setCallback(getCallback());
          mBitmapDrawable.invalidateSelf();
          invalidateSelf();
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
      });
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
