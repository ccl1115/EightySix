/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
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

    int index = 1;
    while (true) {
      View childAt = getChildAt(getChildCount() - index);
      if (childAt != null) {
        int rightMargin = ((MarginLayoutParams) childAt.getLayoutParams()).rightMargin;
        if (childAt.getMeasuredWidth() == 0 ||
            childAt.getRight() + rightMargin == getRight() - getPaddingRight()) {
          removeViewInLayout(childAt);
        } else {
          break;
        }
      } else {
        break;
      }
      index++;
    }
  }
}
