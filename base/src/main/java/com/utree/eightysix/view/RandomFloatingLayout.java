/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 */
public class RandomFloatingLayout extends AdapterView<Adapter> {

  private Adapter mAdapter;

  private LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

  private static final float[][] POSITIONS = {
      {0.5f, 0.52f},
      {0.35f, 0.4f},
      {0.7f, 0.24f},
      {0.8f, 0.6f},
      {0.65f, 0.75f},
      {0.75f, 0.9f},
      {0.28f, 0.8f},
      {0.20f, 0.68f}
  };

  public RandomFloatingLayout(Context context) {
    this(context, null, 0);
  }

  public RandomFloatingLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RandomFloatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public Adapter getAdapter() {
    return mAdapter;
  }

  @Override
  public void setAdapter(Adapter adapter) {
    mAdapter = adapter;

    if (mAdapter != null) {
      requestLayout();
      invalidate();
    }
  }

  @Override
  public View getSelectedView() {
    return null;
  }

  @Override
  public void setSelection(int position) {

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthSize = widthMeasureSpec & ~(0x3 << 30);
    final int heightSize = heightMeasureSpec & ~(0x3 << 30);

    setMeasuredDimension(widthSize, heightSize);
  }


  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (mAdapter != null) {
      final Adapter adapter = mAdapter;

      int min = Math.min(8, adapter.getCount());

      final int width = right - left;
      final int height = bottom - top;

      removeAllViewsInLayout();

      for (int i = 0; i < min; i++) {
        float[] position = POSITIONS[i];
        int vw = (int) (width * position[0]);
        int vh = (int) (height * position[1]);
        View view = adapter.getView(i, null, this);
        addViewInLayout(view, i, mParams);
        view.measure(width + MeasureSpec.AT_MOST, height + MeasureSpec.AT_MOST);
        view.layout(vw - (view.getMeasuredWidth() >> 1),
            vh - (view.getMeasuredHeight() >> 1),
            vw + (view.getMeasuredWidth() >> 1),
            vh + (view.getMeasuredHeight() >> 1));
      }
    }
  }
}
