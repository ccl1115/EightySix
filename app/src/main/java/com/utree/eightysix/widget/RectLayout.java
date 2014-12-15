package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author simon
 */
public class RectLayout extends FrameLayout {
  public RectLayout(Context context) {
    this(context, null);
  }

  public RectLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final int w = r - l;
    final int h = b - t;
    for (int i = 0, s = getChildCount(); i < s; i++) {
      View view = getChildAt(i);
      final int cw = view.getMeasuredWidth();
      final int ch = view.getMeasuredHeight();
      view.layout((w - cw) >> 1, (h - ch) >> 1, (w + cw) >> 1, (h + ch) >> 1);
    }
  }
}
