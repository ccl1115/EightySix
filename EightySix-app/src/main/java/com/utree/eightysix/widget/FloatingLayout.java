package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.utree.eightysix.BuildConfig;
import de.akquinet.android.androlog.Log;

/**
 */
public class FloatingLayout extends ViewGroup {
  private static final String TAG = "FloatingLayout";

  private static final boolean DEBUG = BuildConfig.DEBUG;

  private int mUsedWidth;
  private int mUsedHeight;

  private int mMeasuredWidth;
  private int mMeasuredHeight;

  public FloatingLayout(Context context) {
    this(context, null);
  }

  public FloatingLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    mUsedWidth = 0;
    mUsedHeight = 0;

    measureHorizontal(widthMeasureSpec, heightMeasureSpec);

    setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);

    if (DEBUG) {
      Log.d(TAG, String.format("measured width: %d, height: %d", getMeasuredWidth(), getMeasuredHeight()));
    }

  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    layoutHorizontal();
  }

  @Override
  protected MarginLayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
    int cW, cH;
    int maxChildHeight = 0;
    mMeasuredWidth = 0;
    mMeasuredHeight = 0;
    final int widthSize = widthMeasureSpec & ~(0x3 << 30) - getPaddingRight() - getPaddingLeft();
    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child == null || child.getVisibility() == GONE) continue;
      MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
      if (lp == null) lp = generateDefaultLayoutParams();
      measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
      cW = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
      cH = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
      if (DEBUG) {
        Log.d(TAG, "measureHorizontal " + String.format("child width: %d, height %d", cW, cH));
      }
      maxChildHeight = Math.max(cH, maxChildHeight);
      mUsedHeight = maxChildHeight;
      mUsedWidth += cW;

      if (mUsedWidth > widthSize) {
        mUsedWidth = cW;
        mMeasuredHeight += mUsedHeight;
        maxChildHeight = 0;
      } else {
        mMeasuredWidth = Math.max(mMeasuredWidth, widthSize);
      }

    }

    mMeasuredWidth += getPaddingLeft() + getPaddingRight();
    mMeasuredHeight = mMeasuredHeight + mUsedHeight + getPaddingTop() + getPaddingBottom();
  }

  private void layoutHorizontal() {

    final int count = getChildCount();
    final int width = getMeasuredWidth();

    int left = getPaddingLeft();
    int top = getPaddingTop();

    int cW, cH;
    int maxRowHeight = 0;

    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child != null && child.getVisibility() != GONE) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        if (lp == null) lp = generateDefaultLayoutParams();
        // 开始其中一个子View的布局

        // 预先计算宽高，如果大于当前剩下的空间则从新的一行开始布局。

        cW = lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
        cH = lp.topMargin + lp.bottomMargin + child.getMeasuredHeight();

        if (width - getPaddingRight() - left < cW) {
          // 空间不足，从新的一行开始布局
          left = getPaddingLeft();
          top += maxRowHeight;
          maxRowHeight = 0;
        }

        // 布局的时候要考虑到子View的margin，padding是由子View自己控制的。
        child.layout(left + lp.leftMargin,
            top + lp.topMargin,
            left + lp.leftMargin + child.getMeasuredWidth(),
            top + lp.topMargin + child.getMeasuredHeight());

        Log.d(TAG, String.format("%d %d %d %d",
            child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));

        left += cW;
        maxRowHeight = cH > maxRowHeight ? cH : maxRowHeight;

      }
    }
  }
}
