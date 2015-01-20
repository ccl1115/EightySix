package com.utree.eightysix.drawable;

import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * @author simon
 */
public class RoundRectDrawable extends Drawable {

  private final RectF mRectF = new RectF();
  private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private ColorStateList mColorStateList;
  private int mColor = Color.WHITE;
  private int mRadius;

  private BitmapShader mBitmapShader;

  public RoundRectDrawable(int radius, ColorStateList stateList) {
    mRadius = radius;
    mColorStateList = stateList;
    mPaint.setAntiAlias(true);
    mPaint.setColor(mColorStateList.getDefaultColor());
  }

  public RoundRectDrawable(int radius, int color) {
    mRadius = radius;
    mColor = color;
    mPaint.setColor(mColor);
  }

  public RoundRectDrawable(int radius, Bitmap bitmap) {
    mRadius = radius;
    mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    mPaint.setShader(mBitmapShader);
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
    setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
  }

  @Override
  public void setBounds(Rect bounds) {
    super.setBounds(bounds);
    mRectF.left = bounds.left;
    mRectF.right = bounds.right;
    mRectF.top = bounds.top;
    mRectF.bottom = bounds.bottom;
  }

  @Override
  protected boolean onStateChange(int[] state) {
    if (mColorStateList != null) {
      mPaint.setColor(mColorStateList.getColorForState(state, Color.WHITE));
      invalidateSelf();
      return true;
    }
    return false;
  }

  @Override
  public boolean isStateful() {
    return mColorStateList != null;
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    mRectF.left = bounds.left;
    mRectF.right = bounds.right;
    mRectF.top = bounds.top;
    mRectF.bottom = bounds.bottom;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
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
