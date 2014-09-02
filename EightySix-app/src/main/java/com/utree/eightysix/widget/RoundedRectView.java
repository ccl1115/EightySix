package com.utree.eightysix.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import com.utree.eightysix.R;
import com.utree.eightysix.drawable.RoundRectDrawable;

/**
 * @author simon
 */
public class RoundedRectView extends View {
  public RoundedRectView(Context context) {
    this(context, null, 0);
  }

  public RoundedRectView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RoundedRectView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton, defStyleAttr, 0);

    int radius = ta.getDimensionPixelOffset(R.styleable.RoundedButton_radius, 0);
    ColorStateList colors = ta.getColorStateList(R.styleable.RoundedButton_background);

    setBackgroundDrawable(new RoundRectDrawable(radius, colors));
  }
}
