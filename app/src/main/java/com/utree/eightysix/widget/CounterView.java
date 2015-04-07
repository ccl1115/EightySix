/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.utree.eightysix.R;

/**
 */
public class CounterView extends RoundedButton {
  public CounterView(Context context) {
    this(context, null, 0);
  }

  public CounterView(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.counterViewStyle);
  }

  public CounterView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setCount(int count) {
    count = Math.min(99, count);
    setVisibility(count == 0 ? GONE : VISIBLE);
    setText(String.valueOf(count));
  }
}
