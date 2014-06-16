package com.utree.eightysix.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.utree.eightysix.R;

/**
 * @author simon
 */
public class RefreshIndicator extends ViewGroup {

  private final Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private final RectF mBound = new RectF();
  private final RectF mFactoredBound = new RectF();

  private TextView mTvText;

  private ProgressBar mPbLoading;

  public RefreshIndicator(Context context) {
    super(context);
  }

  public RefreshIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);

    init(context, attrs);
  }

  public RefreshIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    mTvText = new TextView(context, attrs);
    mPbLoading = new ProgressBar(context, attrs);

    mPbLoading.setVisibility(INVISIBLE);

    addView(mTvText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    addView(mPbLoading, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    mBackgroundPaint.setColor(getResources().getColor(R.color.apptheme_primary_light_color));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    mTvText.measure(widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.AT_MOST);
    mPbLoading.measure(widthSize + MeasureSpec.AT_MOST, heightSize + MeasureSpec.AT_MOST);

    setMeasuredDimension(widthSize, heightSize);

    mBound.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int width = r - l;
    final int height = b - t;

    mTvText.layout((width - mTvText.getMeasuredWidth()) >> 1, (height - mTvText.getMeasuredHeight()) >> 1,
        (width + mTvText.getMeasuredWidth()) >> 1, (height + mTvText.getMeasuredHeight()) >> 1);

    mPbLoading.layout((width - mPbLoading.getMeasuredWidth()) >> 1, (height - mPbLoading.getMeasuredHeight()) >> 1,
        (width + mPbLoading.getMeasuredWidth()) >> 1, (height + mPbLoading.getMeasuredHeight()) >> 1);
  }

  public void setText(String text) {
    mTvText.setText(text);
  }

  public void showLoading(boolean loading) {
    if (loading) {
      mTvText.setVisibility(INVISIBLE);
      mPbLoading.setVisibility(VISIBLE);
    } else {
      mTvText.setVisibility(VISIBLE);
      mPbLoading.setVisibility(INVISIBLE);
    }
    invalidate();
  }

  /**
   *
   * @param t from 0f to 1f
   */
  public void setFactor(float t) {
    float width = mBound.width();
    float factoredW = width * t;
    mFactoredBound.set((width - factoredW) / 2f, mBound.top, (width + factoredW) / 2f, mBound.bottom);
    invalidate();
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.drawRect(mFactoredBound, mBackgroundPaint);

    super.dispatchDraw(canvas);
  }
}
