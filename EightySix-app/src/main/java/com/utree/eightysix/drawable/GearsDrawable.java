package com.utree.eightysix.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import com.nineoldandroids.animation.ValueAnimator;
import com.utree.eightysix.R;
import com.utree.eightysix.U;

/**
 * @author simon
 */
public class GearsDrawable extends Drawable {
  private static final int ANIMATION_FRAME_DURATION = 16;
  private static final int MSG_ANIMATE = 0x1;

  private final int mIntrinsicWidth;
  private final int mIntrinsicHeight;

  private BitmapDrawable mGear5;
  private BitmapDrawable mGear6;
  private BitmapDrawable mGear8;

  private final Rect mG8R = new Rect();
  private final Rect mG6R = new Rect();
  private final Rect mG5R = new Rect();

  private final int[] mG8C = new int[2];
  private final int[] mG6C = new int[2];
  private final int[] mG5C = new int[2];

  private int mDegree;

  private ValueAnimator mDegreeAnimator;

  public GearsDrawable() {

    mGear5 = (BitmapDrawable) U.gd(R.drawable.gear5);
    mGear6 = (BitmapDrawable) U.gd(R.drawable.gear6);
    mGear8 = (BitmapDrawable) U.gd(R.drawable.gear8);

    mIntrinsicWidth = U.dp2px(128);
    mIntrinsicHeight = U.dp2px(128);

    resetRect();

    mDegreeAnimator = ValueAnimator.ofInt(0, 360);
    mDegreeAnimator.setDuration(2000);
    mDegreeAnimator.setInterpolator(new LinearInterpolator());
    mDegreeAnimator.setRepeatCount(Integer.MAX_VALUE);
    mDegreeAnimator.setRepeatMode(ValueAnimator.INFINITE);
    mDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mDegree = (Integer) animation.getAnimatedValue();
        invalidateSelf();
      }
    });
  }

  private void resetRect() {
    mG8R.set(0, 0, mGear8.getIntrinsicWidth(), mGear8.getIntrinsicHeight());
    mG6R.set(0, 0, mGear6.getIntrinsicWidth(), mGear6.getIntrinsicHeight());
    mG5R.set(0, 0, mGear5.getIntrinsicWidth(), mGear5.getIntrinsicHeight());
  }

  @Override
  public int getIntrinsicWidth() {
    return mIntrinsicWidth;
  }

  @Override
  public int getIntrinsicHeight() {
    return mIntrinsicHeight;
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);

    final int width = bounds.width();
    final int height = bounds.height();

    resetRect();

    final int cx = width >> 1;
    final int cy = height >> 1;

    final int g8r = mG8R.width() >> 1;
    final int g6r = mG6R.width() >> 1;
    final int g5r = mG5R.width() >> 1;

    final int g8cx = cx - g8r;
    final int g8cy = cy;

    mG8C[0] = g8cx;
    mG8C[1] = g8cy;

    final int g8l = cx - mG8R.width();
    final int g8t = cy - g8r;

    final int d_g6_g8 = (int) ((g8r + g6r - U.dp2px(5)) / Math.sqrt(2));

    final int g6cx = g8cx + d_g6_g8;
    final int g6cy = g8cy - d_g6_g8;

    mG6C[0] = g6cx;
    mG6C[1] = g6cy;

    final int g6l = g6cx - g6r;
    final int g6t = g6cy - g6r;

    final int d_g5_g8 = (int) ((g8r + g5r - U.dp2px(5)) / Math.sqrt(2));

    final int g5cx = g8cx + d_g5_g8;
    final int g5cy = g8cy + d_g5_g8;

    mG5C[0] = g5cx;
    mG5C[1] = g5cy;

    final int g5l = g5cx - g5r;
    final int g5t = g5cy - g5r;

    mG8R.offsetTo(g8l, g8t);
    mG6R.offsetTo(g6l, g6t);
    mG5R.offsetTo(g5l, g5t);

    mGear8.setBounds(mG8R);
    mGear6.setBounds(mG6R);
    mGear5.setBounds(mG5R);
  }

  @Override
  public boolean setVisible(boolean visible, boolean restart) {
    if (visible) {
      mDegreeAnimator.start();
    } else {
      mDegreeAnimator.end();
    }

    return super.setVisible(visible, restart);
  }

  @Override
  public void draw(Canvas canvas) {
    // draw gear 8
    final int count8 = canvas.save();
    canvas.rotate(mDegree, mG8C[0], mG8C[1]);
    mGear8.draw(canvas);
    canvas.restoreToCount(count8);

    final int count6 = canvas.save();
    canvas.rotate(-(mDegree * 1.33f) + 10, mG6C[0], mG6C[1]);
    mGear6.draw(canvas);
    canvas.restoreToCount(count6);

    final int count5 = canvas.save();
    canvas.rotate(-(mDegree * 1.6f), mG5C[0], mG5C[1]);
    mGear5.draw(canvas);
    canvas.restoreToCount(count6);
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
