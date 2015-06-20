/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is magic, use with caution.
 */
public class NonBreakLinearLayout extends LinearLayout {
  public NonBreakLinearLayout(Context context) {
    super(context);
  }

  public NonBreakLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NonBreakLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    View v;
    for (int i = 0, size = getChildCount(); i < size; i++) {
      v = getChildAt(i);
      if (((TextView) v).getLineCount() > 1) {
        v.setVisibility(GONE);
      }
    }
  }
}
