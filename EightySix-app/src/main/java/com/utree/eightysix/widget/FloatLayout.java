package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 */
public class FloatLayout extends ViewGroup {

  private int mCount;

  public FloatLayout(Context context) {
    this(context, null);
  }

  public FloatLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public int getCount() {
    return mCount;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int l = getPaddingLeft(), t = getPaddingRight();
    int maxRowHeight = 0;
    for (int i = 0, size = getChildCount(); i < size; i++) {
      final View view = getChildAt(i);

      if (l + view.getMeasuredWidth() > right - getPaddingRight()) {
        if (t + view.getMeasuredHeight() > bottom - getPaddingBottom()) {
          mCount = i + 1;
          return;
        }
        l = 0;
        t += maxRowHeight;
        maxRowHeight = 0;
      }
      view.layout(l, t, l += view.getMeasuredWidth(), t += view.getMeasuredHeight());
      maxRowHeight = Math.max(t, maxRowHeight);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    int widthUsed = 0, maxRowHeight = 0;
    int measuredHeight = 0;

    for (int i = 0, size = getChildCount(); i < size; i++) {
      final View view = getChildAt(i);

      measureChild(view, widthMeasureSpec, heightMeasureSpec);

      widthUsed += view.getMeasuredWidth();
      maxRowHeight = Math.max(maxRowHeight, view.getMeasuredHeight());

      if (widthSize - widthUsed < 0) {
        measuredHeight += maxRowHeight;
        if (measuredHeight > heightSize) {
          break;
        }
        widthUsed = 0;
        maxRowHeight = 0;
      }
    }

    setMeasuredDimension(widthSize, Math.min(heightSize, measuredHeight));
  }
}
