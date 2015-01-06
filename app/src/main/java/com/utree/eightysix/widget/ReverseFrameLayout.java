/*
 * Copyright (c) 2014. All rights reserved by utree.cn
 */

package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 */
public class ReverseFrameLayout extends FrameLayout {
  public ReverseFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ReverseFrameLayout(Context context) {
    super(context);
  }

  @Override
  public void addView(View child) {
    if (getChildCount() == 1) {
      addView(child, 0);
    } else {
      super.addView(child);
    }
  }
}
