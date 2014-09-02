package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * @author simon
 */
public class LoadingAsyncImageView extends AsyncImageView {

  private Drawable mLoadingDrawable;

  public LoadingAsyncImageView(Context context) {
    this(context, null);
  }

  public LoadingAsyncImageView(Context context, AttributeSet attrs) {
    super(context, attrs);

    setLoadingDrawable(android.R.drawable.progress_horizontal);
  }

  public void setLoadingDrawable(Drawable drawable) {
    mLoadingDrawable = drawable;
  }

  public void setLoadingDrawable(int res) {
    mLoadingDrawable = getResources().getDrawable(res);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    if (mLoadingDrawable != null) {
      int dw, dh;
      dw = mLoadingDrawable.getIntrinsicWidth() == 0 ? widthSize : mLoadingDrawable.getIntrinsicWidth();
      dh = mLoadingDrawable.getIntrinsicHeight() == 0 ? heightSize : mLoadingDrawable.getIntrinsicHeight();
      mLoadingDrawable.setBounds((widthSize - dw) >> 1, (heightSize - dh) >> 1,
          (widthSize + dw) >> 1, (heightSize + dh) >> 1);
    }
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    mLoadingDrawable.draw(canvas);
    super.dispatchDraw(canvas);
  }
}
